package com.proyecto.cine.controlador;

import com.proyecto.cine.dto.FuncionDTO;
import com.proyecto.cine.dto.MapaSillasDTO;
import com.proyecto.cine.servicio.FuncionService;
import org.springframework.web.bind.annotation.*;

/** Endpoints REST de funciones: /api/funciones */
@RestController
@RequestMapping("/api/funciones")
public class FuncionController {

    private final FuncionService funcionService;

    public FuncionController(FuncionService funcionService) {
        this.funcionService = funcionService;
    }

    @GetMapping("/{id}")
    public FuncionDTO obtener(@PathVariable Long id) {
        return funcionService.obtener(id);
    }

    /** Mapa de la sala con las sillas libres (.) y ocupadas (#). */
    @GetMapping("/{id}/sillas")
    public MapaSillasDTO sillas(@PathVariable Long id) {
        return funcionService.mapaDeSillas(id);
    }
}
