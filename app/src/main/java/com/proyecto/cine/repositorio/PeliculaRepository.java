package com.proyecto.cine.repositorio;

import com.proyecto.cine.modelo.Pelicula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/** Acceso a datos de peliculas. Spring Data genera la implementacion. */
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {

    /** Busqueda por parte del titulo, sin distinguir mayusculas. */
    Page<Pelicula> findByTituloContainingIgnoreCase(String texto, Pageable pageable);

    /**
     * Consulta pesada: une peliculas, funciones, salas y reservas para calcular
     * cuantas sillas se han vendido de cada pelicula frente a su capacidad total.
     */
    @Query(value = """
            SELECT p.id                                 AS peliculaId,
                   p.titulo                             AS titulo,
                   COUNT(f.id)                          AS funciones,
                   SUM(s.filas * s.sillas_por_fila)     AS capacidad,
                   COALESCE(SUM(oc.reservadas), 0)      AS reservadas
            FROM peliculas p
            JOIN funciones f ON f.pelicula_id = p.id
            JOIN salas s     ON s.id = f.sala_id
            LEFT JOIN (SELECT funcion_id, COUNT(*) AS reservadas
                       FROM reservas GROUP BY funcion_id) oc ON oc.funcion_id = f.id
            GROUP BY p.id, p.titulo
            ORDER BY COALESCE(SUM(oc.reservadas), 0) * 1.0 / SUM(s.filas * s.sillas_por_fila) DESC
            """, nativeQuery = true)
    List<OcupacionPelicula> ocupacionPorPelicula();

    /** Proyeccion (solo lectura) con el resultado de la consulta anterior. */
    interface OcupacionPelicula {
        Long getPeliculaId();
        String getTitulo();
        Long getFunciones();
        Long getCapacidad();
        Long getReservadas();
    }
}
