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
@Table(name = "TRAININGRECORDS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingDTO {

  @Id @Column private String id;
  private String uuid;
  @NotNull private String exercise;
  private float duration;
  private float caloriesLost;
  private LocalDate date;
}
