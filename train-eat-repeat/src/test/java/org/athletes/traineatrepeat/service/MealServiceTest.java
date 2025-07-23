package org.athletes.traineatrepeat.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.athletes.traineatrepeat.api.UsdaClient;
import org.athletes.traineatrepeat.api.model.UsdaSearchResponse;
import org.athletes.traineatrepeat.converter.MealRecordConverter;
import org.athletes.traineatrepeat.exceptions.TrainEatRepeatException;
import org.athletes.traineatrepeat.model.TimePeriod;
import org.athletes.traineatrepeat.model.request.MealRecordRequest;
import org.athletes.traineatrepeat.model.response.MealRecordResponse;
import org.athletes.traineatrepeat.model.response.UserNutritionStatisticsResponse;
import org.athletes.traineatrepeat.repository.MealRecordRepository;
import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.athletes.traineatrepeat.util.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MealServiceTest {

    private static final String TEST_UUID = UUID.randomUUID().toString();
    private static final LocalDate TODAY = LocalDate.of(2025, 7, 15);

    @Mock
    private MealRecordRepository mealRecordRepository;
    @Mock
    private MealRecordConverter mealRecordConverter;
    @Mock
    private UsdaClient usdaClient;
    @Mock
    private TimeProvider timeProvider;
    @InjectMocks
    private MealService mealService;

    private MealRecordRequest validMealRecordRequest;
    private MealDTO savedMealDTO;
    private MealRecordResponse expectedMealRecordResponse;
    private UsdaSearchResponse usdaSuccessResponse;

    @BeforeEach
    void setUp() {
        validMealRecordRequest = new MealRecordRequest("Apple", 150.0f, null);

        savedMealDTO = buildMealDTO(UUID.randomUUID().toString(), "Apple", 78.0f, 20.7f, 0.45f, 0.3f, 150.0f, TODAY);
        expectedMealRecordResponse = buildMealRecordResponse(
                savedMealDTO.getId(),
                savedMealDTO.getFoodName(),
                savedMealDTO.getCalories(),
                savedMealDTO.getCarbs(),
                savedMealDTO.getProtein(),
                savedMealDTO.getFat(),
                savedMealDTO.getWeightInGrams(),
                savedMealDTO.getDate()
        );

        usdaSuccessResponse = new UsdaSearchResponse(List.of(
                new UsdaSearchResponse.Food(
                        "Apple", null, null, null, null, null, null,
                        List.of(
                                new UsdaSearchResponse.Food.Nutrient("Energy", 52.0f),
                                new UsdaSearchResponse.Food.Nutrient("Carbohydrate, by difference", 13.8f),
                                new UsdaSearchResponse.Food.Nutrient("Protein", 0.3f),
                                new UsdaSearchResponse.Food.Nutrient("Total lipid (fat)", 0.2f)
                        ))
        ));

        lenient().when(timeProvider.getCurrentDate()).thenReturn(TODAY);
        lenient().when(usdaClient.searchFood(anyString())).thenReturn(usdaSuccessResponse);
        lenient().when(mealRecordRepository.save(any(MealDTO.class))).thenReturn(savedMealDTO);
        lenient().when(mealRecordConverter.toResponse(any(MealDTO.class))).thenReturn(expectedMealRecordResponse);
    }

    @Test
    @DisplayName("Should submit a meal successfully with current date")
    void submitMeal_Success_CurrentDate() {
        MealRecordResponse response = mealService.submitMeal(TEST_UUID, validMealRecordRequest);

        assertNotNull(response);
        assertEquals(expectedMealRecordResponse, response);

        ArgumentCaptor<MealDTO> captor = ArgumentCaptor.forClass(MealDTO.class);
        verify(mealRecordRepository).save(captor.capture());

        MealDTO captured = captor.getValue();
        assertEquals(TEST_UUID, captured.getUuid());
        assertEquals("Apple", captured.getFoodName());
        assertEquals(150.0f, captured.getWeightInGrams());
        assertEquals(TODAY, captured.getDate());
        assertEquals(78.0f, captured.getCalories(), 0.01f);
        assertEquals(20.7f, captured.getCarbs(), 0.01f);
        assertEquals(0.45f, captured.getProtein(), 0.01f);
        assertEquals(0.3f, captured.getFat(), 0.01f);
    }

    @Test
    @DisplayName("Should throw exception if food name is invalid")
    void submitMeal_InvalidFoodName_Throws() {
        List<String> invalidNames = Arrays.asList(null, "", "   ");

        for (String invalidName : invalidNames) {
            TrainEatRepeatException exception = assertThrows(TrainEatRepeatException.class,
                    () -> mealService.submitMeal(TEST_UUID, new MealRecordRequest(invalidName, 100f, null))
            );
            assertEquals("Food name cannot be null or empty", exception.getMessage());
        }

        verifyNoInteractions(usdaClient);
        verifyNoInteractions(mealRecordRepository);
    }

    @Test
    @DisplayName("Should throw exception if food not found")
    void submitMeal_FoodNotFound_Throws() {
        when(usdaClient.searchFood(anyString())).thenReturn(new UsdaSearchResponse(Collections.emptyList()));

        TrainEatRepeatException ex = assertThrows(TrainEatRepeatException.class,
                () -> mealService.submitMeal(TEST_UUID, validMealRecordRequest));

        assertEquals("Food not found: Apple", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception if no nutritional information found for food")
    void submitMeal_NoNutritionalInfo_Throws() {
        UsdaSearchResponse usdaResponseNoNutrients = new UsdaSearchResponse(
                List.of(new UsdaSearchResponse.Food("FoodWithoutNutrients", null, null, null, null, null, null, Collections.emptyList()))
        );
        when(usdaClient.searchFood(anyString())).thenReturn(usdaResponseNoNutrients);

        TrainEatRepeatException exception = assertThrows(TrainEatRepeatException.class, () ->
                mealService.submitMeal(TEST_UUID, new MealRecordRequest("FoodWithoutNutrients", 100.0f, null)));

        assertEquals("No nutritional information found for: FoodWithoutNutrients", exception.getMessage());
        verify(usdaClient).searchFood("FoodWithoutNutrients");
        verifyNoInteractions(mealRecordRepository);
    }

    @Test
    @DisplayName("Should get meals by time period")
    void getMealsForUser_ByPeriod() {
        List<MealDTO> meals = List.of(savedMealDTO);

        // When timePeriod is null
        when(mealRecordRepository.findAllByUuid(TEST_UUID)).thenReturn(meals);
        List<MealRecordResponse> all = mealService.getMealsForUser(TEST_UUID, null);
        assertEquals(1, all.size());

        // When timePeriod is DAY
        when(mealRecordRepository.findAllByUuidAndDate(TEST_UUID, TODAY)).thenReturn(meals);
        List<MealRecordResponse> day = mealService.getMealsForUser(TEST_UUID, TimePeriod.DAY);
        assertEquals(1, day.size());

        // When timePeriod is WEEK or MONTH, use findMealsByUuidAndDateBetween
        LocalDate startOfWeek = TODAY.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = TODAY.with(java.time.DayOfWeek.SUNDAY);
        when(mealRecordRepository.findMealsByUuidAndDateBetween(eq(TEST_UUID), eq(startOfWeek), eq(endOfWeek))).thenReturn(meals);
        List<MealRecordResponse> week = mealService.getMealsForUser(TEST_UUID, TimePeriod.WEEK);
        assertEquals(1, week.size());

        LocalDate startOfMonth = TODAY.withDayOfMonth(1);
        LocalDate endOfMonth = TODAY.withDayOfMonth(TODAY.lengthOfMonth());
        when(mealRecordRepository.findMealsByUuidAndDateBetween(eq(TEST_UUID), eq(startOfMonth), eq(endOfMonth))).thenReturn(meals);
        List<MealRecordResponse> month = mealService.getMealsForUser(TEST_UUID, TimePeriod.MONTH);
        assertEquals(1, month.size());


        verify(mealRecordConverter, times(4)).toResponse(any(MealDTO.class));
    }

    @Test
    @DisplayName("Should throw exception if no meals found for user")
    void getMealsForUser_NoMealsFound_Throws() {
        when(mealRecordRepository.findAllByUuid(TEST_UUID)).thenReturn(Collections.emptyList());

        TrainEatRepeatException ex = assertThrows(TrainEatRepeatException.class,
                () -> mealService.getMealsForUser(TEST_UUID, null));

        assertEquals("No meals found for user: " + TEST_UUID, ex.getMessage());
    }

    @Test
    @DisplayName("Should delete meal by ID successfully")
    void deleteMealById_Success() {
        String id = savedMealDTO.getId();
        when(mealRecordRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> mealService.deleteMealById(id));

        verify(mealRecordRepository).existsById(id);
        verify(mealRecordRepository).deleteById(id);
        verifyNoMoreInteractions(mealRecordRepository);
        verifyNoInteractions(usdaClient, mealRecordConverter, timeProvider);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing meal")
    void deleteMealById_NotFound() {
        String id = "non-existing-id";
        when(mealRecordRepository.existsById(id)).thenReturn(false);

        TrainEatRepeatException exception = assertThrows(TrainEatRepeatException.class,
                () -> mealService.deleteMealById(id));

        assertEquals("Meal not found with ID: " + id, exception.getMessage());

        verify(mealRecordRepository).existsById(id);
        verifyNoMoreInteractions(mealRecordRepository);
        verifyNoInteractions(usdaClient, mealRecordConverter, timeProvider);
    }

    @Test
    @DisplayName("Should update meal successfully")
    void updateMeal_Success() {
        String mealId = savedMealDTO.getId();
        MealRecordRequest updateRequest = new MealRecordRequest("Banana", 150.0f, null);

        when(mealRecordRepository.findById(mealId)).thenReturn(Optional.of(savedMealDTO));
        when(mealRecordRepository.save(any(MealDTO.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MealRecordResponse updatedExpectedResponse = buildMealRecordResponse(
                mealId,
                "Banana",
                78.0f,
                20.7f,
                0.45f,
                0.3f,
                150.0f,
                TODAY
        );
        when(mealRecordConverter.toResponse(any(MealDTO.class))).thenReturn(updatedExpectedResponse);

        MealRecordResponse response = mealService.updateMeal(mealId, updateRequest);

        assertNotNull(response);
        assertEquals(updatedExpectedResponse, response);

        ArgumentCaptor<MealDTO> captor = ArgumentCaptor.forClass(MealDTO.class);
        verify(mealRecordRepository).save(captor.capture());
        MealDTO saved = captor.getValue();

        assertEquals(mealId, saved.getId());
        assertEquals("Banana", saved.getFoodName());
        assertEquals(150.0f, saved.getWeightInGrams());
        assertEquals(TODAY, saved.getDate());

        lenient().when(mealRecordConverter.toResponse(any(MealDTO.class))).thenReturn(expectedMealRecordResponse);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing meal")
    void updateMeal_NotFound() {
        String mealId = "non-existing-id";
        when(mealRecordRepository.findById(mealId)).thenReturn(Optional.empty());

        TrainEatRepeatException ex = assertThrows(TrainEatRepeatException.class,
                () -> mealService.updateMeal(mealId, validMealRecordRequest));

        assertEquals("Meal not found with ID: " + mealId, ex.getMessage());
    }

    @Test
    @DisplayName("Should calculate nutrition statistics with meals")
    void getNutritionStatistics_WithMeals() {
        LocalDate todayInTest = TODAY;
        LocalDate startOfWeek = todayInTest.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = todayInTest.with(java.time.DayOfWeek.SUNDAY);

        List<MealDTO> mealDTOs = List.of(
                buildMealDTO(UUID.randomUUID().toString(), "Meal1", 100f, 10f, 5f, 2f, 200f, startOfWeek),
                buildMealDTO(UUID.randomUUID().toString(), "Meal2", 200f, 20f, 10f, 4f, 400f, endOfWeek)
        );

        when(mealRecordRepository.findMealsByUuidAndDateBetween(eq(TEST_UUID), eq(startOfWeek), eq(endOfWeek)))
                .thenReturn(mealDTOs);

        UserNutritionStatisticsResponse stats = mealService.getNutritionStatistics(TEST_UUID, TimePeriod.WEEK);

        assertNotNull(stats);
        assertAll("NutritionAverages",
                () -> assertEquals(150f, stats.avgCalories(), 0.01f),
                () -> assertEquals(15f, stats.avgCarbs(), 0.01f),
                () -> assertEquals(7.5f, stats.avgProtein(), 0.01f),
                () -> assertEquals(3f, stats.avgFat(), 0.01f),
                () -> assertEquals(300f, stats.avgWeightInGrams(), 0.01f)
        );
    }

    @Test
    @DisplayName("Should calculate nutrition statistics with no meals")
    void getNutritionStatistics_NoMeals() {
        LocalDate todayInTest = TODAY;
        LocalDate startOfWeek = todayInTest.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = todayInTest.with(java.time.DayOfWeek.SUNDAY);

        when(mealRecordRepository.findMealsByUuidAndDateBetween(eq(TEST_UUID), eq(startOfWeek), eq(endOfWeek)))
                .thenReturn(Collections.emptyList());

        UserNutritionStatisticsResponse stats = mealService.getNutritionStatistics(TEST_UUID, TimePeriod.WEEK);

        assertNotNull(stats);
        assertEquals(0f, stats.avgCalories());
    }

    private MealDTO buildMealDTO(String id, String foodName, float calories, float carbs, float protein, float fat, float weight, LocalDate date) {
        MealDTO dto = new MealDTO();
        dto.setId(id);
        dto.setUuid(TEST_UUID);
        dto.setFoodName(foodName);
        dto.setCalories(calories);
        dto.setCarbs(carbs);
        dto.setProtein(protein);
        dto.setFat(fat);
        dto.setWeightInGrams(weight);
        dto.setDate(date);
        return dto;
    }

    private MealRecordResponse buildMealRecordResponse(String id, String foodName, float calories, float carbs, float protein, float fat, float weight, LocalDate date) {
        return MealRecordResponse.builder()
                .id(id)
                .foodName(foodName)
                .calories(calories)
                .carbs(carbs)
                .protein(protein)
                .fat(fat)
                .weightInGrams(weight)
                .date(date)
                .build();
    }
}
