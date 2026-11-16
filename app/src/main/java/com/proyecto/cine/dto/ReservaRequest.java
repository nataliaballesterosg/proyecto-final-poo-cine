package com.proyecto.cine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Datos que recibe la API para reservar una silla. La fila 1 es la "A". */
public record ReservaRequest(@NotNull Long funcionId,
                             @NotNull @Positive Integer fila,
                             @NotNull @Positive Integer numero,
                             @NotBlank @Size(max = 80) String cliente) {
}
