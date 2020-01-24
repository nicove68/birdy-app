package com.nicodev.birdyapp.service;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.nicodev.birdyapp.client.GoogleOAuthClient;
import com.nicodev.birdyapp.client.GooglePeopleClient;
import com.nicodev.birdyapp.model.dto.GoogleConnectionResponseDTO;
import com.nicodev.birdyapp.model.entity.Contact;
import com.nicodev.birdyapp.model.entity.User;
import com.nicodev.birdyapp.repository.ContactRepository;
import com.nicodev.birdyapp.util.TestUtils;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@DataMongoTest
public class ContactServiceTest {

  @Mock
  private GooglePeopleClient googlePeopleClient;

  @Mock
  private GoogleOAuthClient googleOAuthClient;

  @Resource
  private ContactRepository contactRepository;

  private ObjectMapper objectMapperCamelCase;
  private ContactService contactService;
  private User user;

  @Before
  public void setUp() {
    contactService = new ContactService(googlePeopleClient, googleOAuthClient, contactRepository);
    user = TestUtils.getTestUser();
    objectMapperCamelCase = new ObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setSerializationInclusion(Include.NON_NULL)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .registerModule(new Jdk8Module());;
  }

  @Before
  public void init() {
    contactRepository.deleteAll();
  }

  @Test
  public void when_people_connection_has_all_fields_then_save_contact() {
    String stringResponse = TestUtils.readFile("google-people-api-json-responses/connection_with_all_fields.json");
    GoogleConnectionResponseDTO googleConnectionResponse = TestUtils.stringToObject(stringResponse, GoogleConnectionResponseDTO.class, objectMapperCamelCase);

    when(googlePeopleClient.getGoogleUserConnections(anyString())).thenReturn(googleConnectionResponse);

    List<Contact> contacts = contactService.createContacts(user);
    Contact theContact = contacts.get(0);

    Assert.assertNotNull(theContact.getOwnerEmail());
    Assert.assertNotNull(theContact.getName());
    Assert.assertNotNull(theContact.getPhotoUrl());
    Assert.assertEquals(4, theContact.getDayOfBirth());
    Assert.assertEquals(6, theContact.getMonthOfBirth());
    Assert.assertNotNull(theContact.getGooglePersonId());

    List<Contact> savedContacts = contactRepository.findAll();
    Assert.assertEquals(1, savedContacts.size());
  }

  @Test
  public void when_people_connection_has_not_birthday_day_then_discard_contact() {
    String stringResponse = TestUtils.readFile("google-people-api-json-responses/connection_without_birthday_day.json");
    GoogleConnectionResponseDTO googleConnectionResponse = TestUtils.stringToObject(stringResponse, GoogleConnectionResponseDTO.class, objectMapperCamelCase);

    when(googlePeopleClient.getGoogleUserConnections(anyString())).thenReturn(googleConnectionResponse);

    List<Contact> contacts = contactService.createContacts(user);

    Assert.assertEquals(0, contacts.size());
  }

  @Test
  public void when_people_connection_has_not_birthday_then_discard_contact() {
    String stringResponse = TestUtils.readFile("google-people-api-json-responses/connection_without_birthdays.json");
    GoogleConnectionResponseDTO googleConnectionResponse = TestUtils.stringToObject(stringResponse, GoogleConnectionResponseDTO.class, objectMapperCamelCase);

    when(googlePeopleClient.getGoogleUserConnections(anyString())).thenReturn(googleConnectionResponse);

    List<Contact> contacts = contactService.createContacts(user);

    Assert.assertEquals(0, contacts.size());
  }

  @Test
  public void when_people_connection_has_not_name_then_discard_contact() {
    String stringResponse = TestUtils.readFile("google-people-api-json-responses/connection_without_names.json");
    GoogleConnectionResponseDTO googleConnectionResponse = TestUtils.stringToObject(stringResponse, GoogleConnectionResponseDTO.class, objectMapperCamelCase);

    when(googlePeopleClient.getGoogleUserConnections(anyString())).thenReturn(googleConnectionResponse);

    List<Contact> contacts = contactService.createContacts(user);

    Assert.assertEquals(0, contacts.size());
  }

  @Test
  public void when_people_connection_has_not_photos_then_return_contact_with_default_photo() {
    String stringResponse = TestUtils.readFile("google-people-api-json-responses/connection_without_photos.json");
    GoogleConnectionResponseDTO googleConnectionResponse = TestUtils.stringToObject(stringResponse, GoogleConnectionResponseDTO.class, objectMapperCamelCase);

    when(googlePeopleClient.getGoogleUserConnections(anyString())).thenReturn(googleConnectionResponse);

    List<Contact> contacts = contactService.createContacts(user);
    Contact theContact = contacts.get(0);

    Assert.assertNotNull(theContact.getOwnerEmail());
    Assert.assertNotNull(theContact.getName());
    Assert.assertNotNull(theContact.getPhotoUrl());
    Assert.assertEquals("empty_profile_url", theContact.getPhotoUrl());
    Assert.assertEquals(4, theContact.getDayOfBirth());
    Assert.assertEquals(6, theContact.getMonthOfBirth());
    Assert.assertNotNull(theContact.getGooglePersonId());

    List<Contact> savedContacts = contactRepository.findAll();

    Assert.assertEquals(1, savedContacts.size());
  }
}
