package com.chiacchio.together.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CrearViajeRequestDTO {
    private String ciudadOrigen;
    private String ciudadDestino;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime horarioEstimado;
    private Integer cuposDisponibles;
    private BigDecimal costoPorCupo;

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

    public BigDecimal getCostoPorCupo() {
        return costoPorCupo;
    }

    public void setCostoPorCupo(BigDecimal costoPorCupo) {
        this.costoPorCupo = costoPorCupo;
    }
}
