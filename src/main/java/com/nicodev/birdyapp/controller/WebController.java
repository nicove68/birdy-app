package com.nicodev.birdyapp.controller;

import com.nicodev.birdyapp.model.entity.Contact;
import com.nicodev.birdyapp.model.entity.User;
import com.nicodev.birdyapp.service.ContactService;
import com.nicodev.birdyapp.service.UserService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

  private static Logger logger = LoggerFactory.getLogger(WebController.class);

  private static final String MSG_OK_TITLE = "Yey!";
  private static final String MSG_OK_PRIMARY = "¡%s agendamos tus %s cumpleaños!";
  private static final String MSG_OK_SECONDARY = "Recibirás los recordatorios en tu correo %s";

  private static final String MSG_ERROR_TITLE = "Ups!";
  private static final String MSG_ERROR_PRIMARY = "Tuvimos un problema";
  private static final String MSG_ERROR_SECONDARY = "Intenta conectarte más tarde por favor";

  private static final String MSG_LINK_TEXT_TO_HOME = "Volver a la home";
  private static final String MSG_LINK_URL_TO_HOME = "home";

  private UserService userService;
  private ContactService contactService;

  @Autowired
  public WebController(UserService userService, ContactService contactService) {
    this.userService = userService;
    this.contactService = contactService;
  }


  @GetMapping(value = {"/", "/home"})
  public String showHome() {
    return "home";
  }

  @GetMapping("/import_contacts")
  public String importContacts(
      @RequestParam(value = "code") String googleAuthCode,
      Model model
  ) {
    try {
      User user = userService.createUser(googleAuthCode);
      List<Contact> contacts = contactService.createContacts(user);

      String primaryText = String.format(MSG_OK_PRIMARY, user.getName(), contacts.size());
      String secondaryText = String.format(MSG_OK_SECONDARY, user.getEmail());

      model.addAttribute("title", MSG_OK_TITLE);
      model.addAttribute("primary", primaryText);
      model.addAttribute("secondary", secondaryText);
      model.addAttribute("link_text", MSG_LINK_TEXT_TO_HOME);
      model.addAttribute("link_url", MSG_LINK_URL_TO_HOME);

      return "message";

    } catch (Exception ex) {

      logger.error(ex.getMessage());

      model.addAttribute("title", MSG_ERROR_TITLE);
      model.addAttribute("primary", MSG_ERROR_PRIMARY);
      model.addAttribute("secondary", MSG_ERROR_SECONDARY);
      model.addAttribute("link_text", MSG_LINK_TEXT_TO_HOME);
      model.addAttribute("link_url", MSG_LINK_URL_TO_HOME);

      return "message";
    }
  }
}
