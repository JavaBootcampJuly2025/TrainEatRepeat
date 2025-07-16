package org.athletes.traineatrepeat.repository;

import org.athletes.traineatrepeat.repository.dto.MealDTO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MealRecordRepository {

    private static Long idCounter = 0L;

    public MealDTO save(MealDTO dto) {
        String newId = (dto.id() == null || dto.id().isEmpty()) ? String.valueOf(++idCounter) : dto.id();

        MealDTO savedDto = MealDTO.builder()
                .id(newId)
                .userUuid(dto.userUuid())
                .foodName(dto.foodName())
                .caloriesConsumed(dto.caloriesConsumed())
                .carbs(dto.carbs())
                .protein(dto.protein())
                .fat(dto.fat())
                .date(dto.date())
                .build();

        // TODO: Implement save logic
        System.out.println(savedDto);
        return savedDto;
    }

    public List<MealDTO> findByUserUuid(String userUuid) {
        // TODO: Implement findByUserUuid logic
        return List.of();
    }

    public Optional<MealDTO> findById(Long id) {
        // TODO: Implement findById logic
        return Optional.empty();
    }
}