package com.nicodev.birdyapp.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("email")
    private String email;

    @Field("google_auth_code")
    private String googleAuthCode;

    @Field("google_refresh_token")
    private String googleRefreshToken;

    @Field("created_at")
    private String createdAt;

    public User() {
    }

    @PersistenceConstructor
    public User(String name, String email, String googleAuthCode, String googleRefreshToken, String createdAt) {
        this.name = name;
        this.email = email;
        this.googleAuthCode = googleAuthCode;
        this.googleRefreshToken = googleRefreshToken;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getGoogleAuthCode() {
        return googleAuthCode;
    }

    public String getGoogleRefreshToken() {
        return googleRefreshToken;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
