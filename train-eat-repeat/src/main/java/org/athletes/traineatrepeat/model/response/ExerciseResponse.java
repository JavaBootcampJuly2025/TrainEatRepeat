package org.athletes.traineatrepeat.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ExerciseResponse(String id, String name, @JsonProperty("MET") float met) {}
