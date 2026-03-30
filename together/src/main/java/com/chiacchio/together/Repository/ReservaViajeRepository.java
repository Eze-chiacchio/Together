package com.chiacchio.together.Repository;

import com.chiacchio.together.Model.EstadoReserva;
import com.chiacchio.together.Model.ReservaViaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservaViajeRepository extends JpaRepository<ReservaViaje, Long> {
    boolean existsByViajeIdAndPasajeroIdAndEstadoIn(Long viajeId, Long pasajeroId, List<EstadoReserva> estados);

    Optional<ReservaViaje> findByIdAndViajeConductorId(Long reservaId, Long conductorId);

    List<ReservaViaje> findByPasajeroIdOrderByFechaSolicitudDesc(Long pasajeroId);

    List<ReservaViaje> findByViajeConductorIdOrderByFechaSolicitudDesc(Long conductorId);
}
