package com.evi.teamfinderauth.controller;

import com.evi.teamfinderauth.domain.User;
import com.evi.teamfinderauth.exception.UserNotFoundException;
import com.evi.teamfinderauth.listeners.OnAccountDeleteCompleteEvent;
import com.evi.teamfinderauth.listeners.OnEmailChangeCompleteEvent;
import com.evi.teamfinderauth.model.ChangePasswordDTO;
import com.evi.teamfinderauth.model.EmailDTO;
import com.evi.teamfinderauth.security.model.TokenResponse;
import com.evi.teamfinderauth.security.model.UserCredentials;
import com.evi.teamfinderauth.service.AuthService;
import com.evi.teamfinderauth.utils.UserDetailsHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {


    private final AuthService authService;
    private final ApplicationEventPublisher eventPublisher;
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

    @GetMapping("/confirmAccountRegister")
    public ResponseEntity<TokenResponse> confirmAccountRegister(@RequestParam("token") String token) {
        return ResponseEntity.ok(authService.confirmAccountRegister(token));
    }

    @GetMapping("/confirmEmailChange")
    public ResponseEntity<?> confirmEmailChange(@RequestParam("token") String token){
        authService.confirmEmailChange(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/emailChange")
    public ResponseEntity<?> emailChange(@Valid @RequestBody EmailDTO email, HttpServletRequest request){
        eventPublisher.publishEvent(new OnEmailChangeCompleteEvent(UserDetailsHelper.getCurrentUser(),request.getLocale(),email.getEmail(),request.getContextPath()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteAccount(HttpServletRequest request){
        eventPublisher.publishEvent(new OnAccountDeleteCompleteEvent(UserDetailsHelper.getCurrentUser(),request.getLocale(),request.getContextPath()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/deleteAccountConfirm")
    public ResponseEntity<?> confirmDeleteAccount(@RequestParam("token") String token){
        authService.confirmDeleteAccount(token);
        return new ResponseEntity<>(HttpStatus.OK);

    }


    @PostMapping("/password-change")
    public ResponseEntity<?> changeUserPassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        authService.changePassword(changePasswordDTO);
        return ResponseEntity.ok("");
    }


}
