package sorryplspls.EchoAFK.placeholders;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import sorryplspls.EchoAFK.Main;
import sorryplspls.EchoAFK.db.AfkStats;

public class AfkPlaceholder extends PlaceholderExpansion {


    @Override
    public @NotNull String getIdentifier() {
        return "stat";
    }

    @Override
    public @NotNull String getAuthor() {
        return "sorryplspls";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        if (params.equalsIgnoreCase("afktime")) {
            AfkStats stats = Main.getInstance().getStatsCache().get(player.getUniqueId());

            if (stats != null) {
                return String.valueOf(stats.getTotalAfkSeconds());
            }
            return "0";
        }

        return null;
    }
}
