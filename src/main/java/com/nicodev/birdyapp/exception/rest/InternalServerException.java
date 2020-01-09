package com.nicodev.birdyapp.exception.rest;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class InternalServerException extends RestException {

  public InternalServerException() {
    super(new RestResponse(INTERNAL_SERVER_ERROR, "Internal Server exception"));
  }

  public InternalServerException(String body) {
    super(new RestResponse(INTERNAL_SERVER_ERROR, body));
  }
}