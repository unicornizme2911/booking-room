package com.booking.configuration;

import com.booking.services.AuthenticationService;
import com.booking.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal( @NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
//        if (path.equals("/api/v1/auth/login") ||
//                path.equals("/api/v1/auth/register") ||
//                path.equals("/api/v1/auth/refresh")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
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
            System.out.println("đang access token");
            String email = jwtService.extractEmail(accessToken);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (userDetails != null && jwtService.isTokenValid(accessToken, userDetails)) {
                    setAuthentication(userDetails, request);
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
        cookie.setMaxAge(15000/1000);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }
}
