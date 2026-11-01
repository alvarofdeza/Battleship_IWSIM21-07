package es.upm.etsisi.fis.server.service.mail;

/**
 * Puerto de salida para el despacho de correos transaccionales y notificaciones militares.
 * Abstrae los proveedores de correo (Brevo, SMTP generico, Mock) garantizando desacoplamiento
 * y portabilidad total frente a vendor lock-in.
 */
public interface EmailSender {

    /**
     * Despacha el correo de verificacion de cuenta con el PIN militar de 6 digitos.
     *
     * @param toEmail  Direccion de correo destinataria.
     * @param username Nombre o callsign militar del oficial.
     * @param otp      Codigo PIN de 6 digitos.
     */
    void sendVerificationEmail(String toEmail, String username, String otp);

    /**
     * Despacha el correo de recuperacion de credenciales con el PIN de restablecimiento.
     *
     * @param toEmail  Direccion de correo destinataria.
     * @param username Nombre o callsign militar del oficial.
     * @param otp      Codigo PIN de 6 digitos.
     */
    void sendPasswordResetEmail(String toEmail, String username, String otp);
}
