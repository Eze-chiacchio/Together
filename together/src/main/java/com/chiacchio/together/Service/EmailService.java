package com.chiacchio.together.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Bienvenido a Together - Confirmá tu cuenta");

        // La URL de tu app en Railway
        String confirmationUrl = "https://protective-healing-production-85f3.up.railway.app/api/auth/confirm?token=" + token;

        message.setText("¡Hola! Gracias por sumarte. Para activar tu cuenta, hacé clic acá: \n" + confirmationUrl);

        mailSender.send(message);
    }
}