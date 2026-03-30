package com.chiacchio.together.Controller;

import com.chiacchio.together.Model.Usuario;
import com.chiacchio.together.Repository.UsuarioRepository;
import com.chiacchio.together.Service.ProfileImageStorageService;
import com.chiacchio.together.Service.ReservaViajeService;
import com.chiacchio.together.dto.ReservaViajeResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaViajeController {

    @Autowired
    private ReservaViajeService reservaViajeService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProfileImageStorageService profileImageStorageService;

    @PostMapping("/viaje/{viajeId}")
    public ResponseEntity<ReservaViajeResponseDTO> solicitarReserva(
            @PathVariable Long viajeId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario pasajero = obtenerUsuarioAutenticado(userDetails);

        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(buildReservaResponse(reservaViajeService.solicitarReserva(viajeId, pasajero)));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping("/{reservaId}/aceptar")
    public ResponseEntity<ReservaViajeResponseDTO> aceptarReserva(
            @PathVariable Long reservaId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario conductor = obtenerUsuarioAutenticado(userDetails);

        try {
            return ResponseEntity.ok(buildReservaResponse(
                    reservaViajeService.aceptarReserva(reservaId, conductor)
            ));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping("/{reservaId}/rechazar")
    public ResponseEntity<ReservaViajeResponseDTO> rechazarReserva(
            @PathVariable Long reservaId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario conductor = obtenerUsuarioAutenticado(userDetails);

        try {
            return ResponseEntity.ok(buildReservaResponse(
                    reservaViajeService.rechazarReserva(reservaId, conductor)
            ));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/mis-solicitudes")
    public ResponseEntity<List<ReservaViajeResponseDTO>> listarMisReservas(
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario pasajero = obtenerUsuarioAutenticado(userDetails);
        List<ReservaViajeResponseDTO> reservas = reservaViajeService.listarMisReservas(pasajero.getId())
                .stream()
                .map(this::buildReservaResponse)
                .toList();

        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/recibidas")
    public ResponseEntity<List<ReservaViajeResponseDTO>> listarReservasRecibidas(
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario conductor = obtenerUsuarioAutenticado(userDetails);
        List<ReservaViajeResponseDTO> reservas = reservaViajeService.listarReservasRecibidas(conductor.getId())
                .stream()
                .map(this::buildReservaResponse)
                .toList();

        return ResponseEntity.ok(reservas);
    }

    private Usuario obtenerUsuarioAutenticado(UserDetails userDetails) {
        return usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private ReservaViajeResponseDTO buildReservaResponse(com.chiacchio.together.Model.ReservaViaje reserva) {
        return new ReservaViajeResponseDTO(
                reserva,
                profileImageStorageService.generatePresignedUrl(reserva.getViaje().getConductor().getProfileImageKey()),
                profileImageStorageService.generatePresignedUrl(reserva.getPasajero().getProfileImageKey())
        );
    }
}
