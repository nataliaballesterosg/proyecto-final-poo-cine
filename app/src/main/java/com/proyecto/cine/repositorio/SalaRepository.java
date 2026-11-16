package com.proyecto.cine.repositorio;

import com.proyecto.cine.modelo.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a datos de salas. */
public interface SalaRepository extends JpaRepository<Sala, Long> {
}
