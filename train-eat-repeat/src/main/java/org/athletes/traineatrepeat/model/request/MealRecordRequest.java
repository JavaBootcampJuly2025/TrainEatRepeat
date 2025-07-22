package org.athletes.traineatrepeat.model.request;

import static org.athletes.traineatrepeat.common.ValidationCommon.NAME_REGEX;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record MealRecordRequest(
    @NotNull(message = "Food name cannot be null")
        @Pattern(regexp = NAME_REGEX, message = "Invalid exercise name format")
        String foodName,
    @DecimalMin(value = "0.1", message = "Minimal allowed weight for meal is 0.1 gram")
        float weightInGrams,
    LocalDate date) {}
