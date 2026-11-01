package es.upm.etsisi.fis.server.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adaptador simulado en memoria para entornos de desarrollo local, pruebas unitarias y pipelines de CI.
 * Registra en log estructurado la operacion sin intentar transmision a traves de la red.
 */
public class MockEmailSender implements EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(MockEmailSender.class);

    @Override
    public void sendVerificationEmail(String toEmail, String username, String otp) {
        logger.info("[OFFLINE MOCK EMAIL] Recipient: {} | Officer: {} | Purpose: VERIFICATION | Verification OTP: [{}]",
                toEmail, username, otp);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String username, String otp) {
        logger.info("[OFFLINE MOCK EMAIL] Recipient: {} | Officer: {} | Purpose: RESET_PASSWORD | Reset OTP: [{}]",
                toEmail, username, otp);
    }
}
