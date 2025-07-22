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

@Service
@RequiredArgsConstructor
public class AccountService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public void registerUser(@Valid RegisterRequest registerRequest, BindingResult result) {
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
            .build();

    userRepository.save(user);
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
