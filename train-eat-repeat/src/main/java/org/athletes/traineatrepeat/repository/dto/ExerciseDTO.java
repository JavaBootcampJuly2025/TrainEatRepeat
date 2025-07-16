package org.athletes.traineatrepeat.repository.dto;

import lombok.Builder;

@Builder
public record ExerciseDTO(
        String id,
        String name,
        float MET
) {}