package com.logistique.gestiontournees.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;

    private LocalDate deliveryDate;
    private LocalTime plannedTime;
    private LocalTime actualTime;
    private Long delayInMinutes; // Durée du retard en minutes

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    // On peut aussi stocker l'ID de la livraison d'origine
    private Long originalDeliveryId;

    // Et l'adresse au moment de l'historique
    private String address;
}