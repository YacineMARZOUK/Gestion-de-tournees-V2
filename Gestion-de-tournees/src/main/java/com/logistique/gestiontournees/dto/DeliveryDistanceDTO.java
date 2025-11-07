package com.logistique.gestiontournees.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDistanceDTO {
    private Long targetDeliveryId;
    private String targetDeliveryAddress;
    private Double distanceKm;
}

