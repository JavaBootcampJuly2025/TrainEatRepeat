package org.athletes.traineatrepeat.model.request;

import jakarta.validation.constraints.*;

public record PasswordResetRequest(
        @NotEmpty(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        @NotEmpty(message = "Password is required")
        @Size(min = 9, message = "Minimum password length is 9 characters")
        String password,
        @NotEmpty(message = "Confirm password is required")
        String confirmPassword) {}