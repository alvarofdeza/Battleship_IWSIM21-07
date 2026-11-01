package es.upm.etsisi.fis.server.service.mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Adaptador transaccional para el despacho de correos electronicos a traves de la API REST HTTPS v3
 * de Brevo (Puerto 443), inmune al bloqueo de puertos SMTP en plataformas PaaS como Render.
 */
public class BrevoEmailSender implements EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(BrevoEmailSender.class);
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String brevoApiKey;
    private final String senderEmail;
    private final String replyToEmail;

    public BrevoEmailSender(HttpClient httpClient, ObjectMapper objectMapper, String brevoApiKey, String senderEmail, String replyToEmail) {
        this.httpClient = httpClient != null ? httpClient : HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
        this.brevoApiKey = brevoApiKey != null ? brevoApiKey.trim() : "";
        this.senderEmail = (senderEmail != null && !senderEmail.isBlank()) ? senderEmail.trim() : "noreply@fleetgrid.me";
        this.replyToEmail = (replyToEmail != null && !replyToEmail.isBlank()) ? replyToEmail.trim() : "hello@fleetgrid.me";
    }

    @Override
    public void sendVerificationEmail(String toEmail, String username, String otp) {
        String subject = "[FLEETGRID COMMAND] Naval Verification PIN [" + otp + "]";
        String html = buildVerificationEmailHtml(username, otp);
        sendBrevoRequest(toEmail, username, subject, html, "Verification");
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String username, String otp) {
        String subject = "[FLEETGRID COMMAND] Password Reset PIN [" + otp + "]";
        String html = buildPasswordResetEmailHtml(username, otp);
        sendBrevoRequest(toEmail, username, subject, html, "Password Reset");
    }

    private void sendBrevoRequest(String toEmail, String username, String subject, String htmlContent, String purpose) {
        if (brevoApiKey.isEmpty() || brevoApiKey.equalsIgnoreCase("mock") || toEmail.endsWith("@test.com") || toEmail.endsWith("@example.com")) {
            logger.info("[OFFLINE MOCK EMAIL] Recipient: {} | Officer: {} | Purpose: {} | Code: [{}]", toEmail, username, purpose, htmlContent.contains(purpose) ? purpose : "PIN");
            return;
        }

        try {
            Map<String, Object> payload = Map.of(
                    "sender", Map.of("name", "FleetGrid Command", "email", senderEmail),
                    "replyTo", Map.of("name", "FleetGrid Command", "email", replyToEmail),
                    "to", List.of(Map.of("email", toEmail, "name", username != null ? username : "Officer")),
                    "subject", subject,
                    "htmlContent", htmlContent
            );

            String requestBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BREVO_API_URL))
                    .header("api-key", brevoApiKey)
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                logger.info("{} email delivered successfully via Brevo API to {} (Officer: {}) - Status: {}",
                        purpose, toEmail, username, response.statusCode());
            } else {
                logger.error("Error sending {} email via Brevo API to {}. HTTP Status: {}, Response: {}",
                        purpose, toEmail, response.statusCode(), response.body());
            }
        } catch (Exception e) {
            logger.error("Exception connecting to Brevo API for {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Construye la plantilla HTML de verificacion de cuenta.
     */
    public String buildVerificationEmailHtml(String username, String otp) {
        String safeUsername = HtmlUtils.htmlEscape(username != null ? username : "Officer");
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head>" +
                "<body style='background-color:#0b1120; color:#e2e8f0; font-family:Courier New, monospace; padding:30px; margin:0;'>" +
                "  <div style='max-width:550px; margin:0 auto; background-color:#1e293b; border:2px solid #0284c7; padding:25px; box-shadow:0 0 20px rgba(2,132,199,0.3);'>" +
                "    <div style='border-bottom:1px solid #334155; padding-bottom:15px; margin-bottom:20px;'>" +
                "      <h1 style='color:#38bdf8; font-size:18px; margin:0; letter-spacing:2px;'>[ FLEETGRID // FLEET COMMAND ]</h1>" +
                "      <p style='color:#94a3b8; font-size:12px; margin:5px 0 0;'>CLASSIFIED TRANSMISSION // NAVAL SERVICE DIRECTIVE</p>" +
                "    </div>" +
                "    <p style='font-size:14px; line-height:1.6;'>Greetings, Officer <strong>" + safeUsername + "</strong>.</p>" +
                "    <p style='font-size:13px; color:#cbd5e1; line-height:1.6;'>To verify your service record in the Naval Admiralty Roll and enable official score recording on FleetGrid, submit the following 6-digit access PIN:</p>" +
                "    <div style='background-color:#0f172a; border:2px dashed #38bdf8; text-align:center; padding:18px; margin:25px 0;'>" +
                "      <span style='font-size:32px; font-weight:bold; letter-spacing:10px; color:#38bdf8;'>" + otp + "</span>" +
                "    </div>" +
                "    <p style='font-size:12px; color:#94a3b8;'>This military code will expire automatically in <strong>10 minutes</strong>. If you did not request this transmission, please disregard this message.</p>" +
                "    <div style='border-top:1px solid #334155; padding-top:15px; margin-top:25px; font-size:11px; color:#64748b; text-align:center;'>" +
                "      FLEETGRID NAVAL WARFARE PLATFORM // EST. 2026" +
                "    </div>" +
                "  </div>" +
                "</body></html>";
    }

    /**
     * Construye la plantilla HTML de restablecimiento seguro de contrasena.
     */
    public String buildPasswordResetEmailHtml(String username, String otp) {
        String safeUsername = HtmlUtils.htmlEscape(username != null ? username : "Officer");
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head>" +
                "<body style='background-color:#0b1120; color:#e2e8f0; font-family:Courier New, monospace; padding:30px; margin:0;'>" +
                "  <div style='max-width:550px; margin:0 auto; background-color:#1e293b; border:2px solid #f59e0b; padding:25px; box-shadow:0 0 20px rgba(245,158,11,0.3);'>" +
                "    <div style='border-bottom:1px solid #334155; padding-bottom:15px; margin-bottom:20px;'>" +
                "      <h1 style='color:#fbbf24; font-size:18px; margin:0; letter-spacing:2px;'>[ FLEETGRID // SECURITY DIRECTIVE ]</h1>" +
                "      <p style='color:#94a3b8; font-size:12px; margin:5px 0 0;'>PASSWORD RECOVERY // ACCESS CONTROL REAUTHORIZATION</p>" +
                "    </div>" +
                "    <p style='font-size:14px; line-height:1.6;'>Greetings, Officer <strong>" + safeUsername + "</strong>.</p>" +
                "    <p style='font-size:13px; color:#cbd5e1; line-height:1.6;'>A security clearance reset request was initiated for your command account. Use the 6-digit authorization PIN below to configure a new access password:</p>" +
                "    <div style='background-color:#0f172a; border:2px dashed #fbbf24; text-align:center; padding:18px; margin:25px 0;'>" +
                "      <span style='font-size:32px; font-weight:bold; letter-spacing:10px; color:#fbbf24;'>" + otp + "</span>" +
                "    </div>" +
                "    <p style='font-size:12px; color:#94a3b8;'>This authorization PIN will expire in <strong>10 minutes</strong>. If you did not request this credentials reset, your credentials remain secure and you can safely disregard this directive.</p>" +
                "    <div style='border-top:1px solid #334155; padding-top:15px; margin-top:25px; font-size:11px; color:#64748b; text-align:center;'>" +
                "      FLEETGRID NAVAL WARFARE PLATFORM // SECURITY OPERATIONS" +
                "    </div>" +
                "  </div>" +
                "</body></html>";
    }
}
