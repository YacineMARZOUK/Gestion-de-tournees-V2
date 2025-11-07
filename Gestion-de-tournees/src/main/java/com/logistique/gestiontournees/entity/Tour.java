package com.logistique.gestiontournees.entity;

import jakarta.persistence.*;
import lombok.*; // Importer tout Lombok

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate tourDate;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderColumn(name = "delivery_order")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Delivery> deliveries = new ArrayList<>();
}

