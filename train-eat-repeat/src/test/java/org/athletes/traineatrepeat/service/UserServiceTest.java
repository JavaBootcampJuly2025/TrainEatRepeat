package org.athletes.traineatrepeat.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import org.athletes.traineatrepeat.converter.UserConverter;
import org.athletes.traineatrepeat.exceptions.TrainEatRepeatException;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.response.UserNutritionStatisticsResponse;
import org.athletes.traineatrepeat.model.response.UserResponse;
import org.athletes.traineatrepeat.model.response.UserTrainingStatisticsResponse;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.athletes.traineatrepeat.repository.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private UserConverter userConverter;
  @Mock private MealService mealService;
  @Mock private TrainingService trainingService;

  @InjectMocks private UserService userService;

  private final String userUuid = "test-uuid";
  private final String userEmail = "test@example.com";
  private UserDTO mockUserDTO;
  private UserResponse mockUserResponse;

  @BeforeEach
  void setUp() {
    mockUserDTO = new UserDTO();
    mockUserDTO.setUuid(userUuid);
    mockUserDTO.setEmail(userEmail);
    mockUserDTO.setPassword("encodedPassword");
    mockUserDTO.setRole("USER");

    mockUserResponse =
        new UserResponse(
            userUuid,
            "testuser",
            30,
            "male",
            Collections.emptyList(),
            Collections.emptyList(),
            70.0f,
            175.0f,
            22.86f,
            1735.0f,
            "USER");
  }

  @Test
  @DisplayName("Should return user when a valid UUID is provided")
  void getUser_ValidUuid_ReturnsUserResponse() {
    when(userRepository.getUserByUuid(userUuid)).thenReturn(mockUserDTO);
    when(userRepository.existsById(userUuid)).thenReturn(true);
    when(userConverter.convertToUserDTO(mockUserDTO)).thenReturn(mockUserResponse);

    UserResponse result = userService.getUser(userUuid);

    assertNotNull(result);
    assertEquals(userUuid, result.uuid());
    verify(userRepository).getUserByUuid(userUuid);
    verify(userRepository).existsById(userUuid);
    verify(userConverter).convertToUserDTO(mockUserDTO);
  }

  @Test
  @DisplayName("Should throw exception when an invalid UUID is provided to getUser")
  void getUser_InvalidUuid_ThrowsTrainEatRepeatException() {
    when(userRepository.getUserByUuid(userUuid)).thenReturn(null);
    when(userRepository.existsById(userUuid)).thenReturn(false);

    TrainEatRepeatException exception =
        assertThrows(
            TrainEatRepeatException.class,
            () -> userService.getUser(userUuid),
            "Expected TrainEatRepeatException for invalid UUID");

    assertEquals("User not found with UUID: " + userUuid, exception.getMessage());
    verify(userRepository).getUserByUuid(userUuid);
    verify(userRepository).existsById(userUuid);
    verifyNoInteractions(userConverter);
  }

  @Test
  @DisplayName("Should return a list of all users")
  void getAllUsers_UsersExist_ReturnsListOfUsers() {
    List<UserDTO> userList = List.of(mockUserDTO);
    List<UserResponse> userResponseList = List.of(mockUserResponse);
    when(userRepository.findAll()).thenReturn(userList);
    when(userConverter.convertToUserDTO(mockUserDTO)).thenReturn(mockUserResponse);

    List<UserResponse> result = userService.getAllUsers();

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    assertEquals(userResponseList, result);
    verify(userRepository).findAll();
    verify(userConverter).convertToUserDTO(mockUserDTO);
  }

  @Test
  @DisplayName("Should throw exception when no users are found")
  void getAllUsers_NoUsers_ThrowsTrainEatRepeatException() {
    when(userRepository.findAll()).thenReturn(Collections.emptyList());

    TrainEatRepeatException exception =
        assertThrows(
            TrainEatRepeatException.class,
            () -> userService.getAllUsers(),
            "Expected TrainEatRepeatException when no users found");

    assertEquals("No users found", exception.getMessage());
    verify(userRepository).findAll();
    verifyNoInteractions(userConverter);
  }

  @Test
  @DisplayName("Should return nutrition statistics for a valid user and time period")
  void getNutritionStatistics_ValidUser_ReturnsStatistics() {
    TimePeriod period = TimePeriod.WEEK;
    UserNutritionStatisticsResponse mockResponse =
        new UserNutritionStatisticsResponse(0, 0, 0, 0, 0);
    when(userRepository.existsById(userUuid)).thenReturn(true);
    when(mealService.getNutritionStatistics(userUuid, period)).thenReturn(mockResponse);

    UserNutritionStatisticsResponse result = userService.getNutritionStatistics(userUuid, period);

    assertNotNull(result);
    assertEquals(mockResponse, result);
    verify(userRepository).existsById(userUuid);
    verify(mealService).getNutritionStatistics(userUuid, period);
  }

  @Test
  @DisplayName("Should throw exception when requesting nutrition stats for an invalid user")
  void getNutritionStatistics_InvalidUser_ThrowsTrainEatRepeatException() {
    TimePeriod period = TimePeriod.WEEK;
    when(userRepository.existsById(userUuid)).thenReturn(false);

    TrainEatRepeatException exception =
        assertThrows(
            TrainEatRepeatException.class,
            () -> userService.getNutritionStatistics(userUuid, period),
            "Expected TrainEatRepeatException for invalid user UUID");

    assertEquals("User not found with UUID: " + userUuid, exception.getMessage());
    verify(userRepository).existsById(userUuid);
    verifyNoInteractions(mealService);
  }

  @Test
  @DisplayName("Should return training statistics for a valid user and time period")
  void getTrainingStatistics_ValidUser_ReturnsStatistics() {
    TimePeriod period = TimePeriod.MONTH;
    UserTrainingStatisticsResponse mockResponse = new UserTrainingStatisticsResponse(0, 0);
    when(userRepository.existsById(userUuid)).thenReturn(true);
    when(trainingService.getTrainingStatistics(userUuid, period)).thenReturn(mockResponse);

    UserTrainingStatisticsResponse result = userService.getTrainingStatistics(userUuid, period);

    assertNotNull(result);
    assertEquals(mockResponse, result);
    verify(userRepository).existsById(userUuid);
    verify(trainingService).getTrainingStatistics(userUuid, period);
  }

  @Test
  @DisplayName("Should throw exception when requesting training stats for an invalid user")
  void getTrainingStatistics_InvalidUser_ThrowsTrainEatRepeatException() {
    TimePeriod period = TimePeriod.MONTH;
    when(userRepository.existsById(userUuid)).thenReturn(false);

    TrainEatRepeatException exception =
        assertThrows(
            TrainEatRepeatException.class,
            () -> userService.getTrainingStatistics(userUuid, period),
            "Expected TrainEatRepeatException for invalid user UUID");

    assertEquals("User not found with UUID: " + userUuid, exception.getMessage());
    verify(userRepository).existsById(userUuid);
    verifyNoInteractions(trainingService);
  }

  @Test
  @DisplayName("Should load user by username (email) successfully")
  void loadUserByUsername_ValidEmail_ReturnsUserDetails() {
    when(userRepository.findByEmail(userEmail)).thenReturn(mockUserDTO);

    UserDetails userDetails = userService.loadUserByUsername(userEmail);

    assertNotNull(userDetails);
    assertEquals(mockUserDTO.getEmail(), userDetails.getUsername());
    assertEquals(mockUserDTO.getPassword(), userDetails.getPassword());
    assertTrue(
        userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_" + mockUserDTO.getRole())));
    verify(userRepository).findByEmail(userEmail);
  }

  @Test
  @DisplayName("Should return null for a non-existent email")
  void loadUserByUsername_InvalidEmail_ReturnsNull() {
    when(userRepository.findByEmail(userEmail)).thenReturn(null);

    UserDetails userDetails = userService.loadUserByUsername(userEmail);
    assertNull(userDetails);

    verify(userRepository).findByEmail(userEmail);
  }
}
