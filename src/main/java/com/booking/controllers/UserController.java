package com.booking.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserController {
    @GetMapping("/account-setting")
    public String update() {
        return "pages/auth/accountsetting";
    }

    @GetMapping("/login-google-success")
    public String loginGoogleSuccess(@RequestParam String email) {
        return "pages/auth/signingoogle";
    }

    @GetMapping("/login-google-again")
    public String loginFacebookAgain(@RequestParam String email) {
        return "pages/auth/signingoogleagain";
    }
}
