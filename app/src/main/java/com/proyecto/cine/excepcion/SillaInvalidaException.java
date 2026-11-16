package com.proyecto.cine.excepcion;

/** Se lanza cuando la silla pedida no existe en la sala. Responde 400. */
public class SillaInvalidaException extends RuntimeException {

    public SillaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
