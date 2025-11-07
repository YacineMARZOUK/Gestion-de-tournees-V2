package com.logistique.gestiontournees.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDTO {
    private Long id;
    private String address;
    private double latitude;
    private double longitude;
    private LocalTime openingTime;
    private LocalTime closingTime;
}