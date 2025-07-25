package org.athletes.traineatrepeat.model.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record MealRecordRequest(
        @NotNull(message = "Food name cannot be null")
        @Pattern(regexp = "^[A-Za-z][a-zA-Z0-9 '.,&()-]*[a-zA-Z0-9)]$", message = "Invalid food name format")
        String foodName,
        @DecimalMin(value = "0.1", message = "Minimal allowed weight for meal is 0.1 gram")
        float weightInGrams,
        LocalDate date) {}