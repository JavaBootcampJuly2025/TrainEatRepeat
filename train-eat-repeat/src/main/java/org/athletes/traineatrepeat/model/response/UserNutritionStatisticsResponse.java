package org.athletes.traineatrepeat.model.response;

import lombok.Builder;

@Builder
public record UserNutritionStatisticsResponse (
    double avgProtein,
    double avgFat,
    double avgCarbs,
    double avgCalories
) {}