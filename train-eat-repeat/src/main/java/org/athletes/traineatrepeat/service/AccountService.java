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
        // Password confirmation check
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            result.addError(new FieldError(
                    "registerRequest",
                    "confirmPassword",
                    "Password and Confirm Password do not match"
            ));
        }

        // Email uniqueness check
        if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
            result.addError(new FieldError(
                    "registerRequest",
                    "email",
                    "Email address is already used"
            ));
        }

        if (result.hasErrors()) {
            return; // Controller will handle this by checking result.hasErrors()
        }

        // Calculate BMI & BMR (optional)
        float bmi = registerRequest.getWeight() / (registerRequest.getHeight() / 100 * registerRequest.getHeight() / 100);
        float bmr = calculateBMR(registerRequest);

        // Create User entity
        UserDTO user = UserDTO.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword())) // Hash password
                .age(registerRequest.getAge())
                .gender(registerRequest.getGender())
                .weight(registerRequest.getWeight())
                .height(registerRequest.getHeight())
                .BMI(bmi)
                .BMR(bmr)
                .role("user")
                .build();

        userRepository.save(user);
    }

    private float calculateBMR(RegisterRequest request) {
        float weight = request.getWeight();
        float heightCm = request.getHeight();
        int age = request.getAge();

        if ("male".equalsIgnoreCase(request.getGender())) {
            return (10 * weight) + (6.25f * heightCm) - (5 * age) + 5;
        } else {
            return (10 * weight) + (6.25f * heightCm) - (5 * age) - 161;
        }
    }
}
