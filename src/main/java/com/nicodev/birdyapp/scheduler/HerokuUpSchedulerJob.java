package com.nicodev.birdyapp.scheduler;

import com.nicodev.birdyapp.configuration.CustomClientHttpRequestInterceptor;
import com.nicodev.birdyapp.exception.RestTemplateResponseErrorHandler;
import java.net.URI;
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
public class HerokuUpSchedulerJob {

    private static Logger logger = LoggerFactory.getLogger(HerokuUpSchedulerJob.class);
    private static final String RUN_EVERY_30_MINUTES = "0 0/30 * * * ?";

    private RestTemplate restTemplate;

    @Value("${heroku.app.main-path}")
    private String herokuMainPath;

    @Autowired
    public HerokuUpSchedulerJob() {
        this.restTemplate = new RestTemplateBuilder()
            .errorHandler(new RestTemplateResponseErrorHandler())
            .additionalInterceptors(new CustomClientHttpRequestInterceptor())
            .build();
    }

    @Scheduled(cron = RUN_EVERY_30_MINUTES)
    public void herokuUp() {
        logger.info("Start job: Heroku UP");

        URI herokuUri = UriComponentsBuilder
            .fromUriString(herokuMainPath)
            .build().toUri();

        restTemplate.getForEntity(herokuUri, String.class);

        logger.info("End job: Heroku UP");
    }
}
