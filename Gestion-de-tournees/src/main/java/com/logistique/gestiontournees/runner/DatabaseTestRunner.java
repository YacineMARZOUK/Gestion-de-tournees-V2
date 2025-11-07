package com.logistique.gestiontournees.runner;

import com.logistique.gestiontournees.entity.Warehouse; // Importez l'ENTITÉ
import com.logistique.gestiontournees.repository.WarehouseRepository; // Importez le REPOSITORY
import org.springframework.boot.CommandLineRunner;
import java.time.LocalTime;

/**
 * Ce bean crée l'entrepôt de base (ID=1) au démarrage.
 */
public class DatabaseTestRunner implements CommandLineRunner {

    // 1. Injecter le REPOSITORY directement
    private final WarehouseRepository warehouseRepository;

    // 2. Le constructeur n'a besoin que du repository
    public DatabaseTestRunner(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public void run(String... args) throws Exception {

    }
}