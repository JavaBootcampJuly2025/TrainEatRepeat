package org.athletes.traineatrepeat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.request.PasswordResetRequest;
import org.athletes.traineatrepeat.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {

    private final AccountService accountService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("passwordResetRequest", new PasswordResetRequest("", "", ""));
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String requestPasswordReset(
            @Valid @ModelAttribute("passwordResetRequest") PasswordResetRequest request,
            BindingResult result,
            Model model) {
        if (result.hasFieldErrors("email")) {
            return "forgot-password";
        }

        try {
            boolean initiated = accountService.initiatePasswordReset(request.email());
            if (!initiated) {
                model.addAttribute("error", "No user found with this email address.");
                return "forgot-password";
            }
            return "redirect:/forgot-password?success=true";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to send password reset email. Please try again.");
            return "forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, @RequestParam String email, Model model) {
        boolean isValid = accountService.validateResetToken(email, token);
        if (!isValid) {
            model.addAttribute("error", "Invalid or expired password reset token.");
            return "reset-password";
        }
        model.addAttribute("passwordResetRequest", new PasswordResetRequest(email, "", ""));
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @Valid @ModelAttribute("passwordResetRequest") PasswordResetRequest request,
            @RequestParam String token,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("token", token);
            return "reset-password";
        }

        if (!request.password().equals(request.confirmPassword())) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        boolean success = accountService.resetPassword(request.email(), token, request.password());
        if (!success) {
            model.addAttribute("error", "Invalid or expired password reset token.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        return "redirect:/login";
    }
}