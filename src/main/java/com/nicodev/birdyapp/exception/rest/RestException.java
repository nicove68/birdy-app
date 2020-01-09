package com.nicodev.birdyapp.exception.rest;


public class RestException extends RuntimeException {

  private RestResponse response;

  public RestException(String message, Throwable cause) {
    super(message, cause);
  }

  public RestException(RestResponse response) {
    super(response.getStatus() + ":" + response.getBody());
    this.response = response;
  }

  public RestException(RestResponse response, Throwable cause) {
    super(cause);
    this.response = response;
  }

  public RestResponse getResponse() {
    return response;
  }
}
