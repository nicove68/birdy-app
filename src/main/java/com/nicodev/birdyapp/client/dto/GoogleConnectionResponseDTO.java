package com.nicodev.birdyapp.client.dto;

import java.util.List;

public class GoogleConnectionResponseDTO {

    private List<GoogleConnectionItemDTO> connections;
    private int totalPeople;
    private int totalItems;

    public GoogleConnectionResponseDTO() {
    }

    public GoogleConnectionResponseDTO(List<GoogleConnectionItemDTO> connections, int totalPeople, int totalItems) {
        this.connections = connections;
        this.totalPeople = totalPeople;
        this.totalItems = totalItems;
    }

    public List<GoogleConnectionItemDTO> getConnections() {
        return connections;
    }

    public int getTotalPeople() {
        return totalPeople;
    }

    public int getTotalItems() {
        return totalItems;
    }
}
