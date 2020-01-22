package com.nicodev.birdyapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.nicodev.birdyapp.model.entity.User;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

import static org.mockito.Mockito.when;

public class TestUtils {

  public static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

  @SuppressWarnings("UnstableApiUsage")
  public static String readFile(String file) {
    URL url = Resources.getResource(file);
    try {
      return Resources.toString(url, Charsets.UTF_8);
    } catch (IOException e) {
      logger.error("TestUtils:readFile::exceptionMessage={}", e.getMessage());
      return null;
    }
  }

  public static <T> T stringToObject(String data, Class<T> type, ObjectMapper mapper) {
    try {
      return mapper.readValue(data, type);
    } catch (IOException e) {
      logger.error("TestUtils:stringToObject::exceptionMessage={}", e.getMessage());
      return null;
    }
  }

  public static User getTestUser() {
    User user = Mockito.mock(User.class);
    when(user.getId()).thenReturn("123");
    when(user.getName()).thenReturn("Birdy App");
    when(user.getEmail()).thenReturn("test@test.com");
    when(user.getGoogleAccessToken()).thenReturn("access_token");
    when(user.getGoogleRefreshToken()).thenReturn("refresh_token");
    when(user.getCreatedAt()).thenReturn("2020-01-17T11:07:56Z");
    return user;
  }
}
