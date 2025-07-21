package org.athletes.traineatrepeat.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.UserConverter;
import org.athletes.traineatrepeat.exceptions.TrainEatRepeatException;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.response.UserNutritionStatisticsResponse;
import org.athletes.traineatrepeat.model.response.UserResponse;
import org.athletes.traineatrepeat.model.response.UserTrainingStatisticsResponse;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository useRepository;
  private final UserConverter userConverter;
  private final MealService mealService;
  private final TrainingService trainingService;

  public UserResponse getUser(String uuid) {
    if (uuid == null || uuid.isBlank()) {
      throw new TrainEatRepeatException("User UUID cannot be null or empty");
    }
    var user = useRepository.getUserByUuid(uuid);
    if (user == null) {
      throw new TrainEatRepeatException("User not found with UUID: " + uuid);
    }
    return userConverter.convertToUserDTO(user);
  }

  public List<UserResponse> getAllUsers() {
    var users = useRepository.findAll();
    if (users.isEmpty()) {
      throw new TrainEatRepeatException("No users found");
    }
    return users.stream().map(userConverter::convertToUserDTO).toList();
  }

  public UserNutritionStatisticsResponse getNutritionStatistics(String uuid, TimePeriod period) {
    if (uuid == null || uuid.isBlank()) {
      throw new TrainEatRepeatException("User UUID cannot be null or empty");
    }

    if (!useRepository.existsById(uuid)) {
      throw new TrainEatRepeatException("User not found with UUID: " + uuid);
    }
    return mealService.getNutritionStatistics(uuid, period);
  }

  public UserTrainingStatisticsResponse getTrainingStatistics(String uuid, TimePeriod period) {
    if (uuid == null || uuid.isBlank()) {
      throw new TrainEatRepeatException("User UUID cannot be null or empty");
    }

    if (!useRepository.existsById(uuid)) {
      throw new TrainEatRepeatException("User not found with UUID: " + uuid);
    }
    return trainingService.getTrainingStatistics(uuid, period);
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var user = useRepository.findByEmail(email);

    if (user != null) {
      return User.withUsername(user.getEmail())
          .password(user.getPassword())
          .roles(user.getRole())
          .build();
    }
    return null;
  }
}
