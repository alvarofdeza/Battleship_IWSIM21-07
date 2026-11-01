package es.upm.etsisi.fis.server.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Utilidades criptograficas para generacion de hash y verificacion en tiempo constante
 * de codigos de acceso y PINes militares de un solo uso (OTP).
 */
public final class OtpCryptoUtils {

    private OtpCryptoUtils() {
        // Constructor privado para clase utilitaria
    }

    /**
     * Calcula el hash criptografico SHA-256 en representacion hexadecimal del codigo PIN militar.
     *
     * @param otp Codigo de 6 digitos en texto plano.
     * @return Cadena hexadecimal de 64 caracteres con el hash SHA-256.
     */
    public static String hashOtp(String otp) {
        if (otp == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(otp.trim().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", e);
        }
    }

    /**
     * Compara en tiempo constante el codigo OTP facilitado por el usuario frente al hash esperado,
     * mitigando ataques basados en analisis de tiempos (timing attacks).
     *
     * @param rawOtp           Codigo de 6 digitos facilitado por el usuario.
     * @param expectedHexHash  Hash SHA-256 almacenado previamente en la base de datos.
     * @return true si los hashes coinciden de forma exacta; false en caso contrario.
     */
    public static boolean verifyOtp(String rawOtp, String expectedHexHash) {
        if (rawOtp == null || expectedHexHash == null) {
            return false;
        }
        String computedHex = hashOtp(rawOtp);
        byte[] a = computedHex.getBytes(StandardCharsets.UTF_8);
        byte[] b = expectedHexHash.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(a, b);
    }
}
