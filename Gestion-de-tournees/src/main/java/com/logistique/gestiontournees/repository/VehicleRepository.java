package com.logistique.gestiontournees.repository;

import com.logistique.gestiontournees.dto.VehicleDTO;
import com.logistique.gestiontournees.entity.Vehicle;
import com.logistique.gestiontournees.entity.enumeration.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle,Long> {
    List<Vehicle> findVehicleByTypeOrderByMaxWeghtDesc(VehicleType Type);

    @Query("select v from Vehicle v where v.vehicleType = :Type order by v.maxWeight desc ")
    List<Vehicle> findVehicleByTypeOrderByMaxWeightDesc(VehicleType Type);
}
