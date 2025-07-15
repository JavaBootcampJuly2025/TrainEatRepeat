package org.athletes.traineatrepeat.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(length = 36)
    private String uuid;

    private String username;
    private String password;
    private Integer age;
    private String gender;

    @ElementCollection
    private List<String> chronicDiseases;

    @ElementCollection
    private List<String> foodPreferences;

    private float weight;
    private float height;
    private float BMI;
    private float BMR;
    private String role;
}
