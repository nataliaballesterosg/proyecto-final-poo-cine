package com.proyecto.cine.excepcion;

/** Se lanza cuando se intenta reservar una silla que ya tiene reserva. Responde 409. */
public class SillaOcupadaException extends RuntimeException {

    public SillaOcupadaException(String silla, Long funcionId) {
        super("La silla " + silla + " de la funcion " + funcionId + " ya esta ocupada");
    }
}
