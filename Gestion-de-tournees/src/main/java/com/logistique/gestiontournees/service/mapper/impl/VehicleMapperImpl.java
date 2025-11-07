package com.logistique.gestiontournees.service.mapper.impl;

import com.logistique.gestiontournees.dto.VehicleDTO;
import com.logistique.gestiontournees.entity.Vehicle;
import com.logistique.gestiontournees.service.mapper.VehicleMapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class VehicleMapperImpl implements VehicleMapper {


    @Override
    public Vehicle toEntity (VehicleDTO dto){

        if(dto == null){
            return null;
        }
        return Vehicle.builder()
                .id(dto.getId())
                .licensePlate(dto.getLicensePlate())
                .vehicleType(dto.getVehicleType())
                .maxWeight(dto.getMaxWeight())
                .maxVolume(dto.getMaxVolume())
                .build();
    }

        @Override
        public VehicleDTO toDto (Vehicle entity){
            if (entity == null) {
                return null;
            }

            // 4. Remplacer 'getBean' par le Builder
            return VehicleDTO.builder()
                    .id(entity.getId())
                    .licensePlate(entity.getLicensePlate())
                    .vehicleType(entity.getVehicleType())
                    .maxWeight(entity.getMaxWeight())
                    .maxVolume(entity.getMaxVolume())
                    .build();
        }

}
