package com.chiacchio.together.dto;

import java.time.LocalDateTime;

public class CrearViajeRequestDTO {
    private String ciudadOrigen;
    private String ciudadDestino;
    private LocalDateTime horarioEstimado;
    private Integer cuposDisponibles;

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
}
