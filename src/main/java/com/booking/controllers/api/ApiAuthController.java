package com.booking.controllers.api;

import com.booking.authentication.*;
import com.booking.dto.response.MailResponse;
import com.booking.dto.response.MessageResponse;
import com.booking.services.AuthenticationService;
import com.booking.services.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
class ApiAuthController {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private LogoutService logoutService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request, HttpServletResponse httpServletResponse) {
        return ResponseEntity.ok(authenticationService.authenticate(request, httpServletResponse));
    }

    @PostMapping("/logout")
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        logoutService.logout(request, response, authentication);
    }

    @PostMapping("/login-success")
    public String loginSuccess(@RequestBody String token) {
        return authenticationService.loginSuccess(token);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @PostMapping("/send-mail")
    public ResponseEntity<MailResponse> sendMail(
            @RequestBody @Valid EmailRequest request) {
        return ResponseEntity.ok(authenticationService.sendMail(request));
    }

    @PostMapping("/verify-token")
    public ResponseEntity<MessageResponse> verifyToken(
            @RequestBody @Valid TokenRequest request) {
        return ResponseEntity.ok(authenticationService.verifyToken(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
            @RequestBody @Valid PasswordRequest request) {
        return ResponseEntity.ok(authenticationService.changePassword(request));
    }

    @PostMapping("/check-email")
    public ResponseEntity<MessageResponse> checkEmail(
            @RequestBody @Valid EmailRequest request) {
        return ResponseEntity.ok(authenticationService.checkEmail(request));
    }
}
