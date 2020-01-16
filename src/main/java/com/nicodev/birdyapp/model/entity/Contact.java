package com.nicodev.birdyapp.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "contacts")
public class Contact {

    @Id
    private String id;

    @Field("owner")
    private String owner;

    @Field("name")
    private String name;

    @Field("photo_url")
    private String photoUrl;

    @Field("day_of_birth")
    private String dayOfBirth;

    @Field("month_of_birth")
    private String monthOfBirth;

    @Field("created_at")
    private String createdAt;

    public Contact() {
    }

    @PersistenceConstructor
    public Contact(String owner, String name, String photoUrl, String dayOfBirth, String monthOfBirth, String createdAt) {
        this.owner = owner;
        this.name = name;
        this.photoUrl = photoUrl;
        this.dayOfBirth = dayOfBirth;
        this.monthOfBirth = monthOfBirth;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getDayOfBirth() {
        return dayOfBirth;
    }

    public String getMonthOfBirth() {
        return monthOfBirth;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
