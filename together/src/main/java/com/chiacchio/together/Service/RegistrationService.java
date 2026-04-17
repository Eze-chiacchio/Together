package com.chiacchio.together.Service;

import com.chiacchio.together.Model.Usuario;
import com.chiacchio.together.Repository.UsuarioRepository;
import com.chiacchio.together.Repository.VerificationTokenRepository;
import com.chiacchio.together.Security.VerificationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public String registerUserWithConfirmation(Usuario user) {
        Usuario existingUser = usuarioRepository.findByEmail(user.getEmail().trim()).orElse(null);

        if (existingUser != null) {
            if (existingUser.isEnabled()) {
                throw new RuntimeException("El email ya está registrado");
            }

            try {
                resendConfirmationEmail(existingUser);
                return "Ya existía una cuenta pendiente. Te reenviamos el mail de confirmación.";
            } catch (RuntimeException e) {
                log.error("Fallo el reenvio del mail de confirmacion para {}", existingUser.getEmail(), e);
                throw new IllegalStateException("No se pudo enviar el mail de confirmacion", e);
            }
        }

        Usuario savedUser = userService.registrarUsuario(user);

        try {
            resendConfirmationEmail(savedUser);
            return "Usuario registrado correctamente. Revisa tu mail para confirmar la cuenta.";
        } catch (RuntimeException e) {
            log.error("Fallo el envio del mail de confirmacion para {}", savedUser.getEmail(), e);
            throw new IllegalStateException("No se pudo enviar el mail de confirmacion", e);
        }
    }

    @Transactional
    public void confirmRegistration(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de confirmacion invalido"));

        if (verificationToken.getExpiryDate() == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(verificationToken);
            throw new IllegalArgumentException("El token de confirmacion expiro");
        }

        Usuario user = verificationToken.getUser();
        user.setEnabled(true);
        userService.save(user);
        tokenRepository.delete(verificationToken);
    }
    private void resendConfirmationEmail(Usuario user) {
        tokenRepository.findByUserId(user.getId()).ifPresent(tokenRepository::delete);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);
        emailService.sendConfirmationEmail(user.getEmail(), token);
    }
}
