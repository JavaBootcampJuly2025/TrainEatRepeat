package org.athletes.traineatrepeat.service;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.converter.MealRecordConverter;
import org.athletes.traineatrepeat.model.entity.Meal;
import org.athletes.traineatrepeat.model.request.MealRecordRequest;
import org.athletes.traineatrepeat.model.response.MealRecordResponse;
import org.athletes.traineatrepeat.repository.MealRecordRepository;
import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRecordRepository mealRecordRepository;
    private final MealRecordConverter mealRecordConverter;

    public MealRecordResponse submitMeal(String uuid, MealRecordRequest request) {
        Meal mealToSave = mealRecordConverter.fromRequestToEntity(request, uuid);
        Meal savedMeal = mealRecordRepository.save(mealToSave);
        return mealRecordConverter.toResponse(savedMeal);
    }

    public List<MealRecordResponse> getTodaysMealsForUser(String uuid) {
        LocalDate today = LocalDate.now();
        List<Meal> todaysMeals = mealRecordRepository.findAllByUserUuidAndDate(uuid, today);
        return todaysMeals.stream()
                .map(mealRecordConverter::toResponse)
                .toList();
    }

    public void deleteMealById(String mealId) {
        mealRecordRepository.deleteById(mealId);
    }

    public MealRecordResponse updateMeal(String mealId, MealRecordRequest request) {
        Meal existingMeal = mealRecordRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        existingMeal.setFoodName(request.foodName());
        existingMeal.setCalories(request.caloriesConsumed());
        existingMeal.setCarbs(request.carbs());
        existingMeal.setProtein(request.protein());
        existingMeal.setFat(request.fat());
        existingMeal.setDate(request.date());

        Meal savedMeal = mealRecordRepository.save(existingMeal);
        return mealRecordConverter.toResponse(savedMeal);
    }

    public List<MealRecordResponse> getThisWeeksMeals(String uuid) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);

        List<Meal> meals = mealRecordRepository.findMealsByUserUuidAndDateBetween(uuid, startOfWeek, endOfWeek);

        return meals.stream()
                .map(mealRecordConverter::toResponse)
                .toList();
    }

    public List<MealRecordResponse> getThisMonthsMeals(String uuid) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        List<Meal> meals = mealRecordRepository.findMealsByUserUuidAndDateBetween(uuid, startOfMonth, endOfMonth);

        return meals.stream()
                .map(mealRecordConverter::toResponse)
                .toList();
    }
}
