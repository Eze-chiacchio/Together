package com.chiacchio.together.Controller;

import com.chiacchio.together.Model.Usuario;
import com.chiacchio.together.Repository.UsuarioRepository;
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

    @PostMapping
    public ResponseEntity<ViajeResponseDTO> crearViaje(
            @RequestBody CrearViajeRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario conductor = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ViajeResponseDTO(viajeService.crearViaje(request, conductor)));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<List<ViajeResponseDTO>> listarViajesDisponibles() {
        List<ViajeResponseDTO> viajes = viajeService.listarViajesDisponibles()
                .stream()
                .map(ViajeResponseDTO::new)
                .toList();

        return ResponseEntity.ok(viajes);
    }
}
