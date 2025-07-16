package org.athletes.traineatrepeat.repository.dto;

import java.time.LocalDate;

public record TrainingDTO(
        String id,
        String exerciseName,
        String duration,
        String caloriesLost,
        LocalDate date
)
{}