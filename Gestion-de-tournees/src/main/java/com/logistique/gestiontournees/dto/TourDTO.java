package com.logistique.gestiontournees.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourDTO {
    private Long id;
    private LocalDate tourDate;
    private Long vehicleId;
    private List<DeliveryDTO> deliveries;
}
