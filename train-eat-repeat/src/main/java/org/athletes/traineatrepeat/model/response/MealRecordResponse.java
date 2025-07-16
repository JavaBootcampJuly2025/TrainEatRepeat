package org.athletes.traineatrepeat.model.response;

import java.time.LocalDate;

public record MealRecordResponse(
        String id,
        String foodName,
        float caloriesConsumed,
        float carbs,
        float protein,
        float fat,
        LocalDate date
) {}