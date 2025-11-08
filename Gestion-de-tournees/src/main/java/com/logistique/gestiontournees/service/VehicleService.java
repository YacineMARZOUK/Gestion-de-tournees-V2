package com.logistique.gestiontournees.service;

import com.logistique.gestiontournees.dto.VehicleDTO;
import com.logistique.gestiontournees.entity.enumeration.VehicleType;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
@Service
public interface VehicleService {
    VehicleDTO save(VehicleDTO vehicle);
    Optional<VehicleDTO>findById(Long id);
    List<VehicleDTO> findAll();
    void deleteById(Long id);
    List<VehicleDTO> findVehicleByTypeOrderByMaxWeightDesc(VehicleType Type);

}
