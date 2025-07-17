package org.athletes.traineatrepeat.model.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MealRecordResponse(
        String id,
        String userUuid,
        String foodName,
        float calories,
        float carbs,
        float protein,
        float fat,
        LocalDate date
) {}