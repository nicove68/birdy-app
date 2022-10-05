package com.nicodev.birdyapp.exception;

import com.nicodev.birdyapp.exception.rest.RestException;
import com.nicodev.birdyapp.exception.rest.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class RestControllerErrorHandler {

  private final Logger logger = LoggerFactory.getLogger(RestControllerErrorHandler.class);

  private static final String ERROR_EXECUTING = "Error executing ";
  
  @ExceptionHandler(Exception.class)
  public ResponseEntity<RestResponse> handleException(HttpServletRequest req, Exception ex) {
    logger.error(ERROR_EXECUTING + req.getRequestURI(), ex);

    HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    RestResponse error = new RestResponse(httpStatus, ex.getMessage());

    return new ResponseEntity<>(error, httpStatus);
  }

  @ExceptionHandler(RestException.class)
  public ResponseEntity<RestResponse> handleRestException(HttpServletRequest req, RestException ex) {
    logger.error(ERROR_EXECUTING + req.getRequestURI(), ex);

    RestResponse response = ex.getResponse();
    HttpStatus httpStatus = response.getStatus();

    return new ResponseEntity<>(ex.getResponse(), httpStatus);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<RestResponse> handleInvalidFormatException(HttpServletRequest req, RestException ex) {
    logger.error(ERROR_EXECUTING + req.getRequestURI(), ex);

    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    return new ResponseEntity<>(ex.getResponse(), httpStatus);
  }
}
