package org.athletes.traineatrepeat.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.response.UserNutritionStatisticsResponse;
import org.athletes.traineatrepeat.model.response.UserResponse;
import org.athletes.traineatrepeat.model.response.UserTrainingStatisticsResponse;
import org.athletes.traineatrepeat.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

  private final UserService userService;

  @GetMapping("/user-data")
  public UserResponse getUserData(@RequestParam String uuid) {
    return userService.getUser(uuid);
  }

  @GetMapping("/all")
  public List<UserResponse> getAllUsers() {
    return userService.getAllUsers();
  }

  /**
   * COMMENT: This controller method is not needed. If you need to implement endpoint for testing
   * the connection, you can consider implementing Spring Boot Health Indicator or Actuator
   * endpoint. <a
   * href="https://www.baeldung.com/spring-boot-health-indicators">https://www.baeldung.com/spring-boot-health-indicators</a>
   */
  @GetMapping("/test")
  public String test() {
    return "UserController is working";
  }

  @GetMapping("/meal-statistics")
  public UserNutritionStatisticsResponse getMealStatistics(
      @RequestParam String uuid, @RequestParam(required = false) TimePeriod period) {
    return userService.getNutritionStatistics(uuid, period);
  }

  @GetMapping("/training-statistics")
  public UserTrainingStatisticsResponse getTrainingStatistics(
      @RequestParam String uuid, @RequestParam(required = false) TimePeriod period) {
    return userService.getTrainingStatistics(uuid, period);
  }
}
