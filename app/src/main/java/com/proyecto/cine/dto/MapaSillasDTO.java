package com.proyecto.cine.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapa de sillas de una funcion. Cada linea de "mapa" es una fila de la sala:
 *   "C  . . # # . ."   ->  '#' = ocupada, '.' = libre
 */
public record MapaSillasDTO(Long funcionId, String pelicula, String sala, LocalDateTime fechaHora,
                            int capacidad, int ocupadas, int disponibles, List<String> mapa) {
}
