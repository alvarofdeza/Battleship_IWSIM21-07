package es.upm.etsisi.fis.server.service;

import es.upm.etsisi.fis.server.service.mail.BrevoEmailSender;
import es.upm.etsisi.fis.server.service.mail.EmailSender;
import org.springframework.stereotype.Service;

/**
 * Servicio transaccional y fachada de correo para la emision de comunicaciones de mando,
 * PINes de activacion militar y recuperacion segura de contrasenas.
 */
@Service
public class EmailService implements EmailSender {

    private final EmailSender emailSender;
    private final BrevoEmailSender brevoTemplateHelper;

    @org.springframework.beans.factory.annotation.Autowired
    public EmailService(EmailSender emailSender) {
        this.emailSender = emailSender;
        this.brevoTemplateHelper = new BrevoEmailSender(null, null, "", "noreply@fleetgrid.me", "hello@fleetgrid.me");
    }

    // Constructor de compatibilidad para pruebas directas
    public EmailService(java.net.http.HttpClient httpClient,
                        com.fasterxml.jackson.databind.ObjectMapper objectMapper,
                        String brevoApiKey,
                        String senderEmail) {
        this.emailSender = new BrevoEmailSender(httpClient, objectMapper, brevoApiKey, senderEmail, "hello@fleetgrid.me");
        this.brevoTemplateHelper = new BrevoEmailSender(httpClient, objectMapper, brevoApiKey, senderEmail, "hello@fleetgrid.me");
    }

    /**
     * Envia el codigo PIN militar de 6 digitos al correo del oficial.
     *
     * @param toEmail  Direccion de correo destinataria.
     * @param username Nombre del oficial.
     * @param otp      Codigo PIN de 6 digitos.
     */
    public void sendVerificationCode(String toEmail, String username, String otp) {
        sendVerificationEmail(toEmail, username, otp);
    }

    @Override
    public void sendVerificationEmail(String toEmail, String username, String otp) {
        emailSender.sendVerificationEmail(toEmail, username, otp);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String username, String otp) {
        emailSender.sendPasswordResetEmail(toEmail, username, otp);
    }

    /**
     * Construye la plantilla HTML de transmision militar para el correo electronico.
     *
     * @param username Nombre del oficial.
     * @param otp      Codigo PIN de 6 digitos.
     * @return Contenido HTML estructurado.
     */
    public String buildTacticalEmailHtml(String username, String otp) {
        return brevoTemplateHelper.buildVerificationEmailHtml(username, otp);
    }
}
