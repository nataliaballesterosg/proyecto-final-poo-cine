package com.proyecto.cine.excepcion;

/** Se lanza cuando se busca un recurso (pelicula, funcion, reserva) que no existe. Responde 404. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Long id) {
        super(recurso + " con id " + id + " no existe");
    }
}
