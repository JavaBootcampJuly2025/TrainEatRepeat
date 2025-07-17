package org.athletes.traineatrepeat.repository.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "MEALRECORDS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealDTO {

    @Id
    @Column
    private String id;
    private String uuid;
    private String foodName;
    private float calories;
    private float carbs;
    private float protein;
    private float fat;
    private LocalDate date;
}