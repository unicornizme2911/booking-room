package com.booking.services;

import com.booking.authentication.*;
import com.booking.configuration.TokenType;
import com.booking.dto.response.MailResponse;
import com.booking.dto.response.MessageResponse;
import com.booking.exception.PasswordNotMatchException;
import com.booking.exception.TokenNotFoundException;
import com.booking.exception.UserExistException;
import com.booking.exception.UserNotFoundException;
import com.booking.models.*;
import com.booking.repository.TokenRepository;
import com.booking.repository.UserRepository;
import com.booking.services.impl.MailServiceImpl;
import com.booking.utils.GenerateToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
public class AuthenticationService {
    @Value("${application.security.jwt.expiration}")
    private int jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private int refreshExpiration;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MailServiceImpl mailServiceImpl;

    private String tokenToVerify;

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserModel user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, refreshToken);
        createAccessTokenCookie(accessToken, response);
        createRefreshTokenCookie(refreshToken, response);
        createCookieForRole(user.getRole().name(),response);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String loginSuccess(String token) {
        var checkToken = tokenRepository.findByToken(token).orElseThrow(() -> new UserNotFoundException("Token not found"));
        var user = checkToken.getUsers();
        if (user.getRole().equals(Role.USER)) {
            return "/";
        } else {
            return "/auth/login";
        }
    }

    public AuthenticationResponse register(RegisterRequest request, HttpServletResponse response) throws UserExistException {
        if (userRepository.existsUserModelByEmail(request.getEmail())) {
            throw new UserExistException("User already exist");
        }
        var user = UserModel.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .avatar("/images/avtDefault.jpg")
                .date_created(Date.from(java.time.Instant.now()))
                .date_updated(null)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();
        var savedUser = userRepository.save(user);
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, refreshToken);
        createAccessTokenCookie(accessToken, response);
        createRefreshTokenCookie(refreshToken, response);
        createCookieForRole(user.getRole().name(),response);
//        cartService.addCart(CartModel.builder()
//                .user(savedUser)
//                .total_amount(0.0D)
//                .build());
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    protected void saveUserToken(UserModel user, String jwtToken) {
        var token = TokenModel.builder()
                .users(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies == null) return;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
        }
        if (refreshToken == null) return;
        String email = jwtService.extractEmail(refreshToken);
        if (email != null) {
            var user = userRepository.findByEmail(email)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                revokeAllUserTokens(user);
                var newAccessToken = jwtService.generateToken(user);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    protected void revokeAllUserTokens(UserModel user) {
        var validToken = tokenRepository.findAllValidTokenByUser(String.valueOf(user.getId()));
        if (validToken.isEmpty()) {
            return;
        }
        validToken.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validToken);
    }

    public MailResponse sendMail(EmailRequest request) {
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user == null) {
            return null;
        } else {
            tokenToVerify = GenerateToken.randomDigits(6);
            var mail = Mail.builder()
                    .to(user.getEmail())
                    .subject("Code to Reset Your Password")
                    .content(tokenToVerify)
                    .build();
            mailServiceImpl.sendEmail(mail, "mail-sender.html");
        }
        return MailResponse.builder()
                .message("Email sent successfully")
                .status(true)
                .build();
    }

    public MessageResponse verifyToken(TokenRequest request) {
        if (request.getToken().equals(tokenToVerify)) {
            return MessageResponse.builder()
                    .message("Token verified successfully")
                    .build();
        }
        throw new TokenNotFoundException("Token is not valid");
    }

    public MessageResponse changePassword(PasswordRequest request) {
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (request.getPassword().equals(request.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);
            return MessageResponse.builder()
                    .message("Password changed successfully")
                    .build();
        } else {
            throw new PasswordNotMatchException("Password does not match");
        }
    }

    public MessageResponse checkEmail(EmailRequest request) {
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user != null) {
            return MessageResponse.builder()
                    .message("Email is valid")
                    .build();
        }
        throw new UserNotFoundException("User not found");
    }

    protected void createAccessTokenCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(jwtExpiration/1000);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    protected void createRefreshTokenCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(refreshExpiration/1000);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    protected void createCookieForRole(String role, HttpServletResponse response) {
        Cookie cookie = new Cookie("role", role);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(refreshExpiration/1000);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }
}
