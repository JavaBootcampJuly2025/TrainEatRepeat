package org.athletes.traineatrepeat.model.request;

public record MealRecordRequest(
        String foodName,
        float caloriesConsumed,
        float carbs,
        float protein,
        float fat
) {}