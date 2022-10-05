package com.nicodev.birdyapp.service;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import com.nicodev.birdyapp.client.GoogleOAuthClient;
import com.nicodev.birdyapp.client.GoogleUserInfoClient;
import com.nicodev.birdyapp.exception.rest.BadRequestException;
import com.nicodev.birdyapp.exception.rest.NotFoundException;
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

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final GoogleOAuthClient googleOAuthClient;

    private final GoogleUserInfoClient googleUserInfoClient;

    private final UserRepository userRepository;

    @Autowired
    public UserService(
        GoogleOAuthClient googleOAuthClient,
        GoogleUserInfoClient googleUserInfoClient,
        UserRepository userRepository
    ) {
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

        logger.info("Birdy user [{}] was created successfully", user.getEmail());

        return user;
    }

    public List<User> getAllUsers() {
        logger.info("Getting for all birdy users");

        return userRepository.findAll();
    }

    public void deleteUser(User user) {
        googleOAuthClient.revokeGoogleOAuthToken(user.getGoogleAccessToken());

        userRepository.deleteByEmail(user.getEmail());

        logger.info("Birdy user [{}] was deleted successfully", user.getEmail());
    }

    public User getUserForUnsubscribe(String data) {
        String decodedData = new String(Base64.getUrlDecoder().decode(data));
        List<String> values = Arrays.asList(decodedData.split("&"));
        if (values.size() != 2)
            throw new BadRequestException("Failed when validate data: " + data);

        String userId = values.get(0);
        String userEmail = values.get(1);

        return findUserForUnsubscribe(userId, userEmail);
    }

    private User findUserForUnsubscribe(String userId, String userEmail) {
        Optional<User> user = userRepository.findByIdAndEmail(userId, userEmail);
        if (user.isEmpty())
            throw new NotFoundException("User not exists");

        return user.get();
    }
}
