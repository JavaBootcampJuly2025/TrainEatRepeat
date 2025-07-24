package org.athletes.traineatrepeat.service;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.common.ValidationCommon;
import org.athletes.traineatrepeat.model.request.PasswordResetRequest;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;

  public String prepareForgotPasswordForm(Model model) {
    model.addAttribute("passwordResetRequest", new PasswordResetRequest("", "", ""));
    return "forgot-password";
  }

  public String handlePasswordResetRequest(
      PasswordResetRequest request, BindingResult result, Model model) {
    if (result.hasFieldErrors("email")) {
      return "forgot-password";
    }

    try {
      if (!initiatePasswordReset(request.email())) {
        model.addAttribute("error", "No user found with this email address.");
        return "forgot-password";
      }
      return "redirect:/forgot-password?success=true";
    } catch (Exception e) {
      model.addAttribute("error", "Failed to send password reset email. Please try again.");
      return "forgot-password";
    }
  }

  public String prepareResetPasswordForm(String token, String email, Model model) {
    if (validateResetToken(email, token)) {
      model.addAttribute("error", "Invalid or expired password reset token.");
      return "reset-password";
    }

    model.addAttribute("passwordResetRequest", new PasswordResetRequest(email, "", ""));
    model.addAttribute("token", token);
    return "reset-password";
  }

  public String handlePasswordReset(
      PasswordResetRequest request, String token, BindingResult result, Model model) {
    if (result.hasErrors()) {
      model.addAttribute("token", token);
      return "reset-password";
    }

    if (!validatePasswords(request.password(), request.confirmPassword(), model)) {
      model.addAttribute("token", token);
      return "reset-password";
    }

    if (!performPasswordReset(request.email(), token, request.password())) {
      model.addAttribute("error", "Invalid or expired password reset token.");
      model.addAttribute("token", token);
      return "reset-password";
    }

    return "redirect:/login";
  }

  private boolean initiatePasswordReset(String email) {
    UserDTO user = userRepository.findByEmail(email);
    if (user == null || !user.isEmailVerified()) {
      return false;
    }

    String resetToken = UUID.randomUUID().toString();
    LocalDateTime resetTokenExpiresAt = LocalDateTime.now().plusHours(24);

    user.setResetToken(resetToken);
    user.setResetTokenExpiresAt(resetTokenExpiresAt);
    userRepository.save(user);

    try {
      emailService.sendPasswordResetEmail(email, user.getUsername(), resetToken);
    } catch (Exception e) {
      invalidateResetToken(user);
      return false;
    }
    return true;
  }

  private boolean validateResetToken(String email, String token) {
    UserDTO user = userRepository.findByEmail(email);
    if (user == null) {
      return true;
    }

    return user.getResetToken() == null
        || !user.getResetToken().equals(token)
        || user.getResetTokenExpiresAt() == null
        || !LocalDateTime.now().isBefore(user.getResetTokenExpiresAt());
  }

  private boolean validatePasswords(String password, String confirmPassword, Model model) {
    if (password == null || password.trim().isEmpty()) {
      model.addAttribute("error", "Password is required");
      return false;
    }

    if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
      model.addAttribute("error", "Confirm password is required");
      return false;
    }

    if (!password.equals(confirmPassword)) {
      model.addAttribute("error", "Passwords do not match");
      return false;
    }

    if (password.length() < 8) {
      model.addAttribute("error", "Password must be at least 8 characters long");
      return false;
    }

    if (!password.matches(ValidationCommon.PASSWORD_REGEX)) {
      model.addAttribute(
          "error",
          "Password must contain at least one uppercase letter, "
              + "one lowercase letter, one number and one special character");
      return false;
    }

    return true;
  }

  private boolean performPasswordReset(String email, String token, String newPassword) {
    UserDTO user = userRepository.findByEmail(email);
    if (user == null || validateResetToken(email, token)) {
      return false;
    }

    user.setPassword(passwordEncoder.encode(newPassword));
    invalidateResetToken(user);
    return true;
  }

  private void invalidateResetToken(UserDTO user) {
    user.setResetToken(null);
    user.setResetTokenExpiresAt(null);
    userRepository.save(user);
  }
}
