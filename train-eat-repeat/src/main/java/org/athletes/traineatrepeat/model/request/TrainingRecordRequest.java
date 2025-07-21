package org.athletes.traineatrepeat.model.request;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record TrainingRecordRequest(
    // TODO: add validation annotations (regex as well)
    String uuid, String exercise, float duration, float caloriesLost, LocalDate date) {}
