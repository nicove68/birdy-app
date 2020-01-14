package com.nicodev.birdyapp.client;

import com.nicodev.birdyapp.client.dto.GoogleOAuthTokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class GoogleOAuthClient {

    private RestTemplate restTemplateGoogleApi;

    @Value("${google.api.oauth.endpoint}")
    private String googleApiOAuthEndpoint;

    @Value("${google.api.client-id}")
    private String googleApiClientId;

    @Value("${google.api.client-secret}")
    private String googleApiClientSecret;

    @Value("${google.api.oauth.redirect-uri}")
    private String googleApiOAuthRedirectUri;

    @Autowired
    public GoogleOAuthClient(RestTemplate restTemplateGoogleApi) {
        this.restTemplateGoogleApi = restTemplateGoogleApi;
    }


    public GoogleOAuthTokenDTO getGoogleOAuthToken(String authorizationCode) {
        URI uri = getUriForGoogleOAuthToken(authorizationCode);
        return restTemplateGoogleApi.postForObject(uri, null, GoogleOAuthTokenDTO.class);
    }

    private URI getUriForGoogleOAuthToken(String authorizationCode) {
        return UriComponentsBuilder
                .fromUriString(googleApiOAuthEndpoint)
                .path("/token")
                .queryParam("code", authorizationCode)
                .queryParam("client_id", googleApiClientId)
                .queryParam("client_secret", googleApiClientSecret)
                .queryParam("redirect_uri", googleApiOAuthRedirectUri)
                .queryParam("grant_type", "authorization_code")
                .build().toUri();
    }

    public GoogleOAuthTokenDTO getGoogleOAuthTokenUsingRefreshToken(String refreshToken) {
        URI uri = getUriForGoogleOAuthTokenUsingRefreshToken(refreshToken);
        return restTemplateGoogleApi.postForObject(uri, null, GoogleOAuthTokenDTO.class);
    }

    private URI getUriForGoogleOAuthTokenUsingRefreshToken(String refreshToken) {
        return UriComponentsBuilder
                .fromUriString(googleApiOAuthEndpoint)
                .path("/token")
                .queryParam("refresh_token", refreshToken)
                .queryParam("client_id", googleApiClientId)
                .queryParam("client_secret", googleApiClientSecret)
                .queryParam("grant_type", "refresh_token")
                .build().toUri();
    }
}
