package com.logistique.gestiontournees.service.optimizer.impl;

import com.logistique.gestiontournees.entity.Delivery;
import com.logistique.gestiontournees.entity.Vehicle;
import com.logistique.gestiontournees.entity.Warehouse;
import com.logistique.gestiontournees.service.optimizer.TourOptimizer;
import com.logistique.gestiontournees.util.DistanceCalculator;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
@Component("CLARKE_WRIGHT")
public class ClarkeWrightOptimizer implements TourOptimizer {

    /**
     * Classe interne privée pour stocker une "économie" potentielle.
     */
    private static class Saving {
        Delivery deliveryA;
        Delivery deliveryB;
        double savingAmount;

        // On utilise le pattern Builder, comme pour nos entités
        public static SavingBuilder builder() {
            return new SavingBuilder();
        }

        // (Implémentation du Builder... vous pouvez aussi utiliser @Builder si vous
        // ajoutez Lombok et les constructeurs, mais faisons-le manuellement
        // pour cette petite classe privée)
        public static class SavingBuilder {
            private Delivery deliveryA;
            private Delivery deliveryB;
            private double savingAmount;

            public SavingBuilder deliveryA(Delivery a) { this.deliveryA = a; return this; }
            public SavingBuilder deliveryB(Delivery b) { this.deliveryB = b; return this; }
            public SavingBuilder savingAmount(double s) { this.savingAmount = s; return this; }

            public Saving build() {
                Saving s = new Saving();
                s.deliveryA = this.deliveryA;
                s.deliveryB = this.deliveryB;
                s.savingAmount = this.savingAmount;
                return s;
            }
        }
    }

    @Override
    public List<Delivery> calculateOptimalTour(Warehouse warehouse, List<Delivery> deliveries, Vehicle vehicle) {

        if (deliveries == null || deliveries.isEmpty()) {
            return new ArrayList<>(); // Instanciation locale autorisée
        }

        // --- ÉTAPE 1: Calculer toutes les économies ---
        List<Saving> savingsList = new ArrayList<>();

        for (int i = 0; i < deliveries.size(); i++) {
            for (int j = i + 1; j < deliveries.size(); j++) {

                Delivery deliveryA = deliveries.get(i);
                Delivery deliveryB = deliveries.get(j);

                // Économie = Dist(WH,A) + Dist(WH,B) - Dist(A,B)
                double distWA = getDistance(warehouse, deliveryA);
                double distWB = getDistance(warehouse, deliveryB);
                double distAB = getDistance(deliveryA, deliveryB);

                double savingAmount = distWA + distWB - distAB;

                if (savingAmount > 0) {
                    savingsList.add(
                            Saving.builder()
                                    .deliveryA(deliveryA)
                                    .deliveryB(deliveryB)
                                    .savingAmount(savingAmount)
                                    .build()
                    );
                }
            }
        }

        // Trier les économies par ordre décroissant
        savingsList.sort((s1, s2) -> Double.compare(s2.savingAmount, s1.savingAmount));

        // --- ÉTAPE 2: Initialiser les tournées (1 par livraison) ---
        // On utilise une Map pour lier chaque livraison à sa tournée (qui est une Liste)
        Map<Delivery, List<Delivery>> tours = new HashMap<>();
        for (Delivery delivery : deliveries) {
            List<Delivery> singleTour = new ArrayList<>();
            singleTour.add(delivery);
            tours.put(delivery, singleTour);
        }

        // --- ÉTAPE 3: Fusionner les tournées ---
        for (Saving saving : savingsList) {
            Delivery deliveryA = saving.deliveryA;
            Delivery deliveryB = saving.deliveryB;

            List<Delivery> tourA = tours.get(deliveryA);
            List<Delivery> tourB = tours.get(deliveryB);

            // 1. Vérifier s'ils sont déjà dans la même tournée
            if (tourA == tourB) {
                continue;
            }

            // 2. Vérifier les conditions de fusion (A en fin, B en début ou vice-versa)

            boolean aIsAtEnd = tourA.get(tourA.size() - 1) == deliveryA;
            boolean bIsAtStart = tourB.get(0) == deliveryB;

            boolean bIsAtEnd = tourB.get(tourB.size() - 1) == deliveryB;
            boolean aIsAtStart = tourA.get(0) == deliveryA;

            List<Delivery> mergedTour = null;
            List<Delivery> obsoleteTour = null;

            if (aIsAtEnd && bIsAtStart) {
                // Fusion A -> B
                tourA.addAll(tourB);
                mergedTour = tourA;
                obsoleteTour = tourB;
            } else if (bIsAtEnd && aIsAtStart) {
                // Fusion B -> A
                tourB.addAll(tourA);
                mergedTour = tourB;
                obsoleteTour = tourA;
            }

            // 3. Mettre à jour la map si une fusion a eu lieu
            if (mergedTour != null) {
                for (Delivery deliveryInMergedTour : mergedTour) {
                    tours.put(deliveryInMergedTour, mergedTour);
                }
            }
        }

        // --- ÉTAPE 4: Retourner la tournée finale ---
        // À la fin, toutes les livraisons devraient pointer vers la même liste
        return tours.get(deliveries.get(0));
    }

    // Fonctions utilitaires privées pour la lisibilité

    private double getDistance(Warehouse warehouse, Delivery delivery) {
        return DistanceCalculator.calculateDistance(
                warehouse.getLatitude(), warehouse.getLongitude(),
                delivery.getLatitude(), delivery.getLongitude()
        );
    }

    private double getDistance(Delivery deliveryA, Delivery deliveryB) {
        return DistanceCalculator.calculateDistance(
                deliveryA.getLatitude(), deliveryA.getLongitude(),
                deliveryB.getLatitude(), deliveryB.getLongitude()
        );
    }
}