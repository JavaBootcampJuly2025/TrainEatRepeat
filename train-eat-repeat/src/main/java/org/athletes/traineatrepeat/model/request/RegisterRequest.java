package org.athletes.traineatrepeat.model.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RegisterRequest {

    @NotEmpty(message = "Username is required")
    private String username;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Password is required")
    @Size(min = 9, message = "Minimum password length is 9 characters")
    private String password;

    @NotEmpty(message = "Confirm password is required")
    private String confirmPassword;

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be positive")
    private Integer age;

    @NotEmpty(message = "Gender is required")
    private String gender;

    @NotNull(message = "Weight is required")
    @Min(value = 1, message = "Weight must be positive")
    private Float weight;

    @NotNull(message = "Height is required")
    @Min(value = 1, message = "Height must be positive")
    private Float height;
}
