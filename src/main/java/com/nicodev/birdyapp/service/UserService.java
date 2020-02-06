package com.nicodev.birdyapp.service;

import com.nicodev.birdyapp.client.GoogleOAuthClient;
import com.nicodev.birdyapp.client.GoogleUserInfoClient;
import com.nicodev.birdyapp.exception.rest.BadRequestException;
import com.nicodev.birdyapp.exception.rest.NotFoundException;
import com.nicodev.birdyapp.model.dto.GoogleOAuthTokenDTO;
import com.nicodev.birdyapp.model.dto.GoogleUserInfoDTO;
import com.nicodev.birdyapp.model.entity.User;
import com.nicodev.birdyapp.repository.UserRepository;
import com.nicodev.birdyapp.transformer.UserTransformer;
import java.util.List;
import java.util.Optional;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.BasicTextEncryptor;
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
    private BasicTextEncryptor textEncryptor;

    @Autowired
    public UserService(
        GoogleOAuthClient googleOAuthClient,
        GoogleUserInfoClient googleUserInfoClient,
        UserRepository userRepository,
        BasicTextEncryptor textEncryptor
    ) {
        this.googleOAuthClient = googleOAuthClient;
        this.googleUserInfoClient = googleUserInfoClient;
        this.userRepository = userRepository;
        this.textEncryptor = textEncryptor;
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

    public User findUserByEmail(String userEmail) {
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (!user.isPresent())
            throw new NotFoundException("User not exists");

        return user.get();
    }

    public void deleteUser(String userEmail) {
        User user = findUserByEmail(userEmail);

        googleOAuthClient.revokeGoogleOAuthToken(user.getGoogleAccessToken());

        userRepository.deleteByEmail(user.getEmail());

        logger.info("Birdy user [{}] was deleted successfully", user.getEmail());
    }

    public String decryptUserData(String data) {
        try {
            return textEncryptor.decrypt(data);
        } catch (EncryptionOperationNotPossibleException ex) {
            throw new BadRequestException("Failed when decrypt data");
        }
    }

    public String encryptUserEmail(String userEmail) {
        try {
            return textEncryptor.encrypt(userEmail);
        } catch (EncryptionOperationNotPossibleException ex) {
            throw new BadRequestException("Failed when encrypt email");
        }
    }
}
