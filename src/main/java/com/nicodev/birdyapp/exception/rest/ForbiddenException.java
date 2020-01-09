package com.nicodev.birdyapp.exception.rest;

import static org.springframework.http.HttpStatus.FORBIDDEN;

public class ForbiddenException extends RestException {

  public ForbiddenException() {
    super(new RestResponse(FORBIDDEN, "You don't have access to this resource."));
  }

  public ForbiddenException(String body) {
    super(new RestResponse(FORBIDDEN, body));
  }
}
