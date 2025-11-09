package com.logistique.gestiontournees.service;

import com.logistique.gestiontournees.dto.VehicleDTO;
import com.logistique.gestiontournees.entity.enumeration.VehicleType;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    VehicleDTO save(VehicleDTO vehicle);
    Optional<VehicleDTO>findById(Long id);
    Page<VehicleDTO> findAll(Pageable pageable);
    void deleteById(Long id);
    Page<VehicleDTO> searchByWeight(double minWeight, Pageable pageable);

}
