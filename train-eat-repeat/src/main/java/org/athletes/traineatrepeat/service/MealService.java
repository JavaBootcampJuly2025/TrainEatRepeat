package org.athletes.traineatrepeat.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.api.UsdaClient;
import org.athletes.traineatrepeat.converter.MealRecordConverter;
import org.athletes.traineatrepeat.model.Nutrients;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.request.MealRecordRequest;
import org.athletes.traineatrepeat.model.response.MealRecordResponse;
import org.athletes.traineatrepeat.model.response.UserNutritionStatisticsResponse;
import org.athletes.traineatrepeat.repository.MealRecordRepository;
import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class MealService {

  private final MealRecordRepository mealRecordRepository;
  private final MealRecordConverter mealRecordConverter;
  private final UsdaClient usdaClient;

  /**
   * COMMENT: you can actually introduce caching mechanism here to avoid calling third-party API for
   * the same food multiple times. I would recommend using Caffeine Cache <a
   * href="https://www.baeldung.com/spring-boot-caffeine-cache">https://www.baeldung.com/spring-boot-caffeine-cache</a>
   */

  /**
   * Calculates the nutritional values (calories, carbs, protein, fat) for a given food and weight.
   * <p>
   * This method queries the USDA API to retrieve the nutritional information for the specified
   * food name. The retrieved nutrient values are scaled based on the provided weight in grams.
   * Only the first 4 relevant nutrients (calories, carbs, protein, fat) are considered.
   * <p>
   * If the food is not found in the USDA API response, the method currently returns an empty map.
   * (TODO: Consider throwing an exception for better error handling in this case.)
   *
   * @param foodName       The name of the food to search in the USDA database.
   * @param weightInGrams  The weight of the food in grams. The values are scaled based on this weight.
   *                       If set to 0, the method assumes 100g as the default.
   * @return A map containing the calculated nutrient values, where keys are {@link Nutrients} and values are scaled floats.
   */
  private Map<Nutrients, Float> calculateNutritionalValues(String foodName, float weightInGrams) {
    var usdaResponse = usdaClient.searchFood(foodName);
    float factor = calculateFactorByWeight(weightInGrams);
    Map<Nutrients, Float> nutritionMap = new HashMap<>();

    if (!CollectionUtils.isEmpty(usdaResponse.foods())) {
      var food = usdaResponse.foods().getFirst();
      for (var usdaFoodNutrient : food.foodNutrients()) {

        if (nutritionMap.size() == 4) {
          break; // Limit to 4 nutrients (calories, carbs, protein, fat)
        }

        var nutrient = Nutrients.fromUsdaName(usdaFoodNutrient.nutrientName());

        if (nutrient == null) {
          continue; // Skip nutrients that are not recognized
        }

        nutritionMap.put(nutrient, usdaFoodNutrient.value() * factor);
      }
    } // TODO: throw exception here is the foods is empty

    return nutritionMap;
  }

  private float calculateFactorByWeight(float weightInGrams) {
    return weightInGrams == 0 ? 100.0f : weightInGrams / 100.0f;
  }

  public MealRecordResponse submitMeal(String uuid, MealRecordRequest request) {
    Map<Nutrients, Float> nutrition =
        calculateNutritionalValues(request.foodName(), request.weightInGrams());

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
            .date(request.date() != null ? request.date() : LocalDate.now())
            .build();

    var savedMeal = mealRecordRepository.save(mealToSave);
    return mealRecordConverter.toResponse(savedMeal);
  }

  /**
   * Retrieves meals for the users depending on the time period (day, week, month)
   *
   * @param uuid user's id
   * @param timePeriod time period
   * @return lists of meals per time period
   */
  public List<MealRecordResponse> getMealsForUser(String uuid, TimePeriod timePeriod) {
    var meals = getMealsFromTimePeriod(uuid, timePeriod);
    return meals.stream().map(mealRecordConverter::toResponse).toList();
  }

  /**
   * Calculates the average nutritional intake for a user over a specified time period.
   *
   * <p>This method retrieves all meal records for a given user UUID within the specified time period
   * and computes the average values for protein, fat, carbohydrates, calories, and meal weight in grams.
   *
   * <p>If no meals are found for the given user and time period, all averages are returned as zero.
   *
   * @param uuid   The unique identifier of the user whose nutrition statistics are to be calculated.
   * @param period The time period over which the nutritional statistics should be aggregated.
   * @return A {@link UserNutritionStatisticsResponse} containing the average protein, fat, carbs,
   *         calories, and meal weight in grams for the user within the given time period.
   */
  public UserNutritionStatisticsResponse getNutritionStatistics(String uuid, TimePeriod period) {
    var meals = getMealsFromTimePeriod(uuid, period);

    double totalProtein = 0;
    double totalFat = 0;
    double totalCarbs = 0;
    double totalCalories = 0;
    double totalWeightInGrams = 0;
    int count = meals.size();

    for (MealDTO meal : meals) {
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
    mealRecordRepository.deleteById(mealId);
  }

  public MealRecordResponse updateMeal(String mealId, MealRecordRequest request) {
    var existingMeal =
        mealRecordRepository
            .findById(mealId)
            .orElseThrow(() -> new RuntimeException("Meal not found with ID: " + mealId));

    existingMeal.setFoodName(request.foodName());
    existingMeal.setDate(request.date() != null ? request.date() : LocalDate.now());

    Map<Nutrients, Float> nutrition =
        calculateNutritionalValues(request.foodName(), request.weightInGrams());

    existingMeal.setCalories(nutrition.get(Nutrients.CALORIES));
    existingMeal.setCarbs(nutrition.get(Nutrients.CARBS));
    existingMeal.setProtein(nutrition.get(Nutrients.PROTEIN));
    existingMeal.setFat(nutrition.get(Nutrients.FAT));

    var savedMeal = mealRecordRepository.save(existingMeal);
    return mealRecordConverter.toResponse(savedMeal);
  }

  private List<MealDTO> getMealsForToday(String uuid) {
    LocalDate today = LocalDate.now();
    return mealRecordRepository.findAllByUuidAndDate(uuid, today);
  }

  private List<MealDTO> getMealsForWeek(String uuid) {
    LocalDate today = LocalDate.now();
    LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
    LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);
    return mealRecordRepository.findMealsByUuidAndDateBetween(uuid, startOfWeek, endOfWeek);
  }

  private List<MealDTO> getMealsForMonth(String uuid) {
    LocalDate today = LocalDate.now();
    LocalDate startOfMonth = today.withDayOfMonth(1);
    LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
    return mealRecordRepository.findMealsByUuidAndDateBetween(uuid, startOfMonth, endOfMonth);
  }
}
