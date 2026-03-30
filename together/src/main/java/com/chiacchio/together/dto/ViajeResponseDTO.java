package com.chiacchio.together.dto;

import com.chiacchio.together.Model.Viaje;

import java.time.LocalDateTime;

public class ViajeResponseDTO {
    private Long id;
    private String ciudadOrigen;
    private String ciudadDestino;
    private LocalDateTime horarioEstimado;
    private Integer cuposDisponibles;
    private Long conductorId;
    private String conductorNombre;
    private String conductorApellido;
    private String conductorEmail;

    public ViajeResponseDTO(Viaje viaje) {
        this.id = viaje.getId();
        this.ciudadOrigen = viaje.getCiudadOrigen();
        this.ciudadDestino = viaje.getCiudadDestino();
        this.horarioEstimado = viaje.getHorarioEstimado();
        this.cuposDisponibles = viaje.getCuposDisponibles();
        this.conductorId = viaje.getConductor().getId();
        this.conductorNombre = viaje.getConductor().getNombre();
        this.conductorApellido = viaje.getConductor().getApellido();
        this.conductorEmail = viaje.getConductor().getEmail();
    }

    public Long getId() {
        return id;
    }

    public String getCiudadOrigen() {
        return ciudadOrigen;
    }

    public String getCiudadDestino() {
        return ciudadDestino;
    }

    public LocalDateTime getHorarioEstimado() {
        return horarioEstimado;
    }

    public Integer getCuposDisponibles() {
        return cuposDisponibles;
    }

    public Long getConductorId() {
        return conductorId;
    }

    public String getConductorNombre() {
        return conductorNombre;
    }

    public String getConductorApellido() {
        return conductorApellido;
    }

    public String getConductorEmail() {
        return conductorEmail;
    }
}
