package com.nicodev.birdyapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.nicodev.birdyapp.TestUtils;
import com.nicodev.birdyapp.client.GoogleOAuthClient;
import com.nicodev.birdyapp.client.GoogleUserInfoClient;
import com.nicodev.birdyapp.exception.rest.BadRequestException;
import com.nicodev.birdyapp.exception.rest.NotFoundException;
import com.nicodev.birdyapp.model.dto.GoogleOAuthTokenDTO;
import com.nicodev.birdyapp.model.dto.GoogleUserInfoDTO;
import com.nicodev.birdyapp.model.entity.User;
import com.nicodev.birdyapp.repository.UserRepository;
import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = "classpath:application-test.properties")
@DataMongoTest
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
class UserServiceTest {

  @Mock
  private GoogleOAuthClient googleOAuthClient;

  @Mock
  private GoogleUserInfoClient googleUserInfoClient;

  @Resource
  private UserRepository userRepository;

  private ObjectMapper objectMapperSnakeCase;
  private UserService userService;
  private User user;

  @BeforeEach
  void setUp() {
    userService = new UserService(googleOAuthClient, googleUserInfoClient, userRepository);
    objectMapperSnakeCase = new ObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setSerializationInclusion(Include.NON_NULL)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .registerModule(new Jdk8Module());
    user = TestUtils.getTestUser("Test");
  }

  @BeforeEach
  void init() {
    userRepository.deleteAll();
  }

  @Test
  void when_google_oauth_and_userinfo_response_are_ok_then_save_user() {
    ClassLoader classLoader = getClass().getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream("google-oauth-api-json-responses/first_oauth_token.json");
    String oauthTokenStringResponse = TestUtils.readFromInputStream(inputStream);

    GoogleOAuthTokenDTO googleOAuthToken = TestUtils.stringToObject(oauthTokenStringResponse, GoogleOAuthTokenDTO.class, objectMapperSnakeCase);

    when(googleOAuthClient.getGoogleOAuthToken(anyString())).thenReturn(googleOAuthToken);

    InputStream inputStream2 = classLoader.getResourceAsStream("google-userinfo-api-json-responses/userinfo_with_all_fields.json");
    String userInfoStringResponse = TestUtils.readFromInputStream(inputStream2);

    GoogleUserInfoDTO googleUserInfo = TestUtils.stringToObject(userInfoStringResponse, GoogleUserInfoDTO.class, objectMapperSnakeCase);

    when(googleUserInfoClient.getGoogleUserInfo(anyString())).thenReturn(googleUserInfo);

    User user = userService.createUser("google_auth_code");

    assertNotNull(user.getName());
    assertNotNull(user.getEmail());
    assertNotNull(user.getGoogleAccessToken());
    assertNotNull(user.getGoogleRefreshToken());

    List<User> savedUsers = userRepository.findAll();
    assertEquals(1, savedUsers.size());
  }


  @Test
  void when_user_already_exists_then_throw_bad_request_exception() {
    ClassLoader classLoader = getClass().getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream("google-oauth-api-json-responses/first_oauth_token.json");
    String oauthTokenStringResponse = TestUtils.readFromInputStream(inputStream);
    GoogleOAuthTokenDTO googleOAuthToken = TestUtils.stringToObject(oauthTokenStringResponse, GoogleOAuthTokenDTO.class, objectMapperSnakeCase);

    when(googleOAuthClient.getGoogleOAuthToken(anyString())).thenReturn(googleOAuthToken);

    InputStream inputStream2 = classLoader.getResourceAsStream("google-userinfo-api-json-responses/userinfo_with_all_fields.json");
    String userInfoStringResponse = TestUtils.readFromInputStream(inputStream2);
    GoogleUserInfoDTO googleUserInfo = TestUtils.stringToObject(userInfoStringResponse, GoogleUserInfoDTO.class, objectMapperSnakeCase);

    when(googleUserInfoClient.getGoogleUserInfo(anyString())).thenReturn(googleUserInfo);

    userService.createUser("google_auth_code");

    BadRequestException thrown = Assertions.assertThrows(BadRequestException.class,
        () -> userService.createUser("google_auth_code"));
    Assertions.assertEquals("User is already registered", thrown.getResponse().getBody());
  }

  @Test
  void when_delete_user_then_repository_must_be_empty() {
    ClassLoader classLoader = getClass().getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream("google-oauth-api-json-responses/first_oauth_token.json");
    String oauthTokenStringResponse = TestUtils.readFromInputStream(inputStream);
    GoogleOAuthTokenDTO googleOAuthToken = TestUtils.stringToObject(oauthTokenStringResponse, GoogleOAuthTokenDTO.class, objectMapperSnakeCase);

    when(googleOAuthClient.getGoogleOAuthToken(anyString())).thenReturn(googleOAuthToken);

    InputStream inputStream2 = classLoader.getResourceAsStream("google-userinfo-api-json-responses/userinfo_with_all_fields.json");
    String userInfoStringResponse = TestUtils.readFromInputStream(inputStream2);
    GoogleUserInfoDTO googleUserInfo = TestUtils.stringToObject(userInfoStringResponse, GoogleUserInfoDTO.class, objectMapperSnakeCase);

    when(googleUserInfoClient.getGoogleUserInfo(anyString())).thenReturn(googleUserInfo);

    doNothing().when(googleOAuthClient).revokeGoogleOAuthToken(anyString());

    User user = userService.createUser("google_auth_code");

    List<User> savedUsers = userRepository.findAll();
    assertEquals(savedUsers.get(0).getEmail(), "test@test.com");
    assertEquals(1, savedUsers.size());

    userService.deleteUser(user);

    savedUsers = userRepository.findAll();
    assertEquals(0, savedUsers.size());
  }

  @Test
  void when_find_unsubscribe_user_with_valid_request_data_then_get_unsubscribe_user() {
    userRepository.insert(user);

    String userId = user.getId();
    String userEmail= user.getEmail();
    String key = userId+"&"+userEmail;
    String data = Base64.getUrlEncoder().encodeToString(key.getBytes());

    User unsubscribeUser = userService.getUserForUnsubscribe(data);

    assertNotNull(unsubscribeUser);
  }

  @Test
  void when_find_unsubscribe_user_with_invalid_request_data_then_throw_bad_request_exception() {
    String data = "potato";

    BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> userService.getUserForUnsubscribe(data));
    Assertions.assertEquals("Failed when validate data: potato", thrown.getResponse().getBody());
  }

  @Test
  void when_send_valid_request_data_and_inexistent_user_for_unsubscribe_then_throw_not_found_exception() {
    String key = "123&notexists";
    String data = Base64.getUrlEncoder().encodeToString(key.getBytes());

    NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> userService.getUserForUnsubscribe(data));
    Assertions.assertEquals("User not exists", thrown.getResponse().getBody());
  }
}
