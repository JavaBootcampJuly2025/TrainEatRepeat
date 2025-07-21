package org.athletes.traineatrepeat.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.TrainingRecordConverter;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.request.TrainingRecordRequest;
import org.athletes.traineatrepeat.model.response.TrainingRecordResponse;
import org.athletes.traineatrepeat.model.response.UserTrainingStatisticsResponse;
import org.athletes.traineatrepeat.repository.ExerciseRepository;
import org.athletes.traineatrepeat.repository.TrainingRecordRepository;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.athletes.traineatrepeat.repository.dto.TrainingDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingService {

  private final TrainingRecordRepository trainingRecordRepository;
  private final TrainingRecordConverter trainingRecordConverter;
  private final ExerciseRepository exerciseRepository;
  private final UserRepository userRepository;

  public TrainingRecordResponse submitTraining(TrainingRecordRequest request) {
    var user =
        userRepository
            .findById(request.uuid())
            .orElseThrow(() -> new RuntimeException("User not found"));

    var exercise =
        exerciseRepository
            .findByNameIgnoreCase(request.exercise())
            .orElseThrow(() -> new RuntimeException("Exercise not found: " + request.exercise()));

    float calories = calculateCalories(exercise.getMET(), user.getWeight(), request.duration());

    var trainingToSave = trainingRecordConverter.fromRequest(request);
    trainingToSave.setId(UUID.randomUUID().toString());
    trainingToSave.setUuid(user.getUuid());
    trainingToSave.setCaloriesLost(calories);

    var savedTraining = trainingRecordRepository.save(trainingToSave);
    return trainingRecordConverter.toResponse(savedTraining);
  }

  public TrainingRecordResponse updateTrainingById(
      String trainingId, TrainingRecordRequest request) {
    var existingTraining =
        trainingRecordRepository
            .findById(trainingId)
            .orElseThrow(() -> new RuntimeException("Training not found"));

    var user =
        userRepository
            .findById(request.uuid())
            .orElseThrow(() -> new RuntimeException("User not found"));

    var exercise =
        exerciseRepository
            .findByNameIgnoreCase(request.exercise())
            .orElseThrow(() -> new RuntimeException("Exercise not found: " + request.exercise()));

    float calories = calculateCalories(exercise.getMET(), user.getWeight(), request.duration());

    existingTraining.setExercise(request.exercise());
    existingTraining.setDuration(request.duration());
    existingTraining.setCaloriesLost(calories);
    existingTraining.setDate(request.date());

    var updatedTraining = trainingRecordRepository.save(existingTraining);
    return trainingRecordConverter.toResponse(updatedTraining);
  }

  private float calculateCalories(float met, float weight, float durationInMinutes) {
    return (met * weight * 3.5f * durationInMinutes) / 200f;
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
    return trainings.stream().map(trainingRecordConverter::toResponse).toList();
  }

  public UserTrainingStatisticsResponse getTrainingStatistics(String uuid, TimePeriod period) {
    var trainings = getTrainingsFromTimePeriod(uuid, period);
    double avgCaloriesBurned =
        trainings.stream().mapToDouble(TrainingDTO::getCaloriesLost).average().orElse(0);

    int daysInPeriod = 1;
    if (period != null) {

      // private final TimeProvider timeProvider; // Uncomment if you introduce TimeProvider as a
      // component
      /**
       * COMMENT: You can introduce TimeProvider class as a component and retrieve current date from
       * it. This will also simplify unit test creation, as you will be able to just mock the date
       */
      LocalDate today = LocalDate.now();
      switch (period) {
        case WEEK -> {
          LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
          daysInPeriod = (int) (java.time.temporal.ChronoUnit.DAYS.between(startOfWeek, today) + 1);
        }
        case MONTH -> {
          LocalDate startOfMonth = today.withDayOfMonth(1);
          daysInPeriod =
              (int) (java.time.temporal.ChronoUnit.DAYS.between(startOfMonth, today) + 1);
        }
      }
    }

    double avgSessions = trainings.size() / (double) daysInPeriod;

    return UserTrainingStatisticsResponse.builder()
        .avgCaloriesBurnedPerSession(avgCaloriesBurned)
        .avgPerDaySessions(avgSessions)
        .build();
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

    return trainingRecordRepository.findTrainingsByUuidAndDateBetween(
        uuid, startOfMonth, endOfMonth);
  }
}
