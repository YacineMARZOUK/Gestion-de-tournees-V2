package com.logistique.gestiontournees.service.impl;

import com.logistique.gestiontournees.dto.VehicleDTO;
import com.logistique.gestiontournees.entity.Vehicle;
import com.logistique.gestiontournees.entity.enumeration.VehicleType;
import com.logistique.gestiontournees.repository.VehicleRepository;
import com.logistique.gestiontournees.service.VehicleService;
import com.logistique.gestiontournees.service.mapper.VehicleMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<VehicleDTO> findAll() {
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        vehicleRepository.deleteById(id);
    }

    @Override
     public List<VehicleDTO> findVehicleByTypeOrderByMaxWeightDesc(VehicleType type){
        List<Vehicle> vehicleDESC = vehicleRepository.findVehicleByTypeOrderByMaxWeightDesc(type);

        return vehicleDESC.stream()
                .map(vehicle -> vehicleMapper.toDto(vehicle))
                .collect(Collectors.toList());
    }
}
