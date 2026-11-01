-- V2__streamline_to_two_table_schema.sql
-- Consolidacion definitiva a dos tablas (server_users y server_games), eliminacion de tablas efimeras e indexacion optimizada

-- 1. Consolidar registros historicos de server_leaderboard hacia server_games para no perder ningun punto o partida
INSERT INTO server_games (id, user_id, player1_username, player2_username, difficulty, duration_seconds, moves_count, result, points, created_at)
SELECT
    COALESCE(sl.game_id, 'migrated-' || CAST(sl.id AS VARCHAR(20))),
    su.id,
    sl.username,
    'AI Opponent',
    COALESCE(sl.difficulty, 'NORMAL'),
    COALESCE(sl.duration_seconds, 60),
    COALESCE(sl.moves_count, 20),
    COALESCE(sl.result, 'VICTORY'),
    sl.points,
    COALESCE(sl.recorded_at, CURRENT_TIMESTAMP)
FROM server_leaderboard sl
LEFT JOIN server_users su ON su.username = sl.username
LEFT JOIN server_games sg ON (sg.id = sl.game_id)
WHERE sg.id IS NULL AND sl.points IS NOT NULL;

-- 2. Eliminar tablas efimeras y redundantes
DROP TABLE IF EXISTS server_moves;
DROP TABLE IF EXISTS server_leaderboard;

-- 3. Crear indices de alto rendimiento para consultas del Escalafon Militar (Admiralty Roll)
CREATE INDEX IF NOT EXISTS idx_games_points_desc ON server_games(points DESC, duration_seconds ASC);
CREATE INDEX IF NOT EXISTS idx_games_user_points ON server_games(player1_username, points DESC);
CREATE INDEX IF NOT EXISTS idx_games_user_created ON server_games(player1_username, created_at DESC);
