package com.ticketing.transport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Will render src/main/resources/templates/login.html
    }

    @GetMapping("/postLogin")
    public String postLoginRedirect(org.springframework.security.core.Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/user/dashboard";
        }
    }

}
