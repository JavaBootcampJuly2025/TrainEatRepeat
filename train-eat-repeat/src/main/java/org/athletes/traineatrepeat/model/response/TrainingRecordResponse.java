package org.athletes.traineatrepeat.model.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TrainingRecordResponse (
        String id,
        String exercise,
        float duration,
        float caloriesLost,
        LocalDate date
) {}