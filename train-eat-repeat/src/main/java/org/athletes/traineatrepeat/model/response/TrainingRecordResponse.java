package org.athletes.traineatrepeat.model.response;

import java.time.LocalDate;

public record TrainingRecordResponse (
        String id,
        String exercise,
        float duration,
        float caloriesLost,
        LocalDate date
)
{}