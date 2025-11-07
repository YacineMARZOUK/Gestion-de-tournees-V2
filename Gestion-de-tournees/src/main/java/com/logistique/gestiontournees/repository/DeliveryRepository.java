package com.logistique.gestiontournees.repository;

import com.logistique.gestiontournees.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository <Delivery, Long>{
}
