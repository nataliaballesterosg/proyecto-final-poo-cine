package com.proyecto.cine.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/** Respuesta paginada generica (contenido + datos de la pagina). */
public record PaginaDTO<T>(List<T> contenido, int pagina, int tamanio, long totalElementos, int totalPaginas) {

    public static <E, T> PaginaDTO<T> desde(Page<E> page, Function<E, T> conversor) {
        return new PaginaDTO<>(page.getContent().stream().map(conversor).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
