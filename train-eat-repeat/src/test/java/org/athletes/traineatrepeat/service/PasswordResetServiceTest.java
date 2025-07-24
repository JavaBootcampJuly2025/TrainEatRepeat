package org.athletes.traineatrepeat.service;

import static org.athletes.traineatrepeat.service.PasswordResetService.ERROR_ATTRIBUTE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.athletes.traineatrepeat.model.request.PasswordResetRequest;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

class PasswordResetServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private EmailService emailService;

  @Mock private Model model;

  @InjectMocks private PasswordResetService passwordResetService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should prepare forgot password form and return correct view")
  void prepareForgotPasswordForm_addsEmptyRequestAndReturnsView() {
    String view = passwordResetService.prepareForgotPasswordForm(model);
    verify(model).addAttribute(eq("passwordResetRequest"), any(PasswordResetRequest.class));
    assertEquals("forgot-password", view);
  }

  @Test
  @DisplayName("Should initiate password reset and redirect on success")
  void handlePasswordResetRequest_successfulInitiation_redirectsWithSuccess()
      throws MessagingException {
    PasswordResetRequest request = new PasswordResetRequest("email@example.com", "", "");
    UserDTO user = new UserDTO();
    user.setEmail("email@example.com");
    user.setEmailVerified(true);

    when(userRepository.findByEmail("email@example.com")).thenReturn(user);
    doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString(), anyString());
    when(userRepository.save(any(UserDTO.class))).thenReturn(user);

    String result = passwordResetService.handlePasswordResetRequest(request, model);

    assertEquals("redirect:/forgot-password?success=true", result);
    verify(model, never()).addAttribute(eq(ERROR_ATTRIBUTE), anyString());
  }

  @Test
  @DisplayName("Should return forgot-password with error when email not found")
  void handlePasswordResetRequest_noUser_addsErrorAndReturnsForgotPassword() {
    PasswordResetRequest request = new PasswordResetRequest("nonexistent@example.com", "", "");

    when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

    String result = passwordResetService.handlePasswordResetRequest(request, model);

    assertEquals("forgot-password", result);
    verify(model).addAttribute(ERROR_ATTRIBUTE, "No user found with this email address.");
  }

  @Test
  @DisplayName("Should return reset-password form with valid token")
  void prepareResetPasswordForm_validToken_returnsResetPasswordPage() {
    String token = "valid-token";
    String email = "email@example.com";
    UserDTO user = new UserDTO();
    user.setEmail(email);
    user.setResetToken(token);
    user.setResetTokenExpiresAt(LocalDateTime.now().plusHours(1));

    when(userRepository.findByEmail(email)).thenReturn(user);

    String view = passwordResetService.prepareResetPasswordForm(token, email, model);

    assertEquals("reset-password", view);
    verify(model).addAttribute("token", token);
    verify(model).addAttribute(eq("passwordResetRequest"), any(PasswordResetRequest.class));
    verify(model, never()).addAttribute(eq(ERROR_ATTRIBUTE), anyString());
  }

  @Test
  @DisplayName("Should return forgot-password with error when token is invalid")
  void prepareResetPasswordForm_invalidToken_returnsForgotPasswordPageWithError() {
    String token = "invalid-token";
    String email = "email@example.com";
    UserDTO user = new UserDTO();
    user.setEmail(email);
    user.setResetToken("some-other-token");
    user.setResetTokenExpiresAt(LocalDateTime.now().plusHours(1));

    when(userRepository.findByEmail(email)).thenReturn(user);

    String view = passwordResetService.prepareResetPasswordForm(token, email, model);

    assertEquals("forgot-password", view);
    verify(model).addAttribute(ERROR_ATTRIBUTE, "Invalid or expired password reset token.");
    verify(model).addAttribute(eq("passwordResetRequest"), any(PasswordResetRequest.class));
  }

  @Test
  @DisplayName("Should reset password and redirect to login on success")
  void handlePasswordReset_validPasswordsAndToken_redirectsToLogin() {
    String token = "valid-token";
    String email = "email@example.com";
    String newPassword = "Valid123!";
    PasswordResetRequest request = new PasswordResetRequest(email, newPassword, newPassword);

    UserDTO user = new UserDTO();
    user.setEmail(email);
    user.setResetToken(token);
    user.setResetTokenExpiresAt(LocalDateTime.now().plusHours(1));

    when(userRepository.findByEmail(email)).thenReturn(user);
    when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");

    String result = passwordResetService.handlePasswordReset(request, token, model);

    assertEquals("redirect:/login", result);
    verify(userRepository)
        .save(
            argThat(
                savedUser ->
                    savedUser.getPassword().equals("encodedPassword")
                        && savedUser.getResetToken() == null));
  }

  @Test
  @DisplayName("Should return reset-password with error when passwords mismatch")
  void handlePasswordReset_passwordsMismatch_returnsResetPasswordPageWithError() {
    String token = "valid-token";
    String email = "email@example.com";
    PasswordResetRequest request = new PasswordResetRequest(email, "Password1!", "Password2!");

    String view = passwordResetService.handlePasswordReset(request, token, model);

    assertEquals("reset-password", view);
    verify(model).addAttribute(ERROR_ATTRIBUTE, "Passwords do not match");
    verify(model).addAttribute("token", token);
  }

  @Test
  @DisplayName("Sh    ould return reset-password with error when token is invalid")
  void handlePasswordReset_invalidToken_returnsResetPasswordPageWithError() {
    String token = "invalid-token";
    String email = "email@example.com";
    String password = "Valid123!";
    PasswordResetRequest request = new PasswordResetRequest(email, password, password);

    UserDTO user = new UserDTO();
    user.setEmail(email);
    user.setResetToken("some-other-token");
    user.setResetTokenExpiresAt(LocalDateTime.now().plusHours(1));

    when(userRepository.findByEmail(email)).thenReturn(user);

    String view = passwordResetService.handlePasswordReset(request, token, model);

    assertEquals("reset-password", view);
    verify(model).addAttribute(ERROR_ATTRIBUTE, "Invalid or expired password reset token.");
    verify(model).addAttribute("token", token);
  }

  @Test
  @DisplayName("Should return forgot-password with error when email sending fails")
  void handlePasswordResetRequest_emailSendingFails_returnsForgotPasswordWithError()
      throws Exception {
    String email = "email@example.com";
    PasswordResetRequest request = new PasswordResetRequest(email, "", "");
    UserDTO user = new UserDTO();
    user.setEmail(email);
    user.setEmailVerified(true);
    user.setUsername("someUser");

    when(userRepository.findByEmail(email)).thenReturn(user);
    doThrow(new RuntimeException("SMTP error"))
        .when(emailService)
        .sendPasswordResetEmail(eq(email), anyString(), anyString());

    String result = passwordResetService.handlePasswordResetRequest(request, model);

    assertEquals("forgot-password", result);
    verify(model)
        .addAttribute(ERROR_ATTRIBUTE, "Failed to send password reset email. Please try again.");
    verify(userRepository, times(2)).save(user);
  }

  @ParameterizedTest(name = "[{index}] {2}")
  @MethodSource("invalidPasswordInputs")
  @DisplayName("Should return validation error for invalid passwords in handlePasswordReset")
  void handlePasswordReset_invalidPasswordInputs_returnsError(
      String password, String confirmPassword, String expectedErrorMessage) {
    String token = "valid-token";
    String email = "user@example.com";
    PasswordResetRequest request = new PasswordResetRequest(email, password, confirmPassword);

    String view = passwordResetService.handlePasswordReset(request, token, model);

    assertEquals("reset-password", view);
    verify(model).addAttribute(ERROR_ATTRIBUTE, expectedErrorMessage);
    verify(model).addAttribute("token", token);
    reset(model);
  }

  private static Stream<Arguments> invalidPasswordInputs() {
    return Stream.of(
        Arguments.of(null, "Confirm123!", "Password is required"),
        Arguments.of("   ", "Confirm123!", "Password is required"),
        Arguments.of("Password123!", null, "Confirm password is required"),
        Arguments.of("Password123!", "   ", "Confirm password is required"));
  }

  @Test
  @DisplayName("Should return error when password is too short")
  void handlePasswordReset_shortPassword_returnsError() {
    String token = "valid-token";
    String email = "user@example.com";
    String shortPassword = "P1!a";
    PasswordResetRequest request = new PasswordResetRequest(email, shortPassword, shortPassword);

    String view = passwordResetService.handlePasswordReset(request, token, model);

    assertEquals("reset-password", view);
    verify(model).addAttribute(ERROR_ATTRIBUTE, "Password must be at least 8 characters long");
    verify(model).addAttribute("token", token);
  }

  @Test
  @DisplayName("Should return error when password does not meet complexity")
  void handlePasswordReset_invalidPattern_returnsError() {
    String token = "valid-token";
    String email = "user@example.com";
    String weakPassword = "password";
    PasswordResetRequest request = new PasswordResetRequest(email, weakPassword, weakPassword);

    String view = passwordResetService.handlePasswordReset(request, token, model);

    assertEquals("reset-password", view);
    verify(model)
        .addAttribute(
            ERROR_ATTRIBUTE,
            "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character");
    verify(model).addAttribute("token", token);
  }
}
