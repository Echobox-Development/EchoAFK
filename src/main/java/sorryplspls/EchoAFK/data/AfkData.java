package sorryplspls.EchoAFK.data;

public class AfkData {

    private long lastActivity;
    private long enteredAFK;
    private long lastReward;
    private long lastFeedback;
    private boolean afk;

    public AfkData() {
        this.lastActivity = System.currentTimeMillis();
        this.enteredAFK = 0;
        this.lastReward = 0;
        this.lastFeedback = 0;
        this.afk = false;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

    public long getEnteredAFK() {
        return enteredAFK;
    }

    public void setEnteredAFK(long enteredAFK) {
        this.enteredAFK = enteredAFK;
    }

    public long getLastReward() {
        return lastReward;
    }

    public void setLastReward(long lastReward) {
        this.lastReward = lastReward;
    }

    public long getLastFeedback() {
        return lastFeedback;
    }

    public void setLastFeedback(long lastFeedback) {
        this.lastFeedback = lastFeedback;
    }

    public boolean isAfk() {
        return afk;
    }

    public void setAfk(boolean afk) {
        this.afk = afk;
    }
}
