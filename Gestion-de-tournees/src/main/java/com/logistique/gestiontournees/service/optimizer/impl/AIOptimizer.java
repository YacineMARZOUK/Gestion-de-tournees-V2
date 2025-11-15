package com.logistique.gestiontournees.service.optimizer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistique.gestiontournees.dto.AIResponseDTO;
import com.logistique.gestiontournees.entity.Delivery;
import com.logistique.gestiontournees.entity.DeliveryHistory;
import com.logistique.gestiontournees.entity.Vehicle;
import com.logistique.gestiontournees.entity.Warehouse;
import org.springframework.ai.chat.model.ChatModel;  // <- CHANGEMENT ICI
import com.logistique.gestiontournees.repository.DeliveryHistoryRepository;
import com.logistique.gestiontournees.service.optimizer.TourOptimizer;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("AI_OPTIMIZER")
public class AIOptimizer implements TourOptimizer {
    private final ChatModel chatModel;
    private final DeliveryHistoryRepository historyRepository;
    private final ObjectMapper objectMapper;

    // CONSTRUCTEUR MODIFIÉ
    public AIOptimizer(ChatModel chatModel,
                       DeliveryHistoryRepository historyRepository,
                       ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.historyRepository = historyRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Delivery> calculateOptimalTour(Warehouse warehouse, List<Delivery> deliveries, Vehicle vehicle) {
        List<DeliveryHistory> history = historyRepository.findFirst100ByOrderByDeliveryDateDesc();
        String promptText = buildPrompt(warehouse, deliveries, history);

        // APPEL MODIFIÉ
        String jsonResponse = chatModel.call(promptText);

        System.out.println("--- Réponse de l'IA ---");
        System.out.println(jsonResponse);
        System.out.println("---------------------");

        try {
            String cleanedJson = cleanAiResponse(jsonResponse);
            AIResponseDTO response = objectMapper.readValue(cleanedJson, AIResponseDTO.class);

            System.out.println("[Recommandations de l'IA] : " + response.getRecommendations());

            Map<Long, Delivery> deliveryMap = deliveries.stream()
                    .collect(Collectors.toMap(Delivery::getId, d -> d));

            List<Delivery> orderedList = response.getOrderedDeliveries().stream()
                    .map(dto -> deliveryMap.get(dto.getId()))
                    .collect(Collectors.toList());

            if (orderedList.size() != deliveries.size()) {
                System.err.println("ERREUR IA : Nombre de livraisons incorrect.");
                return deliveries;
            }

            return orderedList;

        } catch (Exception e) {
            System.err.println("ERREUR PARSING JSON : " + e.getMessage());
            return deliveries;
        }
    }

    private String buildPrompt(Warehouse warehouse, List<Delivery> deliveries, List<DeliveryHistory> history) {
        String warehouseJson = String.format(
                "{ \"address\": \"%s\", \"latitude\": %f, \"longitude\": %f }",
                warehouse.getAddress(), warehouse.getLatitude(), warehouse.getLongitude()
        );

        String deliveriesJson = deliveries.stream()
                .map(d -> String.format(
                        "{ \"id\": %d, \"address\": \"%s\", \"latitude\": %f, \"longitude\": %f, \"weight\": %f, \"timeSlot\": \"%s\" }",
                        d.getId(), d.getAddress(), d.getLatitude(), d.getLongitude(), d.getWeight(), d.getTimeSlot()
                ))
                .collect(Collectors.joining(",\n    ", "[\n    ", "\n  ]"));

        String historyJson = history.stream()
                .map(h -> String.format(
                        "{ \"address\": \"%s\", \"dayOfWeek\": \"%s\", \"plannedTime\": \"%s\", \"actualTime\": \"%s\", \"delayInMinutes\": %d }",
                        h.getAddress(), h.getDayOfWeek(), h.getPlannedTime(), h.getActualTime(), h.getDelayInMinutes()
                ))
                .collect(Collectors.joining(",\n    ", "[\n    ", "\n  ]"));

        return String.format(
                """
                Tu es un expert en logistique pour une société de livraison au Maroc.
                Ta mission est d'optimiser une tournée de livraison.
    
                ## CONTEXTE
                - L'entrepôt (point de départ et d'arrivée) est :
                  %s
                - Le véhicule a des contraintes (poids/volume) mais pour l'instant, ignore-les et concentre-toi sur l'ordre.
                
                ## HISTORIQUE DES LIVRAISONS (POUR APPRENDRE)
                Voici les 100 dernières livraisons, note bien les retards (delayInMinutes) :
                %s
                
                ## LIVRAISONS DU JOUR (À OPTIMISER)
                Voici la liste des livraisons à effectuer aujourd'hui. L'ordre actuel est aléatoire.
                %s
                
                ## TA MISSION
                1. Analyse l'historique pour identifier des schémas (ex: retards fréquents à une certaine adresse, créneaux horaires difficiles).
                2. Crée l'ordre de livraison le plus efficace (le plus rapide) pour les "LIVRAISONS DU JOUR".
                3. Priorise les livraisons ayant des créneaux horaires (timeSlot) stricts.
                4. Utilise l'historique pour prédire les retards et éviter les zones problématiques aux heures de pointe.
                
                ## FORMAT DE RÉPONSE OBLIGATOIRE
                Tu DOIS répondre UNIQUEMENT avec un objet JSON valide. Ne dis RIEN d'autre.
                Le JSON doit avoir cette structure exacte :
                {
                  "orderedDeliveries": [
                    { "id": 123, "address": "Adresse A" },
                    { "id": 456, "address": "Adresse B" }
                  ],
                  "recommendations": "Voici pourquoi j'ai choisi cet ordre..."
                }
                
                Assure-toi que la liste "orderedDeliveries" contient TOUTES les livraisons du jour, ni plus, ni moins.
                """, warehouseJson, historyJson, deliveriesJson
        );
    }

    private String cleanAiResponse(String response) {
        int startIndex = response.indexOf("{");
        int endIndex = response.lastIndexOf("}");

        if (startIndex != -1 && endIndex != -1) {
            return response.substring(startIndex, endIndex + 1);
        }
        return response;
    }
}