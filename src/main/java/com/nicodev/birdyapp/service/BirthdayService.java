package com.nicodev.birdyapp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.nicodev.birdyapp.model.entity.Contact;
import com.nicodev.birdyapp.model.entity.User;
import com.nicodev.birdyapp.repository.ContactRepository;
import com.nicodev.birdyapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BirthdayService {

    private static final Logger logger = LoggerFactory.getLogger(BirthdayService.class);

    private final UserRepository userRepository;

    private final ContactRepository contactRepository;

    @Autowired
    public BirthdayService(UserRepository userRepository, ContactRepository contactRepository) {
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
    }

    public Map<User, List<Contact>> getTodayBirthdays(int dayOfBirth, int monthOfBirth) {
        Map<User, List<Contact>> todayBirthdaysMap = new HashMap<>();

        List<Contact> todayBirthdayContacts = contactRepository.findByDayOfBirthAndMonthOfBirth(dayOfBirth, monthOfBirth);

        if (!todayBirthdayContacts.isEmpty()) {

            logger.info("Prepare birthday for sending emails");

            Map<String, List<Contact>> birthdayContactsByOwner = todayBirthdayContacts
                .stream()
                .collect(Collectors.groupingBy(Contact::getOwnerEmail));

            List<String> ownerUserEmails = new ArrayList<>(birthdayContactsByOwner.keySet());

            List<User> ownerUsers = userRepository.findByEmailIn(ownerUserEmails);

            birthdayContactsByOwner.forEach((ownerEmail, birthdayContacts) -> {
                Optional<User> owner = ownerUsers.stream()
                    .filter(ow -> ow.getEmail().equals(ownerEmail))
                    .findFirst();

                owner.ifPresent(user -> todayBirthdaysMap.put(user, birthdayContacts));
            });
        }

        return todayBirthdaysMap;
    }
}
