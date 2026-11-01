package es.upm.etsisi.fis.server.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el algoritmo de hash y comparacion en tiempo constante de codigos OTP.
 */
class OtpCryptoUtilsTest {

    @Test
    @DisplayName("Hash de codigo OTP genera cadena hexadecimal SHA-256 de 64 caracteres")
    void testHashOtpGeneratesValidHex() {
        String hash = OtpCryptoUtils.hashOtp("123456");
        assertNotNull(hash);
        assertEquals(64, hash.length());
        assertEquals("8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", hash);
    }

    @Test
    @DisplayName("Verificacion en tiempo constante devuelve true cuando el PIN coincide exactamente")
    void testVerifyOtpSuccess() {
        String hash = OtpCryptoUtils.hashOtp("654321");
        assertTrue(OtpCryptoUtils.verifyOtp("654321", hash));
    }

    @Test
    @DisplayName("Verificacion en tiempo constante devuelve false cuando el PIN difiere")
    void testVerifyOtpMismatch() {
        String hash = OtpCryptoUtils.hashOtp("654321");
        assertFalse(OtpCryptoUtils.verifyOtp("000000", hash));
        assertFalse(OtpCryptoUtils.verifyOtp("654322", hash));
    }

    @Test
    @DisplayName("Manejo seguro de valores nulos")
    void testNullSafety() {
        assertNull(OtpCryptoUtils.hashOtp(null));
        assertFalse(OtpCryptoUtils.verifyOtp(null, "somehash"));
        assertFalse(OtpCryptoUtils.verifyOtp("123456", null));
    }
}
