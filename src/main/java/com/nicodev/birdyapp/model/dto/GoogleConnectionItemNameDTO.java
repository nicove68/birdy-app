package com.nicodev.birdyapp.model.dto;

public class GoogleConnectionItemNameDTO {

    private GoogleConnectionItemMetadataDTO metadata;
    private String displayName;

    public GoogleConnectionItemNameDTO() {
    }

    public GoogleConnectionItemNameDTO(GoogleConnectionItemMetadataDTO metadata, String displayName) {
        this.metadata = metadata;
        this.displayName = displayName;
    }

    public GoogleConnectionItemMetadataDTO getMetadata() {
        return metadata;
    }

    public String getDisplayName() {
        return displayName;
    }
}
