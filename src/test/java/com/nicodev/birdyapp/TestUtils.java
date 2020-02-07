package com.nicodev.birdyapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.nicodev.birdyapp.model.entity.User;
import java.io.IOException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    return new User(
        "Birdy App",
        "test@test.com",
        "access_token",
        "refresh_token",
        "2020-01-17T11:07:56Z"
    );
  }
}
