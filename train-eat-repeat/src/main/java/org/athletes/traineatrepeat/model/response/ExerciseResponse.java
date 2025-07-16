package org.athletes.traineatrepeat.model.response;

public record ExerciseResponse(
        String id,
        String name,
        float MET
) {}