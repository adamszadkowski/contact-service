package info.szadkowski.contact.controller.exception;

import info.szadkowski.contact.exception.ThrottledRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ThrottledRequestException.class)
  public ResponseEntity<Void> handleThrottling() {
    return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
  }
}
