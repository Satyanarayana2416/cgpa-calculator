package com.example.cgpa.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    String home(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())
                ? "redirect:/dashboard"
                : "redirect:/login";
    }

    @GetMapping("/dashboard")
    String dashboard(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))
                ? "redirect:/admin"
                : "redirect:/student/dashboard";
    }

    @GetMapping("/login")
    String login() {
        return "login";
    }
}