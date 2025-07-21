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

  public UserNutritionStatisticsResponse getNutritionStatistics(String uuid, TimePeriod period) {
    var meals = getMealsFromTimePeriod(uuid, period);

    /**
     * COMMENT: this approach is not memory efficient. Here you are processing List of Meals 4 times
     * through stream, additionally you are creating duplicate functionality by fallback to a 0 each
     * time. Try to use for cycle instead
     */

    // TODO: consider using a single pass to calculate all averages
    double avgProtein = meals.stream().mapToDouble(MealDTO::getProtein).average().orElse(0);
    double avgFat = meals.stream().mapToDouble(MealDTO::getFat).average().orElse(0);
    double avgCarbs = meals.stream().mapToDouble(MealDTO::getCarbs).average().orElse(0);
    double avgCalories = meals.stream().mapToDouble(MealDTO::getCalories).average().orElse(0);

    return UserNutritionStatisticsResponse.builder()
        .avgProtein(avgProtein)
        .avgFat(avgFat)
        .avgCarbs(avgCarbs)
        .avgCalories(avgCalories)
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
