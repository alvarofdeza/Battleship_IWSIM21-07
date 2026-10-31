package es.upm.etsisi.fis.server.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.net.URI;

/**
 * Configuracion avanzada de origen de datos para entornos cloud (Render, Railway, Docker).
 * Normaliza automaticamente URLs de conexion de formato postgres:// al estandar JDBC de PostgreSQL.
 */
@Configuration
@Profile("prod")
public class DatabaseConfig {

    /**
     * Construye y proporciona el DataSource de produccion asegurando compatibilidad con proveedores cloud.
     *
     * @param env Entorno de ejecucion de Spring con las variables de configuracion.
     * @return Origen de datos conectado al servidor PostgreSQL.
     */
    @Bean
    public DataSource dataSource(Environment env) {
        String rawUrl = env.getProperty("SPRING_DATASOURCE_URL");
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = env.getProperty("DATABASE_URL");
        }
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = "jdbc:postgresql://localhost:5432/battleship_db";
        }

        String username = env.getProperty("SPRING_DATASOURCE_USERNAME");
        String password = env.getProperty("SPRING_DATASOURCE_PASSWORD");

        // Soporte para URLs completas emitidas por proveedores cloud (postgres://user:password@host:port/database)
        if (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://")) {
            try {
                URI uri = new URI(rawUrl.replace("jdbc:", ""));
                if (uri.getUserInfo() != null) {
                    String[] userInfo = uri.getUserInfo().split(":");
                    username = userInfo[0];
                    if (userInfo.length > 1) {
                        password = userInfo[1];
                    }
                }
                int port = uri.getPort() != -1 ? uri.getPort() : 5432;
                String path = uri.getPath() != null ? uri.getPath() : "/neondb";
                String query = (uri.getQuery() != null && !uri.getQuery().isBlank()) ? "?" + uri.getQuery() : "?sslmode=require";
                rawUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + path + query;
            } catch (Exception ignored) {
                if (!rawUrl.startsWith("jdbc:")) {
                    rawUrl = "jdbc:" + rawUrl;
                }
            }
        }

        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url(rawUrl)
                .username(username != null ? username : "postgres")
                .password(password != null ? password : "postgres")
                .build();
    }
}
