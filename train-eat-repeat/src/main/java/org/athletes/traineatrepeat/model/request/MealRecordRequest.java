package org.athletes.traineatrepeat.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record MealRecordRequest(
    @NotNull(message = "Food name cannot be null") String foodName,
    @DecimalMin(value = "0.0", message = "Weight must be a positive number") float weightInGrams,
    LocalDate date) {}
