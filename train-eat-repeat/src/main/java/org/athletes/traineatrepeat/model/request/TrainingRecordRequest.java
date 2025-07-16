package org.athletes.traineatrepeat.model.request;

import java.time.LocalDate;

public record TrainingRecordRequest (
        String exercise,
        float duration,
        float caloriesLost,
        LocalDate date
)
{}