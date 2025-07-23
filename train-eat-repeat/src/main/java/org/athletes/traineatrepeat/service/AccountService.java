package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.athletes.traineatrepeat.model.request.RegisterRequest;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.athletes.traineatrepeat.common.ValidationCommon.EMAIL_REGEX;
import static org.athletes.traineatrepeat.common.ValidationCommon.PASSWORD_REGEX;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;

  public String prepareRegistrationForm(Model model) {
    var registerRequest = RegisterRequest.ofInitialRequestForm();
    model.addAttribute("registerRequest", registerRequest);
    return "register";
  }

  public String registerUser(RegisterRequest registerRequest, BindingResult result, Model model) {
    validateUsername(registerRequest.username(), result);
    validateEmail(registerRequest.email(), result);
    validatePasswords(registerRequest.password(), registerRequest.confirmPassword(), result);
    validateAge(registerRequest.age(), result);
    validateGender(registerRequest.gender(), result);
    validateWeight(registerRequest.weight(), result);
    validateHeight(registerRequest.height(), result);

    if (result.hasErrors()) {
      model.addAttribute("registerRequest", registerRequest);
      return "register";
    }

    float bmi = registerRequest.weight() / (registerRequest.height() / 100 * registerRequest.height() / 100);
    float bmr = calculateBMR(registerRequest);

    String verificationCode = UUID.randomUUID().toString();
    LocalDateTime verificationExpiresAt = LocalDateTime.now().plusHours(24);

    UserDTO user = UserDTO.builder()
            .username(registerRequest.username())
            .email(registerRequest.email())
            .password(passwordEncoder.encode(registerRequest.password()))
            .age(registerRequest.age())
            .gender(registerRequest.gender())
            .weight(registerRequest.weight())
            .height(registerRequest.height())
            .bmi(bmi)
            .bmr(bmr)
            .role("user")
            .verificationCode(verificationCode)
            .verificationExpiresAt(verificationExpiresAt)
            .isEmailVerified(false)
            .build();

    userRepository.save(user);

    try {
      emailService.sendVerificationEmail(registerRequest.email(), registerRequest.username(), verificationCode);
      return "redirect:/verify-email?email=" + registerRequest.email();
    } catch (Exception e) {
      model.addAttribute("error", "Failed to send verification email. Please try again.");
      model.addAttribute("registerRequest", registerRequest);
      return "register";
    }
  }

  public String prepareVerificationPage(String email, Model model) {
    validateEmailFormat(email, model);
    if (model.containsAttribute("error")) {
      return "verify-email";
    }
    model.addAttribute("email", email);
    return "verify-email";
  }

  public String verifyEmail(String email, String code, Model model) {
    validateEmailFormat(email, model);
    validateVerificationCode(code, model);

    if (model.containsAttribute("error")) {
      model.addAttribute("email", email);
      return "verify-email";
    }

    UserDTO user = userRepository.findByEmail(email);
    if (user == null) {
      model.addAttribute("error", "No user found with this email");
      model.addAttribute("email", email);
      return "verify-email";
    }

    if (user.isEmailVerified()) {
      return "redirect:/login";
    }

    if (user.getVerificationCode() != null
            && user.getVerificationCode().equals(code)
            && user.getVerificationExpiresAt() != null
            && LocalDateTime.now().isBefore(user.getVerificationExpiresAt())) {
      user.setEmailVerified(true);
      user.setVerificationCode(null);
      user.setVerificationExpiresAt(null);
      userRepository.save(user);
      return "redirect:/login";
    }

    model.addAttribute("error", "Invalid or expired verification code");
    model.addAttribute("email", email);
    return "verify-email";
  }

  private void validateUsername(String username, BindingResult result) {
    if (StringUtils.isEmpty(username)) {
      result.addError(new FieldError("registerRequest", "username", "Username is required"));
    } else if (username.length() < 5 || username.length() > 16) {
      result.addError(new FieldError("registerRequest", "username", "Username must be between 5 and 16 characters long"));
    }
  }

  private void validateEmail(String email, BindingResult result) {
    if (StringUtils.isEmpty(email)) {
      result.addError(new FieldError("registerRequest", "email", "Email is required"));
    } else if (!email.matches(EMAIL_REGEX)) {
      result.addError(new FieldError("registerRequest", "email", "Invalid email format"));
    } else if (userRepository.findByEmail(email) != null) {
      result.addError(new FieldError("registerRequest", "email", "Email address is already used"));
    }
  }

  private void validatePasswords(String password, String confirmPassword, BindingResult result) {
    if (StringUtils.isEmpty(password)) {
      result.addError(new FieldError("registerRequest", "password", "Password is required"));
    } else if (password.length() < 8 || password.length() > 32) {
      result.addError(new FieldError("registerRequest", "password", "Password must be between 8 and 32 characters long"));
    } else if (!password.matches(PASSWORD_REGEX)) {
      result.addError(new FieldError("registerRequest", "password", "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character"));
    } else if (!password.equals(confirmPassword)) {
      result.addError(new FieldError("registerRequest", "confirmPassword", "Password and Confirm Password do not match"));
    }
  }

  private void validateAge(Integer age, BindingResult result) {
    if (age == null) {
      result.addError(new FieldError("registerRequest", "age", "Age is required"));
    } else if (age < 1) {
      result.addError(new FieldError("registerRequest", "age", "Age must be positive"));
    }
  }

  private void validateGender(String gender, BindingResult result) {
    if (StringUtils.isEmpty(gender)) {
      result.addError(new FieldError("registerRequest", "gender", "Gender is required"));
    }
  }

  private void validateWeight(Float weight, BindingResult result) {
    if (weight == null) {
      result.addError(new FieldError("registerRequest", "weight", "Weight is required"));
    } else if (weight < 1) {
      result.addError(new FieldError("registerRequest", "weight", "Weight must be positive"));
    }
  }

  private void validateHeight(Float height, BindingResult result) {
    if (height == null) {
      result.addError(new FieldError("registerRequest", "height", "Height is required"));
    } else if (height < 1) {
      result.addError(new FieldError("registerRequest", "height", "Height must be positive"));
    }
  }

  private void validateEmailFormat(String email, Model model) {
    if (StringUtils.isEmpty(email)) {
      model.addAttribute("error", "Email is required");
    } else if (!email.matches(EMAIL_REGEX)) {
      model.addAttribute("error", "Invalid email format");
    }
  }

  private void validateVerificationCode(String code, Model model) {
    if (StringUtils.isEmpty(code)) {
      model.addAttribute("error", "Verification code is required");
    }
  }

  private float calculateBMR(RegisterRequest request) {
    float weight = request.weight();
    float heightCm = request.height();
    int age = request.age();

    if ("male".equalsIgnoreCase(request.gender())) {
      return (10 * weight) + (6.25f * heightCm) - (5 * age) + 5;
    } else {
      return (10 * weight) + (6.25f * heightCm) - (5 * age) - 161;
    }
  }
}