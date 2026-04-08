package com.booking.security;

import com.booking.configuration.JwtAuthenticationFilter;
import com.booking.models.CustomOAuth2User;
import com.booking.models.Role;
import com.booking.services.CustomOAuth2UserService;
import com.booking.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Autowired
    private CustomOAuth2UserService oAuth2UserService;

    @Autowired
    private UserService userService;

    private final String[] routes = new String[]{
            "/",
            "/auth/**",
            "/email",
            "/admin/**",
            "/user/**",
            "/contact/**",
            "/about/**",
            "/admin",
            "/contact",
            "/about",
            "/search",
            "/order",
            "/error/**",
            "/payment",
            "/webjars/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/api/user/**",
            "/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/uploads/**",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers(routes).permitAll()
                            .requestMatchers(
                                    "/api/v1/admin/**"
                            ).hasRole(Role.ADMIN.name())
                            .anyRequest().authenticated();
                })
                .formLogin(formLogin -> formLogin
                        .loginPage("/auth/login")
                        .loginPage("/admin/login")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/auth/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                        .defaultSuccessUrl("/", true)
                        .successHandler(
                                (request, response, authentication) -> {
                                    CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
                                    if (userService.isExistUser(oauthUser.getEmail())) {
                                        userService.processOAuthPostLogin(oauthUser.getEmail(), response);
                                        response.sendRedirect("/user/login-google-again?email=" + oauthUser.getEmail());
                                    } else {
                                        userService.processOAuthPostRegister(oauthUser.getEmail(), response);
                                        response.sendRedirect("/user/login-google-success?email=" + oauthUser.getEmail());
                                    }
                                })
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(
                                (request, response, authentication) -> SecurityContextHolder.clearContext()
                        ))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                         .authenticationEntryPoint((request, response, ex) ->{
                             response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                         })
                );
        return http.build();
    }
}
