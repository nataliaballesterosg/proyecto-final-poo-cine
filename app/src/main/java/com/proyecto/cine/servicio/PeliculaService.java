package com.proyecto.cine.servicio;

import com.proyecto.cine.dto.OcupacionDTO;
import com.proyecto.cine.dto.PaginaDTO;
import com.proyecto.cine.dto.PeliculaDTO;
import com.proyecto.cine.dto.PeliculaRequest;
import com.proyecto.cine.excepcion.RecursoNoEncontradoException;
import com.proyecto.cine.modelo.Pelicula;
import com.proyecto.cine.repositorio.PeliculaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Logica de negocio de las peliculas. Los controladores no acceden a los
 * repositorios directamente: siempre pasan por esta capa de servicio.
 */
@Service
@Transactional(readOnly = true)
public class PeliculaService {

    private final PeliculaRepository peliculaRepository;

    public PeliculaService(PeliculaRepository peliculaRepository) {
        this.peliculaRepository = peliculaRepository;
    }

    public PaginaDTO<PeliculaDTO> listar(int pagina, int tamanio) {
        return PaginaDTO.desde(peliculaRepository.findAll(paginacion(pagina, tamanio)), PeliculaDTO::desde);
    }

    public PeliculaDTO obtener(Long id) {
        return PeliculaDTO.desde(buscar(id));
    }

    public PaginaDTO<PeliculaDTO> buscarPorTitulo(String texto, int pagina, int tamanio) {
        return PaginaDTO.desde(peliculaRepository.findByTituloContainingIgnoreCase(texto, paginacion(pagina, tamanio)),
                PeliculaDTO::desde);
    }

    /** Ocupacion de cada pelicula: sillas reservadas / capacidad de todas sus funciones. */
    public List<OcupacionDTO> ocupacion() {
        return peliculaRepository.ocupacionPorPelicula().stream()
                .map(o -> new OcupacionDTO(o.getPeliculaId(), o.getTitulo(), o.getFunciones(), o.getCapacidad(),
                        o.getReservadas(), Math.round(10000.0 * o.getReservadas() / o.getCapacidad()) / 100.0))
                .toList();
    }

    @Transactional
    public PeliculaDTO crear(PeliculaRequest d) {
        Pelicula p = new Pelicula(d.titulo(), d.genero(), d.duracionMinutos(), d.clasificacion(), d.anio());
        return PeliculaDTO.desde(peliculaRepository.save(p));
    }

    @Transactional
    public PeliculaDTO actualizar(Long id, PeliculaRequest d) {
        Pelicula p = buscar(id);
        p.actualizar(d.titulo(), d.genero(), d.duracionMinutos(), d.clasificacion(), d.anio());
        return PeliculaDTO.desde(p);
    }

    @Transactional
    public void eliminar(Long id) {
        peliculaRepository.delete(buscar(id));
    }

    private Pelicula buscar(Long id) {
        return peliculaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Pelicula", id));
    }

    private PageRequest paginacion(int pagina, int tamanio) {
        return PageRequest.of(Math.max(0, pagina), Math.max(1, Math.min(tamanio, 100)), Sort.by("id"));
    }
}
