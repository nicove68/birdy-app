package com.nicodev.birdyapp.client;

import com.nicodev.birdyapp.client.dto.GoogleConnectionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class GooglePeopleClient {

    private static final int MAX_PAGE_SIZE = 2000;

    private RestTemplate restTemplateGooglePeopleApi;

    @Value("${google.api.people.endpoint}")
    private String googleApiPeopleEndpoint;

    @Autowired
    public GooglePeopleClient(RestTemplate restTemplateGooglePeopleApi) {
        this.restTemplateGooglePeopleApi = restTemplateGooglePeopleApi;
    }


    public GoogleConnectionResponseDTO getGoogleUserConnections(String accessToken) {
        URI uri = getUriForGoogleUserConnections(accessToken);
        return restTemplateGooglePeopleApi.getForObject(uri, GoogleConnectionResponseDTO.class);
    }

    private URI getUriForGoogleUserConnections(String accessToken) {
        return UriComponentsBuilder
                .fromUriString(googleApiPeopleEndpoint)
                .path("/v1/people/me/connections")
                .queryParam("access_token", accessToken)
                .queryParam("personFields", "birthdays,names,photos")
                .queryParam("pageSize", MAX_PAGE_SIZE)
                .build().toUri();
    }
}
