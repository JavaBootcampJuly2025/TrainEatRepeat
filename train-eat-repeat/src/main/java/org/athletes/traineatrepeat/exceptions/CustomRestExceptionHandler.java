package org.athletes.traineatrepeat.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(TrainEatRepeatException.class)
  public ResponseEntity<ApiError> handleTrainEatRepeatException(TrainEatRepeatException ex) {
    ApiError apiError =
        new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), "Custom application error");
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGenericException(Exception ex) {
    ApiError apiError =
        new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), "An unexpected error occurred");
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }
}
