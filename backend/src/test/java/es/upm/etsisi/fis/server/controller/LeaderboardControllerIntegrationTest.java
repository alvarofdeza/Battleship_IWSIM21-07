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
 * Suite de pruebas de integracion para los endpoints del Leaderboard y clasificaciones.
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
        LoginRequest login = new LoginRequest("alvaro", "password123");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        this.jwtToken = objectMapper.readTree(responseJson).get("token").asText();

        leaderboardScoreRepository.save(new LeaderboardScoreEntity("alvaro", 94L, "test-game-1"));
        leaderboardScoreRepository.save(new LeaderboardScoreEntity("adrian", 82L, "test-game-2"));
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
                .andExpect(jsonPath("$[0].points").value(94));
    }
}
