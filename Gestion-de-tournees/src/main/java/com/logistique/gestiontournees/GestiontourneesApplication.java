package com.logistique.gestiontournees;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication; // 1. Restaurez ceci
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication // 2. Utilisez l'annotation simple
@ImportResource("classpath:applicationContext.xml") // 3. Gardez juste le XML
public class GestiontourneesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestiontourneesApplication.class, args);
    }
}