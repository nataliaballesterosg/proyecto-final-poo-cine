package com.proyecto.cine.controlador;

import com.proyecto.cine.dto.FuncionDTO;
import com.proyecto.cine.dto.PaginaDTO;
import com.proyecto.cine.dto.PeliculaDTO;
import com.proyecto.cine.dto.PeliculaRequest;
import com.proyecto.cine.servicio.FuncionService;
import com.proyecto.cine.servicio.PeliculaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoints REST de peliculas: /api/peliculas */
@RestController
@RequestMapping("/api/peliculas")
public class PeliculaController {

    private final PeliculaService peliculaService;
    private final FuncionService funcionService;

    public PeliculaController(PeliculaService peliculaService, FuncionService funcionService) {
        this.peliculaService = peliculaService;
        this.funcionService = funcionService;
    }

    @GetMapping
    public PaginaDTO<PeliculaDTO> listar(@RequestParam(defaultValue = "0") int pagina,
                                         @RequestParam(defaultValue = "20") int tamanio) {
        return peliculaService.listar(pagina, tamanio);
    }

    @GetMapping("/{id}")
    public PeliculaDTO obtener(@PathVariable Long id) {
        return peliculaService.obtener(id);
    }

    @GetMapping("/buscar")
    public PaginaDTO<PeliculaDTO> buscar(@RequestParam String titulo,
                                         @RequestParam(defaultValue = "0") int pagina,
                                         @RequestParam(defaultValue = "20") int tamanio) {
        return peliculaService.buscarPorTitulo(titulo, pagina, tamanio);
    }

    /** Cartelera de una pelicula: todas sus funciones. */
    @GetMapping("/{id}/funciones")
    public List<FuncionDTO> funciones(@PathVariable Long id) {
        return funcionService.deLaPelicula(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PeliculaDTO crear(@Valid @RequestBody PeliculaRequest datos) {
        return peliculaService.crear(datos);
    }

    @PutMapping("/{id}")
    public PeliculaDTO actualizar(@PathVariable Long id, @Valid @RequestBody PeliculaRequest datos) {
        return peliculaService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        peliculaService.eliminar(id);
    }
}
