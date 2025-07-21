package org.athletes.traineatrepeat.model.response;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record MealRecordResponse(
    String id,
    String uuid,
    String foodName,
    float calories,
    float carbs,
    float protein,
    float fat,
    float weightInGrams,
    LocalDate date) {}
