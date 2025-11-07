package com.logistique.gestiontournees.service.optimizer.impl;

import com.logistique.gestiontournees.entity.Delivery;
import com.logistique.gestiontournees.entity.Vehicle;
import com.logistique.gestiontournees.entity.Warehouse;
import com.logistique.gestiontournees.service.optimizer.TourOptimizer;
import com.logistique.gestiontournees.util.DistanceCalculator;

import java.util.ArrayList;
import java.util.List;

public class NearestNeighborOptimizer implements TourOptimizer {

    @Override
    public List<Delivery> calculateOptimalTour(Warehouse warehouse, List<Delivery> deliveries, Vehicle vehicle) {

        // Copie modifiable de la liste des livraisons
        List<Delivery> remainingDeliveries = new ArrayList<>(deliveries);
        List<Delivery> orderedTour = new ArrayList<>();

        // Le point de départ est l'entrepôt
        double currentLat = warehouse.getLatitude();
        double currentLon = warehouse.getLongitude();

        while (!remainingDeliveries.isEmpty()) {
            Delivery closestDelivery = null; // Renommé pour plus de clarté
            double shortestDistance = Double.MAX_VALUE;

            // --- ÉTAPE 1 : TROUVER le plus proche ---
            // Itérer sur TOUTES les livraisons restantes
            for (Delivery delivery : remainingDeliveries) {
                double distance = DistanceCalculator.calculateDistance(currentLat, currentLon,
                        delivery.getLatitude(), delivery.getLongitude());

                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    closestDelivery = delivery;
                }
            } // <-- FIN DE LA BOUCLE 'FOR'

            // --- ÉTAPE 2 : TRAITER le plus proche (après la fin de la boucle) ---
            // Votre erreur était que le bloc "if (closesDelivery != null)"
            // était À L'INTÉRIEUR de la boucle "for"

            if (closestDelivery != null) {
                // 2a. L'ajouter à la tournée optimisée
                orderedTour.add(closestDelivery);

                // 2b. Le retirer des livraisons restantes
                remainingDeliveries.remove(closestDelivery);

                // 2c. Mettre à jour la position actuelle pour la PROCHAINE itération
                currentLat = closestDelivery.getLatitude();
                currentLon = closestDelivery.getLongitude();

            } else {
                // Sécurité : si on ne trouve rien, on arrête pour éviter une boucle infinie
                break;
            }
        } // <-- FIN DE LA BOUCLE 'WHILE'

        return orderedTour;
    }
}