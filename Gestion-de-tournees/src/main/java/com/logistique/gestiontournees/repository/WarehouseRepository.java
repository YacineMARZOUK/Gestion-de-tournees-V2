package com.logistique.gestiontournees.repository;

import com.logistique.gestiontournees.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findFirstByOrderByIdAsc();
}
