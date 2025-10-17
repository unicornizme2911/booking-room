package com.booking.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @GetMapping("/login")
    public String login() {
        return "pages/auth/signin";
    }

    @GetMapping("/register")
    public String register() {
        return "pages/auth/signup";
    }

    @GetMapping("/logout")
    public String logout() {
        return "pages/auth/signin";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "pages/auth/forgotpassword";
    }

    @GetMapping("/login-google")
    public String loginGoogle() {
        return "redirect:/oauth2/authorization/google";
    }
}
