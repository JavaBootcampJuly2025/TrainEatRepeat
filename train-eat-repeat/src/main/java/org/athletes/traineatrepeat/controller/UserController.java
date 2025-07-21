package org.athletes.traineatrepeat.controller;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.response.UserNutritionStatisticsResponse;
import org.athletes.traineatrepeat.model.response.UserResponse;
import org.athletes.traineatrepeat.model.response.UserTrainingStatisticsResponse;
import org.athletes.traineatrepeat.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     * COMMENT: I would say, that it is more feasible to use common Authentication header with Bearer authentication scheme, rather than introduce custom JWT token header.
     * <a href="https://stackoverflow.com/questions/33265812/best-http-authorization-header-type-for-jwt">https://stackoverflow.com/questions/33265812/best-http-authorization-header-type-for-jwt</a>
     */
    @GetMapping("/user-data")
    public UserResponse getUserData(@RequestParam String uuid, @RequestHeader (value = "jwtToken", required = false) String jwtToken) {
        return userService.getUser(uuid, jwtToken);
    }
    @GetMapping("/all")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * COMMENT: This controller method is not needed. If you need to implement endpoint for testing the connection,
     * you can consider implementing Spring Boot Health Indicator or Actuator endpoint. <a href="https://www.baeldung.com/spring-boot-health-indicators">https://www.baeldung.com/spring-boot-health-indicators</a>
     */
    @GetMapping("/test")
    public String test() {
        return "UserController is working";
    }

    @GetMapping("/meal-statistics")
    public UserNutritionStatisticsResponse getMealStatistics(
            @RequestParam String uuid,
            @RequestParam (required = false) TimePeriod period) {
        return userService.getNutritionStatistics(uuid, period);
    }

    @GetMapping("/training-statistics")
    public UserTrainingStatisticsResponse getTrainingStatistics(
            @RequestParam String uuid,
            @RequestParam (required = false) TimePeriod period) {
        return userService.getTrainingStatistics(uuid, period);
    }
}