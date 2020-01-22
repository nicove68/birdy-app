package com.nicodev.birdyapp.transformer;

import com.nicodev.birdyapp.model.dto.GoogleConnectionItemBirthdayDTO;
import com.nicodev.birdyapp.model.dto.GoogleConnectionItemBirthdayDateDTO;
import com.nicodev.birdyapp.model.dto.GoogleConnectionItemDTO;
import com.nicodev.birdyapp.model.dto.GoogleConnectionItemNameDTO;
import com.nicodev.birdyapp.model.dto.GoogleConnectionItemPhotoDTO;
import com.nicodev.birdyapp.model.entity.Contact;
import com.nicodev.birdyapp.model.entity.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class ContactTransformer {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final String WITHOUT_NAME = "CONTACTO SIN NOMBRE";
    private static final String EMPTY_PHOTO_URL = "empty_profile_url";

    public static Contact toContact(User user, GoogleConnectionItemDTO connectionItem) {
        GoogleConnectionItemBirthdayDateDTO birthdayDate = getMainBirthdayDate(connectionItem);
        return new Contact(
            user.getEmail(),
            getMainName(connectionItem),
            getMainPhotoUrl(connectionItem),
            birthdayDate.getDay(),
            birthdayDate.getMonth(),
            connectionItem.getResourceName(),
            LocalDateTime.now().format(dateTimeFormatter)
        );
    }

    private static String getMainName(GoogleConnectionItemDTO connectionItem) {
        return connectionItem.getNames().stream()
            .filter(name -> name.getMetadata().isPrimary())
            .findFirst()
            .map(GoogleConnectionItemNameDTO::getDisplayName)
            .orElse(WITHOUT_NAME);
    }

    private static String getMainPhotoUrl(GoogleConnectionItemDTO connectionItem) {
        if (connectionItem.getPhotos() != null && !connectionItem.getPhotos().isEmpty())
            return connectionItem.getPhotos().stream()
                .filter(photo -> photo.getMetadata().isPrimary())
                .findFirst()
                .map(GoogleConnectionItemPhotoDTO::getUrl)
                .orElse(EMPTY_PHOTO_URL);

        return EMPTY_PHOTO_URL;
    }

    private static GoogleConnectionItemBirthdayDateDTO getMainBirthdayDate(GoogleConnectionItemDTO connectionItem) {
        return connectionItem.getBirthdays().stream()
            .filter(birthday -> birthday.getMetadata().isPrimary())
            .findFirst()
            .map(GoogleConnectionItemBirthdayDTO::getDate)
            .orElse(new GoogleConnectionItemBirthdayDateDTO());
    }
}
