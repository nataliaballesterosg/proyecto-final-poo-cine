package com.proyecto.cine.servicio;

import com.proyecto.cine.dto.FuncionDTO;
import com.proyecto.cine.dto.MapaSillasDTO;
import com.proyecto.cine.excepcion.RecursoNoEncontradoException;
import com.proyecto.cine.modelo.Funcion;
import com.proyecto.cine.modelo.Sala;
import com.proyecto.cine.modelo.Silla;
import com.proyecto.cine.repositorio.FuncionRepository;
import com.proyecto.cine.repositorio.PeliculaRepository;
import com.proyecto.cine.repositorio.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/** Logica de negocio de las funciones y del mapa de sillas. */
@Service
@Transactional(readOnly = true)
public class FuncionService {

    private final FuncionRepository funcionRepository;
    private final PeliculaRepository peliculaRepository;
    private final ReservaRepository reservaRepository;

    public FuncionService(FuncionRepository funcionRepository, PeliculaRepository peliculaRepository,
                          ReservaRepository reservaRepository) {
        this.funcionRepository = funcionRepository;
        this.peliculaRepository = peliculaRepository;
        this.reservaRepository = reservaRepository;
    }

    public FuncionDTO obtener(Long id) {
        return FuncionDTO.desde(buscar(id));
    }

    public List<FuncionDTO> deLaPelicula(Long peliculaId) {
        if (!peliculaRepository.existsById(peliculaId)) {
            throw new RecursoNoEncontradoException("Pelicula", peliculaId);
        }
        return funcionRepository.findByPeliculaIdOrderByFechaHora(peliculaId).stream().map(FuncionDTO::desde).toList();
    }

    /** Construye el mapa de la sala marcando con '#' las sillas ocupadas y con '.' las libres. */
    public MapaSillasDTO mapaDeSillas(Long funcionId) {
        Funcion funcion = buscar(funcionId);
        Sala sala = funcion.getSala();

        boolean[][] ocupada = new boolean[sala.getFilas() + 1][sala.getSillasPorFila() + 1];
        List<Object[]> reservadas = reservaRepository.sillasOcupadas(funcionId);
        for (Object[] silla : reservadas) {
            ocupada[(Integer) silla[0]][(Integer) silla[1]] = true;
        }

        List<String> mapa = new ArrayList<>();
        for (int fila = 1; fila <= sala.getFilas(); fila++) {
            StringBuilder linea = new StringBuilder().append(Silla.letraFila(fila)).append(' ');
            for (int numero = 1; numero <= sala.getSillasPorFila(); numero++) {
                linea.append(' ').append(ocupada[fila][numero] ? '#' : '.');
            }
            mapa.add(linea.toString());
        }
        return new MapaSillasDTO(funcion.getId(), funcion.getPelicula().getTitulo(), sala.getNombre(),
                funcion.getFechaHora(), sala.capacidad(), reservadas.size(),
                sala.capacidad() - reservadas.size(), mapa);
    }

    private Funcion buscar(Long id) {
        return funcionRepository.findCompletaById(id).orElseThrow(() -> new RecursoNoEncontradoException("Funcion", id));
    }
}
