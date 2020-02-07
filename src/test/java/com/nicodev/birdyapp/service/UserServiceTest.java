package com.nicodev.birdyapp.service;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
import java.util.Base64;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@DataMongoTest
public class UserServiceTest {

  @Mock
  private GoogleOAuthClient googleOAuthClient;

  @Mock
  private GoogleUserInfoClient googleUserInfoClient;

  @Resource
  private UserRepository userRepository;

  private ObjectMapper objectMapperSnakeCase;
  private UserService userService;
  private User user;

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Before
  public void setUp() {
    userService = new UserService(googleOAuthClient, googleUserInfoClient, userRepository);
    objectMapperSnakeCase = new ObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setSerializationInclusion(Include.NON_NULL)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .registerModule(new Jdk8Module());
    user = TestUtils.getTestUser();
  }

  @Before
  public void init() {
    userRepository.deleteAll();
  }

  @Test
  public void when_google_oauth_and_userinfo_response_are_ok_then_save_user() {
    String oauthTokenStringResponse = TestUtils.readFile("google-oauth-api-json-responses/first_oauth_token.json");
    GoogleOAuthTokenDTO googleOAuthToken = TestUtils.stringToObject(oauthTokenStringResponse, GoogleOAuthTokenDTO.class, objectMapperSnakeCase);

    when(googleOAuthClient.getGoogleOAuthToken(anyString())).thenReturn(googleOAuthToken);

    String userInfoStringResponse = TestUtils.readFile("google-userinfo-api-json-responses/userinfo_with_all_fields.json");
    GoogleUserInfoDTO googleUserInfo = TestUtils.stringToObject(userInfoStringResponse, GoogleUserInfoDTO.class, objectMapperSnakeCase);

    when(googleUserInfoClient.getGoogleUserInfo(anyString())).thenReturn(googleUserInfo);

    User user = userService.createUser("google_auth_code");

    Assert.assertNotNull(user.getName());
    Assert.assertNotNull(user.getEmail());
    Assert.assertNotNull(user.getGoogleAccessToken());
    Assert.assertNotNull(user.getGoogleRefreshToken());

    List<User> savedUsers = userRepository.findAll();
    Assert.assertEquals(1, savedUsers.size());
  }


  @Test
  public void when_user_already_exists_then_throw_bad_request_exception() {
    String oauthTokenStringResponse = TestUtils.readFile("google-oauth-api-json-responses/first_oauth_token.json");
    GoogleOAuthTokenDTO googleOAuthToken = TestUtils.stringToObject(oauthTokenStringResponse, GoogleOAuthTokenDTO.class, objectMapperSnakeCase);

    when(googleOAuthClient.getGoogleOAuthToken(anyString())).thenReturn(googleOAuthToken);

    String userInfoStringResponse = TestUtils.readFile("google-userinfo-api-json-responses/userinfo_with_all_fields.json");
    GoogleUserInfoDTO googleUserInfo = TestUtils.stringToObject(userInfoStringResponse, GoogleUserInfoDTO.class, objectMapperSnakeCase);

    when(googleUserInfoClient.getGoogleUserInfo(anyString())).thenReturn(googleUserInfo);

    exceptionRule.expect(BadRequestException.class);
    exceptionRule.expectMessage("User is already registered");

    userService.createUser("google_auth_code");
    userService.createUser("google_auth_code");
  }

  @Test
  public void when_delete_user_then_repository_must_be_empty() {
    String oauthTokenStringResponse = TestUtils.readFile("google-oauth-api-json-responses/first_oauth_token.json");
    GoogleOAuthTokenDTO googleOAuthToken = TestUtils.stringToObject(oauthTokenStringResponse, GoogleOAuthTokenDTO.class, objectMapperSnakeCase);

    when(googleOAuthClient.getGoogleOAuthToken(anyString())).thenReturn(googleOAuthToken);

    String userInfoStringResponse = TestUtils.readFile("google-userinfo-api-json-responses/userinfo_with_all_fields.json");
    GoogleUserInfoDTO googleUserInfo = TestUtils.stringToObject(userInfoStringResponse, GoogleUserInfoDTO.class, objectMapperSnakeCase);

    when(googleUserInfoClient.getGoogleUserInfo(anyString())).thenReturn(googleUserInfo);

    doNothing().when(googleOAuthClient).revokeGoogleOAuthToken(anyString());

    User user = userService.createUser("google_auth_code");

    List<User> savedUsers = userRepository.findAll();
    Assert.assertEquals(savedUsers.get(0).getEmail(), "test@test.com");
    Assert.assertEquals(1, savedUsers.size());

    userService.deleteUser(user);

    savedUsers = userRepository.findAll();
    Assert.assertEquals(0, savedUsers.size());
  }

  @Test
  public void when_find_unsubscribe_user_with_valid_request_data_then_get_unsubscribe_user() {
    userRepository.insert(user);

    String userId = user.getId();
    String userEmail= user.getEmail();
    String key = userId+"&"+userEmail;
    String data = Base64.getUrlEncoder().encodeToString(key.getBytes());

    User unsubscribeUser = userService.getUserForUnsubscribe(data);

    Assert.assertNotNull(unsubscribeUser);
  }

  @Test
  public void when_find_unsubscribe_user_with_invalid_request_data_then_throw_bad_request_exception() {
    exceptionRule.expect(BadRequestException.class);
    exceptionRule.expectMessage("Failed when validate data: potato");

    String data = "potato";

    userService.getUserForUnsubscribe(data);
  }

  @Test
  public void when_send_valid_request_data_and_inexistent_user_for_unsubscribe_then_throw_not_found_exception() {
    exceptionRule.expect(NotFoundException.class);
    exceptionRule.expectMessage("User not exists");

    String key = "123&notexists";
    String data = Base64.getUrlEncoder().encodeToString(key.getBytes());

    userService.getUserForUnsubscribe(data);
  }
}
