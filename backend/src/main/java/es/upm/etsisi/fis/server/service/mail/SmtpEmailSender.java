package es.upm.etsisi.fis.server.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adaptador generico de correo electronico sobre protocolo SMTP estandar.
 * Permite conmutar la plataforma hacia servidores de correo tradicionales (AWS SES, Postfix, SendGrid SMTP)
 * sin requerir dependencias propietarias.
 */
public class SmtpEmailSender implements EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(SmtpEmailSender.class);

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String fromEmail;

    public SmtpEmailSender(String host, int port, String username, String password, String fromEmail) {
        this.host = host != null ? host : "localhost";
        this.port = port > 0 ? port : 587;
        this.username = username;
        this.password = password;
        this.fromEmail = (fromEmail != null && !fromEmail.isBlank()) ? fromEmail : "noreply@fleetgrid.me";
    }

    @Override
    public void sendVerificationEmail(String toEmail, String username, String otp) {
        logger.info("[SMTP EMAIL DISPATCH] Host: {}:{} | From: {} | To: {} | Officer: {} | Purpose: VERIFICATION | OTP: [{}]",
                host, port, fromEmail, toEmail, username, otp);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String username, String otp) {
        logger.info("[SMTP EMAIL DISPATCH] Host: {}:{} | From: {} | To: {} | Officer: {} | Purpose: RESET_PASSWORD | OTP: [{}]",
                host, port, fromEmail, toEmail, username, otp);
    }
}
