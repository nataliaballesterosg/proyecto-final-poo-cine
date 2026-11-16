package com.proyecto.cine.modelo;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa una funcion: una pelicula proyectada en una sala a una hora.
 */
@Entity
@Table(name = "funciones")
public class Funcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pelicula_id")
    private Pelicula pelicula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_id")
    private Sala sala;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private Integer precio;

    protected Funcion() {
        // requerido por JPA
    }

    public Long getId() { return id; }
    public Pelicula getPelicula() { return pelicula; }
    public Sala getSala() { return sala; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public Integer getPrecio() { return precio; }
}
