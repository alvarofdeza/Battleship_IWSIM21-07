package es.upm.etsisi.fis.server.dto.leaderboard;

/**
 * Respuesta DTO para el expediente militar y balance de carrera de un oficial.
 */
public class UserStatsResponse {
    private String username;
    private long totalPoints;
    private long totalGames;
    private long totalWins;
    private long totalLosses;
    private double winRate;
    private String navalRank;

    public UserStatsResponse() {}

    public UserStatsResponse(String username, long totalPoints, long totalGames,
                             long totalWins, long totalLosses, double winRate, String navalRank) {
        this.username = username;
        this.totalPoints = totalPoints;
        this.totalGames = totalGames;
        this.totalWins = totalWins;
        this.totalLosses = totalLosses;
        this.winRate = winRate;
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

    public long getTotalLosses() {
        return totalLosses;
    }

    public void setTotalLosses(long totalLosses) {
        this.totalLosses = totalLosses;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public String getNavalRank() {
        return navalRank;
    }

    public void setNavalRank(String navalRank) {
        this.navalRank = navalRank;
    }
}
