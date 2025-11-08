package com.logistique.gestiontournees.service.mapper.impl;

import com.logistique.gestiontournees.dto.DeliveryDTO;
import com.logistique.gestiontournees.entity.Delivery;
import com.logistique.gestiontournees.entity.Tour;
import com.logistique.gestiontournees.service.mapper.DeliveryMapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapperImpl implements DeliveryMapper {




    @Override
    public Delivery toEntity(DeliveryDTO dto){

        if(dto == null){
            return null;
        }
        Tour tour = null;
        if(dto.getTourId() != null){
            tour = Tour.builder().id(dto.getTourId()).build();
        }
        return Delivery.builder()
                .id(dto.getId())
                .address(dto.getAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .weight(dto.getWeight())
                .volume(dto.getVolume())
                .status(dto.getStatus())
                .timeSlot(dto.getTimeSlot())
                .tour(tour)
                .build();

    }

    @Override
    public DeliveryDTO toDto(Delivery entity) {
        if (entity == null) {return null;}

        Long tourId = null;

        if(entity.getTour() != null){
            tourId = entity.getTour().getId();
        }
        return DeliveryDTO.builder()
                .id(entity.getId())
                .address(entity.getAddress())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .weight(entity.getWeight())
                .volume(entity.getVolume())
                .status(entity.getStatus())
                .timeSlot(entity.getTimeSlot())
                .tourId(tourId) // Assigner l'ID du tour
                .build();
    }

    }



