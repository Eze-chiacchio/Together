package com.chiacchio.together.Controller;

import com.chiacchio.together.Model.JwtResponse;
import com.chiacchio.together.Model.Usuario;
import com.chiacchio.together.Repository.UsuarioRepository;
import com.chiacchio.together.Repository.VerificationTokenRepository;
import com.chiacchio.together.Security.JwtUtils;
import com.chiacchio.together.Security.VerificationToken;
import com.chiacchio.together.Service.EmailService;
import com.chiacchio.together.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Usuario user) {
        // ... tus validaciones de email/dni existentes ...

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false); // Aseguramos que empiece desactivado
        usuarioRepository.save(user);

        // Generamos un código único al azar
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);

        // Mandamos el mail
        emailService.sendConfirmationEmail(user.getEmail(), token);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuario registrado. Por favor, revisá tu mail para activar la cuenta.");
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmRegistration(@RequestParam("token") String token) {
        return tokenRepository.findByToken(token)
                .map(verificationToken -> {
                    Usuario user = verificationToken.getUser();
                    user.setEnabled(true);
                    usuarioRepository.save(user);
                    tokenRepository.delete(verificationToken);
                    return ResponseEntity.ok("Cuenta activada correctamente. Ya podés loguearte.");
                })
                .orElse(ResponseEntity.badRequest().body("Token inválido o expirado."));
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest) { // Cambié Usuario por User
        try {
            // Ahora usamos el email para autenticar
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);

            return ResponseEntity.ok(new JwtResponse(jwt));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Credenciales inválidas");
        }
    }

}