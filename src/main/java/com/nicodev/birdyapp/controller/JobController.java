package com.nicodev.birdyapp.controller;

import java.util.List;

import com.nicodev.birdyapp.exception.rest.BadRequestException;
import com.nicodev.birdyapp.model.entity.User;
import com.nicodev.birdyapp.service.ContactService;
import com.nicodev.birdyapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

  private static final Logger logger = LoggerFactory.getLogger(JobController.class);

  @Value("${birdy.api.key}")
  private String birdyApiKey;

  private final UserService userService;

  private final ContactService contactService;

  @Autowired
  public JobController(
      UserService userService,
      ContactService contactService
  ) {
    this.userService = userService;
    this.contactService = contactService;
  }

  @PostMapping("/import_contacts")
  public void importContactsManually(
      @RequestParam(value = "apikey") String apiKey
  ) {
    if (apiKey.isEmpty() || !apiKey.equals(birdyApiKey))
      throw new BadRequestException("Wrong apikey!");

    logger.info("Start job: import contacts manually");
    List<User> allUsers = userService.getAllUsers();
    allUsers.forEach(contactService::updateContacts);
    logger.info("End job: import contacts manually");
  }
}