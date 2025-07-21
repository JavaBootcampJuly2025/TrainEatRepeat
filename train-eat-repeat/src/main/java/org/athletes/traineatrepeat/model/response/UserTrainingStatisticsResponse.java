package org.athletes.traineatrepeat.model.response;

import lombok.Builder;

@Builder
public record UserTrainingStatisticsResponse(
    double avgCaloriesBurnedPerSession, double avgPerDaySessions) {}
