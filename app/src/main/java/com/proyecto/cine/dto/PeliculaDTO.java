package com.proyecto.cine.dto;

import com.proyecto.cine.modelo.Pelicula;

/** Datos de una pelicula que devuelve la API (no se expone la entidad directamente). */
public record PeliculaDTO(Long id, String titulo, String genero, Integer duracionMinutos,
                          String clasificacion, Integer anio) {

    public static PeliculaDTO desde(Pelicula p) {
        return new PeliculaDTO(p.getId(), p.getTitulo(), p.getGenero(), p.getDuracionMinutos(),
                p.getClasificacion(), p.getAnio());
    }
}
