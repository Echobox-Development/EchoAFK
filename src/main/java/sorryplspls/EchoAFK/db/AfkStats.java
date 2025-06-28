package sorryplspls.EchoAFK.db;

import com.j256.ormlite.field.DatabaseField;

public class AfkStats {

    @DatabaseField(id = true)
    private String playerUUID;

    @DatabaseField
    private long totalAfkSeconds;

    public AfkStats() {
    }

    public AfkStats(String playerUUID) {
        this.playerUUID = playerUUID;
        this.totalAfkSeconds = 0;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public long getTotalAfkSeconds() {
        return totalAfkSeconds;
    }

    public void setTotalAfkSeconds(long totalAfkSeconds) {
        this.totalAfkSeconds = totalAfkSeconds;
    }
}
