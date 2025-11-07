package com.logistique.gestiontournees.service.impl;

import com.logistique.gestiontournees.dto.TourDTO;
import com.logistique.gestiontournees.entity.Delivery;
import com.logistique.gestiontournees.entity.Tour;
import com.logistique.gestiontournees.entity.Vehicle;
import com.logistique.gestiontournees.entity.Warehouse;
import com.logistique.gestiontournees.repository.TourRepository;
import com.logistique.gestiontournees.repository.WarehouseRepository;
import com.logistique.gestiontournees.service.TourService;
import com.logistique.gestiontournees.service.mapper.TourMapper;
import com.logistique.gestiontournees.service.optimizer.TourOptimizer;
import com.logistique.gestiontournees.util.DistanceCalculator; // Assurez-vous que cet import est correct
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final WarehouseRepository warehouseRepository;
    private final Map<String, TourOptimizer> optimizers;

    // Constructeur basé sur votre configuration Spring XML
    public TourServiceImpl(TourRepository tourRepository, TourMapper tourMapper, WarehouseRepository warehouseRepository, Map<String, TourOptimizer> optimizers) {
        this.tourRepository = tourRepository;
        this.tourMapper = tourMapper;
        this.warehouseRepository = warehouseRepository;
        this.optimizers = optimizers;
    }

    @Override
    public TourDTO save(TourDTO tourDTO) {
        // Votre implémentation de 'save'
        // ... (Je suppose que vous l'avez déjà)
        Tour tour = tourMapper.toEntity(tourDTO);
        // Logique pour lier le véhicule, etc.
        tour = tourRepository.save(tour);
        return tourMapper.toDto(tour);
    }

    @Override
    public Optional<TourDTO> findById(Long id) {
        return tourRepository.findById(id).map(tourMapper::toDto);
    }

    @Override
    public List<TourDTO> findAll() {
        return tourRepository.findAll().stream()
                .map(tourMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        tourRepository.deleteById(id);
    }


    // ======================================================
    // MÉTHODE CORRIGÉE (Optimisation)
    // ======================================================

    @Override
    public TourDTO getOptimizedTour(Long tourId, String algorithmName) {

        // 1. Sélectionner le bon algorithme
        TourOptimizer optimizer = optimizers.get(algorithmName);
        if (optimizer == null) {
            throw new IllegalArgumentException("Algorithme non trouvé : " + algorithmName);
        }

        // 2. Récupérer l'entité Tour complète
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tournée non trouvée : " + tourId));

        // 3. Récupérer les données de la tournée
        Vehicle vehicle = tour.getVehicle();
        List<Delivery> currentDeliveries = tour.getDeliveries();

        // 4. CORRECTION : Récupérer l'entrepôt via le repository
        //    (Nous supposons qu'il n'y a qu'un seul entrepôt, ou que le premier est le bon)
        Warehouse warehouse = warehouseRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Aucun entrepôt de départ n'a été trouvé."));

        // 5. Lancer le calcul d'optimisation
        List<Delivery> optimizedList = optimizer.calculateOptimalTour(warehouse, currentDeliveries, vehicle);

        // 6. ======================================================
        //    CORRECTION DU BUG : Sauvegarder le nouvel ordre
        // ======================================================

        // Vider l'ancienne liste (nécessaire à cause de @OrderColumn)
        tour.getDeliveries().clear();

        // Ajouter la nouvelle liste dans le bon ordre
        tour.getDeliveries().addAll(optimizedList);

        // 7. SAUVEGARDER L'ENTITÉ Tour en base de données
        Tour savedTour = tourRepository.save(tour); // <-- C'EST LA LIGNE QUI MANQUAIT

        // 8. Retourner le DTO de la tournée mise à jour
        return tourMapper.toDto(savedTour);
    }

    // ======================================================
    // MÉTHODE CORRIGÉE (Calcul de distance)
    // ======================================================

    @Override
    public double getTotalDistance(long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tournée non trouvée : " + tourId));

        // 1. Récupérer l'entrepôt (même logique que pour l'optimisation)
        Warehouse warehouse = warehouseRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Aucun entrepôt de départ n'a été trouvé."));

        // 2. Récupérer la liste des livraisons (maintenant ordonnée si l'optimisation a eu lieu)
        List<Delivery> deliveries = tour.getDeliveries();

        if (deliveries == null || deliveries.isEmpty()) {
            return 0.0;
        }

        double totalDistance = 0.0;

        // Position actuelle (commence à l'entrepôt)
        double currentLat = warehouse.getLatitude();
        double currentLon = warehouse.getLongitude();

        // 3. Itérer sur chaque livraison dans l'ordre
        for (Delivery delivery : deliveries) {
            // Ajouter la distance du point actuel à la livraison
            totalDistance += DistanceCalculator.calculateDistance(
                    currentLat, currentLon,
                    delivery.getLatitude(), delivery.getLongitude()
            );

            // Mettre à jour la position actuelle
            currentLat = delivery.getLatitude();
            currentLon = delivery.getLongitude();
        }

        // 4. Ajouter le retour à l'entrepôt
        totalDistance += DistanceCalculator.calculateDistance(
                currentLat, currentLon, // Position de la dernière livraison
                warehouse.getLatitude(), warehouse.getLongitude()
        );

        return totalDistance;
    }
}