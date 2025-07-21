package org.athletes.traineatrepeat.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

/**
 * COMMENT: Try to introduce a custom exception handler to receive more specific error responses from the API
 * <a href="https://www.baeldung.com/global-error-handler-in-a-spring-rest-api">https://www.baeldung.com/global-error-handler-in-a-spring-rest-api</a>
 */
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {



}
