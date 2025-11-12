package com.logistique.gestiontournees.repository;

import com.logistique.gestiontournees.entity.DeliveryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryHistoryRepository extends JpaRepository<DeliveryHistory, Long> {
    List<DeliveryHistory> findFirst100ByOrderByDeliveryDateDesc();
}
