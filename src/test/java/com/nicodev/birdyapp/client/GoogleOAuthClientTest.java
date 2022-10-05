package com.nicodev.birdyapp.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicodev.birdyapp.model.dto.GoogleOAuthTokenDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class GoogleOAuthClientTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private GoogleOAuthClient client;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(client, "googleApiClientId", "client_id");
    ReflectionTestUtils.setField(client, "googleApiClientSecret", "client_secret");
    ReflectionTestUtils.setField(client, "googleApiOAuthRedirectUri", "https://redirect.com");
    ReflectionTestUtils.setField(client, "googleApiOAuthEndpoint", "https://api.example.com");
  }

  @Test
  void verify_google_oauth_client_test() {
    when(restTemplate.postForObject(any(), any(), eq(GoogleOAuthTokenDTO.class)))
            .thenReturn(new GoogleOAuthTokenDTO());

    client.getGoogleOAuthToken("auth_code");

    verify(restTemplate, times(1)).postForObject(any(), any(), any());
  }

  @Test
  void verify_google_oauth_refresh_client_test() {
    when(restTemplate.postForObject(any(), any(), eq(GoogleOAuthTokenDTO.class)))
            .thenReturn(new GoogleOAuthTokenDTO());

    client.getGoogleOAuthTokenUsingRefreshToken("refresh_token");

    verify(restTemplate, times(1)).postForObject(any(), any(), any());
  }
}
