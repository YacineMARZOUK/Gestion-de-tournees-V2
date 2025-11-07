package com.logistique.gestiontournees.dto;


import com.logistique.gestiontournees.entity.enumeration.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryDTO {
    private Long id;
    private String address;
    private double latitude;
    private double longitude;
    private double weight;
    private double volume;
    private DeliveryStatus status;
    private String timeSlot;
    private Long tourId;
}
