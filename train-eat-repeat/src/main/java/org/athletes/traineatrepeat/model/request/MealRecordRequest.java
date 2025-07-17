package org.athletes.traineatrepeat.model.request;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MealRecordRequest(
        String id,
        String userUuid,
        String foodName,
        float caloriesConsumed,
        float carbs,
        float protein,
        float fat,
        LocalDate date
) {}