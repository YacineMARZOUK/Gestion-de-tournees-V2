package com.logistique.gestiontournees.service;

import com.logistique.gestiontournees.dto.VehicleDTO;
import com.logistique.gestiontournees.entity.Vehicle;
import com.logistique.gestiontournees.repository.VehicleRepository;
import com.logistique.gestiontournees.service.impl.VehicleServiceImpl;
import com.logistique.gestiontournees.service.mapper.VehicleMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {
    @Mock
    VehicleRepository vehicleRepository;

    @Mock
    VehicleMapper vehicleMapper;

    @InjectMocks
    VehicleServiceImpl vehicleService;

    @Test
    void TestVehicleSave_Success(){
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setId(1L);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);


        when(vehicleMapper.toEntity(vehicleDTO)).thenReturn(vehicle);
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when (vehicleMapper.toDto(vehicle)).thenReturn(vehicleDTO);

        VehicleDTO dto = vehicleService.save(vehicleDTO);

        assertNotNull(dto);
        verify(vehicleMapper,times(1)).toEntity(vehicleDTO);
        verify(vehicleRepository, times(1)).save(vehicle);
        verify(vehicleMapper, times(1)).toDto(vehicle);
    }

    @Test
    void testFindById(){
        Vehicle vehicle = new Vehicle();
        VehicleDTO vehicleDTO = new VehicleDTO();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDTO);

        Optional<VehicleDTO> dto = vehicleService.findById(1L);

        assertNotNull(dto);
        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleMapper, times(1)).toDto(vehicle);

    }
}
