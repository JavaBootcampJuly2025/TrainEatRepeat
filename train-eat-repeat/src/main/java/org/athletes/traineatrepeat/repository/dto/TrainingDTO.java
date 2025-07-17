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
@Table(name = "TRAININGRECORDS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingDTO {

    @Id
    @Column
    private String id;
    private String uuid;
    private String exercise;
    private float duration;
    private float caloriesLost;
    private LocalDate date;
}