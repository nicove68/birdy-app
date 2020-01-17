package com.nicodev.birdyapp.service;

import com.nicodev.birdyapp.client.GoogleOAuthClient;
import com.nicodev.birdyapp.client.GoogleUserInfoClient;
import com.nicodev.birdyapp.exception.rest.BadRequestException;
import com.nicodev.birdyapp.model.dto.GoogleOAuthTokenDTO;
import com.nicodev.birdyapp.model.dto.GoogleUserInfoDTO;
import com.nicodev.birdyapp.model.entity.User;
import com.nicodev.birdyapp.repository.UserRepository;
import com.nicodev.birdyapp.transformer.UserTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    private GoogleOAuthClient googleOAuthClient;
    private GoogleUserInfoClient googleUserInfoClient;
    private UserRepository userRepository;

    @Autowired
    public UserService(GoogleOAuthClient googleOAuthClient, GoogleUserInfoClient googleUserInfoClient, UserRepository userRepository) {
        this.googleOAuthClient = googleOAuthClient;
        this.googleUserInfoClient = googleUserInfoClient;
        this.userRepository = userRepository;
    }

    public User createUser(String googleAuthCode){
        logger.info("Creating new birdy user with google api user information");

        GoogleOAuthTokenDTO googleOAuthToken = googleOAuthClient.getGoogleOAuthToken(googleAuthCode);
        GoogleUserInfoDTO googleUserInfo = googleUserInfoClient.getGoogleUserInfo(googleOAuthToken.getAccessToken());

        if (userRepository.existsByEmail(googleUserInfo.getEmail()))
            throw new BadRequestException("User is already registered");

        User user = UserTransformer.toUser(googleOAuthToken, googleUserInfo);

        userRepository.save(user);

        logger.info("Birdy user was created successfully");

        return user;
    }
}
