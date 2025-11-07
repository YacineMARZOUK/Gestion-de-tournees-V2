package com.logistique.gestiontournees.service.mapper.impl;

import com.logistique.gestiontournees.dto.TourDTO;
import com.logistique.gestiontournees.entity.Tour;
import com.logistique.gestiontournees.entity.Vehicle;
import com.logistique.gestiontournees.service.mapper.DeliveryMapper;
import com.logistique.gestiontournees.service.mapper.TourMapper;
import com.logistique.gestiontournees.service.mapper.VehicleMapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.stream.Collectors;

public class TourMapperImpl implements TourMapper {

    private final VehicleMapper vehicleMapper;
    private final DeliveryMapper deliveryMapper;


    public TourMapperImpl(VehicleMapper vehicleMapper, DeliveryMapper deliveryMapper) {
        this.vehicleMapper = vehicleMapper;
        this.deliveryMapper = deliveryMapper;
    }

    @Override
    public Tour toEntity(TourDTO dto){
        if(dto == null){ return null;}

        Vehicle vehicle = null;

        if(dto.getVehicleId()!= null){
            vehicle = Vehicle.builder().id(dto.getVehicleId()).build();
        }

        return Tour.builder()
                .id(dto.getId())
                .tourDate(dto.getTourDate())
                .vehicle(vehicle)
                .build();
    }

    @Override
    public TourDTO toDto(Tour entity){
        if(entity == null){ return null;}

        return TourDTO.builder()
                .id(entity.getId())
                .tourDate(entity.getTourDate())
                .vehicleId((entity.getVehicle() != null) ? entity.getVehicle().getId() : null)
                .deliveries((entity.getDeliveries() != null) ?
                        entity.getDeliveries().stream()
                                .map(deliveryMapper::toDto)
                                .collect(Collectors.toList()) : null)
                .build();
    }



}
