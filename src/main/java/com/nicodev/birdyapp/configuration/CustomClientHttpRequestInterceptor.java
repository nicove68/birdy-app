package com.nicodev.birdyapp.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(CustomClientHttpRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        logRequestDetails(request);

        return execution.execute(request, body);
    }

    private void logRequestDetails(HttpRequest request) {
        logger.info("Request Headers: {}", request.getHeaders());
        logger.info("Request Method: {}", request.getMethod());
        logger.info("Request URI: {}", request.getURI());
    }
}
