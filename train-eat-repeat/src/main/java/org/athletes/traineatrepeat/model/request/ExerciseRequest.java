package org.athletes.traineatrepeat.model.request;

import lombok.Builder;

@Builder
public record ExerciseRequest(
        String name,
        /**
         * COMMENT: Java variable names should be in camelCase. If you want to break the rule,
         * use the @JsonProperty annotation to specify the JSON field name
         */
        float MET
) {}