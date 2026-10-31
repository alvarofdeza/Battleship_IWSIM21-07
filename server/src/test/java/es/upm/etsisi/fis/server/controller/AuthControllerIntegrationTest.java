package es.upm.etsisi.fis.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.etsisi.fis.server.dto.auth.LoginRequest;
import es.upm.etsisi.fis.server.dto.auth.RegisterRequest;
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
    @DisplayName("Rechazar registro de usuario duplicado con error 500/400")
    void testRegisterDuplicateFails() throws Exception {
        RegisterRequest request = new RegisterRequest("admin", "admin@upm.es", "password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
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
}
