package com.logistique.gestiontournees.repository;

import com.logistique.gestiontournees.entity.DeliveryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryHistoryRepository extends JpaRepository<DeliveryHistory, Long> {
    List<DeliveryHistory> findFirst100ByOrderByDeliveryDateDesc();
}
