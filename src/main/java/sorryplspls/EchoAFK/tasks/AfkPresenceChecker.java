package sorryplspls.EchoAFK.tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import sorryplspls.EchoAFK.Main;

public class AfkPresenceChecker extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Main.getInstance().getRegionService().isInRegion(player.getLocation())) {
                if (!Main.getInstance().getAfkService().isAfk(player)) {
                    Main.getInstance().getAfkService().setAfk(player);
                }
            } else {
                if (Main.getInstance().getAfkService().isAfk(player)) {
                    Main.getInstance().getAfkService().unsetAfk(player);
                }
            }
        }
    }
}
