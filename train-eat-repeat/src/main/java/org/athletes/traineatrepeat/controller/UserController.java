package org.athletes.traineatrepeat.controller;

import static org.athletes.traineatrepeat.common.ValidationCommon.UUID_REGEX;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.response.UserNutritionStatisticsResponse;
import org.athletes.traineatrepeat.model.response.UserResponse;
import org.athletes.traineatrepeat.model.response.UserTrainingStatisticsResponse;
import org.athletes.traineatrepeat.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

  private final UserService userService;

  @GetMapping("/user-data")
  public UserResponse getUserData(
      @RequestParam @NotBlank @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format")
          String uuid) {
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
      @RequestParam @NotBlank @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format")
          String uuid,
      @RequestParam(required = false) TimePeriod period) {
    return userService.getNutritionStatistics(uuid, period);
  }

  @GetMapping("/training-statistics")
  public UserTrainingStatisticsResponse getTrainingStatistics(
      @RequestParam @NotBlank @Pattern(regexp = UUID_REGEX, message = "Invalid UUID format")
          String uuid,
      @RequestParam(required = false) TimePeriod period) {
    return userService.getTrainingStatistics(uuid, period);
  }
}
