package com.chiacchio.together.Controller;

import com.chiacchio.together.Model.Usuario;
import com.chiacchio.together.Repository.UsuarioRepository;
import com.chiacchio.together.Service.ProfileImageStorageService;
import com.chiacchio.together.Service.ViajeService;
import com.chiacchio.together.dto.CrearViajeRequestDTO;
import com.chiacchio.together.dto.ViajeResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/viajes")
public class ViajeController {

    @Autowired
    private ViajeService viajeService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProfileImageStorageService profileImageStorageService;

    @PostMapping
    public ResponseEntity<?> crearViaje(
            @RequestBody CrearViajeRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Logs de entrada (Ya vimos que llegan bien)
        System.out.println("DEBUG - Intentando crear viaje para: " + userDetails.getUsername());

        try {
            // 1. Validar existencia del usuario
            Usuario conductor = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la DB"));

            // 2. Intentar la lógica de negocio
            com.chiacchio.together.Model.Viaje nuevoViaje = viajeService.crearViaje(request, conductor);

            // 3. Construir respuesta
            ViajeResponseDTO response = buildViajeResponse(nuevoViaje);

            System.out.println("DEBUG - Viaje creado exitosamente con ID: " + nuevoViaje.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            System.err.println("ERROR DE VALIDACIÓN: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // ESTO ES LO MÁS IMPORTANTE: Imprime el stacktrace completo en la consola de IntelliJ
            System.err.println("ERROR CRÍTICO AL CREAR VIAJE:");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ViajeResponseDTO>> listarViajesDisponibles() {
        try {
            List<ViajeResponseDTO> viajes = viajeService.listarViajesDisponibles()
                    .stream()
                    .map(this::buildViajeResponse)
                    .toList();
            return ResponseEntity.ok(viajes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{viajeId}")
    public ResponseEntity<ViajeResponseDTO> obtenerViaje(@PathVariable Long viajeId) {
        try {
            return ResponseEntity.ok(buildViajeResponse(viajeService.obtenerViajePorId(viajeId)));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    private ViajeResponseDTO buildViajeResponse(com.chiacchio.together.Model.Viaje viaje) {
        String profileImageUrl = null;
        if (viaje.getConductor().getProfileImageKey() != null) {
            profileImageUrl = profileImageStorageService.generatePresignedUrl(viaje.getConductor().getProfileImageKey());
        }
        return new ViajeResponseDTO(viaje, profileImageUrl);
    }
}
