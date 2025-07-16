package org.athletes.traineatrepeat.converter;

import org.athletes.traineatrepeat.model.request.TrainingRecordRequest;
import org.athletes.traineatrepeat.model.response.TrainingRecordResponse;
import org.athletes.traineatrepeat.repository.dto.TrainingDTO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;

@Component
public class TrainingRecordConverter {

    public TrainingDTO fromRequest(TrainingRecordRequest request) {
        return TrainingDTO.builder()
                .exercise(request.exercise())
                .duration(request.duration())
                .caloriesLost(request.caloriesLost())
                .date(request.date())
                .build();
    }

    public TrainingRecordResponse toResponse(TrainingDTO dto) {
        return new TrainingRecordResponse(
                dto.id(),
                dto.exercise(),
                dto.duration(),
                dto.caloriesLost(),
                dto.date()
        );
    }
}