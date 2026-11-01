-- V1__init_schema.sql
-- Esquema relacional inicial de FleetGrid antes de la simplificacion arquitectonica

CREATE TABLE IF NOT EXISTS server_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    otp_hash VARCHAR(64),
    otp_purpose VARCHAR(30),
    otp_expires_at TIMESTAMP,
    otp_attempts INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS server_games (
    id VARCHAR(64) PRIMARY KEY,
    user_id BIGINT,
    player1_username VARCHAR(50) NOT NULL,
    player2_username VARCHAR(50),
    difficulty VARCHAR(20),
    duration_seconds BIGINT,
    moves_count INT,
    result VARCHAR(20),
    points BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_games_user FOREIGN KEY (user_id) REFERENCES server_users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS server_moves (
    id BIGSERIAL PRIMARY KEY,
    game_id VARCHAR(64) NOT NULL,
    player_username VARCHAR(50) NOT NULL,
    coord_row INT NOT NULL,
    coord_col INT NOT NULL,
    result VARCHAR(30) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS server_leaderboard (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    points BIGINT NOT NULL,
    game_id VARCHAR(64),
    difficulty VARCHAR(20),
    duration_seconds BIGINT,
    moves_count INT,
    result VARCHAR(20),
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
