package es.upm.etsisi.fis.server.dto.leaderboard;

/**
 * Respuesta DTO para el escalafon de oficiales por suma acumulada de puntos de carrera.
 */
public class PlayerRankingResponse {
    private String username;
    private long totalPoints;
    private long totalGames;
    private long totalWins;
    private String navalRank;

    public PlayerRankingResponse() {}

    public PlayerRankingResponse(String username, long totalPoints, long totalGames, long totalWins, String navalRank) {
        this.username = username;
        this.totalPoints = totalPoints;
        this.totalGames = totalGames;
        this.totalWins = totalWins;
        this.navalRank = navalRank;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(long totalPoints) {
        this.totalPoints = totalPoints;
    }

    public long getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(long totalGames) {
        this.totalGames = totalGames;
    }

    public long getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(long totalWins) {
        this.totalWins = totalWins;
    }

    public String getNavalRank() {
        return navalRank;
    }

    public void setNavalRank(String navalRank) {
        this.navalRank = navalRank;
    }
}
