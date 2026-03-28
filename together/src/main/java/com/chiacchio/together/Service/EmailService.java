package com.chiacchio.together.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.confirmation.url:http://localhost:8080/api/auth/confirm?token=}")
    private String confirmationBaseUrl;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    public void sendConfirmationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (fromAddress != null && !fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }

        message.setTo(to);
        message.setSubject("Bienvenido a Together - Confirma tu cuenta");

        String confirmationUrl = confirmationBaseUrl + token;
        message.setText("Hola! Gracias por sumarte. Para activar tu cuenta, hace clic aca:\n" + confirmationUrl);

        log.info("Enviando mail de confirmacion a {}", to);
        mailSender.send(message);
    }
}
