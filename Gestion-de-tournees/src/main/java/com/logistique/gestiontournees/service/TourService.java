package com.logistique.gestiontournees.service;

import com.logistique.gestiontournees.dto.TourDTO;

import java.util.List;
import java.util.Optional;

public interface TourService {

    TourDTO save(TourDTO tourDTO);

    Optional<TourDTO> findById(Long id);

    List<TourDTO> findAll();

    void deleteById(Long id);

    /**
     * Calcule et SAUVEGARDE un nouvel ordre de livraison optimisé.
     */
    TourDTO getOptimizedTour(Long tourId, String algorithmName);

    /**
     * Calcule la distance totale d'une tournée (basée sur l'ordre sauvegardé).
     */
    double getTotalDistance(long tourId);
}