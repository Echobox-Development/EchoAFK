package sorryplspls.EchoAFK.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import sorryplspls.EchoAFK.Main;
import sorryplspls.EchoAFK.data.AfkData;
import sorryplspls.EchoAFK.db.AfkStats;
import java.sql.SQLException;
import java.util.UUID;

public class AfkListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        try {
            AfkStats stats = Main.getInstance().getDatabaseManager()
                    .getOrCreateStats(player.getUniqueId().toString());

            Main.getInstance().getStatsCache().put(player.getUniqueId(), stats);

            AfkData data = new AfkData();
            data.setAfk(false);
            Main.getInstance().getAfkService()
                    .getAfkDataMap()
                    .put(player.getUniqueId(), data);

        } catch (SQLException e) {
            Main.getInstance().getLogger()
                    .warning("Failed to load AFK stats for " + player.getName());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        AfkData data = Main.getInstance().getAfkService().getAfkDataMap().get(uuid);

        if (data != null && data.isAfk()) {
            long sessionSeconds = (System.currentTimeMillis() - data.getEnteredAFK()) / 1000;

            AfkStats stats = Main.getInstance().getStatsCache().get(uuid);

            if (stats != null) {
                stats.setTotalAfkSeconds(stats.getTotalAfkSeconds() + sessionSeconds);

                try {
                    Main.getInstance().getDatabaseManager().updateStats(stats);
                } catch (SQLException e) {
                    Main.getInstance().getLogger().warning("Failed to save AFK stats for " + player.getName() + ": " + e.getMessage());
                }
            }
        }

        Main.getInstance().getAfkService().getAfkDataMap().remove(uuid);
        Main.getInstance().getStatsCache().remove(uuid);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        boolean isInRegion = Main.getInstance()
                .getRegionService()
                .isInRegion(player.getLocation());
        boolean isAfk = Main.getInstance()
                .getAfkService()
                .isAfk(player);

        if (isInRegion && !isAfk) {
            Main.getInstance().getAfkService().setAfk(player);
        } else if (!isInRegion && isAfk) {
            Main.getInstance().getAfkService().unsetAfk(player);
        }
    }
}
