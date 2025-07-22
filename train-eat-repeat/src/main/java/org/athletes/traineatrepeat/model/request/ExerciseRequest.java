package org.athletes.traineatrepeat.model.request;

import static org.athletes.traineatrepeat.common.ValidationCommon.NAME_REGEX;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record ExerciseRequest(
    @NotNull(message = "Exercise name cannot be null")
        @Pattern(regexp = NAME_REGEX, message = "Invalid exercise name format")
        String name,
    @JsonProperty("MET") @DecimalMin(value = "0.1", message = "Minimal allowed MET is 0.1")
        float met) {}
