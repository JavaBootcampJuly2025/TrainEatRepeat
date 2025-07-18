package org.athletes.traineatrepeat.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MealRecordRequest(
        @NotNull(message = "Food name cannot be null")
        String foodName,
        @NotNull(message = "Weight in grams cannot be null")
        float weightInGrams,
        LocalDate date
) {}