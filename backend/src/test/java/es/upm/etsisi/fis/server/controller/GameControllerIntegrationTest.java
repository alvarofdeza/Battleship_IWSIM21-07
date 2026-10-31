package es.upm.etsisi.fis.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.etsisi.fis.server.dto.auth.LoginRequest;
import es.upm.etsisi.fis.server.dto.game.CreateGameRequest;
import es.upm.etsisi.fis.server.dto.game.FireShotRequest;
import org.junit.jupiter.api.BeforeEach;
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
 * Suite de pruebas de integracion para la creacion de partidas y ejecucion de disparos REST.
 */
@SpringBootTest
@AutoConfigureMockMvc
class GameControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        LoginRequest login = new LoginRequest("alvaro", "password123");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        this.jwtToken = objectMapper.readTree(responseJson).get("token").asText();
    }

    @Test
    @DisplayName("Crear partida contra IA devuelve 201 y tableros inicializados")
    void testCreateSoloGame() throws Exception {
        CreateGameRequest request = new CreateGameRequest("NORMAL");

        mockMvc.perform(post("/api/v1/games")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gameId").isString())
                .andExpect(jsonPath("$.player1").value("alvaro"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.myFleetGrid").isArray())
                .andExpect(jsonPath("$.enemyFogGrid").isArray());
    }

    @Test
    @DisplayName("Efectuar disparo y recibir contraataque sincronizado de la IA")
    void testFireShotAndAiCounterAttack() throws Exception {
        // 1. Crear partida
        CreateGameRequest createReq = new CreateGameRequest("EASY");
        MvcResult createResult = mockMvc.perform(post("/api/v1/games")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String gameId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("gameId").asText();

        // 2. Disparar sobre (0, 0)
        FireShotRequest shotReq = new FireShotRequest(0, 0);

        mockMvc.perform(post("/api/v1/games/" + gameId + "/shots")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shotReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(gameId))
                .andExpect(jsonPath("$.playerRow").value(0))
                .andExpect(jsonPath("$.playerCol").value(0))
                .andExpect(jsonPath("$.playerShotResult").isString())
                .andExpect(jsonPath("$.aiCounterAttacked").value(true));

        // 3. Consultar historial de disparos
        mockMvc.perform(get("/api/v1/games/" + gameId + "/history")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)); // Disparo humano + contraataque IA
    }

    @Test
    @DisplayName("Rechazar disparo con coordenadas fuera de los limites de la cuadricula")
    void testFireShotOutOfBoundsRejected() throws Exception {
        CreateGameRequest createReq = new CreateGameRequest("NORMAL");
        MvcResult createResult = mockMvc.perform(post("/api/v1/games")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String gameId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("gameId").asText();

        // Coordenada fuera de limites (10, 5)
        FireShotRequest shotReq = new FireShotRequest(10, 5);

        mockMvc.perform(post("/api/v1/games/" + gameId + "/shots")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shotReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Rechazar disparo repetido en casilla previamente atacada")
    void testFireShotDuplicateRejected() throws Exception {
        CreateGameRequest createReq = new CreateGameRequest("EASY");
        MvcResult createResult = mockMvc.perform(post("/api/v1/games")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String gameId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("gameId").asText();

        FireShotRequest shotReq = new FireShotRequest(2, 2);

        // Primer disparo: exitoso
        mockMvc.perform(post("/api/v1/games/" + gameId + "/shots")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shotReq)))
                .andExpect(status().isOk());

        // Segundo disparo a la misma casilla: rechazado con 400
        mockMvc.perform(post("/api/v1/games/" + gameId + "/shots")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shotReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Consultar partida inexistente devuelve 404 Not Found")
    void testGetGameNotFoundReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/games/nonexistent-id")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Crear partida contra IA en dificultad HARD (algoritmo mapa de calor)")
    void testCreateGameWithHardAi() throws Exception {
        CreateGameRequest request = new CreateGameRequest("HARD");

        mockMvc.perform(post("/api/v1/games")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.player2").value("AI-Expert"));
    }

    @Test
    @DisplayName("Modo invitado: crear partida y disparar sin token de autenticacion")
    void testCreateAndPlayGuestGameWithoutAuth() throws Exception {
        // 1. Crear partida como invitado (sin token)
        CreateGameRequest request = new CreateGameRequest("NORMAL");

        MvcResult result = mockMvc.perform(post("/api/v1/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.player1").value("Guest"))
                .andReturn();

        String gameId = objectMapper.readTree(result.getResponse().getContentAsString()).get("gameId").asText();

        // 2. Disparar sin token
        FireShotRequest shotReq = new FireShotRequest(0, 0);
        mockMvc.perform(post("/api/v1/games/" + gameId + "/shots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shotReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(gameId));

        // 3. Consultar historial de disparos del invitado (debe devolver los 2 movimientos en memoria)
        mockMvc.perform(get("/api/v1/games/" + gameId + "/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].playerUsername").value("Guest"));
    }

    @Test
    @DisplayName("Reclamar partida sin token JWT es rechazado con 403 Forbidden")
    void testClaimGameWithoutTokenForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/games/dummy-id/claim"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Reclamar partida aun en curso rechaza la peticion")
    void testClaimUnfinishedGameFails() throws Exception {
        // Crear partida de invitado
        CreateGameRequest request = new CreateGameRequest("EASY");
        MvcResult result = mockMvc.perform(post("/api/v1/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String gameId = objectMapper.readTree(result.getResponse().getContentAsString()).get("gameId").asText();

        // Intentar reclamarla antes de finalizar el combate
        mockMvc.perform(post("/api/v1/games/" + gameId + "/claim")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Reclamar partida inexistente devuelve 404 Not Found")
    void testClaimNonExistentGameReturns404() throws Exception {
        mockMvc.perform(post("/api/v1/games/non-existent-session/claim")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }
}
