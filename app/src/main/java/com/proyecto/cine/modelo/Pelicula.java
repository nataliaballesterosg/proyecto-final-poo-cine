package com.proyecto.cine.modelo;

import jakarta.persistence.*;

/**
 * Entidad que representa una pelicula de la cartelera.
 */
@Entity
@Table(name = "peliculas")
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 40)
    private String genero;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    @Column(nullable = false, length = 10)
    private String clasificacion;

    private Integer anio;

    protected Pelicula() {
        // requerido por JPA
    }

    public Pelicula(String titulo, String genero, Integer duracionMinutos, String clasificacion, Integer anio) {
        this.titulo = titulo;
        this.genero = genero;
        this.duracionMinutos = duracionMinutos;
        this.clasificacion = clasificacion;
        this.anio = anio;
    }

    /** Actualiza los datos de la pelicula (comportamiento propio del objeto). */
    public void actualizar(String titulo, String genero, Integer duracionMinutos, String clasificacion, Integer anio) {
        this.titulo = titulo;
        this.genero = genero;
        this.duracionMinutos = duracionMinutos;
        this.clasificacion = clasificacion;
        this.anio = anio;
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getGenero() { return genero; }
    public Integer getDuracionMinutos() { return duracionMinutos; }
    public String getClasificacion() { return clasificacion; }
    public Integer getAnio() { return anio; }
}
