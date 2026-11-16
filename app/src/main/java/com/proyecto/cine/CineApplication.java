package com.proyecto.cine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la API REST de reservas del Cine.
 */
@SpringBootApplication
public class CineApplication {

    public static void main(String[] args) {
        SpringApplication.run(CineApplication.class, args);
    }
}
