package sorryplspls.EchoAFK;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import sorryplspls.EchoAFK.data.AfkData;
import sorryplspls.EchoAFK.listener.AfkListener;
import sorryplspls.EchoAFK.placeholders.AfkPlaceholder;
import sorryplspls.EchoAFK.service.AfkService;
import sorryplspls.EchoAFK.service.RegionService;
import sorryplspls.EchoAFK.tasks.AfkPresenceChecker;
import sorryplspls.EchoAFK.tasks.AfkTasks;
import sorryplspls.EchoAFK.db.DatabaseManager;
import sorryplspls.EchoAFK.db.AfkStats;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class Main extends JavaPlugin {

    private static Main instance;

    private AfkService afkService;
    private RegionService regionService;

    private long rewardInterval;
    private int baseAmount;

    private DatabaseManager databaseManager;
    private final Map<UUID, AfkStats> statsCache = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        rewardInterval = getConfig().getLong("afk.reward-interval", 60000L);
        baseAmount = getConfig().getInt("afk.base-amount", 1);

        Location corner1 = loadLocationFromConfig("region.corner1");
        Location corner2 = loadLocationFromConfig("region.corner2");

        this.afkService = new AfkService();
        this.regionService = new RegionService();
        this.regionService.setRegionCorners(corner1, corner2);

        databaseManager = new DatabaseManager();
        try {
            databaseManager.initialize();

            List<AfkStats> allStats = databaseManager.loadAllStats();
            for (AfkStats stats : allStats) {
                UUID uuid = UUID.fromString(stats.getPlayerUUID());
                statsCache.put(uuid, stats);
            }
        } catch (SQLException e) {
            getLogger().severe("Failed to initialize database: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new AfkListener(), this);
        Bukkit.getScheduler().runTaskTimer(this, new AfkTasks(), 20L, 20L);
        new AfkPresenceChecker().runTaskTimer(this, 100L, 1200L);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new AfkPlaceholder().register();
        }

        verifyDB();
    }

    @Override
    public void onDisable() {
        for (UUID uuid : getAfkService().getAfkDataMap().keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                getAfkService().unsetAfk(player);
            }
        }

        if (databaseManager != null) {
            databaseManager.close();
        }
    }

    private void verifyDB(){
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    AfkStats stats = getDatabaseManager()
                            .getOrCreateStats(player.getUniqueId().toString());
                    getStatsCache().put(player.getUniqueId(), stats);
                    AfkData data = new AfkData();
                    data.setAfk(false);
                    getAfkService().getAfkDataMap().put(player.getUniqueId(), data);
                } catch (SQLException e) {
                    getLogger().warning("Failed to initialize " + player.getName());
                }
            }
        }, 20L);
    }

    public static Main getInstance() {
        return instance;
    }

    public AfkService getAfkService() {
        return afkService;
    }

    public RegionService getRegionService() {
        return regionService;
    }

    public long getRewardInterval() {
        return rewardInterval;
    }

    public int getBaseAmount() {
        return baseAmount;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public Map<UUID, AfkStats> getStatsCache() {
        return statsCache;
    }

    private Location loadLocationFromConfig(String path) {
        var section = getConfig().getConfigurationSection(path);

        if (section == null) {
            return null;
        }

        String worldName = section.getString("world");
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        if (worldName != null) {
            return new Location(Bukkit.getWorld(worldName), x, y, z);
        }
        return null;
    }
}
