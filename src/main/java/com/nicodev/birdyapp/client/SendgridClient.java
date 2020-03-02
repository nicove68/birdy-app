package com.nicodev.birdyapp.client;

import com.nicodev.birdyapp.model.BirdyEmail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SendgridClient {

  private static Logger logger = LoggerFactory.getLogger(SendgridClient.class);

  private static final String FROM_EMAIL = "no-reply@birdyapp.herokuapp.com";
  private static final String FROM_NAME = "Birdy";

  @Value("${sendgrid.api.apikey}")
  private String sendgridApiKey;

  public void sendEmail(BirdyEmail birdyEmail) {
    Email from = new Email(FROM_EMAIL, FROM_NAME);
    Email to = new Email(birdyEmail.getToEmail(), birdyEmail.getToName());

    Personalization personalization = new Personalization();
    personalization.addTo(to);
    personalization.addDynamicTemplateData("user_name", birdyEmail.getToName());
    personalization.addDynamicTemplateData("subject", birdyEmail.getSubject());
    personalization.addDynamicTemplateData("header_message", birdyEmail.getHeaderMessage());
    personalization.addDynamicTemplateData("birthday_names", birdyEmail.getBirthdayNames());
    personalization.addDynamicTemplateData("unsubscribe_link", birdyEmail.getUnsubscribeLink());

    Mail mail = new Mail();
    mail.setFrom(from);
    mail.setTemplateId(birdyEmail.getTemplateId());
    mail.addPersonalization(personalization);

    SendGrid sg = new SendGrid(sendgridApiKey);
    Request request = new Request();

    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      sg.api(request);

    } catch (IOException ex) {

      logger.error("Sendgrid Api error: {}", ex.getMessage(), ex);
    }
  }
}
