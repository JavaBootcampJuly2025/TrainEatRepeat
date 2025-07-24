package org.athletes.traineatrepeat.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.athletes.traineatrepeat.converter.TrainingRecordConverter;
import org.athletes.traineatrepeat.exceptions.TrainEatRepeatException;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.request.TrainingRecordRequest;
import org.athletes.traineatrepeat.model.response.TrainingRecordResponse;
import org.athletes.traineatrepeat.model.response.UserTrainingStatisticsResponse;
import org.athletes.traineatrepeat.repository.ExerciseRepository;
import org.athletes.traineatrepeat.repository.TrainingRecordRepository;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.athletes.traineatrepeat.repository.dto.ExerciseDTO;
import org.athletes.traineatrepeat.repository.dto.TrainingDTO;
import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.athletes.traineatrepeat.util.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {
  @Mock private TrainingRecordRepository trainingRepo;
  @Mock private ExerciseRepository exerciseRepo;
  @Mock private UserRepository userRepo;
  @Mock private TimeProvider timeProvider;
  @Mock private TrainingRecordConverter converter;

  @InjectMocks private TrainingService service;

  private final String userUuid = UUID.randomUUID().toString();
  private final String trainingId = UUID.randomUUID().toString();
  private LocalDate today;
  private TrainingDTO savedDto;

  @BeforeEach
  void setUp() {
    today = LocalDate.of(2025, 7, 23);

    UserDTO userDto = new UserDTO();
    userDto.setUuid(userUuid);
    userDto.setWeight(70.0f);

    ExerciseDTO exerciseDto = new ExerciseDTO();
    exerciseDto.setName("Running");
    exerciseDto.setMet(8.0f);

    savedDto =
        TrainingDTO.builder()
            .id(trainingId)
            .uuid(userUuid)
            .exercise("Running")
            .duration(30.0f)
            .caloriesLost(calcCalories(8f, 70f, 30f))
            .date(today)
            .build();

    lenient().when(timeProvider.getCurrentDate()).thenReturn(today);
    lenient().when(userRepo.existsById(userUuid)).thenReturn(true);
    lenient().when(userRepo.findById(userUuid)).thenReturn(Optional.of(userDto));
    lenient()
        .when(exerciseRepo.findByNameIgnoreCase("Running"))
        .thenReturn(Optional.of(exerciseDto));
  }

  private float calcCalories(float met, float weight, float duration) {
    return (met * weight * 3.5f * duration) / 200f;
  }

  @Test
  @DisplayName("submitTraining: success")
  void submitTraining_Success() {
    var req = new TrainingRecordRequest(userUuid, "Running", 30.0f, 0.0f, today);

    when(trainingRepo.save(any(TrainingDTO.class))).thenReturn(savedDto);
    when(converter.toResponse(savedDto))
        .thenReturn(
            new TrainingRecordResponse(
                trainingId, "Running", 30.0f, savedDto.getCaloriesLost(), today));

    var resp = service.submitTraining(req);

    assertEquals(trainingId, resp.id());
    assertEquals(savedDto.getCaloriesLost(), resp.caloriesLost());
    assertEquals(today, resp.date());
    verify(converter).toResponse(savedDto);
  }

  @Test
  @DisplayName("submitTraining: user not found")
  void submitTraining_UserNotFound() {
    when(userRepo.findById(userUuid)).thenReturn(Optional.empty());
    var req = new TrainingRecordRequest(userUuid, "Running", 30f, 0f, today);

    var ex = assertThrows(TrainEatRepeatException.class, () -> service.submitTraining(req));
    assertEquals("User not found with UUID: " + userUuid, ex.getMessage());
  }

  @Test
  @DisplayName("submitTraining: exercise not found")
  void submitTraining_ExerciseNotFound() {
    when(exerciseRepo.findByNameIgnoreCase("Running")).thenReturn(Optional.empty());
    var req = new TrainingRecordRequest(userUuid, "Running", 30f, 0f, today);

    var ex = assertThrows(TrainEatRepeatException.class, () -> service.submitTraining(req));
    assertEquals("Exercise not found: Running", ex.getMessage());
  }

  @Test
  @DisplayName("updateTrainingById: success")
  void updateTraining_Success() {
    var req = new TrainingRecordRequest(userUuid, "Jogging", 45f, 0f, today);
    var joggingDto = new ExerciseDTO();
    joggingDto.setName("Jogging");
    joggingDto.setMet(6f);
    float expected = calcCalories(6f, 70f, 45f);

    when(trainingRepo.findById(trainingId)).thenReturn(Optional.of(savedDto));
    when(exerciseRepo.findByNameIgnoreCase("Jogging")).thenReturn(Optional.of(joggingDto));
    when(trainingRepo.save(any(TrainingDTO.class)))
        .thenReturn(
            TrainingDTO.builder()
                .id(trainingId)
                .uuid(userUuid)
                .exercise("Jogging")
                .duration(45f)
                .caloriesLost(expected)
                .date(today)
                .build());
    when(converter.toResponse(any(TrainingDTO.class)))
        .thenAnswer(
            inv -> {
              var dto = inv.getArgument(0, TrainingDTO.class);
              return new TrainingRecordResponse(
                  dto.getId(),
                  dto.getExercise(),
                  dto.getDuration(),
                  dto.getCaloriesLost(),
                  dto.getDate());
            });

    var resp = service.updateTrainingById(trainingId, req);

    assertEquals("Jogging", resp.exercise());
    assertEquals(expected, resp.caloriesLost(), 1e-6);
  }

  @Test
  @DisplayName("updateTrainingById: training not found")
  void updateTraining_NotFound() {
    when(trainingRepo.findById(trainingId)).thenReturn(Optional.empty());
    var req = new TrainingRecordRequest(userUuid, "Running", 30f, 0f, today);

    var ex =
        assertThrows(
            TrainEatRepeatException.class, () -> service.updateTrainingById(trainingId, req));
    assertEquals("Training not found with ID: " + trainingId, ex.getMessage());
  }

  @Test
  @DisplayName("updateTrainingById: user not found")
  void updateTraining_UserNotFound() {
    when(trainingRepo.findById(trainingId)).thenReturn(Optional.of(savedDto));
    when(userRepo.findById(userUuid)).thenReturn(Optional.empty());
    var req = new TrainingRecordRequest(userUuid, "Running", 30f, 0f, today);

    var ex =
        assertThrows(
            TrainEatRepeatException.class, () -> service.updateTrainingById(trainingId, req));
    assertEquals("User not found with UUID: " + userUuid, ex.getMessage());
  }

  @Test
  @DisplayName("updateTrainingById: exercise not found")
  void updateTraining_ExerciseNotFound() {
    when(trainingRepo.findById(trainingId)).thenReturn(Optional.of(savedDto));
    when(exerciseRepo.findByNameIgnoreCase("Running")).thenReturn(Optional.empty());
    var req = new TrainingRecordRequest(userUuid, "Running", 30f, 0f, today);

    var ex =
        assertThrows(
            TrainEatRepeatException.class, () -> service.updateTrainingById(trainingId, req));
    assertEquals("Exercise not found: Running", ex.getMessage());
  }

  @Test
  @DisplayName("getTrainingsFromTimePeriod: user not found")
  void getTrainingsFromTimePeriod_UserNotFound() {
    when(userRepo.existsById(userUuid)).thenReturn(false);

    var ex =
        assertThrows(
            TrainEatRepeatException.class, () -> service.getTrainingsForUser(userUuid, null));
    assertEquals("User not found with UUID: " + userUuid, ex.getMessage());
  }

  @Test
  @DisplayName("deleteTrainingById: success")
  void deleteTraining_Success() {
    when(trainingRepo.existsById(trainingId)).thenReturn(true);
    assertDoesNotThrow(() -> service.deleteTrainingById(trainingId));
    verify(trainingRepo).deleteById(trainingId);
  }

  @Test
  @DisplayName("deleteTrainingById: not found")
  void deleteTraining_NotFound() {
    when(trainingRepo.existsById(trainingId)).thenReturn(false);
    var ex =
        assertThrows(TrainEatRepeatException.class, () -> service.deleteTrainingById(trainingId));
    assertEquals("Training not found with ID: " + trainingId, ex.getMessage());
  }

  @Test
  @DisplayName("getTrainingsForUser: all periods")
  void getTrainingsForUser_VariousPeriods() {
    List<TrainingDTO> list = List.of(savedDto);

    when(trainingRepo.findAllByUuid(userUuid)).thenReturn(list);

    when(trainingRepo.findAllByUuidAndDate(userUuid, today)).thenReturn(list);

    assertEquals(1, service.getTrainingsForUser(userUuid, null).size());
    assertEquals(1, service.getTrainingsForUser(userUuid, TimePeriod.DAY).size());

    LocalDate monday = today.with(java.time.DayOfWeek.MONDAY);
    LocalDate sunday = today.with(java.time.DayOfWeek.SUNDAY);
    when(trainingRepo.findTrainingsByUuidAndDateBetween(userUuid, monday, sunday)).thenReturn(list);
    assertEquals(1, service.getTrainingsForUser(userUuid, TimePeriod.WEEK).size());

    LocalDate first = today.withDayOfMonth(1);
    LocalDate last = today.withDayOfMonth(today.lengthOfMonth());
    when(trainingRepo.findTrainingsByUuidAndDateBetween(userUuid, first, last)).thenReturn(list);
    assertEquals(1, service.getTrainingsForUser(userUuid, TimePeriod.MONTH).size());
  }

  @Test
  @DisplayName("getTrainingStatistics: no trainings returns zeros")
  void getStatistics_NoTrainings() {
    when(trainingRepo.findAllByUuid(userUuid)).thenReturn(Collections.emptyList());
    UserTrainingStatisticsResponse stats = service.getTrainingStatistics(userUuid, null);
    assertEquals(0.0f, stats.avgPerDaySessions());
    assertEquals(0.0, stats.avgCaloriesBurnedPerSession());
  }

  @Test
  @DisplayName("getTrainingStatistics: week and month")
  void getStatistics_Periods() {
    TrainingDTO t1 = TrainingDTO.builder().caloriesLost(100f).build();
    TrainingDTO t2 = TrainingDTO.builder().caloriesLost(200f).build();
    List<TrainingDTO> list = List.of(t1, t2);

    when(timeProvider.getCurrentDate()).thenReturn(today);

    when(trainingRepo.findTrainingsByUuidAndDateBetween(eq(userUuid), any(), any()))
        .thenReturn(list);

    assertAll(
        "Statistics for week and month",

        // WEEK (from MONDAY to TODAY)
        () -> {
          LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
          int daysInWeek = (int) ChronoUnit.DAYS.between(startOfWeek, today) + 1;

          var weekStats = service.getTrainingStatistics(userUuid, TimePeriod.WEEK);
          assertEquals(
              150.0,
              weekStats.avgCaloriesBurnedPerSession(),
              1e-6,
              "WEEK: avgCaloriesBurnedPerSession");
          assertEquals(
              2.0f / daysInWeek, weekStats.avgPerDaySessions(), 1e-6, "WEEK: avgPerDaySessions");
        },

        // MONTH (from 1st day to TODAY)
        () -> {
          LocalDate startOfMonth = today.withDayOfMonth(1);
          int daysInMonth = (int) ChronoUnit.DAYS.between(startOfMonth, today) + 1;

          var monthStats = service.getTrainingStatistics(userUuid, TimePeriod.MONTH);
          assertEquals(
              150.0,
              monthStats.avgCaloriesBurnedPerSession(),
              1e-6,
              "MONTH: avgCaloriesBurnedPerSession");
          assertEquals(
              2.0f / daysInMonth, monthStats.avgPerDaySessions(), 1e-6, "MONTH: avgPerDaySessions");
        });
  }
}
