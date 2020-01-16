package com.nicodev.birdyapp.model.dto;

import java.util.List;

public class GoogleConnectionItemDTO {

    private String resourceName;
    private String etag;
    private List<GoogleConnectionItemNameDTO> names;
    private List<GoogleConnectionItemPhotoDTO> photos;
    private List<GoogleConnectionItemBirthdayDTO> birthdays;

    public GoogleConnectionItemDTO() {
    }

    public GoogleConnectionItemDTO(String resourceName, String etag, List<GoogleConnectionItemNameDTO> names, List<GoogleConnectionItemPhotoDTO> photos, List<GoogleConnectionItemBirthdayDTO> birthdays) {
        this.resourceName = resourceName;
        this.etag = etag;
        this.names = names;
        this.photos = photos;
        this.birthdays = birthdays;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getEtag() {
        return etag;
    }

    public List<GoogleConnectionItemNameDTO> getNames() {
        return names;
    }

    public List<GoogleConnectionItemPhotoDTO> getPhotos() {
        return photos;
    }

    public List<GoogleConnectionItemBirthdayDTO> getBirthdays() {
        return birthdays;
    }
}
