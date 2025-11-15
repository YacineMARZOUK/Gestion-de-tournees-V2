package com.logistique.gestiontournees.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AIResponseDTO {

    @JsonProperty("orderedDeliveries")
    private List<OptimizedDelivery> orderedDeliveries;

    @JsonProperty("recommendations")
    private String recommendations;

    // Getters et Setters (publics)
    public List<OptimizedDelivery> getOrderedDeliveries() {
        return orderedDeliveries;
    }
    public void setOrderedDeliveries(List<OptimizedDelivery> orderedDeliveries) {
        this.orderedDeliveries = orderedDeliveries;
    }
    public String getRecommendations() {
        return recommendations;
    }
    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    /**
     * Classe interne
     * ASSUREZ-VOUS QUE CETTE CLASSE EST 'public'
     */
    public static class OptimizedDelivery {
        @JsonProperty("id")
        private Long id;
        @JsonProperty("address")
        private String address;

        // ASSUREZ-VOUS QUE LES GETTERS SONT 'public'
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }
}