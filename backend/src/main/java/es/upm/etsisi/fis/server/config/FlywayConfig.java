package es.upm.etsisi.fis.server.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de ejecucion de migraciones Flyway con autorreparacion previa.
 * Ejecuta flyway.repair() antes de flyway.migrate() para sanear registros fallidos o desalineados
 * en la tabla flyway_schema_history en entornos de produccion y despliegue continuo.
 */
@Configuration
public class FlywayConfig {

    /**
     * Define la estrategia de migracion ejecutando repair antes de migrate.
     *
     * @return Estrategia de migracion resiliente frente a fallos previos en BD.
     */
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
