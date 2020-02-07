package com.nicodev.birdyapp.service;

import com.nicodev.birdyapp.client.SendgridClient;
import com.nicodev.birdyapp.model.BirdyEmail;
import com.nicodev.birdyapp.model.entity.Contact;
import com.nicodev.birdyapp.model.entity.User;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
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

    private static final String WELCOME_EMAIL_TEMPLATE_ID = "d-baf1a04e25c04e6c86947cbacf52bff7";
    private static final String WELCOME_EMAIL_SUBJECT = "Bienvenido a Birdy";

    private static final String BYE_EMAIL_TEMPLATE_ID = "d-610c62bc91484209a7b26d34880d88e4";
    private static final String BYE_EMAIL_SUBJECT = "Hasta la próxima";

    private static final String BIRTHDAY_EMAIL_TEMPLATE_ID = "d-0762b5a257ff477aafcdc3bfa87a2b1f";
    private static final String BIRTHDAY_EMAIL_SUBJECT = "Birdy - Cumpleaños %s.";

    private static final DateTimeFormatter SUBJECT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM").withLocale(new Locale("es","AR"));
    private static final DateTimeFormatter HEADER_MESSAGE_DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy").withLocale(new Locale("es","AR"));

    @Value("${heroku.app.main-path}")
    private String herokuMainPath;

    private SendgridClient sendGridClient;

    @Autowired
    public SendgridService(SendgridClient sendGridClient) {
        this.sendGridClient = sendGridClient;
    }

    public void sendWelcomeEmail(User user) {
        URI unsubscribeUri = getUnsubscribeLink(user);
        BirdyEmail birdyEmail = new BirdyEmail.Builder()
            .templateId(WELCOME_EMAIL_TEMPLATE_ID)
            .subject(WELCOME_EMAIL_SUBJECT)
            .toName(user.getName())
            .toEmail(user.getEmail())
            .unsubscribeLink(unsubscribeUri.toString())
            .build();

        logger.info("Sending welcome email to " + user.getEmail());
        sendGridClient.sendEmail(birdyEmail);
    }

    public void sendByeEmail(User user) {
        BirdyEmail birdyEmail = new BirdyEmail.Builder()
            .templateId(BYE_EMAIL_TEMPLATE_ID)
            .subject(BYE_EMAIL_SUBJECT)
            .toEmail(user.getEmail())
            .build();

        logger.info("Sending bye email to " + user.getEmail());
        sendGridClient.sendEmail(birdyEmail);
    }

    public void sendBirthdayEmail(User user, List<Contact> contacts) {
        List<String> contactNames = contacts.stream().map(Contact::getName).collect(Collectors.toList());
        LocalDate now = LocalDate.now();
        String subject = String.format(BIRTHDAY_EMAIL_SUBJECT, now.format(SUBJECT_DATE_FORMATTER));
        URI unsubscribeUri = getUnsubscribeLink(user);
        BirdyEmail birdyEmail = new BirdyEmail.Builder()
            .templateId(BIRTHDAY_EMAIL_TEMPLATE_ID)
            .subject(subject)
            .toName(user.getName())
            .toEmail(user.getEmail())
            .birthdayNames(contactNames)
            .headerMessage(now.format(HEADER_MESSAGE_DATE_FORMATTER))
            .unsubscribeLink(unsubscribeUri.toString())
            .build();

        logger.info("Sending birthday email to " + user.getEmail());
        sendGridClient.sendEmail(birdyEmail);
    }

    private URI getUnsubscribeLink(User user) {
        String key = user.getId()+"&"+user.getEmail();
        String data = Base64.getUrlEncoder().encodeToString(key.getBytes());
        return UriComponentsBuilder
            .fromUriString(herokuMainPath)
            .path("/unsubscribe")
            .queryParam("data", data)
            .build().toUri();
    }
}
