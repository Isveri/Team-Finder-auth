package com.evi.teamfinderauth.service;

import com.evi.teamfinderauth.exception.UserNotFoundException;
import com.evi.teamfinderauth.security.model.TokenResponse;
import com.evi.teamfinderauth.domain.User;
import com.evi.teamfinderauth.security.model.UserCredentials;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
    public void createNewAccount(User userDto, HttpServletRequest request);

    public TokenResponse getToken(UserCredentials userCredentials) throws UserNotFoundException;
}
