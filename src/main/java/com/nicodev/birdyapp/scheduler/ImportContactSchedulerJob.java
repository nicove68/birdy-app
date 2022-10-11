package com.nicodev.birdyapp.scheduler;

import java.util.List;

import com.nicodev.birdyapp.model.entity.User;
import com.nicodev.birdyapp.service.ContactService;
import com.nicodev.birdyapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ImportContactSchedulerJob {

    private static final Logger logger = LoggerFactory.getLogger(ImportContactSchedulerJob.class);

    // 21 PM UTC is equivalent to:
    // 18 PM in Argentina.
    // 23 PM in Spain.
    private static final String RUN_ALL_DAYS_AT_21_PM_UTC = "0 0 21 * * ?";

    private final UserService userService;

    private final ContactService contactService;

    @Autowired
    public ImportContactSchedulerJob(UserService userService, ContactService contactService) {
        this.userService = userService;
        this.contactService = contactService;
    }


    @Scheduled(cron = RUN_ALL_DAYS_AT_21_PM_UTC)
    public void updateContacts() {
        logger.info("Start job: import contacts");
        List<User> allUsers = userService.getAllUsers();
        allUsers.forEach(contactService::updateContacts);
        logger.info("End job: import contacts");
    }
}
