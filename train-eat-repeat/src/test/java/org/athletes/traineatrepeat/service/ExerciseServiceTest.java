package org.athletes.traineatrepeat.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.athletes.traineatrepeat.converter.ExerciseConverter;
import org.athletes.traineatrepeat.exceptions.TrainEatRepeatException;
import org.athletes.traineatrepeat.model.request.ExerciseRequest;
import org.athletes.traineatrepeat.model.response.ExerciseResponse;
import org.athletes.traineatrepeat.repository.ExerciseRepository;
import org.athletes.traineatrepeat.repository.dto.ExerciseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

  @Mock private ExerciseRepository exerciseRepository;

  @Mock private ExerciseConverter exerciseConverter;

  @InjectMocks private ExerciseService exerciseService;

  private ExerciseDTO sampleExerciseDTO;
  private ExerciseRequest sampleExerciseRequest;
  private ExerciseResponse sampleExerciseResponse;

  private ExerciseDTO anotherExerciseDTO;
  private ExerciseResponse anotherExerciseResponse;

  private static final String ID_1 = "1e2d3c4b-5a6f-7b8d-9c0e-123456789abc";
  private static final String ID_2 = "2e2d3c4b-5a6f-7b8d-9c0e-123456789abc";

  @BeforeEach
  void setUp() {
    sampleExerciseDTO = ExerciseDTO.builder().id(ID_1).name("Running").met(7.0f).build();

    anotherExerciseDTO = ExerciseDTO.builder().id(ID_2).name("Weightlifting").met(5.0f).build();

    sampleExerciseRequest = new ExerciseRequest("Running", 7.0f);
    sampleExerciseResponse = new ExerciseResponse(ID_1, "Running", 7.0f);
    anotherExerciseResponse = new ExerciseResponse(ID_2, "Weightlifting", 5.0f);
  }

  @Test
  @DisplayName("Should throw exception when exercise with same name already exists")
  void createExercise_NameConflict() {
    when(exerciseRepository.findByNameIgnoreCase(sampleExerciseRequest.name()))
        .thenReturn(Optional.of(sampleExerciseDTO));

    TrainEatRepeatException exception =
        assertThrows(
            TrainEatRepeatException.class,
            () -> exerciseService.createExercise(sampleExerciseRequest));

    assertEquals("Exercise with name 'Running' already exists", exception.getMessage());

    verify(exerciseRepository).findByNameIgnoreCase(sampleExerciseRequest.name());
    verifyNoMoreInteractions(exerciseRepository);
    verifyNoInteractions(exerciseConverter);
  }

  @Test
  @DisplayName("Should create an exercise successfully")
  void createExercise() {
    when(exerciseRepository.save(any(ExerciseDTO.class))).thenReturn(sampleExerciseDTO);
    when(exerciseConverter.toResponseFromEntity(sampleExerciseDTO))
        .thenReturn(sampleExerciseResponse);

    var exerciseResponse = exerciseService.createExercise(sampleExerciseRequest);

    assertNotNull(exerciseResponse);
    assertEquals(sampleExerciseResponse, exerciseResponse);

    verify(exerciseRepository).save(any(ExerciseDTO.class));
    verify(exerciseConverter).toResponseFromEntity(sampleExerciseDTO);
  }

  @Test
  @DisplayName("Should retrieve all exercises successfully")
  void getAllExercises() {
    List<ExerciseDTO> dtoList = Arrays.asList(sampleExerciseDTO, anotherExerciseDTO);
    List<ExerciseResponse> expectedResponseList =
        Arrays.asList(sampleExerciseResponse, anotherExerciseResponse);

    when(exerciseRepository.findAll()).thenReturn(dtoList);
    when(exerciseConverter.toResponseFromEntity(sampleExerciseDTO))
        .thenReturn(sampleExerciseResponse);
    when(exerciseConverter.toResponseFromEntity(anotherExerciseDTO))
        .thenReturn(anotherExerciseResponse);

    var result = exerciseService.getAllExercises();

    assertNotNull(result);
    assertEquals(expectedResponseList.size(), result.size());

    assertEquals(expectedResponseList, result);
    verify(exerciseConverter, times(dtoList.size())).toResponseFromEntity(any(ExerciseDTO.class));
  }

  @Test
  @DisplayName("Should throw exception when no exercises found")
  void getAllExercises_NoExercisesFound() {
    when(exerciseRepository.findAll()).thenReturn(List.of());

    TrainEatRepeatException exception =
        assertThrows(TrainEatRepeatException.class, () -> exerciseService.getAllExercises());

    assertEquals("No exercises found", exception.getMessage());

    verify(exerciseRepository).findAll();
    verifyNoMoreInteractions(exerciseRepository);
    verifyNoInteractions(exerciseConverter);
  }

  @Test
  @DisplayName("Should update an exercise successfully")
  void updateExercise() {
    String id = sampleExerciseDTO.getId();

    ExerciseRequest updateRequest = new ExerciseRequest("Cycling", 8.5f);

    ExerciseDTO updatedDTO = ExerciseDTO.builder().id(id).name("Cycling").met(8.5f).build();
    ExerciseResponse expectedResponse = new ExerciseResponse(id, "Cycling", 8.5f);

    when(exerciseRepository.findById(id)).thenReturn(Optional.of(sampleExerciseDTO));
    when(exerciseRepository.findByNameIgnoreCase(updateRequest.name()))
        .thenReturn(Optional.empty());
    when(exerciseRepository.save(any(ExerciseDTO.class))).thenReturn(updatedDTO);
    when(exerciseConverter.toResponseFromEntity(updatedDTO)).thenReturn(expectedResponse);

    ExerciseResponse result = exerciseService.updateExercise(id, updateRequest);

    assertNotNull(result);
    assertEquals(expectedResponse, result);

    verify(exerciseRepository).findById(id);
    verify(exerciseRepository).findByNameIgnoreCase(updateRequest.name());
    verify(exerciseRepository).save(any(ExerciseDTO.class));
    verify(exerciseConverter).toResponseFromEntity(updatedDTO);
  }

  @Test
  @DisplayName("Should throw exception when exercise not found for update")
  void updateExercise_NotFound() {
    String id = "non-existing-id";
    ExerciseRequest updateRequest = new ExerciseRequest("Swimming", 6.0f);

    when(exerciseRepository.findById(id)).thenReturn(Optional.empty());

    Exception exception =
        assertThrows(
            RuntimeException.class, () -> exerciseService.updateExercise(id, updateRequest));

    assertEquals("Exercise not found with ID: " + id, exception.getMessage());

    verify(exerciseRepository).findById(id);
    verifyNoMoreInteractions(exerciseRepository);
    verifyNoInteractions(exerciseConverter);
  }

  @Test
  @DisplayName("Should throw exception if exercise with the same name already exists")
  void updateExercise_NameConflict() {
    String id = sampleExerciseDTO.getId();
    ExerciseRequest updateRequest = new ExerciseRequest("Running", 6.0f);

    when(exerciseRepository.findById(id)).thenReturn(Optional.of(sampleExerciseDTO));
    when(exerciseRepository.findByNameIgnoreCase(updateRequest.name()))
        .thenReturn(Optional.of(anotherExerciseDTO));

    TrainEatRepeatException exception =
        assertThrows(
            TrainEatRepeatException.class, () -> exerciseService.updateExercise(id, updateRequest));

    assertEquals("Exercise with name 'Running' already exists", exception.getMessage());

    verify(exerciseRepository).findById(id);
    verify(exerciseRepository).findByNameIgnoreCase(updateRequest.name());
    verifyNoMoreInteractions(exerciseRepository);
    verifyNoInteractions(exerciseConverter);
  }

  @Test
  @DisplayName("Should delete an exercise successfully")
  void deleteExercise() {
    String id = sampleExerciseDTO.getId();

    when(exerciseRepository.existsById(id)).thenReturn(true);

    assertDoesNotThrow(() -> exerciseService.deleteExercise(id));

    verify(exerciseRepository).existsById(id);
    verify(exerciseRepository).deleteById(id);
    verifyNoMoreInteractions(exerciseRepository);
    verifyNoInteractions(exerciseConverter);
  }

  @Test
  @DisplayName("Should throw exception when deleting non-existing exercise")
  void deleteExercise_NotFound() {
    String id = "non-existing-id";

    when(exerciseRepository.existsById(id)).thenReturn(false);

    TrainEatRepeatException exception =
        assertThrows(TrainEatRepeatException.class, () -> exerciseService.deleteExercise(id));

    assertEquals("Exercise not found with ID: " + id, exception.getMessage());

    verify(exerciseRepository).existsById(id);
    verifyNoMoreInteractions(exerciseRepository);
    verifyNoInteractions(exerciseConverter);
  }

  @Test
  @DisplayName("Should throw exception when deleting exercise with invalid ID")
  void deleteExercise_InvalidId() {
    String invalidId = "invalid-id";

    when(exerciseRepository.existsById(invalidId)).thenReturn(false);

    TrainEatRepeatException exception =
        assertThrows(
            TrainEatRepeatException.class, () -> exerciseService.deleteExercise(invalidId));

    assertEquals("Exercise not found with ID: " + invalidId, exception.getMessage());

    verify(exerciseRepository).existsById(invalidId);
    verifyNoMoreInteractions(exerciseRepository);
    verifyNoInteractions(exerciseConverter);
  }
}
