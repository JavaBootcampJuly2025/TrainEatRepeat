package org.athletes.traineatrepeat.converter;

import org.athletes.traineatrepeat.model.request.ExerciseRequest;
import org.athletes.traineatrepeat.model.response.ExerciseResponse;
import org.athletes.traineatrepeat.repository.dto.ExerciseDTO;
import org.springframework.stereotype.Component;

@Component
public class ExerciseConverter {

    public ExerciseDTO fromRequest(ExerciseRequest request) {
        return ExerciseDTO.builder()
                .name(request.name())
                .MET(request.MET())
                .build();
    }

    public ExerciseResponse toResponse(ExerciseDTO dto) {
        return new ExerciseResponse(
                dto.id(),
                dto.name(),
                dto.MET()
        );
    }
}