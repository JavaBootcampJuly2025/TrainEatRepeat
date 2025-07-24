package org.athletes.traineatrepeat.service;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.common.ValidationCommon;
import org.athletes.traineatrepeat.model.PasswordResetResult;
import org.athletes.traineatrepeat.model.request.PasswordResetRequest;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
  public static final String FORGOT_PASSWORD_PAGE = "forgot-password";
  public static final String ERROR_ATTRIBUTE = "error";
  public static final String RESET_PASSWORD_PAGE = "reset-password";
  public static final String TOKEN_ATTRIBUTE = "token";
  public static final String PASSWORD_RESET_REQUEST_ATTRIBUTE = "passwordResetRequest";
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;

  public String prepareForgotPasswordForm(Model model) {
    model.addAttribute(PASSWORD_RESET_REQUEST_ATTRIBUTE, new PasswordResetRequest("", "", ""));
    return FORGOT_PASSWORD_PAGE;
  }

  public String handlePasswordResetRequest(PasswordResetRequest request, Model model) {
    PasswordResetResult result = initiatePasswordReset(request.email());
    if (result != PasswordResetResult.SUCCESS) {
      String errorMessage =
          result == PasswordResetResult.USER_NOT_FOUND
              ? "No user found with this email address."
              : "Failed to send password reset email. Please try again.";
      model.addAttribute(ERROR_ATTRIBUTE, errorMessage);
      return FORGOT_PASSWORD_PAGE;
    }
    return "redirect:/forgot-password?success=true";
  }

  public String prepareResetPasswordForm(String token, String email, Model model) {
    if (validateResetToken(email, token)) {
      model.addAttribute(ERROR_ATTRIBUTE, "Invalid or expired password reset token.");
      model.addAttribute(PASSWORD_RESET_REQUEST_ATTRIBUTE, new PasswordResetRequest("", "", ""));
      return FORGOT_PASSWORD_PAGE; // Return a different page on error
    }
    model.addAttribute(PASSWORD_RESET_REQUEST_ATTRIBUTE, new PasswordResetRequest(email, "", ""));
    model.addAttribute(TOKEN_ATTRIBUTE, token);
    return RESET_PASSWORD_PAGE;
  }

  public String handlePasswordReset(PasswordResetRequest request, String token, Model model) {

    if (!validatePasswords(request.password(), request.confirmPassword(), model)) {
      model.addAttribute(TOKEN_ATTRIBUTE, token);
      return RESET_PASSWORD_PAGE;
    }

    if (!performPasswordReset(request.email(), token, request.password())) {
      model.addAttribute(ERROR_ATTRIBUTE, "Invalid or expired password reset token.");
      model.addAttribute(TOKEN_ATTRIBUTE, token);
      return RESET_PASSWORD_PAGE;
    }

    return "redirect:/login";
  }

  private PasswordResetResult initiatePasswordReset(String email) {
    UserDTO user = userRepository.findByEmail(email);
    if (user == null || !user.isEmailVerified()) {
      return PasswordResetResult.USER_NOT_FOUND;
    }

    String resetToken = UUID.randomUUID().toString();
    LocalDateTime resetTokenExpiresAt = LocalDateTime.now().plusHours(24);

    user.setResetToken(resetToken);
    user.setResetTokenExpiresAt(resetTokenExpiresAt);
    userRepository.save(user);

    try {
      emailService.sendPasswordResetEmail(email, user.getUsername(), resetToken);
      return PasswordResetResult.SUCCESS;
    } catch (Exception e) {
      invalidateResetToken(user);
      return PasswordResetResult.EMAIL_FAILED;
    }
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
      model.addAttribute(ERROR_ATTRIBUTE, "Password is required");
      return false;
    }

    if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
      model.addAttribute(ERROR_ATTRIBUTE, "Confirm password is required");
      return false;
    }

    if (!password.equals(confirmPassword)) {
      model.addAttribute(ERROR_ATTRIBUTE, "Passwords do not match");
      return false;
    }

    if (password.length() < 8) {
      model.addAttribute(ERROR_ATTRIBUTE, "Password must be at least 8 characters long");
      return false;
    }

    if (!password.matches(ValidationCommon.PASSWORD_REGEX)) {
      model.addAttribute(
          ERROR_ATTRIBUTE,
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
