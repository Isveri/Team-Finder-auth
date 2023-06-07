package com.evi.teamfinderauth.service;

import com.evi.teamfinderauth.domain.Role;
import com.evi.teamfinderauth.repository.RoleRepository;
import com.evi.teamfinderauth.security.jwt.JwtTokenUtil;
import com.evi.teamfinderauth.security.model.TokenResponse;
import com.evi.teamfinderauth.domain.User;
import com.evi.teamfinderauth.security.model.UserCredentials;
import com.evi.teamfinderauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final RoleRepository roleRepository;

    @Override
    public TokenResponse getToken(UserCredentials userCredentials) {
        User user = (User) userDetailsService.loadUserByUsername(userCredentials.getUsername());
        if (passwordEncoder.matches(userCredentials.getPassword(), user.getPassword())) {
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

    }

}
