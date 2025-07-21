package org.athletes.traineatrepeat.repository.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEALRECORDS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealDTO {
  @Id private String id;

  private String uuid;

  @NotNull private String foodName;

  private float calories;
  private float carbs;
  private float protein;
  private float fat;

  @Column(name = "weight_in_grams", nullable = false)
  private float weightInGrams;

  private LocalDate date;
}
