package com.nicodev.birdyapp.exception;

import com.nicodev.birdyapp.exception.rest.RestException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class RestTemplateResponseErrorHandlerTest {

  @Resource
  private RestTemplateResponseErrorHandler handler;

  @Mock
  private ClientHttpResponse response;

  @Test
  public void test_400_bad_request_handler_error() throws IOException {
    Assert.assertNotNull(this.handler);
    Assert.assertNotNull(this.response);

    String body = "This is a bad request exception";
    Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
    InputStream inputStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

    Mockito.when(response.getBody()).thenReturn(inputStream);

    try {
      handler.handleError(response);
    } catch (RestException ex) {
      Assert.assertTrue(ex.getResponse().getStatus().is4xxClientError());
      Assert.assertEquals(ex.getResponse().getStatus(), HttpStatus.BAD_REQUEST);
      Assert.assertEquals(body, ex.getResponse().getBody());
    }
  }

  @Test
  public void test_404_not_found_handler_error() throws IOException {
    Assert.assertNotNull(this.handler);
    Assert.assertNotNull(this.response);

    String body = "This is a not found exception";
    Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);
    InputStream inputStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

    Mockito.when(response.getBody()).thenReturn(inputStream);

    try {
      handler.handleError(response);
    } catch (RestException ex) {
      Assert.assertTrue(ex.getResponse().getStatus().is4xxClientError());
      Assert.assertEquals(ex.getResponse().getStatus(), HttpStatus.NOT_FOUND);
      Assert.assertEquals(body, ex.getResponse().getBody());
    }
  }

  @Test
  public void test_500_internal_server_handler_error() throws IOException {
    Assert.assertNotNull(this.handler);
    Assert.assertNotNull(this.response);

    String body = "This is a internal server exception";
    Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    InputStream inputStream = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

    Mockito.when(response.getBody()).thenReturn(inputStream);

    try {
      handler.handleError(response);
    } catch (RestException ex) {
      Assert.assertTrue(ex.getResponse().getStatus().is5xxServerError());
      Assert.assertEquals(ex.getResponse().getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
      Assert.assertEquals(body, ex.getResponse().getBody());
    }
  }

  @Test
  public void test_handler_has_error() throws IOException {
    Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
    boolean hasError = handler.hasError(response);
    Assert.assertTrue(hasError);
  }

  @Test
  public void test_handler_has_error_false() throws IOException {
    Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.OK);
    boolean hasError = handler.hasError(response);
    Assert.assertFalse(hasError);
  }
}
