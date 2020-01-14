package com.nicodev.birdyapp.client.dto;

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
