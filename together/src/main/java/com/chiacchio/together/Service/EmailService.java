package com.chiacchio.together.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @Value("${app.confirmation.url:http://localhost:8080/api/auth/confirm?token=}")
    private String confirmationBaseUrl;

    @Value("${resend.mail.api.key}")
    private String resendApiKey;

    @Value("${resend.mail.from:onboarding@resend.dev}")
    private String fromAddress;

    @Value("${resend.mail.endpoint:https://api.resend.com/emails}")
    private String resendEndpoint;

    public void sendConfirmationEmail(String to, String token) {
        String confirmationUrl = confirmationBaseUrl + token;
        String text = "Hola! Gracias por sumarte. Para activar tu cuenta, hace clic aca:\n" + confirmationUrl;
        String html = "<p>Hola! Gracias por sumarte.</p>"
                + "<p>Para activar tu cuenta, hace clic en este enlace:</p>"
                + "<p><a href=\"" + escapeHtml(confirmationUrl) + "\">Confirmar cuenta</a></p>";

        String payload = """
                {
                  "from": "%s",
                  "to": ["%s"],
                  "subject": "Bienvenido a Together - Confirma tu cuenta",
                  "text": "%s",
                  "html": "%s"
                }
                """.formatted(
                escapeJson(fromAddress),
                escapeJson(to),
                escapeJson(text),
                escapeJson(html)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(resendEndpoint))
                .header("Authorization", "Bearer " + resendApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        try {
            log.info("Enviando mail de confirmacion por Resend a {}", to);
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Resend devolvio " + response.statusCode() + ": " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException("No se pudo enviar el mail de confirmacion", e);
        }
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
