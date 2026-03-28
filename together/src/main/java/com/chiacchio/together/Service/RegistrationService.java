package com.chiacchio.together.Service;

import com.chiacchio.together.Model.Usuario;
import com.chiacchio.together.Repository.VerificationTokenRepository;
import com.chiacchio.together.Security.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RegistrationService {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public void registerUserWithConfirmation(Usuario user) {
        Usuario savedUser = userService.registrarUsuario(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, savedUser);
        tokenRepository.save(verificationToken);

        try {
            emailService.sendConfirmationEmail(savedUser.getEmail(), token);
        } catch (MailException e) {
            throw new IllegalStateException("No se pudo enviar el mail de confirmacion", e);
        }
    }
}
