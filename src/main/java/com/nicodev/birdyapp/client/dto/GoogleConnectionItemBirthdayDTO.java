package com.nicodev.birdyapp.client.dto;

public class GoogleConnectionItemBirthdayDTO {

    private GoogleConnectionItemMetadataDTO metadata;
    private GoogleConnectionItemBirthdayDateDTO date;
    private String text;

    public GoogleConnectionItemBirthdayDTO() {
    }

    public GoogleConnectionItemBirthdayDTO(GoogleConnectionItemMetadataDTO metadata, GoogleConnectionItemBirthdayDateDTO date, String text) {
        this.metadata = metadata;
        this.date = date;
        this.text = text;
    }

    public GoogleConnectionItemMetadataDTO getMetadata() {
        return metadata;
    }

    public GoogleConnectionItemBirthdayDateDTO getDate() {
        return date;
    }

    public String getText() {
        return text;
    }
}
