package com.nicodev.birdyapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.nicodev.birdyapp.exception.rest.InternalServerException;
import com.nicodev.birdyapp.model.entity.Contact;
import com.nicodev.birdyapp.model.entity.User;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SendgridService {

    private static final Logger logger = LoggerFactory.getLogger(SendgridService.class);

    private static final String FROM_EMAIL = "nicodev68@gmail.com";
    private static final String FROM_NAME = "Birdy";

    private static final String WELCOME_EMAIL_TEMPLATE_NAME = "email_welcome.html";
    private static final String WELCOME_EMAIL_SUBJECT = "Bienvenido a Birdy";

    private static final String BYE_EMAIL_TEMPLATE_NAME = "email_bye.html";
    private static final String BYE_EMAIL_SUBJECT = "Hasta la próxima";

    private static final String BIRTHDAY_EMAIL_TEMPLATE_NAME = "email_birthday.html";
    private static final String BIRTHDAY_EMAIL_SUBJECT = "Birdy - Cumpleaños %s.";

    private static final String TAG_USER_NAME = "{{user_name}}";
    private static final String TAG_UNSUBSCRIBE_LINK = "{{unsubscribe_link}}";
    private static final String TAG_HEADER_MESSAGE = "{{header_message}}";
    private static final String TAG_BIRTHDAY_NAMES = "{{birthday_names}}";



    private static final DateTimeFormatter SUBJECT_DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMM").withLocale(new Locale("es","AR"));
    private static final DateTimeFormatter HEADER_MESSAGE_DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy").withLocale(new Locale("es","AR"));

    @Value("${app.main-path}")
    private String appMainPath;

    private final SendGrid sendGridClient;

    @Autowired
    public SendgridService(SendGrid sendGridClient) {
        this.sendGridClient = sendGridClient;
    }

    public void sendEmail(List<String> recipients, String subject, String text) {
        Content content = new Content("text/html", text);
        Email senderEmail = new Email(FROM_EMAIL, FROM_NAME);

        Mail mail = new Mail();
        Personalization personalization = new Personalization();

        addRecipients(personalization, recipients);

        mail.addPersonalization(personalization);
        mail.setFrom(senderEmail);
        mail.setSubject(subject);
        mail.addContent(content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGridClient.api(request);

            if (!HttpStatus.valueOf(response.getStatusCode()).is2xxSuccessful()) {
                logger.error(response.getBody());
                throw new InternalServerException(response.getBody());
            }

            logger.info("Sendgrid api send email to {} with status: {}", recipients, response.getStatusCode());
        } catch (IOException ex) {
            logger.error("Sendgrid api fail when send email to {}", recipients, ex);
            throw new InternalServerException("Sendgrid fail when send email to " + recipients);
        }
    }

    private void addRecipients(Personalization personalization, List<String> recipients) {
        if (recipients != null && !recipients.isEmpty()) {
            for (String to : recipients) {
                personalization.addTo(new Email(to));
            }
        }
    }

    public void sendWelcomeEmail(User user) {
        URI unsubscribeUri = getUnsubscribeLink(user);
        String template = loadTemplate(WELCOME_EMAIL_TEMPLATE_NAME);
        String text = template
            .replace(TAG_USER_NAME, user.getName())
            .replace(TAG_UNSUBSCRIBE_LINK, unsubscribeUri.toString());

        logger.info("Sending welcome email to {}", user.getEmail());
        sendEmail(Collections.singletonList(user.getEmail()), WELCOME_EMAIL_SUBJECT, text);
    }

    public void sendByeEmail(User user) {
        String template = loadTemplate(BYE_EMAIL_TEMPLATE_NAME);

        logger.info("Sending bye email to {}", user.getEmail());
        sendEmail(Collections.singletonList(user.getEmail()), BYE_EMAIL_SUBJECT, template);
    }

    public void sendBirthdayEmail(User user, List<Contact> contacts) {
        List<String> contactNames = contacts.stream().map(Contact::getName).collect(Collectors.toList());
        StringBuilder contactRows = new StringBuilder();
        contactNames.forEach(name -> {
            contactRows.append("<p align=\"center\" style=\"background: #f9f9f9; padding: 20px; border-right: 3px solid #dc3545; "
                + "border-left: 3px solid #dc3545; font: 20px Arial, sans-serif; color: #333333; margin: 0; margin-bottom: 5px; "
                + "text-transform: uppercase;\">");
            contactRows.append(name);
            contactRows.append("</p>");
        });

        LocalDate now = LocalDate.now();
        String subject = String.format(BIRTHDAY_EMAIL_SUBJECT, now.format(SUBJECT_DATE_FORMATTER));
        URI unsubscribeUri = getUnsubscribeLink(user);
        String template = loadTemplate(BIRTHDAY_EMAIL_TEMPLATE_NAME);
        String text = template
            .replace(TAG_HEADER_MESSAGE, now.format(HEADER_MESSAGE_DATE_FORMATTER))
            .replace(TAG_BIRTHDAY_NAMES, contactRows.toString())
            .replace(TAG_UNSUBSCRIBE_LINK, unsubscribeUri.toString());

        logger.info("Sending birthday email to {}", user.getEmail());
        sendEmail(Collections.singletonList(user.getEmail()), subject, text);
    }

    private URI getUnsubscribeLink(User user) {
        String key = user.getId()+"&"+user.getEmail();
        String data = Base64.getUrlEncoder().encodeToString(key.getBytes());
        return UriComponentsBuilder
            .fromUriString(appMainPath)
            .path("/unsubscribe")
            .queryParam("data", data)
            .build().toUri();
    }

    private static String loadTemplate(String templateName) {
        try {
            Resource resource = new ClassPathResource("templates/" + templateName);
            InputStream resourceInputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceInputStream));

            StringBuilder builder = new StringBuilder();
            String currentLine = reader.readLine();
            while (currentLine != null) {
                builder.append(currentLine);
                builder.append("\n");
                currentLine = reader.readLine();
            }

            reader.close();

            return builder.toString();

        } catch (IOException ex) {
            throw new InternalServerException("Unable to load template name: " + templateName);
        }
    }
}
