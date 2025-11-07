package com.logistique.gestiontournees.service.mapper;

import com.logistique.gestiontournees.dto.TourDTO;
import com.logistique.gestiontournees.entity.Tour;

public interface TourMapper {
    Tour toEntity(TourDTO dto);
    TourDTO toDto(Tour tour);
}
