package es.upm.etsisi.fis.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio transaccional para el envio de correos electronicos con codigos de verificacion militar mediante la API de Resend.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final String resendApiKey;
    private final String fromEmail;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public EmailService(
            @Value("${resend.api.key:}") String resendApiKey,
            @Value("${resend.from.email:onboarding@resend.dev}") String fromEmail,
            ObjectMapper objectMapper
    ) {
        this.resendApiKey = resendApiKey != null ? resendApiKey.trim() : "";
        this.fromEmail = fromEmail != null ? fromEmail.trim() : "onboarding@resend.dev";
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Envia el codigo PIN militar de 6 digitos al correo del oficial.
     * @param toEmail Direccion de correo destinataria.
     * @param username Nombre del oficial.
     * @param otp Codigo de 6 digitos.
     */
    public void sendVerificationCode(String toEmail, String username, String otp) {
        if (resendApiKey.isEmpty() || resendApiKey.equals("mock") || toEmail.endsWith("@test.com") || toEmail.endsWith("@example.com")) {
            logger.info("[OFFLINE MOCK EMAIL] Destinatario: {} | Oficial: {} | Codigo OTP Militar: [{}]", toEmail, username, otp);
            return;
        }

        try {
            String subject = "CENTRO DE MANDO NAVAL: Codigo de Acceso Militar [" + otp + "]";
            String htmlContent = buildTacticalEmailHtml(username, otp);

            Map<String, Object> payload = new HashMap<>();
            payload.put("from", "Battleship Command <" + fromEmail + ">");
            payload.put("to", List.of(toEmail));
            payload.put("subject", subject);
            payload.put("html", htmlContent);

            String requestBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                logger.info("Correo de verificacion militar entregado con exito a {} (Resend HTTP {})", toEmail, response.statusCode());
            } else {
                logger.error("Error devuelto por la API de Resend al enviar a {} (HTTP {}): {}", toEmail, response.statusCode(), response.body());
            }
        } catch (Exception e) {
            logger.error("Excepcion al conectar con la API de Resend para {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildTacticalEmailHtml(String username, String otp) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='background-color:#0b1120; color:#e2e8f0; font-family:Courier New, monospace; padding:30px; margin:0;'>" +
                "  <div style='max-width:550px; margin:0 auto; background-color:#1e293b; border:2px solid #0284c7; padding:25px; box-shadow:0 0 20px rgba(2,132,199,0.3);'>" +
                "    <div style='border-bottom:1px solid #334155; padding-bottom:15px; margin-bottom:20px;'>" +
                "      <h1 style='color:#38bdf8; font-size:18px; margin:0; letter-spacing:2px;'>[ BATTLESHIP FLEET COMMAND ]</h1>" +
                "      <p style='color:#94a3b8; font-size:12px; margin:5px 0 0;'>TRANSMISION CLASIFICADA // ORDEN DE SERVICIO</p>" +
                "    </div>" +
                "    <p style='font-size:14px; line-height:1.6;'>Saludos, Oficial <strong>" + username + "</strong>.</p>" +
                "    <p style='font-size:13px; color:#cbd5e1; line-height:1.6;'>Para verificar tu expediente en el Escalafon Militar de la Armada y habilitar el registro definitivo de tus records navales, introduce el siguiente codigo de 6 digitos:</p>" +
                "    <div style='background-color:#0f172a; border:2px dashed #38bdf8; text-align:center; padding:18px; margin:25px 0;'>" +
                "      <span style='font-size:32px; font-weight:bold; letter-spacing:10px; color:#38bdf8;'>" + otp + "</span>" +
                "    </div>" +
                "    <p style='font-size:12px; color:#94a3b8;'>Este codigo militar expirara automaticamente en <strong>10 minutos</strong>. Si tu no has solicitado esta transmision, ignora este mensaje.</p>" +
                "    <div style='border-top:1px solid #334155; padding-top:15px; margin-top:25px; font-size:11px; color:#64748b; text-align:center;'>" +
                "      NAVAL WARFARE COMBAT PLATFORM // EST. 2026" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }
}
