package com.evi.teamfinderauth.service;

import com.evi.teamfinderauth.exception.UserNotFoundException;
import com.evi.teamfinderauth.model.ChangePasswordDTO;
import com.evi.teamfinderauth.security.model.TokenResponse;
import com.evi.teamfinderauth.domain.User;
import com.evi.teamfinderauth.security.model.UserCredentials;
import com.evi.teamfinderauth.security.model.VerificationToken;


import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

public interface AuthService {
     void createNewAccount(User userDto, HttpServletRequest request);
     TokenResponse getToken(UserCredentials userCredentials) throws UserNotFoundException;

     TokenResponse confirmAccountRegister(String token);

    void confirmEmailChange(String token);

    void confirmDeleteAccount(String token);

    void changePassword(ChangePasswordDTO changePasswordDTO);


    void sendMessage(MimeMessage mimeMessage);

    void deleteUser();

    void createVerificationToken(User user, String token);

    void createEmailChangeToken(User user, String token, String email);

    VerificationToken getVerificationToken(String VerificationToken);


}
