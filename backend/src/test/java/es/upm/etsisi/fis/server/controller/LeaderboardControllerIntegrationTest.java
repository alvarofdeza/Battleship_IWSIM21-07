package es.upm.etsisi.fis.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.etsisi.fis.server.dto.auth.LoginRequest;
import es.upm.etsisi.fis.server.entity.GameRecordEntity;
import es.upm.etsisi.fis.server.repository.GameRecordRepository;
import es.upm.etsisi.fis.server.service.LeaderboardService;
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
    private GameRecordRepository gameRecordRepository;

    @Autowired
    private LeaderboardService leaderboardService;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        gameRecordRepository.deleteAll();
        leaderboardService.invalidateLeaderboardCache();

        LoginRequest login = new LoginRequest("alvaro@alumnos.upm.es", "password123");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        this.jwtToken = objectMapper.readTree(responseJson).get("token").asText();

        gameRecordRepository.save(new GameRecordEntity("test-game-1", null, "alvaro", "AI-Opponent", "NORMAL", 110L, 24, "VICTORY", 1250L));
        gameRecordRepository.save(new GameRecordEntity("test-game-2", null, "adrian", "AI-Opponent", "EASY", 150L, 30, "VICTORY", 820L));
        gameRecordRepository.save(new GameRecordEntity("test-game-3", null, "alvaro", "AI-Opponent", "NORMAL", 95L, 18, "DEFEAT", 300L));
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
        mockMvc.perform(get("/api/v1/leaderboard/global"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].points").value(1250));
    }

    @Test
    @DisplayName("Consultar top 10 partidas devuelve ordenacion por puntos descendente")
    void testGetTopMatches() throws Exception {
        mockMvc.perform(get("/api/v1/leaderboard/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].playerUsername").value("alvaro"))
                .andExpect(jsonPath("$[0].points").value(1250))
                .andExpect(jsonPath("$[1].playerUsername").value("adrian"))
                .andExpect(jsonPath("$[1].points").value(820));
    }

    @Test
    @DisplayName("Consultar top jugadores agrupa por suma total de puntos de carrera")
    void testGetTopPlayers() throws Exception {
        mockMvc.perform(get("/api/v1/leaderboard/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("alvaro"))
                .andExpect(jsonPath("$[0].totalPoints").value(1550))
                .andExpect(jsonPath("$[0].totalGames").value(2))
                .andExpect(jsonPath("$[0].totalWins").value(1))
                .andExpect(jsonPath("$[1].username").value("adrian"))
                .andExpect(jsonPath("$[1].totalPoints").value(820));
    }

    @Test
    @DisplayName("Consultar expediente propio de partidas requiere token JWT")
    void testGetMyMatchesRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/v1/leaderboard/me/matches"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/leaderboard/me/matches")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].playerUsername").value("alvaro"));
    }

    @Test
    @DisplayName("Consultar historial reciente del oficial devuelve partidas en orden cronologico")
    void testGetMyRecentMatches() throws Exception {
        mockMvc.perform(get("/api/v1/leaderboard/me/recent")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].playerUsername").value("alvaro"));
    }

    @Test
    @DisplayName("Consultar estadisticas agregadas del perfil calcula correctamente victorias y rango")
    void testGetMyStats() throws Exception {
        mockMvc.perform(get("/api/v1/leaderboard/me/stats")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alvaro"))
                .andExpect(jsonPath("$.totalPoints").value(1550))
                .andExpect(jsonPath("$.totalWins").value(1))
                .andExpect(jsonPath("$.totalLosses").value(1))
                .andExpect(jsonPath("$.winRate").value(0.5))
                .andExpect(jsonPath("$.navalRank").value("Naval Cadet"));
    }
}
