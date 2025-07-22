package org.athletes.traineatrepeat.model.request;

import jakarta.validation.constraints.*;
import org.athletes.traineatrepeat.validation.PasswordMatches;

@PasswordMatches
public record RegisterRequest(
    @NotEmpty(message = "Username is required") String username,
    @NotEmpty(message = "Email is required") @Email(message = "Invalid email format") String email,
    @NotEmpty(message = "Password is required")
        @Size(min = 9, message = "Minimum password length is 9 characters")
        String password,
    @NotEmpty(message = "Confirm password is required") String confirmPassword,
    @NotNull(message = "Age is required") @Min(value = 1, message = "Age must be positive")
        Integer age,
    @NotEmpty(message = "Gender is required") String gender,
    @NotNull(message = "Weight is required") @Min(value = 1, message = "Weight must be positive")
        Float weight,
    @NotNull(message = "Height is required") @Min(value = 1, message = "Height must be positive")
        Float height) {

  public static RegisterRequest ofInitialRequestForm() {
    return new RegisterRequest("", "", "", "", null, "", null, null);
  }
}
