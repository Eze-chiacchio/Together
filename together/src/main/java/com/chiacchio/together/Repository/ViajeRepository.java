package com.chiacchio.together.Repository;

import com.chiacchio.together.Model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ViajeRepository extends JpaRepository<Viaje, Long> {
    List<Viaje> findByCuposDisponiblesGreaterThanOrderByHorarioEstimadoAsc(Integer cuposDisponibles);
}
