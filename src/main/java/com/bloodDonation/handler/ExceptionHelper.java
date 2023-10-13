package com.bloodDonation.handler;

import com.bloodDonation.enity.BaseApiResponse;
import com.bloodDonation.exception.BadRequestException;
import com.bloodDonation.exception.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(annotations = RestController.class)
@Slf4j
public class ExceptionHelper extends ResponseEntityExceptionHandler {
  @ExceptionHandler(GenericException.class)
  public ResponseEntity<? extends BaseApiResponse> handleGenericException(GenericException ex) {
    BaseApiResponse responseBody = new BaseApiResponse("FAILURE", ex.getMessage());
    return new ResponseEntity<>(responseBody, HttpStatus.OK);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<? extends BaseApiResponse> handleBadRequestException(
      BadRequestException ex) {
    BaseApiResponse responseBody = new BaseApiResponse("FAILURE", ex.getMessage());
    return new ResponseEntity<>(responseBody, HttpStatus.OK);
  }
}
