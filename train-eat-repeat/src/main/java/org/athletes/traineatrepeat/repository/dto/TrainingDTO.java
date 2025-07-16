package org.athletes.traineatrepeat.repository.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TrainingDTO(
        String id,
        String exercise,
        float duration,
        float caloriesLost,
        LocalDate date
)
{}