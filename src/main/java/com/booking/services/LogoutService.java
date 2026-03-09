package com.booking.services;

import com.booking.configuration.TokenType;
import com.booking.models.TokenModel;
import com.booking.repository.TokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class LogoutService implements LogoutHandler {

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }
        System.out.println("refreshToken: " + refreshToken);
        if (refreshToken == null) return;
        TokenModel storedToken = tokenRepository.findByToken(refreshToken)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }
        clearCookies(response);
    }

    private void clearCookies(HttpServletResponse response) {

        Cookie access = new Cookie("accessToken", null);
        access.setMaxAge(0);
        access.setPath("/");
        access.setSecure(false);

        Cookie refresh = new Cookie("refreshToken", null);
        refresh.setMaxAge(0);
        refresh.setPath("/");
        refresh.setSecure(false);

        Cookie role = new Cookie("role", null);
        role.setMaxAge(0);
        role.setPath("/");
        role.setSecure(false);

        response.addCookie(access);
        response.addCookie(refresh);
        response.addCookie(role);
    }
}
