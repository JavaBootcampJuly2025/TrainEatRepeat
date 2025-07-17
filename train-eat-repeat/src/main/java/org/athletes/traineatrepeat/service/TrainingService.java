package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.TrainingRecordConverter;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.request.TrainingRecordRequest;
import org.athletes.traineatrepeat.model.response.TrainingRecordResponse;
import org.athletes.traineatrepeat.repository.TrainingRecordRepository;
import org.athletes.traineatrepeat.repository.dto.TrainingDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRecordRepository trainingRecordRepository;
    private final TrainingRecordConverter trainingRecordConverter;

    public TrainingRecordResponse submitTraining(TrainingRecordRequest request) {
        var trainingToSave = trainingRecordConverter.fromRequest(request);
        var savedTraining = trainingRecordRepository.save(trainingToSave);
        return trainingRecordConverter.toResponse(savedTraining);
    }

    /**
     * Retrieves trainings for the users depending on the time period (day, week, month)
     *
     * @param uuid user's id
     * @param timePeriod time period
     * @return lists of trainings per time period
     */
    public List<TrainingRecordResponse> getTrainingsForUser(String uuid, TimePeriod timePeriod) {
        var trainings = getTrainingsFromTimePeriod(uuid, timePeriod);
        return trainings.stream()
                .map(trainingRecordConverter::toResponse)
                .toList();
    }

    private List<TrainingDTO> getTrainingsFromTimePeriod(String uuid, TimePeriod timePeriod) {
        if (timePeriod == null) {
            return trainingRecordRepository.findAllByUuid(uuid);
        }
        return switch (timePeriod) {
            case DAY -> getTrainingsForToday(uuid);
            case WEEK -> getTrainingsForWeek(uuid);
            case MONTH -> getTrainingsForMonth(uuid);
        };
    }

    public void deleteTrainingById(String trainingId) {
        trainingRecordRepository.deleteById(trainingId);
    }

    public TrainingRecordResponse updateTrainingById(String trainingId, TrainingRecordRequest request) {
        var existingTraining = trainingRecordRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        existingTraining.setExercise(request.exercise());
        existingTraining.setDuration(request.duration());
        existingTraining.setCaloriesLost(request.caloriesLost());
        existingTraining.setDate(request.date());

        var updatedTraining = trainingRecordRepository.save(existingTraining);
        return trainingRecordConverter.toResponse(updatedTraining);
    }

    private List<TrainingDTO> getTrainingsForToday(String uuid) {
        LocalDate today = LocalDate.now();
        return trainingRecordRepository.findAllByUuidAndDate(uuid, today);
    }

    private List<TrainingDTO> getTrainingsForWeek(String uuid) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);

        return trainingRecordRepository.findTrainingsByUuidAndDateBetween(uuid, startOfWeek, endOfWeek);
    }

    private List<TrainingDTO> getTrainingsForMonth(String uuid) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        return trainingRecordRepository.findTrainingsByUuidAndDateBetween(uuid, startOfMonth, endOfMonth);
    }
}