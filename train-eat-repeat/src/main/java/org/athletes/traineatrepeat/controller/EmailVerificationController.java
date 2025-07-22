package org.athletes.traineatrepeat.controller;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class EmailVerificationController {

    private final AccountService accountService;

    @GetMapping("/verify-email")
    public String showVerificationPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "verify-email";
    }

    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam String email, @RequestParam String code, Model model) {
        boolean isVerified = accountService.verifyEmail(email, code);
        if (isVerified) {
            return "redirect:/login";
        } else {
            model.addAttribute("error", "Invalid or expired verification code");
            model.addAttribute("email", email);
            return "verify-email";
        }
    }
}