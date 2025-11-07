package com.logistique.gestiontournees.service;

import com.logistique.gestiontournees.dto.DeliveryDTO;
import com.logistique.gestiontournees.dto.DeliveryDistanceDTO;
import com.logistique.gestiontournees.entity.Delivery;

import java.util.List;
import java.util.Optional;

public interface DeliveryService {
    DeliveryDTO save(DeliveryDTO deliveryDTO);
    Optional<DeliveryDTO> findById(Long id);
    List<DeliveryDTO> findAll();
    void deleteById(Long id);
    List<DeliveryDistanceDTO> getDistancesFromDelivery(Long sourceDeliveryId);
}
