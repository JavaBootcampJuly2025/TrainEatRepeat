package org.athletes.traineatrepeat.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.TrainingRecordConverter;
import org.athletes.traineatrepeat.exceptions.TrainEatRepeatException;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.request.TrainingRecordRequest;
import org.athletes.traineatrepeat.model.response.TrainingRecordResponse;
import org.athletes.traineatrepeat.model.response.UserTrainingStatisticsResponse;
import org.athletes.traineatrepeat.repository.ExerciseRepository;
import org.athletes.traineatrepeat.repository.TrainingRecordRepository;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.athletes.traineatrepeat.repository.dto.TrainingDTO;
import org.athletes.traineatrepeat.util.TimeProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingService {

  private final TrainingRecordRepository trainingRecordRepository;
  private final TrainingRecordConverter trainingRecordConverter;
  private final ExerciseRepository exerciseRepository;
  private final UserRepository userRepository;
  private final TimeProvider timeProvider;

  public TrainingRecordResponse submitTraining(TrainingRecordRequest request) {
    var user =
            userRepository
                    .findById(request.uuid())
                    .orElseThrow(
                            () -> new TrainEatRepeatException("User not found with UUID: " + request.uuid()));

    var exercise =
            exerciseRepository
                    .findByNameIgnoreCase(request.exercise())
                    .orElseThrow(
                            () -> new TrainEatRepeatException("Exercise not found: " + request.exercise()));

    float calories = calculateCalories(exercise.getMet(), user.getWeight(), request.duration());

    LocalDate trainingDate = request.date() != null ? request.date() : timeProvider.getCurrentDate();

    var trainingToSave =
            TrainingDTO.builder()
                    .id(UUID.randomUUID().toString())
                    .uuid(user.getUuid())
                    .exercise(request.exercise())
                    .duration(request.duration())
                    .caloriesLost(calories)
                    .date(trainingDate)
                    .build();

    var savedTraining = trainingRecordRepository.save(trainingToSave);
    return trainingRecordConverter.toResponse(savedTraining);
  }

  public TrainingRecordResponse updateTrainingById(
          String trainingId, TrainingRecordRequest request) {
    var existingTraining =
            trainingRecordRepository
                    .findById(trainingId)
                    .orElseThrow(
                            () -> new TrainEatRepeatException("Training not found with ID: " + trainingId));

    var user =
            userRepository
                    .findById(request.uuid())
                    .orElseThrow(
                            () -> new TrainEatRepeatException("User not found with UUID: " + request.uuid()));

    var exercise =
            exerciseRepository
                    .findByNameIgnoreCase(request.exercise())
                    .orElseThrow(
                            () -> new TrainEatRepeatException("Exercise not found: " + request.exercise()));

    float calories = calculateCalories(exercise.getMet(), user.getWeight(), request.duration());

    LocalDate trainingDate = request.date() != null ? request.date() : timeProvider.getCurrentDate();

    existingTraining.setExercise(request.exercise());
    existingTraining.setDuration(request.duration());
    existingTraining.setCaloriesLost(calories);
    existingTraining.setDate(trainingDate);

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
    List<TrainingDTO> trainings;

    if (!userRepository.existsById(uuid)) {
      throw new TrainEatRepeatException("User not found with UUID: " + uuid);
    }

    if (timePeriod == null) {
      trainings = getTrainingsForToday(uuid);
    } else {
      trainings = getTrainingsFromTimePeriod(uuid, timePeriod);
    }

    return trainings.stream().map(trainingRecordConverter::toResponse).toList();
  }

  public UserTrainingStatisticsResponse getTrainingStatistics(String uuid, TimePeriod period) {
    List<TrainingDTO> trainings;

    if (period == null) {
      trainings = getTrainingsForToday(uuid);
    } else {
      trainings = getTrainingsFromTimePeriod(uuid, period);
    }

    double avgCaloriesBurned =
            trainings.stream().mapToDouble(TrainingDTO::getCaloriesLost).average().orElse(0);

    int daysInPeriod = 1;
    if (period != null) {
      LocalDate today = timeProvider.getCurrentDate();
      daysInPeriod = getDaysInPeriod(period, today);
    }

    float avgSessions = trainings.size() / (float) daysInPeriod;

    return UserTrainingStatisticsResponse.builder()
            .avgCaloriesBurnedPerSession(avgCaloriesBurned)
            .avgPerDaySessions(avgSessions)
            .build();
  }

  private static int getDaysInPeriod(TimePeriod period, LocalDate today) {
    switch (period) {
      case DAY -> {
        return 1;
      }
      case WEEK -> {
        var startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        return (int) (java.time.temporal.ChronoUnit.DAYS.between(startOfWeek, today) + 1);
      }
      case MONTH -> {
        var startOfMonth = today.withDayOfMonth(1);
        return (int) (java.time.temporal.ChronoUnit.DAYS.between(startOfMonth, today) + 1);
      }
      default ->
              throw new TrainEatRepeatException(
                      "Invalid time period for training statistics: " + period);
    }
  }

  private List<TrainingDTO> getTrainingsFromTimePeriod(String uuid, TimePeriod timePeriod) {
    if (!userRepository.existsById(uuid)) {
      throw new TrainEatRepeatException("User not found with UUID: " + uuid);
    }

    if (timePeriod == null) {
      return getTrainingsForToday(uuid);
    }
    return switch (timePeriod) {
      case DAY -> getTrainingsForToday(uuid);
      case WEEK -> getTrainingsForWeek(uuid);
      case MONTH -> getTrainingsForMonth(uuid);
    };
  }

  public void deleteTrainingById(String trainingId) {
    if (!trainingRecordRepository.existsById(trainingId)) {
      throw new TrainEatRepeatException("Training not found with ID: " + trainingId);
    }
    trainingRecordRepository.deleteById(trainingId);
  }

  private List<TrainingDTO> getTrainingsForToday(String uuid) {
    LocalDate today = timeProvider.getCurrentDate();
    return trainingRecordRepository.findAllByUuidAndDate(uuid, today);
  }

  private List<TrainingDTO> getTrainingsForWeek(String uuid) {
    LocalDate today = timeProvider.getCurrentDate();
    var startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
    var endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);

    return trainingRecordRepository.findTrainingsByUuidAndDateBetween(uuid, startOfWeek, endOfWeek);
  }

  private List<TrainingDTO> getTrainingsForMonth(String uuid) {
    LocalDate today = timeProvider.getCurrentDate();
    var startOfMonth = today.withDayOfMonth(1);
    var endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

    return trainingRecordRepository.findTrainingsByUuidAndDateBetween(
            uuid, startOfMonth, endOfMonth);
  }
}