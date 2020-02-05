package com.nicodev.birdyapp.scheduler;

import com.nicodev.birdyapp.model.entity.User;
import com.nicodev.birdyapp.service.ContactService;
import com.nicodev.birdyapp.service.UserService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ImportContactSchedulerJob {

    private static Logger logger = LoggerFactory.getLogger(ImportContactSchedulerJob.class);
    private static final String RUN_ALL_DAYS_AT_6_AM_UTC = "0 0 6 * * ?";

    private UserService userService;
    private ContactService contactService;

    @Autowired
    public ImportContactSchedulerJob(UserService userService, ContactService contactService) {
        this.userService = userService;
        this.contactService = contactService;
    }


    @Scheduled(cron = RUN_ALL_DAYS_AT_6_AM_UTC)
    public void updateContacts() {
        logger.info("Start job: import contacts");
        List<User> allUsers = userService.getAllUsers();
        allUsers.forEach(user -> contactService.updateContacts(user));
        logger.info("End job: import contacts");
    }
}
