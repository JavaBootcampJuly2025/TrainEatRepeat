package org.athletes.traineatrepeat.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.UserConverter;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.response.UserNutritionStatisticsResponse;
import org.athletes.traineatrepeat.model.response.UserResponse;
import org.athletes.traineatrepeat.model.response.UserTrainingStatisticsResponse;
import org.athletes.traineatrepeat.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository useRepository;
  private final UserConverter userConverter;
  private final MealService mealService;
  private final TrainingService trainingService;

  public UserResponse getUser(String uuid) {
    var user = useRepository.getUserByUuid(uuid);
    return userConverter.convertToUserDTO(user);
  }

  public List<UserResponse> getAllUsers() {
    var users = useRepository.findAll();
    return users.stream().map(userConverter::convertToUserDTO).toList();
  }

  public UserNutritionStatisticsResponse getNutritionStatistics(String uuid, TimePeriod period) {
    return mealService.getNutritionStatistics(uuid, period);
  }

  public UserTrainingStatisticsResponse getTrainingStatistics(String uuid, TimePeriod period) {
    return trainingService.getTrainingStatistics(uuid, period);
  }
}
