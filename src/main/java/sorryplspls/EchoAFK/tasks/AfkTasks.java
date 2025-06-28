package sorryplspls.EchoAFK.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import sorryplspls.EchoAFK.Main;
import sorryplspls.EchoAFK.data.AfkData;
import sorryplspls.EchoAFK.rewards.RewardMultiplier;
import sorryplspls.EchoAFK.service.AfkService;
import sorryplspls.EchoAFK.ui.ActionBar;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class AfkTasks implements Runnable {

    private final RewardMultiplier rewardMultiplier = new RewardMultiplier();

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, AfkData>> iterator = Main.getInstance().getAfkService().getAfkDataMap().entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, AfkData> entry = iterator.next();
            AfkData data = entry.getValue();

            if (!data.isAfk()) continue;

            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null || !player.isOnline()) {
                iterator.remove();
                continue;
            }

            long timeSinceLastReward = now - data.getLastReward();
            long timeLeft = AfkService.REWARD_INTERVAL - timeSinceLastReward;

            if (timeSinceLastReward <= AfkService.ACTIONBAR_DURATION) {
                String timeLeftStr = formatTime(timeLeft);
                ActionBar.sendActionBar(player, "§aTime Left: §2" + timeLeftStr);
            }

            if (now - data.getLastFeedback() >= AfkService.FEEDBACK_INTERVAL) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Rewards in " + formatTime(timeLeft) + "."));
                data.setLastFeedback(now);
            }

            if (timeLeft <= 0) {
                rewardMultiplier.giveReward(player);

                data.setLastReward(now);
                data.setLastFeedback(now);
            }
        }
    }


    private String formatTime(long ms) {
        if (ms < 0) ms = 0;
        long totalSeconds = ms / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        if (minutes > 0) {
            return String.format("%d minute(s), %d second(s)", minutes, seconds);
        } else {
            return String.format("%d second(s)", seconds);
        }
    }
}
