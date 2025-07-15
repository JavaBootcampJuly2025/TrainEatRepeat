package org.athletes.traineatrepeat.model;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record MealRecord (
    String id,
    String foodName,
    int caloriesConsumed,
    float carbs,
    float protein,
    float fat,
    LocalDate date
) {}
