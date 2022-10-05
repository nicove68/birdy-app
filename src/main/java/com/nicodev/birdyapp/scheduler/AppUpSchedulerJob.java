package com.nicodev.birdyapp.scheduler;

import java.net.URI;

import com.nicodev.birdyapp.configuration.CustomClientHttpRequestInterceptor;
import com.nicodev.birdyapp.exception.RestTemplateResponseErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AppUpSchedulerJob {

    private static final Logger logger = LoggerFactory.getLogger(AppUpSchedulerJob.class);
    private static final String RUN_EVERY_5_MINUTES = "0 0/5 * * * ?";

    private final RestTemplate restTemplate;

    @Value("${app.main-path}")
    private String appMainPath;

    @Autowired
    public AppUpSchedulerJob() {
        this.restTemplate = new RestTemplateBuilder()
            .errorHandler(new RestTemplateResponseErrorHandler())
            .additionalInterceptors(new CustomClientHttpRequestInterceptor())
            .build();
    }

    @Scheduled(cron = RUN_EVERY_5_MINUTES)
    public void appIsUp() {
        logger.info("Start job: is BirdyApp UP?");

        URI appUri = UriComponentsBuilder
            .fromUriString(appMainPath)
            .build().toUri();

        restTemplate.getForEntity(appUri, String.class);

        logger.info("End job: Yes, BirdyApp is UP!");
    }
}
