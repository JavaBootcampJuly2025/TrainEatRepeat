package org.athletes.traineatrepeat.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.athletes.traineatrepeat.model.request.RegisterRequest;

public class PasswordMatchesValidator
    implements ConstraintValidator<PasswordMatches, RegisterRequest> {

  @Override
  public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
    if (request.password() == null || request.confirmPassword() == null) {
      return false;
    }

    boolean matches = request.password().equals(request.confirmPassword());

    if (!matches) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
          .addPropertyNode("confirmPassword")
          .addConstraintViolation();
    }

    return matches;
  }
}
