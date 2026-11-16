package com.proyecto.cine.modelo;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa la reserva de UNA silla para UNA funcion.
 * La base de datos tiene una restriccion UNIQUE (funcion_id, fila, numero):
 * es imposible que dos reservas ocupen la misma silla, aunque lleguen a la vez
 * a replicas distintas de la API.
 */
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "funcion_id")
    private Funcion funcion;

    @Column(nullable = false)
    private Integer fila;

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false, length = 80)
    private String cliente;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDateTime fechaReserva;

    protected Reserva() {
        // requerido por JPA
    }

    public Reserva(Funcion funcion, int fila, int numero, String cliente) {
        this.funcion = funcion;
        this.fila = fila;
        this.numero = numero;
        this.cliente = cliente;
        this.fechaReserva = LocalDateTime.now();
    }

    /** Nombre de la silla tal como aparece en el boleto, por ejemplo "C7". */
    public String nombreSilla() {
        return Silla.nombre(fila, numero);
    }

    public Long getId() { return id; }
    public Funcion getFuncion() { return funcion; }
    public Integer getFila() { return fila; }
    public Integer getNumero() { return numero; }
    public String getCliente() { return cliente; }
    public LocalDateTime getFechaReserva() { return fechaReserva; }
}
