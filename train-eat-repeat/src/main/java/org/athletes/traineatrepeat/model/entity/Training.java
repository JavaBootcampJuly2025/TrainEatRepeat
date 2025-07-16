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
@Table(name = "trainingrecords")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Training {

    @Id
    @Column
    private String id;
    private String userUuid;
    private String exercise;
    private float duration;
    private float caloriesLost;
    private LocalDate date;

}
