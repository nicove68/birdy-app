package com.nicodev.birdyapp.controller;

import com.nicodev.birdyapp.model.entity.User;
import com.nicodev.birdyapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImportContactsController {

    private UserService userService;

    @Autowired
    public ImportContactsController(UserService userService) {
        this.userService = userService;
    }



    @GetMapping("/oauth/google")
    public void createBirdyUserAndImportContacts(
            @RequestParam(value = "code") String googleAuthCode
    ) {


        User user = userService.createUser(googleAuthCode);


    }


}
