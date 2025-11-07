package com.logistique.gestiontournees.service.optimizer;

import com.logistique.gestiontournees.entity.Delivery;
import com.logistique.gestiontournees.entity.Vehicle;
import com.logistique.gestiontournees.entity.Warehouse;

import java.util.List;

public interface TourOptimizer {
    List<Delivery> calculateOptimalTour(Warehouse warehouse, List<Delivery> deliveries, Vehicle vehicle);
}

