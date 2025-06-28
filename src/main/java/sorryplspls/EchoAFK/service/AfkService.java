package sorryplspls.EchoAFK.service;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import sorryplspls.EchoAFK.Main;
import sorryplspls.EchoAFK.data.AfkData;
import sorryplspls.EchoAFK.db.AfkStats;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AfkService {

    private final Map<UUID, AfkData> afkDataMap = new HashMap<>();

    public static final long REWARD_INTERVAL = Main.getInstance().getRewardInterval() * 60 * 1000;
    public static final long FEEDBACK_INTERVAL = 60 * 1000;
    public static final long ACTIONBAR_DURATION = 2 * 60 * 1000;

    public Map<UUID, AfkData> getAfkDataMap() {
        return afkDataMap;
    }

    public boolean isAfk(Player player) {
        AfkData data = afkDataMap.get(player.getUniqueId());
        return data != null && data.isAfk();
    }

    public void setAfk(Player player) {
        if (!Main.getInstance().isEnabled()) {
            return;
        }

        AfkData data = afkDataMap.computeIfAbsent(player.getUniqueId(), uuid -> new AfkData());

        if (!data.isAfk()) {
            long now = System.currentTimeMillis();
            data.setAfk(true);
            data.setEnteredAFK(now);
            data.setLastReward(now);
            data.setLastFeedback(now);
            data.setLastActivity(now);

            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            player.sendTitle(
                    ChatColor.translateAlternateColorCodes('&', "&a&LYou are now AFK."),
                    ChatColor.translateAlternateColorCodes('&', "&aRewards will now be distributed."),
                    1, 70, 1
            );
        }
    }

    public void unsetAfk(Player player) {
        if (!Main.getInstance().isEnabled()) {
            return;
        }

        AfkData data = afkDataMap.get(player.getUniqueId());
        if (data != null && data.isAfk()) {
            long sessionSeconds = (System.currentTimeMillis() - data.getEnteredAFK()) / 1000;

            AfkStats stats = Main.getInstance()
                    .getStatsCache()
                    .get(player.getUniqueId());

            if (stats != null) {
                stats.setTotalAfkSeconds(stats.getTotalAfkSeconds() + sessionSeconds);

                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    try {
                        Main.getInstance().getDatabaseManager().updateStats(stats);
                    } catch (SQLException e) {
                        Main.getInstance().getLogger().warning("Failed to update AFK stats for " + player.getName());
                    }
                });

                player.sendTitle(
                        ChatColor.translateAlternateColorCodes('&', "&c&LYou are no longer AFK."),
                        ChatColor.translateAlternateColorCodes('&', "&cRewards will no longer be distributed."),
                        1, 70, 1
                );
            }

            data.setAfk(false);
            data.setEnteredAFK(0);
            data.setLastActivity(System.currentTimeMillis());

            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }
}
