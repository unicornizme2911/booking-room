package com.booking.configuration;

import com.booking.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${application.security.jwt.expiration}")
    private int jwtExpiration;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal( @NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        String accessToken = null;
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    accessToken = cookie.getValue();
                }
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }
        if (accessToken != null) {
            String email = jwtService.extractEmail(accessToken);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (userDetails != null && jwtService.isTokenValid(accessToken, userDetails)) {
                    setAuthentication(userDetails, request);
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }else{
            try{
                if (refreshToken != null) {
                    String email = jwtService.extractEmail(refreshToken);
                    UserDetails userDetails =
                            userDetailsService.loadUserByUsername(email);
                    if (email != null && jwtService.isTokenValid(refreshToken, userDetails)) {
                        var newAccessToken = jwtService.generateToken(userDetails);
                        createAccessTokenCookie(newAccessToken, response);
                        setAuthentication(userDetails, request);
                    }
                }
            } catch (Exception ex) {
                System.out.println("Refresh token invalid");
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    protected void createAccessTokenCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(jwtExpiration/1000);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }
}
