package org.athletes.traineatrepeat.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MealRecordRequest(
        @NotNull(message = "Food name cannot be null")
        String foodName,

        /**
         * COMMENT: this value cannot be null, as it is Java primitive type. If you want ot introduce more specific validation
         * use Min annotations from Jakarta Bean Validation
         */
        @NotNull(message = "Weight in grams cannot be null")
        float weightInGrams,
        LocalDate date
) {}