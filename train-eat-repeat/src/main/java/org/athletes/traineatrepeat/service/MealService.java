package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.MealRecordConverter;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.request.MealRecordRequest;
import org.athletes.traineatrepeat.model.response.MealRecordResponse;
import org.athletes.traineatrepeat.model.response.UserNutritionStatisticsResponse;
import org.athletes.traineatrepeat.api.UsdaClient;
import org.athletes.traineatrepeat.repository.MealRecordRepository;
import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRecordRepository mealRecordRepository;
    private final MealRecordConverter mealRecordConverter;
    private final UsdaClient usdaClient;

    /**
     * COMMENT: you can actually introduce caching mechanism here to avoid calling third-party API for the same food multiple times.
     * I would recommend using Caffeine Cache <a href="https://www.baeldung.com/spring-boot-caffeine-cache">https://www.baeldung.com/spring-boot-caffeine-cache</a>
     */
    private Map<String, Float> calculateNutritionalValues(String foodName, float weightInGrams) {
        var usdaResponse = usdaClient.searchFood(foodName);

        float calories = 0.0f;
        float carbs = 0.0f;
        float protein = 0.0f;
        float fat = 0.0f;

        /**
         * COMMENT: NIT. in Spring Boot there is a method CollectionUtils.isEmpty(...) which allow to check if collection is null or empty. Also consider adding either
         * Exception or error log if the food is not found in the USDA database.
         */
        if (usdaResponse != null && usdaResponse.getFoods() != null && !usdaResponse.getFoods().isEmpty()) {
            /**
             * COMMENT: you can use .getFirst() method for the collection in Java 21
             */
            var food = usdaResponse.getFoods().get(0);
            for (var nutrient : food.getFoodNutrients()) {
                /**
                 * COMMENT: These switch-case values can be replaced with functional processing through ENUM. You can introduce these Strings as ENUM values
                 * for CALORIES, PROTEIN, FAT, CARBS and use them during for cycle
                 */
                switch (nutrient.getNutrientName()) {
                    case "Energy" -> calories = nutrient.getValue();
                    case "Protein" -> protein = nutrient.getValue();
                    case "Total lipid (fat)" -> fat = nutrient.getValue();
                    case "Carbohydrate, by difference" -> carbs = nutrient.getValue();
                }
            }
        }

        /**
         * COMMENT: This should be extracted into the separate method, for example {@code calculateFactorByWeight}
         */
        float actualWeight = weightInGrams == 0 ? 100.0f : weightInGrams;
        float factor = actualWeight / 100.0f;

        /**
         * COMMENT: NIT. You should avoid using primitives wrapper classes as map parameters, because with these types, unexpected values can be put into the map.
         * Map Keys should be either enum or model class.
         */
        Map<String, Float> nutrition = new HashMap<>();
        nutrition.put("calories", calories * factor);
        nutrition.put("carbs", carbs * factor);
        nutrition.put("protein", protein * factor);
        nutrition.put("fat", fat * factor);

        return nutrition;
    }

    public MealRecordResponse submitMeal(String uuid, MealRecordRequest request) {
        Map<String, Float> nutrition = calculateNutritionalValues(request.foodName(), request.weightInGrams());

        var mealToSave = MealDTO.builder()
                .id(UUID.randomUUID().toString())
                .uuid(uuid)
                .foodName(request.foodName())
                .calories(nutrition.get("calories"))
                .carbs(nutrition.get("carbs"))
                .protein(nutrition.get("protein"))
                .fat(nutrition.get("fat"))
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
        return meals.stream()
                .map(mealRecordConverter::toResponse)
                .toList();
    }

    public UserNutritionStatisticsResponse getNutritionStatistics(String uuid, TimePeriod period) {
        var meals = getMealsFromTimePeriod(uuid, period);

        /**
         * COMMENT: this approach is not memory efficient. Here you are processing List of Meals 4 times through stream,
         * additionally you are creating duplicate functionality by fallback to a 0 each time. Try to use for cycle instead
         */
        double avgProtein = meals.stream().mapToDouble(MealDTO::getProtein).average().orElse(0);
        double avgFat = meals.stream().mapToDouble(MealDTO::getFat).average().orElse(0);
        double avgCarbs = meals.stream().mapToDouble(MealDTO::getCarbs).average().orElse(0);
        double avgCalories = meals.stream().mapToDouble(MealDTO::getCalories).average().orElse(0);
        double avgWeightInGrams = meals.stream().mapToDouble(MealDTO::getWeightInGrams).average().orElse(0);

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
        var existingMeal = mealRecordRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found with ID: " + mealId));

        existingMeal.setFoodName(request.foodName());
        existingMeal.setDate(request.date() != null ? request.date() : LocalDate.now());

        Map<String, Float> nutrition = calculateNutritionalValues(request.foodName(), request.weightInGrams());

        existingMeal.setCalories(nutrition.get("calories"));
        existingMeal.setCarbs(nutrition.get("carbs"));
        existingMeal.setProtein(nutrition.get("protein"));
        existingMeal.setFat(nutrition.get("fat"));

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