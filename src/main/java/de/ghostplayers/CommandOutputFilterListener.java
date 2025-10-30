package de.ghostplayers;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class CommandOutputFilterListener implements Listener {

    private final GhostPlayersPlugin plugin;

    public CommandOutputFilterListener(GhostPlayersPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        UUID senderUUID = sender.getUniqueId();

        if (sender.hasPermission("ghostplayers.seehidden") || plugin.canPlayerSeeHidden(senderUUID)) {
            return;
        }

        Component message = event.message();
        String plainText = message.toString().toLowerCase();

        if (plainText.contains("playtime") || plainText.contains("top") || plainText.contains("rank")) {
            String messageString = plainText;

            for (UUID hiddenUUID : plugin.getAllHiddenPlayers()) {
                Player hiddenPlayer = plugin.getServer().getPlayer(hiddenUUID);
                if (hiddenPlayer != null) {
                    String hiddenName = hiddenPlayer.getName().toLowerCase();
                    messageString = messageString.replaceAll("(?i)" + hiddenName, "");
                }
            }

            for (UUID hiddenUUID : plugin.getHiddenFromAll()) {
                Player hiddenPlayer = plugin.getServer().getPlayer(hiddenUUID);
                if (hiddenPlayer != null) {
                    String hiddenName = hiddenPlayer.getName().toLowerCase();
                    messageString = messageString.replaceAll("(?i)" + hiddenName, "");
                }
            }
        }
    }
}
