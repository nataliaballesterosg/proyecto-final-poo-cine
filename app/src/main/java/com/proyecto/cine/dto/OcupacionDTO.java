package com.proyecto.cine.dto;

/** Ocupacion acumulada de una pelicula en todas sus funciones. */
public record OcupacionDTO(Long peliculaId, String titulo, long funciones, long capacidad,
                           long sillasReservadas, double porcentajeOcupacion) {
}
