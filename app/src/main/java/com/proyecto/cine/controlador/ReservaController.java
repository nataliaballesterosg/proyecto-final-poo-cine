package com.proyecto.cine.controlador;

import com.proyecto.cine.dto.ReservaDTO;
import com.proyecto.cine.dto.ReservaRequest;
import com.proyecto.cine.servicio.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Endpoints REST de reservas: /api/reservas */
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /** Reserva una silla. 201 si se reservo, 409 si ya estaba ocupada. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaDTO reservar(@Valid @RequestBody ReservaRequest datos) {
        return reservaService.reservar(datos);
    }

    @GetMapping("/{id}")
    public ReservaDTO obtener(@PathVariable Long id) {
        return reservaService.obtener(id);
    }

    /** Cancela la reserva y libera la silla. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(@PathVariable Long id) {
        reservaService.cancelar(id);
    }
}
