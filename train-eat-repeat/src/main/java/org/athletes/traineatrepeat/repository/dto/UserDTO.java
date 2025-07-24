package org.athletes.traineatrepeat.repository.dto;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "USERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(length = 36)
  private String uuid;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(unique = true, nullable = false)
  private String email;

  private String password;
  private int age;
  private String gender;
  private float weight;
  private float height;

  @Column(name = "BMI")
  private float bmi;

  @Column(name = "BMR")
  private float bmr;

  private String role;

  @Column(name = "verification_code")
  private String verificationCode;

  @Column(name = "verification_expires_at")
  private LocalDateTime verificationExpiresAt;

  @Column(name = "is_email_verified")
  private boolean isEmailVerified;

  @Column(name = "reset_token")
  private String resetToken;

  @Column(name = "reset_token_expires_at")
  private LocalDateTime resetTokenExpiresAt;
}
