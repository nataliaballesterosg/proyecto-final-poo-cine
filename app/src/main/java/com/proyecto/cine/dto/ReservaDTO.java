package com.proyecto.cine.dto;

import com.proyecto.cine.modelo.Reserva;

import java.time.LocalDateTime;

/** Datos de una reserva que devuelve la API. */
public record ReservaDTO(Long id, Long funcionId, String silla, String cliente, LocalDateTime fechaReserva) {

    public static ReservaDTO desde(Reserva r) {
        return new ReservaDTO(r.getId(), r.getFuncion().getId(), r.nombreSilla(), r.getCliente(), r.getFechaReserva());
    }
}
