package org.athletes.traineatrepeat.model.request;

import java.time.LocalDate;

public record MealRecordRequest(
        String foodName,
        float caloriesConsumed,
        float carbs,
        float protein,
        float fat,
        LocalDate date
) {}