package org.athletes.traineatrepeat.model.request;

import static org.athletes.traineatrepeat.common.ValidationCommon.UUID_REGEX;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TrainingRecordRequest(
    @NotBlank(message = "UUID cannot be empty")
        @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format")
        String uuid,
    @NotBlank(message = "Exercise name cannot be empty") String exercise,
    @PositiveOrZero(message = "Duration must be greater than 0") float duration,
    @PositiveOrZero(message = "Calories lost must be greater than 0") float caloriesLost,
    @NotNull(message = "Date cannot be null") LocalDate date) {}
