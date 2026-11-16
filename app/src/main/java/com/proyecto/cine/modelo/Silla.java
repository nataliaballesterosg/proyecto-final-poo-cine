package com.proyecto.cine.modelo;

/** Utilidades para nombrar sillas: la fila 1 es "A", la 2 es "B", etc. */
public final class Silla {

    private Silla() {
    }

    public static char letraFila(int fila) {
        return (char) ('A' + fila - 1);
    }

    public static String nombre(int fila, int numero) {
        return letraFila(fila) + String.valueOf(numero);
    }
}
