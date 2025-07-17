package org.athletes.traineatrepeat.model.request;

import lombok.Builder;

@Builder
public record ExerciseRequest(
        String name,
        float MET
) {}