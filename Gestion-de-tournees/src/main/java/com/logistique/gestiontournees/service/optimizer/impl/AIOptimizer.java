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

    // Dans AIOptimizer.java

    private String buildPrompt(Warehouse warehouse, List<Delivery> deliveries, List<DeliveryHistory> history) {
        // Les conversions en JSON (warehouseJson, deliveriesJson, historyJson)
        // restent les mêmes.

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
                Tu es un outil de tri JSON. Ta seule tâche est de trier une liste de livraisons.
                
                DONNÉES:
                - Entrepôt: %s
                - Livraisons à trier: %s
                - Historique des retards: %s
                
                RÈGLES DE TRI:
                1. Trie la liste "Livraisons à trier" pour qu'elle soit la plus efficace.
                2. Regarde l'historique. Si une adresse (ex: "Adresse A (Loin)") a des retards fréquents ("delayInMinutes" > 90), place-la à la FIN de la tournée.
                3. Respecte les "timeSlot" (créneaux horaires) en priorité (ex: "09:00-11:00" doit être au début).
                
                FORMAT DE RÉPONSE OBLIGATOIRE (JSON SEULEMENT):
                {
                  "orderedDeliveries": [
                    { "id": 123, "address": "Adresse B" },
                    { "id": 456, "address": "Adresse C" },
                    { "id": 789, "address": "Adresse A" }
                  ],
                  "recommendations": "J'ai trié par créneau horaire et mis l'Adresse A en dernier à cause des retards."
                }
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