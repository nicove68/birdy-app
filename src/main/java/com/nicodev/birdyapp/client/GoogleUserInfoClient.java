package com.nicodev.birdyapp.client;

import com.nicodev.birdyapp.model.dto.GoogleUserInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class GoogleUserInfoClient {

    private RestTemplate restTemplateGoogleApi;

    @Value("${google.api.userinfo.endpoint}")
    private String googleApiUserInfoEndpoint;

    @Autowired
    public GoogleUserInfoClient(RestTemplate restTemplateGoogleApi) {
        this.restTemplateGoogleApi = restTemplateGoogleApi;
    }


    public GoogleUserInfoDTO getGoogleUserInfo(String accessToken) {
        URI uri = getUriForGoogleUserInfo(accessToken);
        return restTemplateGoogleApi.getForObject(uri, GoogleUserInfoDTO.class);
    }

    private URI getUriForGoogleUserInfo(String accessToken) {
        return UriComponentsBuilder
                .fromUriString(googleApiUserInfoEndpoint)
                .path("/oauth2/v1/userinfo")
                .queryParam("access_token", accessToken)
                .build().toUri();
    }
}
