package com.nicodev.birdyapp.exception.rest;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class RestResponse implements Serializable {

	private HttpStatus status;
	private String body;

	public RestResponse() {}

	public RestResponse(HttpStatus status, String body) {
		this.status = status;
		this.body = body;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getBody() {
		return body;
	}
}
