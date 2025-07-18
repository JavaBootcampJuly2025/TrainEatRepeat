package org.athletes.traineatrepeat.model.request;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TrainingRecordRequest (
        String uuid,
        String exercise,
        float duration,
        float caloriesLost,
        LocalDate date
) {}