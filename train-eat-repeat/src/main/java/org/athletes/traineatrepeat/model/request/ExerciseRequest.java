package org.athletes.traineatrepeat.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ExerciseRequest(
    @NotNull
        // TODO: add regex here to validate the name
        // @Pattern()
        String name,
    @JsonProperty("MET") @DecimalMin(value = "0.0", message = "MET must be a positive number")
        float met) {}
