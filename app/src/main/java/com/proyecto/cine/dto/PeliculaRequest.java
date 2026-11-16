package com.proyecto.cine.dto;

import jakarta.validation.constraints.*;

/** Datos que recibe la API para crear o actualizar una pelicula. */
public record PeliculaRequest(
        @NotBlank @Size(max = 150) String titulo,
        @NotBlank @Size(max = 40) String genero,
        @NotNull @Min(30) @Max(300) Integer duracionMinutos,
        @NotBlank @Size(max = 10) String clasificacion,
        @Min(1900) @Max(2100) Integer anio) {
}
