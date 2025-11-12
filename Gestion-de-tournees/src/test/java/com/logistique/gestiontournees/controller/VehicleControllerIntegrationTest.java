package com.logistique.gestiontournees.controller;

import com.logistique.gestiontournees.entity.Vehicle;
import com.logistique.gestiontournees.entity.enumeration.VehicleType;
import com.logistique.gestiontournees.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is; // <-- Import statique pour "is()"
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test d'intégration pour le VehicleController.
 * @SpringBootTest charge le contexte complet de l'application (y compris la DB H2)
 * @AutoConfigureMockMvc nous donne accès à MockMvc pour simuler des appels HTTP
 */
@SpringBootTest
@AutoConfigureMockMvc
public class VehicleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // L'outil pour simuler les requêtes HTTP

    @Autowired
    private VehicleRepository vehicleRepository; // Accès direct à la DB pour le setup

    /**
     * Avant chaque test, nous nettoyons la DB et insérons nos données de test.
     */
    @BeforeEach
    void setUp() {
        // Vider la table pour un test propre
        vehicleRepository.deleteAll();

        // Créer nos véhicules de test
        Vehicle truckLight = Vehicle.builder()
                .licensePlate("TRUCK-1000")
                .vehicleType(VehicleType.TRUCK)
                .maxWeight(1000.0)
                .maxVolume(10.0)
                .build();

        Vehicle truckHeavy = Vehicle.builder()
                .licensePlate("TRUCK-5000")
                .vehicleType(VehicleType.TRUCK)
                .maxWeight(5000.0)
                .maxVolume(50.0)
                .build();

        Vehicle van = Vehicle.builder()
                .licensePlate("VAN-800")
                .vehicleType(VehicleType.VAN)
                .maxWeight(800.0)
                .maxVolume(6.0)
                .build();

        // Sauvegarder les données
        vehicleRepository.save(truckLight);
        vehicleRepository.save(truckHeavy);
        vehicleRepository.save(van);
    }

    @Test
    void testSearchVehiclesByWeight_ShouldReturnHeavyTruckOnly() throws Exception {
        // --- Action ---
        // Nous appelons l'endpoint de recherche que vous venez de créer.
        // Nous cherchons des véhicules avec un poids > 1500 kg.
        // Nous demandons la première page (page=0) de 5 résultats (size=5).
        mockMvc.perform(get("/api/vehicles/search/by-weight")
                        .param("minWeight", "1500")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))

                // --- Assert (Vérification) ---

                // 1. Vérifie que le statut HTTP est 200 OK
                .andExpect(status().isOk())

                // 2. Vérifie (via jsonPath) que la réponse JSON
                //    indique qu'il y a 1 seul résultat total.
                .andExpect(jsonPath("$.totalElements", is(1)))

                // 3. Vérifie que le premier (et seul) élément dans la
                //    liste "content" a la bonne plaque d'immatriculation.
                .andExpect(jsonPath("$.content[0].licensePlate", is("TRUCK-5000")))

                // 4. Vérifie que son poids est correct.
                .andExpect(jsonPath("$.content[0].maxWeight", is(5000.0)));
    }
}