package com.nicodev.birdyapp.service;

import com.nicodev.birdyapp.client.GooglePeopleClient;
import com.nicodev.birdyapp.model.dto.GoogleConnectionItemBirthdayDTO;
import com.nicodev.birdyapp.model.dto.GoogleConnectionItemDTO;
import com.nicodev.birdyapp.model.dto.GoogleConnectionResponseDTO;
import com.nicodev.birdyapp.model.entity.Contact;
import com.nicodev.birdyapp.model.entity.User;
import com.nicodev.birdyapp.repository.ContactRepository;
import com.nicodev.birdyapp.transformer.ContactTransformer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private static Logger logger = LoggerFactory.getLogger(ContactService.class);

    private GooglePeopleClient googlePeopleClient;
    private ContactRepository contactRepository;

    @Autowired
    public ContactService(GooglePeopleClient googlePeopleClient, ContactRepository contactRepository) {
        this.googlePeopleClient = googlePeopleClient;
        this.contactRepository = contactRepository;
    }

    public List<Contact> createContacts(User user){
        logger.info("Creating new birdy user contacts with google people api");

        GoogleConnectionResponseDTO googleConnectionResponse = googlePeopleClient.getGoogleUserConnections(user.getGoogleAccessToken());

        List<Contact> contacts = googleConnectionResponse.getConnections().stream()
            .filter(this::hasPrimaryName)
            .filter(this::hasBirthdayDate)
            .map(connection -> ContactTransformer.toContact(user, connection))
            .collect(Collectors.toList());

        contactRepository.saveAll(contacts);

        logger.info("Birdy user contacts was created successfully");

        return contacts;
    }

    private boolean hasPrimaryName(GoogleConnectionItemDTO connectionItem) {
        return connectionItem.getNames() != null &&
            !connectionItem.getNames().isEmpty() &&
            connectionItem.getNames().stream().anyMatch(name -> name.getMetadata().isPrimary());
    }

    private boolean hasBirthday(GoogleConnectionItemDTO connectionItem) {
        return connectionItem.getBirthdays() != null &&
            !connectionItem.getBirthdays().isEmpty() &&
            connectionItem.getBirthdays().stream().anyMatch(birthday -> birthday.getMetadata().isPrimary());
    }

    private boolean hasBirthdayDate(GoogleConnectionItemDTO connectionItem) {
        if (hasBirthday(connectionItem)) {
            Optional<GoogleConnectionItemBirthdayDTO> date = connectionItem.getBirthdays().stream()
                .filter(birthday -> birthday.getMetadata().isPrimary())
                .findFirst();

            return date.isPresent() && date.get().getDate().getDay() != 0 && date.get().getDate().getMonth() != 0;
        }

        return false;
    }
}
