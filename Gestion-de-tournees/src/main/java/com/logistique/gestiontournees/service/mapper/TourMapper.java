package com.logistique.gestiontournees.service.mapper;

import com.logistique.gestiontournees.dto.TourDTO;
import com.logistique.gestiontournees.entity.Tour;
import org.springframework.stereotype.Component;

@Component
public interface TourMapper {
    Tour toEntity(TourDTO dto);
    TourDTO toDto(Tour tour);
}
