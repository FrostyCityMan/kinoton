package com.kinoton.sales.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String selectLogin() {
        return "auth/login";
    }
}
