package org.athletes.traineatrepeat.model.response;

import lombok.Builder;

@Builder
public record ExerciseResponse(
        String id,
        String name,
        /**
         * COMMENT: Java variable names should be in camelCase. If you want to break the rule,
         * use the @JsonProperty annotation to specify the JSON field name
         */
        float MET
) {}