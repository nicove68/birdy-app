package com.nicodev.birdyapp.exception.rest;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class UnauthorizedException extends RestException {

  public UnauthorizedException() {
    super(new RestResponse(UNAUTHORIZED, "Authentication required."));
  }

  public UnauthorizedException(String body) {
    super(new RestResponse(UNAUTHORIZED, body));
  }
}