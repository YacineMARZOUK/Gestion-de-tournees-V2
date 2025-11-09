package com.logistique.gestiontournees.service.impl;

import com.logistique.gestiontournees.dto.VehicleDTO;
import com.logistique.gestiontournees.entity.Vehicle;
import com.logistique.gestiontournees.entity.enumeration.VehicleType;
import com.logistique.gestiontournees.repository.VehicleRepository;
import com.logistique.gestiontournees.service.VehicleService;
import com.logistique.gestiontournees.service.mapper.VehicleMapper;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {


    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    @Override
    public VehicleDTO save(VehicleDTO vehicleDTO) {
       Vehicle vehicle = vehicleMapper.toEntity(vehicleDTO);
       vehicle = vehicleRepository.save(vehicle);
       return vehicleMapper.toDto(vehicle);
    }

    @Override
    public Optional<VehicleDTO> findById(Long id) {
        return vehicleRepository.findById(id).map(vehicleMapper::toDto);
    }


    @Override
    public Page<VehicleDTO> findAll(Pageable pageable) {
        Page<Vehicle> vehiclePage = vehicleRepository.findAll(pageable);

        return vehiclePage.map(vehicleMapper::toDto);
    }

    @Override
    public void deleteById(Long id) {
        vehicleRepository.deleteById(id);
    }

    @Override
    public Page<VehicleDTO> searchByWeight(double minWeight, Pageable pageable) {
        Page<Vehicle> vehiclePage = vehicleRepository.findByMaxWeightGreaterThan(minWeight, pageable);
        return vehiclePage.map(vehicleMapper::toDto);
    }


}
