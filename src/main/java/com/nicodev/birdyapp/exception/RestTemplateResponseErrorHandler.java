package com.nicodev.birdyapp.exception;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

import com.nicodev.birdyapp.exception.rest.RestException;
import com.nicodev.birdyapp.exception.rest.RestResponse;
import io.micrometer.core.instrument.util.IOUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

  @Override
  public boolean hasError(ClientHttpResponse httpResponse) throws IOException {

    return (httpResponse.getStatusCode().series() == CLIENT_ERROR
            || httpResponse.getStatusCode().series() == SERVER_ERROR);
  }

  @Override
  public void handleError(ClientHttpResponse httpResponse) throws IOException {
    String body = IOUtils.toString(httpResponse.getBody(), StandardCharsets.UTF_8);
    RestResponse restResponse = new RestResponse(httpResponse.getStatusCode(), body);

    throw new RestException(restResponse);
  }
}
