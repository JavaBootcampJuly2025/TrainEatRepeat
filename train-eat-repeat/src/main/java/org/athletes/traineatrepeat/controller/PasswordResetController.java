package org.athletes.traineatrepeat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.request.PasswordResetRequest;
import org.athletes.traineatrepeat.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {
  private final PasswordResetService passwordResetService;

  @GetMapping("/forgot-password")
  public String showForgotPasswordForm(Model model) {
    return passwordResetService.prepareForgotPasswordForm(model);
  }

  @PostMapping("/forgot-password")
  public String requestPasswordReset(
      @Valid @ModelAttribute("passwordResetRequest") PasswordResetRequest request, Model model) {
    return passwordResetService.handlePasswordResetRequest(request, model);
  }

  @GetMapping("/reset-password")
  public String showResetPasswordForm(
      @RequestParam String token, @RequestParam String email, Model model) {
    return passwordResetService.prepareResetPasswordForm(token, email, model);
  }

  @PostMapping("/reset-password")
  public String resetPassword(
      @Valid @ModelAttribute("passwordResetRequest") PasswordResetRequest request,
      @RequestParam String token,
      Model model) {
    return passwordResetService.handlePasswordReset(request, token, model);
  }
}
