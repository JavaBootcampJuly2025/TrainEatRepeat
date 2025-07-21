package org.athletes.traineatrepeat.converter;

import org.athletes.traineatrepeat.model.request.ExerciseRequest;
import org.athletes.traineatrepeat.model.response.ExerciseResponse;
import org.athletes.traineatrepeat.repository.dto.ExerciseDTO;
import org.springframework.stereotype.Component;

@Component
public class ExerciseConverter {

  public ExerciseDTO fromRequestToEntity(ExerciseRequest request) {
    return ExerciseDTO.builder().name(request.name()).MET(request.met()).build();
  }

  public ExerciseResponse toResponseFromEntity(ExerciseDTO entity) {
    return ExerciseResponse.builder()
        .id(entity.getId())
        .name(entity.getName())
        .met(entity.getMET())
        .build();
  }
}
