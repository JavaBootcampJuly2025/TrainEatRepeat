package org.athletes.traineatrepeat.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class ApiError {

  private HttpStatus status;
  private String message;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime timestamp;

  public ApiError(HttpStatus status, String message, String error) {
    this.status = status;
    this.message = String.format("%s : %s", error, message);
    this.timestamp = LocalDateTime.now();
  }
}
