package com.proyecto.cine.dto;

import com.proyecto.cine.modelo.Funcion;

import java.time.LocalDateTime;

/** Datos de una funcion que devuelve la API. */
public record FuncionDTO(Long id, Long peliculaId, String pelicula, String sala,
                         LocalDateTime fechaHora, Integer precio) {

    public static FuncionDTO desde(Funcion f) {
        return new FuncionDTO(f.getId(), f.getPelicula().getId(), f.getPelicula().getTitulo(),
                f.getSala().getNombre(), f.getFechaHora(), f.getPrecio());
    }
}
