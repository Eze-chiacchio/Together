package com.chiacchio.together.Service;

import com.chiacchio.together.Model.PasswordResetToken;
import com.chiacchio.together.Model.Usuario;
import com.chiacchio.together.Repository.PasswordResetTokenRepository;
import com.chiacchio.together.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void requestPasswordReset(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        usuarioRepository.findByEmail(email.trim())
                .filter(Usuario::isEnabled)
                .ifPresent(this::createTokenAndSendMail);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token es obligatorio");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("El enlace de recuperación es inválido"));

        if (resetToken.getExpiryDate() == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new IllegalArgumentException("El enlace de recuperación expiró");
        }

        Usuario user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
    }

    private void createTokenAndSendMail(Usuario user) {
        passwordResetTokenRepository.findByUserId(user.getId()).ifPresent(passwordResetTokenRepository::delete);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }
}
