package com.logistique.gestiontournees.dto;


import com.logistique.gestiontournees.entity.enumeration.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;
    private String licensePlate;
    private VehicleType vehicleType;
    private double maxWeight;
    private double maxVolume;
}