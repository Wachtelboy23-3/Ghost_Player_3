package de.ghostplayers;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class ServerListPingListener implements Listener {

    private final GhostPlayersPlugin plugin;

    public ServerListPingListener(GhostPlayersPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerListPing(PaperServerListPingEvent event) {
        int hiddenCount = 0;

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            UUID playerUUID = player.getUniqueId();
            if (plugin.getAllHiddenPlayers().contains(playerUUID) || plugin.getHiddenFromAll().contains(playerUUID)) {
                hiddenCount++;
            }
        }

        int actualOnline = plugin.getServer().getOnlinePlayers().size() - hiddenCount;
        event.setNumPlayers(actualOnline);
    }
}
