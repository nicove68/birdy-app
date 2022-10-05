package com.nicodev.birdyapp.client;

import com.nicodev.birdyapp.model.dto.GoogleOAuthTokenDTO;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GoogleOAuthClient {

    private final RestTemplate restTemplateGoogleApi;

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

    public void revokeGoogleOAuthToken(String accessToken) {
        URI uri = getUriForRevokeGoogleOAuthToken(accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        restTemplateGoogleApi.exchange(uri, HttpMethod.POST, entity, Void.class);
    }

    private URI getUriForRevokeGoogleOAuthToken(String accessToken) {
        return UriComponentsBuilder
            .fromUriString(googleApiOAuthEndpoint)
            .path("/revoke")
            .queryParam("token", accessToken)
            .build().toUri();
    }
}
