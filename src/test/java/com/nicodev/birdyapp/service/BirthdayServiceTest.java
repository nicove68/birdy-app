package com.nicodev.birdyapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.nicodev.birdyapp.TestUtils;
import com.nicodev.birdyapp.model.entity.Contact;
import com.nicodev.birdyapp.model.entity.User;
import com.nicodev.birdyapp.repository.ContactRepository;
import com.nicodev.birdyapp.repository.UserRepository;
import javax.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = "classpath:application-test.properties")
@DataMongoTest
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
class BirthdayServiceTest {

  @Resource
  private UserRepository userRepository;

  @Resource
  private ContactRepository contactRepository;

  private BirthdayService birthdayService;

  @BeforeEach
  void setUp() {
    birthdayService = new BirthdayService(userRepository, contactRepository);

    String jonName = "Jon Jonson";
    String melName = "Mel Melson";
    String kimName = "Kim Kimson";
    String lenName = "Len Lenson";

    User jon = TestUtils.getTestUser(jonName);
    User mel = TestUtils.getTestUser(melName);
    User kim = TestUtils.getTestUser(kimName);
    User len = TestUtils.getTestUser(lenName);

    List<User> allUsers = Arrays.asList(jon, mel, kim, len);

    List<Contact> jonContacts = TestUtils.getTestContacts(jonName, 6, 8, 3);
    List<Contact> melContacts = TestUtils.getTestContacts(melName, 6, 8, 1);
    List<Contact> kimContacts = TestUtils.getTestContacts(kimName, 6, 8, 2);
    List<Contact> lenContacts = TestUtils.getTestContacts(lenName, 14, 12, 2);

    List<Contact> allContacts = new ArrayList<>();
    allContacts.addAll(jonContacts);
    allContacts.addAll(melContacts);
    allContacts.addAll(kimContacts);
    allContacts.addAll(lenContacts);

    userRepository.saveAll(allUsers);
    contactRepository.saveAll(allContacts);
  }

  @BeforeEach
  public void init() {
    contactRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void when_get_today_birthdays_check_map_of_owner_and_contacts() {
    int day = 6;
    int month = 8;
    Map<User, List<Contact>> todayBirthdaysMap = birthdayService.getTodayBirthdays(day, month);

    assertEquals(3, todayBirthdaysMap.keySet().size());
    todayBirthdaysMap.forEach((owner, contacts) -> {
      if (owner.getEmail().equals("jonjonson@test.com"))
        assertEquals(3, contacts.size());

      if (owner.getEmail().equals("melmelson@test.com"))
        assertEquals(1, contacts.size());

      if (owner.getEmail().equals("kimkimson@test.com"))
        assertEquals(2, contacts.size());
    });

    contactRepository.deleteAll();
    userRepository.deleteAll();
  }
}
