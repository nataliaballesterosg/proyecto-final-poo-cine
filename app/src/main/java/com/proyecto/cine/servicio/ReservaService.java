package com.proyecto.cine.servicio;

import com.proyecto.cine.dto.ReservaDTO;
import com.proyecto.cine.dto.ReservaRequest;
import com.proyecto.cine.excepcion.RecursoNoEncontradoException;
import com.proyecto.cine.excepcion.SillaInvalidaException;
import com.proyecto.cine.excepcion.SillaOcupadaException;
import com.proyecto.cine.modelo.Funcion;
import com.proyecto.cine.modelo.Reserva;
import com.proyecto.cine.modelo.Silla;
import com.proyecto.cine.repositorio.FuncionRepository;
import com.proyecto.cine.repositorio.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Logica de negocio de las reservas (operaciones de escritura). */
@Service
@Transactional(readOnly = true)
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final FuncionRepository funcionRepository;

    public ReservaService(ReservaRepository reservaRepository, FuncionRepository funcionRepository) {
        this.reservaRepository = reservaRepository;
        this.funcionRepository = funcionRepository;
    }

    /**
     * Reserva una silla. Reglas de negocio:
     *  1. La funcion debe existir.
     *  2. La silla debe existir en la sala de esa funcion.
     *  3. La silla no puede estar ocupada. Se revisa antes de guardar y, ademas, la
     *     restriccion UNIQUE de la base de datos lo garantiza si dos peticiones llegan
     *     al mismo tiempo (en ese caso el ManejadorErrores responde 409).
     */
    @Transactional
    public ReservaDTO reservar(ReservaRequest datos) {
        Funcion funcion = funcionRepository.findCompletaById(datos.funcionId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Funcion", datos.funcionId()));

        if (!funcion.getSala().tieneSilla(datos.fila(), datos.numero())) {
            throw new SillaInvalidaException("La silla (fila " + datos.fila() + ", numero " + datos.numero()
                    + ") no existe en la " + funcion.getSala().getNombre() + ", que tiene "
                    + funcion.getSala().getFilas() + " filas y " + funcion.getSala().getSillasPorFila() + " sillas por fila");
        }
        if (reservaRepository.existsByFuncionIdAndFilaAndNumero(funcion.getId(), datos.fila(), datos.numero())) {
            throw new SillaOcupadaException(Silla.nombre(datos.fila(), datos.numero()), funcion.getId());
        }
        Reserva reserva = reservaRepository.saveAndFlush(
                new Reserva(funcion, datos.fila(), datos.numero(), datos.cliente()));
        return ReservaDTO.desde(reserva);
    }

    public ReservaDTO obtener(Long id) {
        return ReservaDTO.desde(buscar(id));
    }

    /** Cancela una reserva: la silla vuelve a quedar libre. */
    @Transactional
    public void cancelar(Long id) {
        reservaRepository.delete(buscar(id));
    }

    private Reserva buscar(Long id) {
        return reservaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Reserva", id));
    }
}
