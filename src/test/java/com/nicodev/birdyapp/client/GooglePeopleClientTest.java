package com.nicodev.birdyapp.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicodev.birdyapp.model.dto.GoogleConnectionResponseDTO;
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
class GooglePeopleClientTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private GooglePeopleClient client;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(client, "googleApiPeopleEndpoint", "https://api.example.com");
  }

  @Test
  void verify_google_people_client_test() {
    when(restTemplate.getForObject(any(), eq(GoogleConnectionResponseDTO.class)))
            .thenReturn(new GoogleConnectionResponseDTO());

    client.getGoogleUserConnections("access_token");

    verify(restTemplate, times(1)).getForObject(any(), any());
  }
}
