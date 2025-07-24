package org.athletes.traineatrepeat.service;

import java.time.LocalDate;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.api.UsdaClient;
import org.athletes.traineatrepeat.converter.MealRecordConverter;
import org.athletes.traineatrepeat.exceptions.TrainEatRepeatException;
import org.athletes.traineatrepeat.model.Nutrients;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.request.MealRecordRequest;
import org.athletes.traineatrepeat.model.response.MealRecordResponse;
import org.athletes.traineatrepeat.model.response.UserNutritionStatisticsResponse;
import org.athletes.traineatrepeat.repository.MealRecordRepository;
import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.athletes.traineatrepeat.util.TimeProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class MealService {

  private final MealRecordRepository mealRecordRepository;
  private final MealRecordConverter mealRecordConverter;
  private final UsdaClient usdaClient;
  private final TimeProvider timeProvider;

  private Map<Nutrients, Float> calculateNutritionalValues(String foodName, float weightInGrams) {
    var usdaResponse = usdaClient.searchFood(foodName);
    if (CollectionUtils.isEmpty(usdaResponse.foods())) {
      throw new TrainEatRepeatException("Food not found: " + foodName);
    }

    float factor = calculateFactorByWeight(weightInGrams);
    var nutritionMap = new HashMap<Nutrients, Float>();

    if (!CollectionUtils.isEmpty(usdaResponse.foods())) {
      var food = usdaResponse.foods().getFirst();
      for (var usdaFoodNutrient : food.foodNutrients()) {

        if (nutritionMap.size() == 4) {
          break;
        }

        var nutrient = Nutrients.fromUsdaName(usdaFoodNutrient.nutrientName());
        if (nutrient != null) {
          nutritionMap.put(nutrient, usdaFoodNutrient.value() * factor);
        }
      }
    }

    if (nutritionMap.isEmpty()) {
      throw new TrainEatRepeatException("No nutritional information found for: " + foodName);
    }

    return nutritionMap;
  }

  private float calculateFactorByWeight(float weightInGrams) {
    return weightInGrams == 0 ? 100.0f : weightInGrams / 100.0f;
  }

  public MealRecordResponse submitMeal(String uuid, MealRecordRequest request) {
    if (request.foodName() == null || request.foodName().isBlank()) {
      throw new TrainEatRepeatException("Food name cannot be null or empty");
    }
    LocalDate today = timeProvider.getCurrentDate();
    var nutrition = calculateNutritionalValues(request.foodName(), request.weightInGrams());
    var mealToSave =
        MealDTO.builder()
            .id(UUID.randomUUID().toString())
            .uuid(uuid)
            .foodName(request.foodName())
            .calories(nutrition.get(Nutrients.CALORIES))
            .carbs(nutrition.get(Nutrients.CARBS))
            .protein(nutrition.get(Nutrients.PROTEIN))
            .fat(nutrition.get(Nutrients.FAT))
            .weightInGrams(request.weightInGrams())
            .date(request.date() != null ? request.date() : today)
            .build();

    var savedMeal = mealRecordRepository.save(mealToSave);
    return mealRecordConverter.toResponse(savedMeal);
  }

  public List<MealRecordResponse> getMealsForUser(String uuid, TimePeriod timePeriod) {
    var meals = getMealsFromTimePeriod(uuid, timePeriod);
    if (meals.isEmpty()) {
      throw new TrainEatRepeatException("No meals found for user: " + uuid);
    }
    return meals.stream().map(mealRecordConverter::toResponse).toList();
  }

  public UserNutritionStatisticsResponse getNutritionStatistics(String uuid, TimePeriod period) {

    var meals = getMealsFromTimePeriod(uuid, period);

    double totalProtein = 0;
    double totalFat = 0;
    double totalCarbs = 0;
    double totalCalories = 0;
    double totalWeightInGrams = 0;
    int count = meals.size();

    for (var meal : meals) {
      totalProtein += meal.getProtein();
      totalFat += meal.getFat();
      totalCarbs += meal.getCarbs();
      totalCalories += meal.getCalories();
      totalWeightInGrams += meal.getWeightInGrams();
    }

    if (count == 0) {
      return UserNutritionStatisticsResponse.builder()
          .avgProtein(0)
          .avgFat(0)
          .avgCarbs(0)
          .avgCalories(0)
          .avgWeightInGrams(0)
          .build();
    }

    double avgProtein = totalProtein / count;
    double avgFat = totalFat / count;
    double avgCarbs = totalCarbs / count;
    double avgCalories = totalCalories / count;
    double avgWeightInGrams = totalWeightInGrams / count;

    return UserNutritionStatisticsResponse.builder()
        .avgProtein(avgProtein)
        .avgFat(avgFat)
        .avgCarbs(avgCarbs)
        .avgCalories(avgCalories)
        .avgWeightInGrams(avgWeightInGrams)
        .build();
  }

  private List<MealDTO> getMealsFromTimePeriod(String uuid, TimePeriod timePeriod) {
    if (timePeriod == null) {
      return mealRecordRepository.findAllByUuid(uuid);
    }
    return switch (timePeriod) {
      case DAY -> getMealsForToday(uuid);
      case WEEK -> getMealsForWeek(uuid);
      case MONTH -> getMealsForMonth(uuid);
    };
  }

  public void deleteMealById(String mealId) {
    if (!mealRecordRepository.existsById(mealId)) {
      throw new TrainEatRepeatException("Meal not found with ID: " + mealId);
    }
    mealRecordRepository.deleteById(mealId);
  }

  public MealRecordResponse updateMeal(String mealId, MealRecordRequest request) {
    LocalDate today = timeProvider.getCurrentDate();
    var existingMeal =
        mealRecordRepository
            .findById(mealId)
            .orElseThrow(() -> new TrainEatRepeatException("Meal not found with ID: " + mealId));

    existingMeal.setFoodName(request.foodName());
    existingMeal.setDate(request.date() != null ? request.date() : today);

    var nutrition = calculateNutritionalValues(request.foodName(), request.weightInGrams());

    existingMeal.setCalories(nutrition.get(Nutrients.CALORIES));
    existingMeal.setCarbs(nutrition.get(Nutrients.CARBS));
    existingMeal.setProtein(nutrition.get(Nutrients.PROTEIN));
    existingMeal.setFat(nutrition.get(Nutrients.FAT));

    var savedMeal = mealRecordRepository.save(existingMeal);
    return mealRecordConverter.toResponse(savedMeal);
  }

  private List<MealDTO> getMealsForToday(String uuid) {
    LocalDate today = timeProvider.getCurrentDate();
    return mealRecordRepository.findAllByUuidAndDate(uuid, today);
  }

  private List<MealDTO> getMealsForWeek(String uuid) {
    LocalDate today = timeProvider.getCurrentDate();
    var startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
    var endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);
    return mealRecordRepository.findMealsByUuidAndDateBetween(uuid, startOfWeek, endOfWeek);
  }

  private List<MealDTO> getMealsForMonth(String uuid) {
    LocalDate today = timeProvider.getCurrentDate();
    var startOfMonth = today.withDayOfMonth(1);
    var endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
    return mealRecordRepository.findMealsByUuidAndDateBetween(uuid, startOfMonth, endOfMonth);
  }

  public Map<String, Double> getWeeklyCalorieChartData(String uuid) {
    LocalDate today = timeProvider.getCurrentDate();
    Map<String, Double> weeklyData = new LinkedHashMap<>();

    for (int i = 6; i >= 0; i--) {
      LocalDate date = today.minusDays(i);
      List<MealDTO> dailyMeals = mealRecordRepository.findAllByUuidAndDate(uuid, date);

      double dailyCalories = dailyMeals.stream().mapToDouble(MealDTO::getCalories).sum();

      String dayName = date.getDayOfWeek().name().substring(0, 3);
      weeklyData.put(dayName, dailyCalories);
    }

    return weeklyData;
  }
}
