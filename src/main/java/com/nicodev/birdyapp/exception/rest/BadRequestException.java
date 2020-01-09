package com.nicodev.birdyapp.exception.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class BadRequestException extends RestException {

  public BadRequestException(String body) {
    super(new RestResponse(BAD_REQUEST, body));
  }
}
