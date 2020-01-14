package com.nicodev.birdyapp.client.dto;

public class GoogleUserInfoDTO {

    private String id;
    private String email;
    private boolean verifiedEmail;
    private String name;
    private String picture;
    private String locale;

    public GoogleUserInfoDTO() {
    }

    public GoogleUserInfoDTO(String id, String email, boolean verifiedEmail, String name, String picture, String locale) {
        this.id = id;
        this.email = email;
        this.verifiedEmail = verifiedEmail;
        this.name = name;
        this.picture = picture;
        this.locale = locale;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public boolean isVerifiedEmail() {
        return verifiedEmail;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public String getLocale() {
        return locale;
    }
}
