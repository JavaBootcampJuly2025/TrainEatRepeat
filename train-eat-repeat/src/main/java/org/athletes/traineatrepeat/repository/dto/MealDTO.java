package org.athletes.traineatrepeat.repository.dto;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record MealDTO(
    String id,
    String foodName,
    float caloriesConsumed,
    float carbs,
    float protein,
    float fat,
    LocalDate date
) {}
