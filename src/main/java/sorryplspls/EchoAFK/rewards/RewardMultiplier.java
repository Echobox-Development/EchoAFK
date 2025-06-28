package sorryplspls.EchoAFK.rewards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import sorryplspls.EchoAFK.Main;

import java.util.Comparator;
import java.util.Map;

public class RewardMultiplier {

    private static final Map<String, Double> rankMultipliers = Map.of(
            "default", 1.0,
            "vip", 1.5,
            "mvp", 1.5,
            "mvp+", 1.5,
            "echo", 2.0,
            "echo+", 3.0,
            "custom", 3.0
    );

    private static final Map<String, Integer> rankPriority = Map.of(
            "custom", 6,
            "echo+", 5,
            "echo", 4,
            "mvp+", 3,
            "mvp", 2,
            "vip", 1,
            "default", 0
    );

    public String getPlayerRank(Player player) {
        return rankPriority.keySet().stream()
                .filter(player::hasPermission)
                .max(Comparator.comparingInt(rankPriority::get))
                .orElse("default");
    }

    public static double getMultiplier(String rank) {
        return rankMultipliers.getOrDefault(rank.toLowerCase(), 1.0);
    }

    public void giveReward(Player player) {
        String playerRank = getPlayerRank(player);
        double multiplier = getMultiplier(playerRank);
        int baseAmount = Main.getInstance().getBaseAmount();

        int finalAmount = (int) Math.floor(baseAmount * multiplier);

        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "afkvoucher " + player.getName() + " " + finalAmount
        );
    }
}
