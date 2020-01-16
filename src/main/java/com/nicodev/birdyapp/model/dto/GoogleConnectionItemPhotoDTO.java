package com.nicodev.birdyapp.model.dto;

public class GoogleConnectionItemPhotoDTO {

    private GoogleConnectionItemMetadataDTO metadata;
    private String url;

    public GoogleConnectionItemPhotoDTO() {
    }

    public GoogleConnectionItemPhotoDTO(GoogleConnectionItemMetadataDTO metadata, String url) {
        this.metadata = metadata;
        this.url = url;
    }

    public GoogleConnectionItemMetadataDTO getMetadata() {
        return metadata;
    }

    public String getUrl() {
        return url;
    }
}
