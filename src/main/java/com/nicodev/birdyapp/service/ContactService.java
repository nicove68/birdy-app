package com.nicodev.birdyapp.service;

import com.nicodev.birdyapp.client.GoogleOAuthClient;
import com.nicodev.birdyapp.client.GooglePeopleClient;
import com.nicodev.birdyapp.model.dto.GoogleConnectionItemBirthdayDTO;
import com.nicodev.birdyapp.model.dto.GoogleConnectionItemDTO;
import com.nicodev.birdyapp.model.dto.GoogleConnectionResponseDTO;
import com.nicodev.birdyapp.model.dto.GoogleOAuthTokenDTO;
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
    private GoogleOAuthClient googleOAuthClient;
    private ContactRepository contactRepository;

    @Autowired
    public ContactService(
        GooglePeopleClient googlePeopleClient,
        GoogleOAuthClient googleOAuthClient,
        ContactRepository contactRepository
    ) {
        this.googlePeopleClient = googlePeopleClient;
        this.googleOAuthClient = googleOAuthClient;
        this.contactRepository = contactRepository;
    }

    public List<Contact> createContacts(User user){
        logger.info("Creating new birdy user contacts with google people api");

        GoogleConnectionResponseDTO googleConnectionResponse = googlePeopleClient.getGoogleUserConnections(user.getGoogleAccessToken());

        List<Contact> contacts = getFilteredContacts(user, googleConnectionResponse);

        contactRepository.saveAll(contacts);
        logger.info("Birdy user [{}] contacts was created successfully", user.getEmail());

        return contacts;
    }

    public void updateContacts(User user) {
        logger.info("Starting update contacts for user email: {}", user.getEmail());

        GoogleOAuthTokenDTO googleOAuthToken = googleOAuthClient.getGoogleOAuthTokenUsingRefreshToken(user.getGoogleRefreshToken());
        GoogleConnectionResponseDTO googleConnectionResponse = googlePeopleClient.getGoogleUserConnections(googleOAuthToken.getAccessToken());

        List<Contact> contacts = getFilteredContacts(user, googleConnectionResponse);

        deleteUserContacts(user);

        contactRepository.saveAll(contacts);
        logger.info("All contacts saved for user email: {}", user.getEmail());
    }

    private List<Contact> getFilteredContacts(User user, GoogleConnectionResponseDTO googleConnectionResponse) {
        return googleConnectionResponse.getConnections().stream()
            .filter(this::hasPrimaryName)
            .filter(this::hasBirthdayDate)
            .map(connection -> ContactTransformer.toContact(user, connection))
            .collect(Collectors.toList());
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

    public void deleteUserContacts(User user) {
        contactRepository.deleteAllByOwnerEmail(user.getEmail());

        logger.info("All contacts removed for user email: {}", user.getEmail());
    }
}
