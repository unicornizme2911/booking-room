package com.booking.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController {
    @GetMapping("/403")
    public String handleError403() {
        return "error/403";
    }

    @GetMapping("/404")
    public String handleError404() {
        return "error/404";
    }

    @GetMapping("/500")
    public String handleError500() {
        return "error/500";
    }
}