package com.chiacchio.together.dto;

import com.chiacchio.together.Model.ReservaViaje;

import java.time.LocalDateTime;

public class ReservaViajeResponseDTO {
    private Long id;
    private String estado;
    private LocalDateTime fechaSolicitud;
    private Long viajeId;
    private String ciudadOrigen;
    private String ciudadDestino;
    private LocalDateTime horarioEstimado;
    private Integer cuposDisponibles;
    private Long conductorId;
    private String conductorNombre;
    private String conductorApellido;
    private String conductorProfileImageUrl;
    private Long pasajeroId;
    private String pasajeroNombre;
    private String pasajeroApellido;
    private String pasajeroEmail;
    private String pasajeroProfileImageUrl;

    public ReservaViajeResponseDTO(ReservaViaje reserva) {
        this(reserva, null, null);
    }

    public ReservaViajeResponseDTO(
            ReservaViaje reserva,
            String conductorProfileImageUrl,
            String pasajeroProfileImageUrl) {
        this.id = reserva.getId();
        this.estado = reserva.getEstado().name();
        this.fechaSolicitud = reserva.getFechaSolicitud();
        this.viajeId = reserva.getViaje().getId();
        this.ciudadOrigen = reserva.getViaje().getCiudadOrigen();
        this.ciudadDestino = reserva.getViaje().getCiudadDestino();
        this.horarioEstimado = reserva.getViaje().getHorarioEstimado();
        this.cuposDisponibles = reserva.getViaje().getCuposDisponibles();
        this.conductorId = reserva.getViaje().getConductor().getId();
        this.conductorNombre = reserva.getViaje().getConductor().getNombre();
        this.conductorApellido = reserva.getViaje().getConductor().getApellido();
        this.conductorProfileImageUrl = conductorProfileImageUrl;
        this.pasajeroId = reserva.getPasajero().getId();
        this.pasajeroNombre = reserva.getPasajero().getNombre();
        this.pasajeroApellido = reserva.getPasajero().getApellido();
        this.pasajeroEmail = reserva.getPasajero().getEmail();
        this.pasajeroProfileImageUrl = pasajeroProfileImageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public Long getViajeId() {
        return viajeId;
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

    public String getConductorProfileImageUrl() {
        return conductorProfileImageUrl;
    }

    public Long getPasajeroId() {
        return pasajeroId;
    }

    public String getPasajeroNombre() {
        return pasajeroNombre;
    }

    public String getPasajeroApellido() {
        return pasajeroApellido;
    }

    public String getPasajeroEmail() {
        return pasajeroEmail;
    }

    public String getPasajeroProfileImageUrl() {
        return pasajeroProfileImageUrl;
    }
}
