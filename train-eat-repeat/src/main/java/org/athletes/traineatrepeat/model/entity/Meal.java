package org.athletes.traineatrepeat.model.entity;

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
@Table(name = "mealrecord")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meal {

    @Id
    @Column
    private String id;
    private String userUuid;
    private String foodName;
    private float calories;
    private float carbs;
    private float protein;
    private float fat;
    private LocalDate date;
}
