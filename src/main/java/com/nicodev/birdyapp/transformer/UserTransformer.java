package com.nicodev.birdyapp.transformer;

import com.nicodev.birdyapp.model.dto.GoogleOAuthTokenDTO;
import com.nicodev.birdyapp.model.dto.GoogleUserInfoDTO;
import com.nicodev.birdyapp.model.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class UserTransformer {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static User toUser(GoogleOAuthTokenDTO googleOAuthToken, GoogleUserInfoDTO googleUserInfo) {
        return new User(
                googleUserInfo.getName(),
                googleUserInfo.getEmail(),
                googleOAuthToken.getAccessToken(),
                googleOAuthToken.getRefreshToken(),
                LocalDateTime.now().format(dateTimeFormatter)
        );
    }
}
