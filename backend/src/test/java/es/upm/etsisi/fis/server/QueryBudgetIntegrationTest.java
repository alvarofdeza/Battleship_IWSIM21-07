package es.upm.etsisi.fis.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.etsisi.fis.server.dto.auth.LoginRequest;
import es.upm.etsisi.fis.server.dto.game.CreateGameRequest;
import es.upm.etsisi.fis.server.dto.game.FireShotRequest;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Suite de pruebas de regresion y presupuesto de consultas a base de datos (Query Budget).
 * Garantiza que la base de datos pueda permanecer en reposo (Neon Serverless auto-suspend).
 */
@SpringBootTest
@AutoConfigureMockMvc
class QueryBudgetIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private ObjectMapper objectMapper;

    private Statistics statistics;

    @BeforeEach
    void setUp() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
    }

    @Test
    @DisplayName("El endpoint /healthz ejecuta exactamente 0 consultas SQL")
    void testHealthzExecutesZeroQueries() throws Exception {
        statistics.clear();

        mockMvc.perform(get("/healthz"))
                .andExpect(status().isOk());

        assertThat(statistics.getPrepareStatementCount())
                .as("Health check must be strictly decoupled from database to allow Neon auto-suspend")
                .isEqualTo(0);
    }

    @Test
    @DisplayName("El ranking de partidas en cache ejecuta 0 consultas SQL")
    void testLeaderboardMatchesCachedExecutesZeroQueries() throws Exception {
        // 1. Peticion inicial para calentar cache
        mockMvc.perform(get("/api/v1/leaderboard/matches"))
                .andExpect(status().isOk());

        // 2. Limpiar contadores
        statistics.clear();

        // 3. Segunda peticion servida desde cache Caffeine en memoria
        mockMvc.perform(get("/api/v1/leaderboard/matches"))
                .andExpect(status().isOk());

        assertThat(statistics.getPrepareStatementCount())
                .as("Cached leaderboard request must execute 0 SQL queries")
                .isEqualTo(0);
    }

    @Test
    @DisplayName("El ranking de jugadores en cache ejecuta 0 consultas SQL")
    void testLeaderboardPlayersCachedExecutesZeroQueries() throws Exception {
        mockMvc.perform(get("/api/v1/leaderboard/players"))
                .andExpect(status().isOk());

        statistics.clear();

        mockMvc.perform(get("/api/v1/leaderboard/players"))
                .andExpect(status().isOk());

        assertThat(statistics.getPrepareStatementCount())
                .as("Cached players ranking must execute 0 SQL queries")
                .isEqualTo(0);
    }

    @Test
    @DisplayName("Peticion con JWT cacheado no realiza consultas de carga a server_users")
    void testCachedJwtAuthenticationExecutesZeroQueriesToUsers() throws Exception {
        LoginRequest login = new LoginRequest("alvaro@alumnos.upm.es", "password123");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();

        // Peticion 1 para almacenar en cache de autenticacion
        mockMvc.perform(get("/api/v1/leaderboard/me/stats")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        statistics.clear();

        // Peticion 2 con token ya en cache Caffeine
        mockMvc.perform(get("/api/v1/leaderboard/me/stats")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        assertThat(statistics.getEntityLoadCount())
                .as("Cached JWT authentication must not reload UserEntity from database")
                .isEqualTo(0);
    }

    @Test
    @DisplayName("Disparo tactico intermedio ejecuta exactamente 0 consultas SQL")
    void testIntermediateShotExecutesZeroQueries() throws Exception {
        CreateGameRequest createReq = new CreateGameRequest("EASY");
        MvcResult createResult = mockMvc.perform(post("/api/v1/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String gameId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("gameId").asText();

        statistics.clear();

        // Disparo en curso en memoria
        FireShotRequest shot = new FireShotRequest(0, 0);
        mockMvc.perform(post("/api/v1/games/" + gameId + "/shots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shot)))
                .andExpect(status().isOk());

        assertThat(statistics.getPrepareStatementCount())
                .as("In-memory battle shot must execute 0 SQL queries")
                .isEqualTo(0);
    }
}
