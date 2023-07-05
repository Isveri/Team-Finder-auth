package com.evi.teamfinderauth.service;

import com.evi.teamfinderauth.domain.Role;
import com.evi.teamfinderauth.exception.*;
import com.evi.teamfinderauth.listeners.OnAccountRegisterCompleteEvent;
import com.evi.teamfinderauth.model.ChangePasswordDTO;
import com.evi.teamfinderauth.repository.RoleRepository;
import com.evi.teamfinderauth.repository.VerificationTokenRepository;
import com.evi.teamfinderauth.security.jwt.JwtTokenUtil;
import com.evi.teamfinderauth.security.model.TokenResponse;
import com.evi.teamfinderauth.domain.User;
import com.evi.teamfinderauth.security.model.UserCredentials;
import com.evi.teamfinderauth.repository.UserRepository;
import com.evi.teamfinderauth.security.model.VerificationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import java.util.Calendar;

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


    @Override
    public TokenResponse getToken(UserCredentials userCredentials) {
        User user = (User) userDetailsService.loadUserByUsername(userCredentials.getUsername());
        if(!user.isEnabled()){
            throw new AccountNotEnabledException("Account not enabled");
        }
        if (!user.isAccountNonLocked()) {
            throw new AccountBannedException("Account banned");
        } else if (passwordEncoder.matches(userCredentials.getPassword(), user.getPassword())) {
            return new TokenResponse(jwtTokenUtil.generateAccessToken(user));
        }
        throw new BadCredentialsException("Given credentials are invalid");
    }

    @Override
    public void createNewAccount(User user, HttpServletRequest request) {
        Role userRole = roleRepository.findByName("ROLE_USER");
        user.setRole(userRole);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        eventPublisher.publishEvent(new OnAccountRegisterCompleteEvent(user,request.getLocale(),request.getContextPath()));

    }

    @Override
    public TokenResponse confirmAccountRegister(String token) {

        validateVerificationToken(token);

        Long userId = verificationTokenRepository.findByToken(token).getUser().getId();
        User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found id:"+userId));
        user.setEnabled(true);
        userRepository.save(user);
        VerificationToken verificationToken = this.getVerificationToken(token);
        verificationTokenRepository.delete(verificationToken);
        return new TokenResponse(jwtTokenUtil.generateAccessToken(user));
    }

    @Override
    public void confirmDeleteAccount(String token) {

        validateVerificationToken(token);
        this.deleteUser();
    }

    @Override
    public void confirmEmailChange(String token) {
        validateVerificationToken(token);

        VerificationToken verificationToken = this.getVerificationToken(token);
        Long userId = verificationTokenRepository.findByToken(token).getUser().getId();
        User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found id:"+userId));
        user.setEmail(verificationToken.getEmail());
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }


    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        User currentUser = getCurrentUser();
        Long id = currentUser.getId();
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found id:" + id));
        //TODO WALIDACJE ZROBIC Z UZYCIEM HIBERNATE
      //  dataValidation.password(changePasswordDTO.getOldPassword());
      //  dataValidation.password(changePasswordDTO.getNewPassword());
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
        long id = currentUser.getId();
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found id:" + id));

        //TODO zrobic to na zasadzie wysyłania za pomocą openfeign zeby usunal uzytkownika z kazdej grupy + pobawic sie w transakcje
        // to jest jak sie nie uda to poprostu blad zwrocic tutaj
//        List<GroupRoom> UserGroupRooms = groupRepository.findAllByGroupLeaderId(id);
//        try {
//            for (GroupRoom groupRoom : UserGroupRooms) {
//                userService.getOutOfGroup(groupRoom.getId());
//            }
            deleteVerificationToken(user);
            userRepository.softDeleteById(id);
//
//        } catch (Exception e) {
//            throw new DeleteUserException("Something wrong with deleting a user");
//        }

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
        }else if(verificationTokenRepository.existsByEmail(email)){
            throw new EmailAlreadyTakenException("Email taken:"+email);
        }else if(userRepository.existsByEmail(email)){
            throw new EmailAlreadyTakenException("Email taken:"+email);
        }
        else {
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

    private void deleteVerificationToken(User user) {
        VerificationToken token = verificationTokenRepository.findByUser(user);
        token.setUser(null);
        Long tempId = token.getId();
        verificationTokenRepository.save(token);
        verificationTokenRepository.deleteById(tempId);
    }

}
