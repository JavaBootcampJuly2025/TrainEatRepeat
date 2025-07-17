package org.athletes.traineatrepeat.model.response;

import lombok.Builder;

import java.util.List;

@Builder
public record UserResponse(
        String uuid,
        String username,
        Integer age,
        String gender,
        List<String> chronicDiseases,
        List<String> foodPreferences,
        float weight,
        float height,
        float BMI,
        float BMR,
        String role
)
{}