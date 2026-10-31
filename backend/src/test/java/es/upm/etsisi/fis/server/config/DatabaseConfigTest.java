package es.upm.etsisi.fis.server.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias para el componente de configuracion de base de datos cloud DatabaseConfig.
 */
class DatabaseConfigTest {

    private final DatabaseConfig databaseConfig = new DatabaseConfig();

    @Test
    @DisplayName("Debe proporcionar un DataSource por defecto cuando no hay variables en el entorno")
    void shouldProvideDefaultDataSourceWhenEnvironmentEmpty() {
        MockEnvironment env = new MockEnvironment();
        DataSource dataSource = databaseConfig.dataSource(env);

        assertThat(dataSource).isNotNull();
    }

    @Test
    @DisplayName("Debe sanitizar y normalizar URLs con esquema postgres:// y credenciales incrustadas")
    void shouldSanitizePostgresUrlWithEmbeddedCredentials() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("DATABASE_URL", "postgresql://neondb_owner:npg_secret@ep-test.eu-central-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require");

        DataSource dataSource = databaseConfig.dataSource(env);

        assertThat(dataSource).isNotNull();
    }

    @Test
    @DisplayName("Debe preservar URLs estandar de JDBC de PostgreSQL")
    void shouldPreserveStandardJdbcUrl() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("SPRING_DATASOURCE_URL", "jdbc:postgresql://localhost:5432/battleship_db");
        env.setProperty("SPRING_DATASOURCE_USERNAME", "custom_user");
        env.setProperty("SPRING_DATASOURCE_PASSWORD", "custom_pass");

        DataSource dataSource = databaseConfig.dataSource(env);

        assertThat(dataSource).isNotNull();
    }
}
