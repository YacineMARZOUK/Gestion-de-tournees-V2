package com.logistique.gestiontournees.service.mapper;

import com.logistique.gestiontournees.dto.DeliveryDTO;
import com.logistique.gestiontournees.entity.Delivery;
import org.springframework.stereotype.Component;

@Component
public interface DeliveryMapper {
    Delivery toEntity(DeliveryDTO dto);
    DeliveryDTO toDto(Delivery entity);

}
