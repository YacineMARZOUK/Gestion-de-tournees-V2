package com.logistique.gestiontournees.controller;

import com.logistique.gestiontournees.dto.VehicleDTO;
import com.logistique.gestiontournees.entity.enumeration.VehicleType;
import com.logistique.gestiontournees.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    @Test
    void testCreateVehicle_ShouldReturn201CreatedAndSavedDTO() {


        VehicleDTO inputDTO = new VehicleDTO();
        inputDTO.setLicensePlate("AA-123-BB");
        inputDTO.setVehicleType(VehicleType.VAN);
        inputDTO.setMaxWeight(1000.0);
        inputDTO.setMaxVolume(10.0);

        VehicleDTO savedDTO = new VehicleDTO();
        savedDTO.setId(1L);
        savedDTO.setLicensePlate("AA-123-BB");
        savedDTO.setVehicleType(VehicleType.VAN);
        savedDTO.setMaxWeight(1000.0);
        savedDTO.setMaxVolume(10.0);

        when(vehicleService.save(any(VehicleDTO.class)))
                .thenReturn(savedDTO);

        ResponseEntity<VehicleDTO> response = vehicleController.createVehicle(inputDTO);

        assertNotNull(response);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(1L, response.getBody().getId());

        assertEquals("AA-123-BB", response.getBody().getLicensePlate());

        verify(vehicleService, times(1)).save(any(VehicleDTO.class));
    }
}
