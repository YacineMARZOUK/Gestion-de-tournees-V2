package com.logistique.gestiontournees.controller;


import com.logistique.gestiontournees.dto.VehicleDTO;
import com.logistique.gestiontournees.entity.Vehicle;
import com.logistique.gestiontournees.entity.enumeration.VehicleType;
import com.logistique.gestiontournees.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary = "Crée un nouveau véhicule")
    @PostMapping
    public ResponseEntity<VehicleDTO> createVehicle(@RequestBody VehicleDTO vehicleDTO) {

//        if(vehicleDTO.getId()==null){
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
        VehicleDTO savedDto = vehicleService.save(vehicleDTO);
        return new ResponseEntity<VehicleDTO>(savedDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Récupère un véhicule par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> getVehicleById(@PathVariable Long id) {
        Optional<VehicleDTO> dtoOpt = vehicleService.findById(id);

        // Utilise ResponseEntity pour renvoyer 200 (OK) si trouvé, ou 404 (Not Found) sinon
        return dtoOpt
                .map(dto -> ResponseEntity.ok(dto))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Récupère la liste de tous les véhicules")
    @GetMapping
    public ResponseEntity<Page<VehicleDTO>> getAllVehicles(Pageable pageable) { // <-- MODIFIÉ
        Page<VehicleDTO> vehiclePage = vehicleService.findAll(pageable);
        return ResponseEntity.ok(vehiclePage);
    }

    @Operation(summary = "Met à jour un véhicule existant")
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> updateVehicle(@PathVariable Long id, @RequestBody VehicleDTO vehicleDTO) {

        if (vehicleService.findById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        vehicleDTO.setId(id);
        VehicleDTO updatedDto = vehicleService.save(vehicleDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @Operation(summary = "Supprime un véhicule par son ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        if (vehicleService.findById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        vehicleService.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Recherche les véhicules par poids minimum (paginé)")
    @GetMapping("/search/by-weight")
    public ResponseEntity<Page<VehicleDTO>> searchVehiclesByWeight(
            @RequestParam("minWeight") double minWeight,
            Pageable pageable) {

        Page<VehicleDTO> vehiclePage = vehicleService.searchByWeight(minWeight, pageable);
        return ResponseEntity.ok(vehiclePage);
    }


}
