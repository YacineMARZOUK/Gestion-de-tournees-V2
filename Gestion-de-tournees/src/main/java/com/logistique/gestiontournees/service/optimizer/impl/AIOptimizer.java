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
        // Validation préalable
        if (deliveries == null || deliveries.isEmpty()) {
            System.err.println("ERREUR: Liste de livraisons vide ou nulle");
            return deliveries;
        }

        List<DeliveryHistory> history = historyRepository.findFirst100ByOrderByDeliveryDateDesc();
        String promptText = buildPrompt(warehouse, deliveries, history);

        String jsonResponse = chatModel.call(promptText);
        System.out.println("--- Réponse de l'IA ---");
        System.out.println(jsonResponse);
        System.out.println("---------------------");

        try {
            String cleanedJson = cleanAiResponse(jsonResponse);
            AIResponseDTO response = objectMapper.readValue(cleanedJson, AIResponseDTO.class);
            System.out.println("[Recommandations de l'IA] : " + response.getRecommendations());

            // Créer la map AVANT de traiter la réponse
            Map<Long, Delivery> deliveryMap = deliveries.stream()
                    .collect(Collectors.toMap(Delivery::getId, d -> d));

            // CORRECTION CRITIQUE: Vérifier chaque ID
            List<Delivery> orderedList = response.getOrderedDeliveries().stream()
                    .map(dto -> {
                        Delivery delivery = deliveryMap.get(dto.getId());
                        if (delivery == null) {
                            System.err.println("ATTENTION: Delivery ID " + dto.getId() + " introuvable!");
                        }
                        return delivery;
                    })
                    .filter(d -> d != null) // Filtrer les nulls
                    .collect(Collectors.toList());

            // Vérification stricte du nombre
            if (orderedList.size() != deliveries.size()) {
                System.err.println("ERREUR IA : Nombre de livraisons incorrect ("
                        + orderedList.size() + " vs " + deliveries.size() + ")");
                System.err.println("IDs reçus: " + response.getOrderedDeliveries().stream()
                        .map(d -> d.getId()).collect(Collectors.toList()));
                System.err.println("IDs attendus: " + deliveryMap.keySet());
                return deliveries; // Retourner l'ordre original
            }

            return orderedList;

        } catch (Exception e) {
            System.err.println("ERREUR PARSING JSON : " + e.getMessage());
            e.printStackTrace();
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
                .collect(Collectors.joining(",\n  ", "[\n  ", "\n]"));

        String historyJson = history.stream()
                .map(h -> String.format(
                        "{ \"address\": \"%s\", \"dayOfWeek\": \"%s\", \"plannedTime\": \"%s\", \"actualTime\": \"%s\", \"delayInMinutes\": %d }",
                        h.getAddress(), h.getDayOfWeek(), h.getPlannedTime(), h.getActualTime(), h.getDelayInMinutes()
                ))
                .collect(Collectors.joining(",\n  ", "[\n  ", "\n]"));

        // IMPORTANT: Lister explicitement les IDs valides
        String validIds = deliveries.stream()
                .map(d -> String.valueOf(d.getId()))
                .collect(Collectors.joining(", "));

        return String.format(
                """
                Tu es un outil d'optimisation de tournées. Ta tâche est de RÉORDONNER une liste de livraisons.
                
                DONNÉES:
                - Entrepôt: %s
                - Livraisons à trier: %s
                - Historique des retards: %s
                
                RÈGLES CRITIQUES:
                1. Tu DOIS utiliser UNIQUEMENT ces IDs: [%s]
                2. Ta réponse DOIT contenir EXACTEMENT %d livraisons
                3. Trie par efficacité: créneaux horaires d'abord, puis proximité géographique
                4. Si l'historique montre des retards fréquents (>90 min) pour une adresse, place-la en fin de tournée
                
                FORMAT DE RÉPONSE (JSON STRICT):
                {
                  "orderedDeliveries": [
                    { "id": 123, "address": "Adresse exacte" },
                    { "id": 456, "address": "Autre adresse" }
                  ],
                  "recommendations": "Explication brève du tri"
                }
                
                ATTENTION: Vérifie que tu renvoies bien tous les IDs listés ci-dessus, sans en ajouter ni en oublier.
                """,
                warehouseJson, deliveriesJson, historyJson, validIds, deliveries.size()
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