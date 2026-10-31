package es.upm.etsisi.fis.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.etsisi.fis.server.dto.auth.*;
import es.upm.etsisi.fis.server.entity.UserEntity;
import es.upm.etsisi.fis.server.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Suite de pruebas de integracion para los endpoints de autenticacion y emision de tokens JWT.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Registro exitoso de nuevo usuario devuelve 201 y token JWT")
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("newplayer", "newplayer@battleship.com", "secure123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.username").value("newplayer"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @DisplayName("Rechazar registro con nombre de usuario duplicado aunque el correo sea nuevo")
    void testRegisterDuplicateUsernameFails() throws Exception {
        RegisterRequest request = new RegisterRequest("admin", "new_email_99@battleship.com", "password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username 'admin' is already taken."));
    }

    @Test
    @DisplayName("Rechazar registro con correo electronico duplicado aunque el username sea nuevo")
    void testRegisterDuplicateEmailFails() throws Exception {
        RegisterRequest request = new RegisterRequest("cadetunique", "admin@upm.es", "password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email 'admin@upm.es' is already registered."));
    }

    @Test
    @DisplayName("Rechazar registro con contrasena menor a 6 caracteres por validacion DTO")
    void testRegisterPasswordTooShortFails() throws Exception {
        RegisterRequest request = new RegisterRequest("cadetshort", "cadetshort@battleship.com", "12345");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.details.password").value("Password must be at least 6 characters"));
    }

    @Test
    @DisplayName("Rechazar registro con formato de correo invalido por validacion DTO")
    void testRegisterInvalidEmailFormatFails() throws Exception {
        RegisterRequest request = new RegisterRequest("cadetinvalid", "not-a-valid-email", "password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.details.email").value("Invalid email format"));
    }

    @Test
    @DisplayName("Login exitoso con usuario semilla devuelve 200 y token")
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("alvaro", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.username").value("alvaro"));
    }

    @Test
    @DisplayName("Login con credenciales incorrectas rechaza la peticion")
    void testLoginInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("alvaro", "wrongPassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Acceso a /api/v1/auth/me protegido con JWT Bearer")
    void testGetCurrentUserProfile() throws Exception {
        // 1. Iniciar sesion para obtener token
        LoginRequest login = new LoginRequest("admin", "admin123");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseJson).get("token").asText();

        // 2. Acceder al endpoint protegido
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Acceso a /api/v1/auth/me sin token es denegado")
    void testGetProfileWithoutTokenForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Cambio exitoso de contrasena actualiza las credenciales")
    void testChangePasswordSuccess() throws Exception {
        // 1. Iniciar sesion
        LoginRequest login = new LoginRequest("adrian", "password123");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();

        // 2. Cambiar contrasena
        ChangePasswordRequest changeReq = new ChangePasswordRequest("password123", "newSecretPassword123");
        mockMvc.perform(put("/api/v1/auth/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeReq)))
                .andExpect(status().isNoContent());

        // 3. Probar nuevo login con la nueva contrasena
        LoginRequest newLogin = new LoginRequest("adrian", "newSecretPassword123");
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLogin)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Cambio de contrasena con contrasena actual incorrecta es rechazado")
    void testChangePasswordWithWrongCurrentPassword() throws Exception {
        LoginRequest login = new LoginRequest("admin", "admin123");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();

        ChangePasswordRequest changeReq = new ChangePasswordRequest("wrongAdminPassword", "brandNewPassword123");
        mockMvc.perform(put("/api/v1/auth/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeReq)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Cambio de contrasena con nueva contrasena menor a 6 caracteres es rechazado")
    void testChangePasswordTooShortFails() throws Exception {
        LoginRequest login = new LoginRequest("admin", "admin123");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();

        ChangePasswordRequest changeReq = new ChangePasswordRequest("admin123", "123");
        mockMvc.perform(put("/api/v1/auth/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.details.newPassword").value("New password must be at least 6 characters long"));
    }

    @Test
    @DisplayName("Verificacion de correo con codigo OTP valido confirma el expediente del oficial")
    void testVerifyEmailWithValidOtpSuccess() throws Exception {
        // 1. Registrar usuario
        RegisterRequest regReq = new RegisterRequest("cadet01", "cadet01@battleship.com", "password123");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.emailVerified").value(false));

        // 2. Obtener el codigo OTP generado en base de datos
        UserEntity user = userRepository.findByUsername("cadet01").orElseThrow();
        String validOtp = user.getVerificationOtp();

        // 3. Verificar con OTP valido
        VerifyEmailRequest verifyReq = new VerifyEmailRequest("cadet01", validOtp);
        mockMvc.perform(post("/api/v1/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailVerified").value(true))
                .andExpect(jsonPath("$.token").isString());

        // 4. Comprobar que en base de datos ya consta como verificado
        UserEntity verifiedUser = userRepository.findByUsername("cadet01").orElseThrow();
        org.junit.jupiter.api.Assertions.assertTrue(verifiedUser.isEmailVerified());
        org.junit.jupiter.api.Assertions.assertNull(verifiedUser.getVerificationOtp());
    }

    @Test
    @DisplayName("Verificacion de correo con codigo OTP erroneo es rechazada con 400 Bad Request")
    void testVerifyEmailWithInvalidOtpFails() throws Exception {
        RegisterRequest regReq = new RegisterRequest("cadet02", "cadet02@battleship.com", "password123");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        VerifyEmailRequest verifyReq = new VerifyEmailRequest("cadet02", "000000");
        mockMvc.perform(post("/api/v1/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Reenvio de codigo OTP genera un nuevo PIN militar")
    void testResendOtpSuccess() throws Exception {
        RegisterRequest regReq = new RegisterRequest("cadet03", "cadet03@battleship.com", "password123");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        ResendOtpRequest resendReq = new ResendOtpRequest("cadet03");
        mockMvc.perform(post("/api/v1/auth/resend-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resendReq)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Verificar correo de usuario ya verificado rechaza con 400 Bad Request")
    void testVerifyAlreadyVerifiedEmailFails() throws Exception {
        // 'alvaro' ya es un usuario pre-sembrado con email verificado
        VerifyEmailRequest verifyReq = new VerifyEmailRequest("alvaro", "123456");
        mockMvc.perform(post("/api/v1/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Verificar codigo OTP expirado rechaza con 400 Bad Request")
    void testVerifyExpiredOtpFails() throws Exception {
        RegisterRequest regReq = new RegisterRequest("cadetexp", "cadetexp@battleship.com", "password123");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated());

        // Forzar expiracion en base de datos
        UserEntity user = userRepository.findByUsername("cadetexp").orElseThrow();
        user.setOtpExpiresAt(java.time.LocalDateTime.now().minusMinutes(5));
        userRepository.save(user);

        VerifyEmailRequest verifyReq = new VerifyEmailRequest("cadetexp", user.getVerificationOtp());
        mockMvc.perform(post("/api/v1/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Reenviar codigo OTP a usuario inexistente devuelve 404 Not Found")
    void testResendOtpNonexistentUserReturns404() throws Exception {
        ResendOtpRequest resendReq = new ResendOtpRequest("nonexistent_user_99");
        mockMvc.perform(post("/api/v1/auth/resend-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resendReq)))
                .andExpect(status().isNotFound());
    }
}
