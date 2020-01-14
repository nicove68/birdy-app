package com.nicodev.birdyapp.client;

import com.nicodev.birdyapp.client.dto.GoogleUserInfoDTO;
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
public class GoogleUserInfoClientTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private GoogleUserInfoClient client;

  @Before
  public void setUp() {
    ReflectionTestUtils.setField(client, "googleApiUserInfoEndpoint", "https://api.example.com");
  }

  @Test
  public void verify_google_user_info_client_test() {
    when(restTemplate.getForObject(any(), eq(GoogleUserInfoDTO.class)))
            .thenReturn(new GoogleUserInfoDTO());

    client.getGoogleUserInfo("access_token");

    verify(restTemplate, times(1)).getForObject(any(), any());
  }
}
