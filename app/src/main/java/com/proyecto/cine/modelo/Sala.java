package com.proyecto.cine.modelo;

import jakarta.persistence.*;

/**
 * Entidad que representa una sala del cine, con una cuadricula de sillas
 * (filas identificadas con letras A, B, C... y sillas numeradas 1, 2, 3...).
 */
@Entity
@Table(name = "salas")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String nombre;

    @Column(nullable = false)
    private Integer filas;

    @Column(name = "sillas_por_fila", nullable = false)
    private Integer sillasPorFila;

    protected Sala() {
        // requerido por JPA
    }

    /** Numero total de sillas de la sala. */
    public int capacidad() {
        return filas * sillasPorFila;
    }

    /** Indica si la silla (fila, numero) existe en esta sala. */
    public boolean tieneSilla(int fila, int numero) {
        return fila >= 1 && fila <= filas && numero >= 1 && numero <= sillasPorFila;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public Integer getFilas() { return filas; }
    public Integer getSillasPorFila() { return sillasPorFila; }
}
