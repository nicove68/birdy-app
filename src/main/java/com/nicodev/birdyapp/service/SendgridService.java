package com.nicodev.birdyapp.service;

import com.nicodev.birdyapp.client.SendgridClient;
import com.nicodev.birdyapp.model.BirdyEmail;
import com.nicodev.birdyapp.model.entity.Contact;
import com.nicodev.birdyapp.model.entity.User;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SendgridService {

    private static Logger logger = LoggerFactory.getLogger(SendgridService.class);

    private static final String WELCOME_EMAIL_TEMPLATE_ID = "d-c50ae6d301cc410aa8a2c1a3450e2f36";
    private static final String WELCOME_EMAIL_SUBJECT = "Bienvenido a Birdy";

    private static final String BYE_EMAIL_TEMPLATE_ID = "d-9fc1db4d4faf48e68a7a7f4250dfbff9";
    private static final String BYE_EMAIL_SUBJECT = "Hasta la próxima";

    private static final String BIRTHDAY_EMAIL_TEMPLATE_ID = "d-10244fbde11741dcb04676b1b5cc5da9";
    private static final String BIRTHDAY_EMAIL_SUBJECT = "Birdy - Cumpleaños %s.";

    private static final DateTimeFormatter SUBJECT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM");
    private static final DateTimeFormatter HEADER_MESSAGE_DATE_FORMATTER = DateTimeFormatter.ofPattern("E dd 'de' MMMM 'de' yyyy");

    @Value("${heroku.app.main-path}")
    private String herokuMainPath;

    private SendgridClient sendGridClient;
    private UserService userService;

    @Autowired
    public SendgridService(SendgridClient sendGridClient, UserService userService) {
        this.sendGridClient = sendGridClient;
        this.userService = userService;
    }

    public void sendWelcomeEmail(User user) {
        URI unsuscribeUri = getUnsuscribeLink(user.getEmail());
        BirdyEmail birdyEmail = new BirdyEmail.Builder()
            .templateId(WELCOME_EMAIL_TEMPLATE_ID)
            .subject(WELCOME_EMAIL_SUBJECT)
            .toName(user.getName())
            .toEmail(user.getEmail())
            .unsuscribeLink(unsuscribeUri.toString())
            .build();

        logger.info("Sending welcome email to " + user.getEmail());
        sendGridClient.sendEmail(birdyEmail);
    }

    public void sendByeEmail(String userEmail) {
        BirdyEmail birdyEmail = new BirdyEmail.Builder()
            .templateId(BYE_EMAIL_TEMPLATE_ID)
            .subject(BYE_EMAIL_SUBJECT)
            .toEmail(userEmail)
            .build();

        logger.info("Sending bye email to " + userEmail);
        sendGridClient.sendEmail(birdyEmail);
    }

    public void sendBirthdayEmail(User user, List<Contact> contacts) {
        List<String> contactNames = contacts.stream().map(Contact::getName).collect(Collectors.toList());
        LocalDate now = LocalDate.now();
        String subject = String.format(BIRTHDAY_EMAIL_SUBJECT, now.format(SUBJECT_DATE_FORMATTER));
        URI unsuscribeUri = getUnsuscribeLink(user.getEmail());
        BirdyEmail birdyEmail = new BirdyEmail.Builder()
            .templateId(BIRTHDAY_EMAIL_TEMPLATE_ID)
            .subject(subject)
            .toName(user.getName())
            .toEmail(user.getEmail())
            .birthdayNames(contactNames)
            .headerMessage(now.format(HEADER_MESSAGE_DATE_FORMATTER))
            .unsuscribeLink(unsuscribeUri.toString())
            .build();

        logger.info("Sending birthday email to " + user.getEmail());
        sendGridClient.sendEmail(birdyEmail);
    }

    private URI getUnsuscribeLink(String userEmail) {
        String data = userService.encryptUserEmail(userEmail);
        return UriComponentsBuilder
            .fromUriString(herokuMainPath)
            .path("/unsuscribe")
            .queryParam("data", data)
            .build().toUri();
    }
}