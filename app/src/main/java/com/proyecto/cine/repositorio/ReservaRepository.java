package com.proyecto.cine.repositorio;

import com.proyecto.cine.modelo.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/** Acceso a datos de reservas. */
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    boolean existsByFuncionIdAndFilaAndNumero(Long funcionId, Integer fila, Integer numero);

    /** Sillas ocupadas de una funcion, como pares [fila, numero]. */
    @Query("SELECT r.fila, r.numero FROM Reserva r WHERE r.funcion.id = :funcionId")
    List<Object[]> sillasOcupadas(@Param("funcionId") Long funcionId);
}
