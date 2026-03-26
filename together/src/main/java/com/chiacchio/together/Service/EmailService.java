package com.chiacchio.together.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Inyectamos la variable definida en los properties o variables de entorno
    @Value("${app.confirmation.url:http://localhost:8080/api/auth/confirm?token=}")
    private String confirmationBaseUrl;

    public void sendConfirmationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Bienvenido a Together - Confirmá tu cuenta");


        String confirmationUrl = confirmationBaseUrl + token;

        message.setText("¡Hola! Gracias por sumarte. Para activar tu cuenta, hacé clic acá: \n" + confirmationUrl);

        mailSender.send(message);
    }
}