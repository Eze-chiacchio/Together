package com.chiacchio.together.Service;

import com.chiacchio.together.Model.EstadoReserva;
import com.chiacchio.together.Model.ReservaViaje;
import com.chiacchio.together.Model.Usuario;
import com.chiacchio.together.Model.Viaje;
import com.chiacchio.together.Repository.ReservaViajeRepository;
import com.chiacchio.together.Repository.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaViajeService {

    @Autowired
    private ReservaViajeRepository reservaViajeRepository;

    @Autowired
    private ViajeRepository viajeRepository;

    @Transactional
    public ReservaViaje solicitarReserva(Long viajeId, Usuario pasajero) {
        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

        if (viaje.getConductor().getId().equals(pasajero.getId())) {
            throw new IllegalArgumentException("No puedes reservar tu propio viaje");
        }

        if (viaje.getCuposDisponibles() <= 0) {
            throw new IllegalArgumentException("No hay cupos disponibles para este viaje");
        }

        boolean yaExiste = reservaViajeRepository.existsByViajeIdAndPasajeroIdAndEstadoIn(
                viajeId,
                pasajero.getId(),
                List.of(EstadoReserva.PENDIENTE, EstadoReserva.ACEPTADA)
        );

        if (yaExiste) {
            throw new IllegalArgumentException("Ya tienes una reserva pendiente o aceptada para este viaje");
        }

        ReservaViaje reserva = new ReservaViaje();
        reserva.setViaje(viaje);
        reserva.setPasajero(pasajero);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setFechaSolicitud(LocalDateTime.now());

        return reservaViajeRepository.save(reserva);
    }

    @Transactional
    public ReservaViaje aceptarReserva(Long reservaId, Usuario conductor) {
        ReservaViaje reserva = reservaViajeRepository.findByIdAndViajeConductorId(reservaId, conductor.getId())
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada para este conductor"));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalArgumentException("Solo puedes aceptar reservas pendientes");
        }

        Viaje viaje = reserva.getViaje();
        if (viaje.getCuposDisponibles() <= 0) {
            throw new IllegalArgumentException("No hay cupos disponibles para aceptar esta reserva");
        }

        reserva.setEstado(EstadoReserva.ACEPTADA);
        viaje.setCuposDisponibles(viaje.getCuposDisponibles() - 1);
        viajeRepository.save(viaje);

        return reservaViajeRepository.save(reserva);
    }

    @Transactional
    public ReservaViaje rechazarReserva(Long reservaId, Usuario conductor) {
        ReservaViaje reserva = reservaViajeRepository.findByIdAndViajeConductorId(reservaId, conductor.getId())
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada para este conductor"));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalArgumentException("Solo puedes rechazar reservas pendientes");
        }

        reserva.setEstado(EstadoReserva.RECHAZADA);
        return reservaViajeRepository.save(reserva);
    }

    public List<ReservaViaje> listarMisReservas(Long pasajeroId) {
        return reservaViajeRepository.findByPasajeroIdOrderByFechaSolicitudDesc(pasajeroId);
    }

    public List<ReservaViaje> listarReservasRecibidas(Long conductorId) {
        return reservaViajeRepository.findByViajeConductorIdOrderByFechaSolicitudDesc(conductorId);
    }
}
