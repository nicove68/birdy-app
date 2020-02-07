package com.nicodev.birdyapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.nicodev.birdyapp.model.entity.Contact;
import com.nicodev.birdyapp.model.entity.User;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
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

  private static String nameToEmail(String name) {
    return name.replaceAll("\\s+","").toLowerCase()+"@test.com";
  }

  public static User getTestUser(String name) {
    return new User(
        name,
        nameToEmail(name),
        "access_token",
        "refresh_token",
        "2020-01-17T11:07:56Z"
    );
  }

  public static List<Contact> getTestContacts(String ownerName, int dayOfBirth, int monthOfBirth, int quantity) {
    List<Contact> contactList = new ArrayList<>();
    for (int i = 0; i < quantity; i++) {
      Contact contact = new Contact(
          nameToEmail(ownerName),
          RandomStringUtils.randomAlphabetic(10),
          "photo",
          dayOfBirth,
          monthOfBirth,
          "person_id",
          "2020-01-17T11:07:56Z"
      );
      contactList.add(contact);
    }
    return contactList;
  }
}
