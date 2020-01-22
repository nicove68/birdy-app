package com.nicodev.birdyapp.controller;

import com.nicodev.birdyapp.model.entity.Contact;
import com.nicodev.birdyapp.model.entity.User;
import com.nicodev.birdyapp.service.ContactService;
import com.nicodev.birdyapp.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImportContactsController {

    private UserService userService;
    private ContactService contactService;

    @Autowired
    public ImportContactsController(UserService userService, ContactService contactService) {
        this.userService = userService;
        this.contactService = contactService;
    }


    @GetMapping("/oauth/google")
    public void createBirdyUserAndImportContacts(
        @RequestParam(value = "code") String googleAuthCode
    ) {

        User user = userService.createUser(googleAuthCode);
        List<Contact> contacts = contactService.createContacts(user);

    }


}
