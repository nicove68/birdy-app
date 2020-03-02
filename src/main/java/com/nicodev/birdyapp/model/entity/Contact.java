package com.nicodev.birdyapp.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "contacts")
@CompoundIndexes({
    @CompoundIndex(name = "idx_owner_email", def = "{'owner_email': 1}"),
    @CompoundIndex(name = "idx_day_of_birth_month_of_birth", def = "{'day_of_birth' : 1, 'month_of_birth': 1}")
})
public class Contact {

    @Id
    private String id;

    @Field("owner_email")
    private String ownerEmail;

    @Field("name")
    private String name;

    @Field("photo_url")
    private String photoUrl;

    @Field("day_of_birth")
    private int dayOfBirth;

    @Field("month_of_birth")
    private int monthOfBirth;

    @Field("google_person_id")
    private String googlePersonId;

    @Field("created_at")
    private String createdAt;

    public Contact() {
    }

    @PersistenceConstructor
    public Contact(String ownerEmail, String name, String photoUrl, int dayOfBirth, int monthOfBirth, String googlePersonId, String createdAt) {
        this.ownerEmail = ownerEmail;
        this.name = name;
        this.photoUrl = photoUrl;
        this.dayOfBirth = dayOfBirth;
        this.monthOfBirth = monthOfBirth;
        this.googlePersonId = googlePersonId;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public int getDayOfBirth() {
        return dayOfBirth;
    }

    public int getMonthOfBirth() {
        return monthOfBirth;
    }

    public String getGooglePersonId() {
        return googlePersonId;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
