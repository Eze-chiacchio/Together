package com.chiacchio.together.Model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "viajes")
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ciudadOrigen;

    @Column(nullable = false)
    private String ciudadDestino;

    @Column(nullable = false)
    private LocalDateTime horarioEstimado;

    @Column(nullable = false)
    private Integer cuposDisponibles;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario conductor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCiudadOrigen() {
        return ciudadOrigen;
    }

    public void setCiudadOrigen(String ciudadOrigen) {
        this.ciudadOrigen = ciudadOrigen;
    }

    public String getCiudadDestino() {
        return ciudadDestino;
    }

    public void setCiudadDestino(String ciudadDestino) {
        this.ciudadDestino = ciudadDestino;
    }

    public LocalDateTime getHorarioEstimado() {
        return horarioEstimado;
    }

    public void setHorarioEstimado(LocalDateTime horarioEstimado) {
        this.horarioEstimado = horarioEstimado;
    }

    public Integer getCuposDisponibles() {
        return cuposDisponibles;
    }

    public void setCuposDisponibles(Integer cuposDisponibles) {
        this.cuposDisponibles = cuposDisponibles;
    }

    public Usuario getConductor() {
        return conductor;
    }

    public void setConductor(Usuario conductor) {
        this.conductor = conductor;
    }
}
