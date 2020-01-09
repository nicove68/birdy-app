package com.nicodev.birdyapp.exception.rest;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class NotFoundException extends RestException {

  public NotFoundException() {
    super(new RestResponse(NOT_FOUND, "The resource you are trying to access does not exist."));
  }

  public NotFoundException(String body) {
    super(new RestResponse(NOT_FOUND, body));
  }
}
