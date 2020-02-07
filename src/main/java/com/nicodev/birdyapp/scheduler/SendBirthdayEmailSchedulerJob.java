package com.nicodev.birdyapp.scheduler;

import com.nicodev.birdyapp.model.entity.Contact;
import com.nicodev.birdyapp.model.entity.User;
import com.nicodev.birdyapp.service.BirthdayService;
import com.nicodev.birdyapp.service.SendgridService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SendBirthdayEmailSchedulerJob {

    private static Logger logger = LoggerFactory.getLogger(SendBirthdayEmailSchedulerJob.class);
    private static final String RUN_ALL_DAYS_AT_11_AM_UTC = "0 0 17 * * ?";

    private BirthdayService birthdayService;
    private SendgridService sendgridService;

    @Autowired
    public SendBirthdayEmailSchedulerJob(
        BirthdayService birthdayService,
        SendgridService sendgridService
    ) {
        this.birthdayService = birthdayService;
        this.sendgridService = sendgridService;
    }

    @Scheduled(cron = RUN_ALL_DAYS_AT_11_AM_UTC)
    public void sendBirthdayEmail() {
        logger.info("Start job: send birthday emails");

        LocalDate today = LocalDate.now();
        int day = today.getDayOfMonth();
        int month = today.getMonthValue();

        Map<User, List<Contact>> todayBirthdaysMap = birthdayService.getTodayBirthdays(day, month);

        todayBirthdaysMap.forEach((owner, contacts) -> {
            try {
                logger.info("Sending birthday email to {} with {} birthday contacts", owner.getEmail(), contacts.size());
                sendgridService.sendBirthdayEmail(owner, contacts);
            } catch (Exception ex) {
                logger.error("Fail when sending birthday email to {} with {} birthday contacts", owner.getEmail(), contacts.size(), ex);
            }
        });

        logger.info("End job: send birthday emails");
    }
}
