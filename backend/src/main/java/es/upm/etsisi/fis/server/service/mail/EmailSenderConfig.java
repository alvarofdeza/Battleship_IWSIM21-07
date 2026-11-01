package es.upm.etsisi.fis.server.service.mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Configuracion de fabrica para la inyeccion de dependencias del puerto de correo EmailSender.
 * Selecciona dinamicamente el adaptador correspondiente (Brevo, SMTP o Mock) conforme
 * a las variables de entorno de la plataforma.
 */
@Configuration
public class EmailSenderConfig {

    private static final Logger logger = LoggerFactory.getLogger(EmailSenderConfig.class);

    @Bean
    @Primary
    public EmailSender emailSender(
            ObjectMapper objectMapper,
            @Value("${mail.provider:}") String mailProvider,
            @Value("${brevo.api.key:}") String brevoApiKey,
            @Value("${brevo.sender.email:noreply@fleetgrid.me}") String senderEmail,
            @Value("${brevo.reply.email:hello@fleetgrid.me}") String replyEmail,
            @Value("${mail.smtp.host:localhost}") String smtpHost,
            @Value("${mail.smtp.port:587}") int smtpPort,
            @Value("${mail.smtp.username:}") String smtpUsername,
            @Value("${mail.smtp.password:}") String smtpPassword
    ) {
        String provider = mailProvider != null ? mailProvider.trim().toLowerCase() : "";

        if ("smtp".equals(provider)) {
            logger.info("Initializing SmtpEmailSender adapter (Host: {}:{})", smtpHost, smtpPort);
            return new SmtpEmailSender(smtpHost, smtpPort, smtpUsername, smtpPassword, senderEmail);
        }

        if ("mock".equals(provider)) {
            logger.info("Initializing MockEmailSender adapter (Offline mock mode active)");
            return new MockEmailSender();
        }

        if ("brevo".equals(provider) || (provider.isEmpty() && brevoApiKey != null && !brevoApiKey.isBlank() && !"mock".equalsIgnoreCase(brevoApiKey.trim()))) {
            logger.info("Initializing BrevoEmailSender adapter via HTTPS REST API v3");
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
            return new BrevoEmailSender(client, objectMapper, brevoApiKey, senderEmail, replyEmail);
        }

        logger.info("No active mail provider credentials supplied. Falling back to MockEmailSender adapter.");
        return new MockEmailSender();
    }
}
