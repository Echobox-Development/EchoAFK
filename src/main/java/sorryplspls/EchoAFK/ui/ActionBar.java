package sorryplspls.EchoAFK.ui;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public final class ActionBar {

    private ActionBar() {}

    public static void sendActionBar(Player player, String message) {
        if(message == null) return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
}
