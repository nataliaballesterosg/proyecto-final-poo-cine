package com.proyecto.cine.repositorio;

import com.proyecto.cine.modelo.Funcion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Acceso a datos de funciones. */
public interface FuncionRepository extends JpaRepository<Funcion, Long> {

    /** Carga la funcion junto con su pelicula y su sala en una sola consulta. */
    @EntityGraph(attributePaths = {"pelicula", "sala"})
    Optional<Funcion> findCompletaById(Long id);

    /** Funciones de una pelicula ordenadas por fecha (con la sala, evita el problema N+1). */
    @EntityGraph(attributePaths = {"pelicula", "sala"})
    List<Funcion> findByPeliculaIdOrderByFechaHora(Long peliculaId);
}
