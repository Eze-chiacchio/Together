package com.chiacchio.together.Controller;

import com.chiacchio.together.Repository.UsuarioRepository;
import com.chiacchio.together.Service.ProfileImageStorageService;
import com.chiacchio.together.dto.UserResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/info")
public class InfoController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProfileImageStorageService profileImageStorageService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return usuarioRepository.findByEmail(userDetails.getUsername())
                .map(user -> ResponseEntity.ok(buildUserResponse(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/profile-photo")
    public ResponseEntity<UserResponseDTO> updateProfilePhoto(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {

        return usuarioRepository.findByEmail(userDetails.getUsername())
                .map(user -> {
                    try {
                        String objectKey = profileImageStorageService.uploadProfileImage(
                                user.getId(),
                                file,
                                user.getProfileImageKey()
                        );

                        user.setProfileImageKey(objectKey);
                        usuarioRepository.save(user);

                        return ResponseEntity.ok(buildUserResponse(user));
                    } catch (IllegalArgumentException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
                    } catch (IllegalStateException e) {
                        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), e);
                    } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo subir la imagen", e);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/profile-photo")
    public ResponseEntity<UserResponseDTO> deleteProfilePhoto(@AuthenticationPrincipal UserDetails userDetails) {
        return usuarioRepository.findByEmail(userDetails.getUsername())
                .map(user -> {
                    try {
                        profileImageStorageService.deleteProfileImage(user.getProfileImageKey());
                        user.setProfileImageKey(null);
                        usuarioRepository.save(user);

                        return ResponseEntity.ok(buildUserResponse(user));
                    } catch (IllegalStateException e) {
                        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), e);
                    } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo borrar la imagen", e);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private UserResponseDTO buildUserResponse(com.chiacchio.together.Model.Usuario user) {
        return new UserResponseDTO(
                user,
                profileImageStorageService.generatePresignedUrl(user.getProfileImageKey())
        );
    }
}
