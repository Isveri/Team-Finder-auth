package com.evi.teamfinderauth.service;

import com.evi.teamfinderauth.domain.Role;
import com.evi.teamfinderauth.exception.*;
import com.evi.teamfinderauth.listener.OnAccountRegisterCompleteEvent;
import com.evi.teamfinderauth.model.ChangePasswordDTO;
import com.evi.teamfinderauth.repository.RoleRepository;
import com.evi.teamfinderauth.repository.VerificationTokenRepository;
import com.evi.teamfinderauth.security.jwt.JwtTokenUtil;
import com.evi.teamfinderauth.security.model.TokenResponse;
import com.evi.teamfinderauth.domain.User;
import com.evi.teamfinderauth.security.model.UserCredentials;
import com.evi.teamfinderauth.repository.UserRepository;
import com.evi.teamfinderauth.security.model.VerificationToken;
import com.evi.teamfinderauth.service.feign.CoreServiceFeignClient;
import com.evi.teamfinderauth.service.feign.GroupManagementServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.evi.teamfinderauth.utils.UserDetailsHelper.getCurrentUser;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final JavaMailSender javaMailSender;
    private final ApplicationEventPublisher eventPublisher;
    private final GroupManagementServiceFeignClient groupManagementServiceFeignClient;
    private final CoreServiceFeignClient coreServiceFeignClient;


    @Override
    public TokenResponse getToken(UserCredentials userCredentials) {
        User user = (User) userDetailsService.loadUserByUsername(userCredentials.getUsername());
        if (!user.isEnabled()) {
            throw new AccountNotEnabledException("Account not enabled");
        }
        if (!user.isAccountNonLocked()) {
            throw new AccountBannedException("Account banned");
        }
        if (user.isDeleted()) {
            throw new UserNotFoundException("Given credentials are invalid");
        } else if (passwordEncoder.matches(userCredentials.getPassword(), user.getPassword())) {
            return new TokenResponse(jwtTokenUtil.generateAccessToken(user));
        }
        throw new WrongPasswordException("Given credentials are invalid");
    }

    @Override
    public void createNewAccount(User user, HttpServletRequest request) {
        Role userRole = roleRepository.findByName("ROLE_USER");
        user.setRole(userRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        eventPublisher.publishEvent(new OnAccountRegisterCompleteEvent(user, request.getLocale(), request.getContextPath()));

    }

    @Transactional
    @Override
    public TokenResponse confirmAccountRegister(String token) {

        validateVerificationToken(token);

        Long userId = verificationTokenRepository.findByToken(token).getUser().getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found id:" + userId));
        user.setEnabled(true);
        userRepository.save(user);
        VerificationToken verificationToken = this.getVerificationToken(token);
        verificationTokenRepository.delete(verificationToken);
        return new TokenResponse(jwtTokenUtil.generateAccessToken(user));
    }

    @Transactional
    @Override
    public void confirmDeleteAccount(String token) {

        validateVerificationToken(token);
        this.deleteUser();
    }

    @Transactional
    @Override
    public void confirmEmailChange(String token) {
        validateVerificationToken(token);

        VerificationToken verificationToken = this.getVerificationToken(token);
        Long userId = verificationTokenRepository.findByToken(token).getUser().getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found id:" + userId));
        user.setEmail(verificationToken.getEmail());
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }


    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        User currentUser = getCurrentUser();
        Long id = currentUser.getId();
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found id:" + id));

        if (passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            try {
                user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
                userRepository.save(user);
            } catch (Exception e) {
                throw new EmailAlreadyTakenException("something wrong with upd password");
            }
        } else {
            throw new WrongPasswordException("Wrong password");
        }
    }

    @Async
    @Override
    public void sendMessage(MimeMessage mimeMessage) {
        javaMailSender.send(mimeMessage);
    }

    @Override
    public void deleteUser() {
        User currentUser = getCurrentUser();
        Long id = currentUser.getId();
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found id:" + id));
        List<Long> removedIds = removeAllFriends();
        String groupBackup = exitAllGroups(removedIds);

        try {
            userRepository.softDeleteById(id);
            deleteVerificationToken(user);
        } catch (Exception e) {
            coreServiceFeignClient.rollbackDelete(removedIds);
            groupManagementServiceFeignClient.rollbackExit(groupBackup);
            throw new RuntimeException("Error deleting user account");
        }

    }

    private String exitAllGroups(List<Long> removedIds) {
        String groupBackup = "";
        try {
            groupBackup = groupManagementServiceFeignClient.exitAllGroups();

        } catch (Exception e) {
            coreServiceFeignClient.rollbackDelete(removedIds);
            throw new RuntimeException("Error deleting user account");
        }
        return groupBackup;
    }

    private List<Long> removeAllFriends() {
        List<Long> removedIds;
        try {
            removedIds = coreServiceFeignClient.removeAllFriends();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user account");
        }
        return removedIds;
    }

    @Override
    public void createVerificationToken(User user, String token) {
        if (verificationTokenRepository.existsByUserId(user.getId())) {
            throw new TokenAlreadySendException("Verification token already send");
        } else {
            VerificationToken myToken = VerificationToken.builder().token(token).user(user).build();
            verificationTokenRepository.save(myToken);
        }
    }

    @Override
    public void createEmailChangeToken(User user, String token, String email) {
        if (verificationTokenRepository.existsByUserId(user.getId())) {
            throw new TokenAlreadySendException("Verification token already send");
        } else if (verificationTokenRepository.existsByEmail(email)) {
            throw new EmailAlreadyTakenException("Email taken:" + email);
        } else if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyTakenException("Email taken:" + email);
        } else {
            VerificationToken myToken = VerificationToken.builder().token(token).email(email).user(user).build();
            verificationTokenRepository.save(myToken);
        }
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return verificationTokenRepository.findByToken(VerificationToken);
    }


    private void validateVerificationToken(String token) {
        VerificationToken verificationToken = this.getVerificationToken(token);
        if (verificationToken == null) {

            throw new TokenExpiredException("Bad token");
        }

        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new TokenExpiredException("Token expired");
        }
    }

    public void deleteVerificationToken(User user) {
        VerificationToken token = verificationTokenRepository.findByUser(user);
        Long tempId = token.getId();
        verificationTokenRepository.deleteById(tempId);
    }

}
