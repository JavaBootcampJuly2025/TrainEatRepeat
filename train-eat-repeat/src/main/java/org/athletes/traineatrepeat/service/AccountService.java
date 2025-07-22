package org.athletes.traineatrepeat.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.request.RegisterRequest;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;

  public void registerUser(@Valid RegisterRequest registerRequest, BindingResult result) {

    if (!registerRequest.password().equals(registerRequest.confirmPassword())) {
      result.addError(
          new FieldError(
              "registerRequest", "confirmPassword", "Password and Confirm Password do not match"));
    }


    if (userRepository.findByEmail(registerRequest.email()) != null) {
      result.addError(new FieldError("registerRequest", "email", "Email address is already used"));
    }

    if (result.hasErrors()) {
      return;
    }


    float bmi =
        registerRequest.weight()
            / (registerRequest.height() / 100 * registerRequest.height() / 100);
    float bmr = calculateBMR(registerRequest);


    String verificationCode = UUID.randomUUID().toString();
    LocalDateTime verificationExpiresAt = LocalDateTime.now().plusHours(24);

    UserDTO user =
            UserDTO.builder()
                    .username(registerRequest.username())
                    .email(registerRequest.email())
                    .password(passwordEncoder.encode(registerRequest.password())) // Hash password
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
    } catch (Exception e) {
      result.addError(new FieldError("registerRequest", "email", "Failed to send verification email. Please try again later."));
    }
  }

  public boolean verifyEmail(String email, String code) {
    UserDTO user = userRepository.findByEmail(email);
    if (user == null) {
      return false;
    }

    if (user.isEmailVerified()) {
      return true;
    }

    if (user.getVerificationCode() != null
            && user.getVerificationCode().equals(code)
            && user.getVerificationExpiresAt() != null
            && LocalDateTime.now().isBefore(user.getVerificationExpiresAt())) {
      user.setEmailVerified(true);
      user.setVerificationCode(null);
      user.setVerificationExpiresAt(null);
      userRepository.save(user);
      return true;
    }
    return false;
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
