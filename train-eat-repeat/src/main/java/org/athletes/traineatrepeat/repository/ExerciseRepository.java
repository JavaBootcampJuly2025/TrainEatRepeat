//package org.athletes.traineatrepeat.repository;
//
//import org.athletes.traineatrepeat.repository.dto.ExerciseDTO;
//import org.springframework.stereotype.Repository;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Repository
//public class ExerciseRepository {
//
//    private final Map<String, ExerciseDTO> storage = new ConcurrentHashMap<>();
//    private static Long idCounter = 0L;
//
//    public ExerciseDTO save(ExerciseDTO dto) {
//        String newId = (dto.id() == null || dto.id().isEmpty()) ? String.valueOf(++idCounter) : dto.id();
//
//        ExerciseDTO savedDto = ExerciseDTO.builder()
//                .id(newId)
//                .name(dto.name())
//                .MET(dto.MET())
//                .build();
//
//        storage.put(newId, savedDto);
//        System.out.println("Сохранено упражнение: " + savedDto);
//        return savedDto;
//    }
//
//    public List<ExerciseDTO> findAll() {
//        return new ArrayList<>(storage.values());
//    }
//
//    public Optional<ExerciseDTO> findById(String id) {
//        return Optional.ofNullable(storage.get(id));
//    }
//}