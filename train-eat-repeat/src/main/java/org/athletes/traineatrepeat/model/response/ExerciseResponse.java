package org.athletes.traineatrepeat.model.response;

import lombok.Builder;

@Builder
public record ExerciseResponse(
        String id,
        String name,
        float MET
) {}