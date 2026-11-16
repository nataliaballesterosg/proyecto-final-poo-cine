package com.proyecto.cine.controlador;

import com.proyecto.cine.dto.OcupacionDTO;
import com.proyecto.cine.servicio.PeliculaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Endpoints REST de estadisticas: /api/estadisticas */
@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticaController {

    private final PeliculaService peliculaService;

    public EstadisticaController(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    /** Consulta pesada: ocupacion de cada pelicula (une 4 tablas y agrupa). */
    @GetMapping("/ocupacion")
    public List<OcupacionDTO> ocupacion() {
        return peliculaService.ocupacion();
    }
}
