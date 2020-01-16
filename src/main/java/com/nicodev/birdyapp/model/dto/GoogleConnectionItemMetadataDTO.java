package com.nicodev.birdyapp.model.dto;

public class GoogleConnectionItemMetadataDTO {

    private boolean primary;

    public GoogleConnectionItemMetadataDTO() {
    }

    public GoogleConnectionItemMetadataDTO(boolean primary) {
        this.primary = primary;
    }

    public boolean isPrimary() {
        return primary;
    }
}
