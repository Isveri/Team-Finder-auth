package com.evi.teamfinderauth.controller;

import com.evi.teamfinderauth.domain.User;
import com.evi.teamfinderauth.exception.UserNotFoundException;
import com.evi.teamfinderauth.security.model.TokenResponse;
import com.evi.teamfinderauth.security.model.UserCredentials;
import com.evi.teamfinderauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserCredentials userCredentials) throws UserNotFoundException {
        TokenResponse token = authService.getToken(userCredentials);
        if (Objects.nonNull(token)) {
            return ResponseEntity.ok(token);
        } else {
            return new ResponseEntity<>("Login error", HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> createNewAccount(@Valid @RequestBody User user, HttpServletRequest request) {
        authService.createNewAccount(user,request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
