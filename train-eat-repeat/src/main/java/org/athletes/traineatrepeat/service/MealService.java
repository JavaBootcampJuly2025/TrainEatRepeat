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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRecordRepository mealRecordRepository;
    private final MealRecordConverter mealRecordConverter;
    private final UsdaClient usdaClient;

    public MealRecordResponse submitMeal(String uuid, MealRecordRequest request) {
        var usdaResponse = usdaClient.searchFood(request.foodName());

        float calories = request.caloriesConsumed();
        float carbs = request.carbs();
        float protein = request.protein();
        float fat = request.fat();

        if (usdaResponse != null && usdaResponse.getFoods() != null && !usdaResponse.getFoods().isEmpty()) {
            var food = usdaResponse.getFoods().get(0);
            for (var nutrient : food.getFoodNutrients()) {
                switch (nutrient.getNutrientName()) {
                    case "Energy" -> calories = nutrient.getValue();
                    case "Protein" -> protein = nutrient.getValue();
                    case "Total lipid (fat)" -> fat = nutrient.getValue();
                    case "Carbohydrate, by difference" -> carbs = nutrient.getValue();
                }
            }
        }

        float weight = request.weightInGrams() == 0 ? 100.0f : request.weightInGrams();
        float factor = weight / 100.0f;

        calories *= factor;
        carbs *= factor;
        protein *= factor;
        fat *= factor;

        var mealToSave = MealDTO.builder()
                .id(UUID.randomUUID().toString())
                .uuid(uuid)
                .foodName(request.foodName())
                .calories(calories)
                .carbs(carbs)
                .protein(protein)
                .fat(fat)
                .date(request.date())
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
        var existingMeal = mealRecordRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        existingMeal.setFoodName(request.foodName());
        existingMeal.setCalories(request.caloriesConsumed());
        existingMeal.setCarbs(request.carbs());
        existingMeal.setProtein(request.protein());
        existingMeal.setFat(request.fat());
        existingMeal.setDate(request.date());

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