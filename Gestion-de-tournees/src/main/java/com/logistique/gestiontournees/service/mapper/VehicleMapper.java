package com.logistique.gestiontournees.service.mapper;

import com.logistique.gestiontournees.dto.VehicleDTO;
import com.logistique.gestiontournees.entity.Vehicle;

public interface VehicleMapper {
    Vehicle toEntity(VehicleDTO dto);
    VehicleDTO toDto(Vehicle entity );
}
