package com.logistique.gestiontournees.repository;

import com.logistique.gestiontournees.dto.VehicleDTO;
import com.logistique.gestiontournees.entity.Vehicle;
import com.logistique.gestiontournees.entity.enumeration.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle,Long> {
    Page<Vehicle> findByMaxWeightGreaterThan(double maxWeight, Pageable pageable);
}
