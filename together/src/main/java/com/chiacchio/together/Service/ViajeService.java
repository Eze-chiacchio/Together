package com.chiacchio.together.Service;

import com.chiacchio.together.Model.Usuario;
import com.chiacchio.together.Model.Viaje;
import com.chiacchio.together.Repository.ViajeRepository;
import com.chiacchio.together.dto.CrearViajeRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ViajeService {

    @Autowired
    private ViajeRepository viajeRepository;

    public Viaje crearViaje(CrearViajeRequestDTO request, Usuario conductor) {
        validarRequest(request);

        Viaje viaje = new Viaje();
        viaje.setCiudadOrigen(request.getCiudadOrigen().trim());
        viaje.setCiudadDestino(request.getCiudadDestino().trim());
        viaje.setHorarioEstimado(request.getHorarioEstimado());
        viaje.setCuposDisponibles(request.getCuposDisponibles());
        viaje.setCostoPorCupo(request.getCostoPorCupo());
        viaje.setConductor(conductor);

        return viajeRepository.save(viaje);
    }

    public List<Viaje> listarViajesDisponibles() {
        return viajeRepository.findByCuposDisponiblesGreaterThanOrderByHorarioEstimadoAsc(0);
    }

    public Viaje obtenerViajePorId(Long viajeId) {
        return viajeRepository.findById(viajeId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Viaje no encontrado"));
    }

    private void validarRequest(CrearViajeRequestDTO request) {
        if (request.getCiudadOrigen() == null || request.getCiudadOrigen().isBlank()) {
            throw new IllegalArgumentException("La ciudad de origen es obligatoria");
        }

        if (request.getCiudadDestino() == null || request.getCiudadDestino().isBlank()) {
            throw new IllegalArgumentException("La ciudad de destino es obligatoria");
        }

        if (request.getHorarioEstimado() == null) {
            throw new IllegalArgumentException("El horario estimado es obligatorio");
        }

        if (request.getHorarioEstimado().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El horario estimado debe ser futuro");
        }

        if (request.getCuposDisponibles() == null || request.getCuposDisponibles() <= 0) {
            throw new IllegalArgumentException("La cantidad de cupos debe ser mayor a cero");
        }

        if (request.getCostoPorCupo() == null || request.getCostoPorCupo().signum() < 0) {
            throw new IllegalArgumentException("El costo por cupo debe ser mayor o igual a cero");
        }
    }
}
