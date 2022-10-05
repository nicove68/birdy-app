package com.nicodev.birdyapp.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.nicodev.birdyapp.exception.rest.RestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@RestClientTest
class RestTemplateResponseErrorHandlerTest {

  @InjectMocks
  private RestTemplateResponseErrorHandler handler;

  @Mock
  private ClientHttpResponse response;

  @Test
  void test_400_bad_request_handler_error() throws IOException {
    assertNotNull(this.handler);
    assertNotNull(this.response);

    String body = "This is a bad request exception";
    Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
    InputStream inputStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

    Mockito.when(response.getBody()).thenReturn(inputStream);

    try {
      handler.handleError(response);
    } catch (RestException ex) {
      assertTrue(ex.getResponse().getStatus().is4xxClientError());
      assertEquals(ex.getResponse().getStatus(), HttpStatus.BAD_REQUEST);
      assertEquals(body, ex.getResponse().getBody());
    }
  }

  @Test
  void test_404_not_found_handler_error() throws IOException {
    assertNotNull(this.handler);
    assertNotNull(this.response);

    String body = "This is a not found exception";
    Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);
    InputStream inputStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

    Mockito.when(response.getBody()).thenReturn(inputStream);

    try {
      handler.handleError(response);
    } catch (RestException ex) {
      assertTrue(ex.getResponse().getStatus().is4xxClientError());
      assertEquals(ex.getResponse().getStatus(), HttpStatus.NOT_FOUND);
      assertEquals(body, ex.getResponse().getBody());
    }
  }

  @Test
  void test_500_internal_server_handler_error() throws IOException {
    assertNotNull(this.handler);
    assertNotNull(this.response);

    String body = "This is a internal server exception";
    Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    InputStream inputStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

    Mockito.when(response.getBody()).thenReturn(inputStream);

    try {
      handler.handleError(response);
    } catch (RestException ex) {
      assertTrue(ex.getResponse().getStatus().is5xxServerError());
      assertEquals(ex.getResponse().getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
      assertEquals(body, ex.getResponse().getBody());
    }
  }

  @Test
  void test_handler_has_error() throws IOException {
    Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
    boolean hasError = handler.hasError(response);
    assertTrue(hasError);
  }

  @Test
  void test_handler_has_error_false() throws IOException {
    Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.OK);
    boolean hasError = handler.hasError(response);
    assertFalse(hasError);
  }
}
