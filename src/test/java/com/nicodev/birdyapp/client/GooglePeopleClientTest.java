package com.nicodev.birdyapp.client;

import com.nicodev.birdyapp.model.dto.GoogleConnectionResponseDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class GooglePeopleClientTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private GooglePeopleClient client;

  @Before
  public void setUp() {
    ReflectionTestUtils.setField(client, "googleApiPeopleEndpoint", "https://api.example.com");
  }

  @Test
  public void verify_google_people_client_test() {
    when(restTemplate.getForObject(any(), eq(GoogleConnectionResponseDTO.class)))
            .thenReturn(new GoogleConnectionResponseDTO());

    client.getGoogleUserConnections("access_token");

    verify(restTemplate, times(1)).getForObject(any(), any());
  }
}
