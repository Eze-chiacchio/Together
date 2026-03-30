package com.chiacchio.together.Controller;

import com.chiacchio.together.Model.JwtResponse;
import com.chiacchio.together.Model.Usuario;
import com.chiacchio.together.Security.JwtUtils;
import com.chiacchio.together.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Usuario user) {
        try {
            userService.registrarUsuario(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Usuario registrado correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token) {
        return ResponseEntity.status(HttpStatus.GONE)
                .body("La confirmacion de cuenta esta deshabilitada temporalmente.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Credenciales invalidas o cuenta no activa");
        }
    }
}
