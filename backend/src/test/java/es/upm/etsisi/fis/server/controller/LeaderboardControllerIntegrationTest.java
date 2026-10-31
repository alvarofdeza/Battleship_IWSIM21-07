package es.upm.etsisi.fis.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.etsisi.fis.server.dto.auth.LoginRequest;
import es.upm.etsisi.fis.server.entity.LeaderboardScoreEntity;
import es.upm.etsisi.fis.server.repository.LeaderboardScoreRepository;
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
 * Suite de pruebas de integracion para los endpoints del Leaderboard, doble ranking y expedientes de oficiales.
 */
@SpringBootTest
@AutoConfigureMockMvc
class LeaderboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LeaderboardScoreRepository leaderboardScoreRepository;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        leaderboardScoreRepository.deleteAll();

        LoginRequest login = new LoginRequest("alvaro", "password123");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        this.jwtToken = objectMapper.readTree(responseJson).get("token").asText();

        leaderboardScoreRepository.save(new LeaderboardScoreEntity("alvaro", 1250L, "test-game-1", "NORMAL", 110, 24, "VICTORY"));
        leaderboardScoreRepository.save(new LeaderboardScoreEntity("adrian", 820L, "test-game-2", "EASY", 150, 30, "VICTORY"));
        leaderboardScoreRepository.save(new LeaderboardScoreEntity("alvaro", 300L, "test-game-3", "NORMAL", 95, 18, "DEFEAT"));
    }

    @Test
    @DisplayName("Consultar clasificacion personal devuelve las mejores puntuaciones del usuario")
    void testGetPersonalLeaderboard() throws Exception {
        mockMvc.perform(get("/api/v1/leaderboard/personal")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("alvaro"));
    }

    @Test
    @DisplayName("Consultar clasificacion global devuelve todas las puntuaciones ordenadas")
    void testGetGlobalLeaderboard() throws Exception {
        mockMvc.perform(get("/api/v1/leaderboard/global")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].points").value(1250));
    }

    @Test
    @DisplayName("Consultar Top 10 mejores partidas individuales publicamente")
    void testGetTopMatches() throws Exception {
        mockMvc.perform(get("/api/v1/leaderboard/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].points").value(1250))
                .andExpect(jsonPath("$[0].result").value("VICTORY"))
                .andExpect(jsonPath("$[0].difficulty").value("NORMAL"));
    }

    @Test
    @DisplayName("Consultar Top 10 jugadores por suma de puntos acumulados")
    void testGetTopPlayers() throws Exception {
        mockMvc.perform(get("/api/v1/leaderboard/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("alvaro"))
                .andExpect(jsonPath("$[0].totalPoints").value(1550)) // 1250 + 300
                .andExpect(jsonPath("$[0].navalRank").isString());
    }

    @Test
    @DisplayName("Consultar mejores partidas personales con metadatos completos")
    void testGetMyBestMatches() throws Exception {
        mockMvc.perform(get("/api/v1/leaderboard/me/matches")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("alvaro"))
                .andExpect(jsonPath("$[0].points").value(1250));
    }

    @Test
    @DisplayName("Consultar expediente militar y estadisticas de carrera del oficial")
    void testGetMyStats() throws Exception {
        mockMvc.perform(get("/api/v1/leaderboard/me/stats")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alvaro"))
                .andExpect(jsonPath("$.totalPoints").value(1550))
                .andExpect(jsonPath("$.totalGames").value(2))
                .andExpect(jsonPath("$.totalWins").value(1))
                .andExpect(jsonPath("$.totalLosses").value(1))
                .andExpect(jsonPath("$.winRate").value(50.0))
                .andExpect(jsonPath("$.navalRank").value("Cadete Naval"));
    }
}
