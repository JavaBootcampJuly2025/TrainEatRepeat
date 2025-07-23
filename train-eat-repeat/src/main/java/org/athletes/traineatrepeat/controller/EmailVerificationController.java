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
        return accountService.prepareVerificationPage(email, model);
    }

    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam String email, @RequestParam String code, Model model) {
        return accountService.verifyEmail(email, code, model);
    }
}