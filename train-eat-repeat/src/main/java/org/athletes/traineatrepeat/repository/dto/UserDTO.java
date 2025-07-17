package org.athletes.traineatrepeat.repository.dto;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    @Id
    @Column(length = 36)
    private String uuid;

    private String username;
    private String password;
    private int age;
    private String gender;

    //TODO: add together with training data
//    @ElementCollection
//    private List<String> chronicDiseases;
    //TODO: add together with food data
//    @ElementCollection
//    private List<String> foodPreferences;

    private float weight;
    private float height;
    private float BMI;
    private float BMR;
    private String role;
}