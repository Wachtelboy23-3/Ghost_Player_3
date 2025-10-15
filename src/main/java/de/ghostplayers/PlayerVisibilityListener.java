package de.ghostplayers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerVisibilityListener implements Listener {

    private final GhostPlayersPlugin plugin;

    public PlayerVisibilityListener(GhostPlayersPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();
        UUID joiningUUID = joiningPlayer.getUniqueId();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                UUID onlineUUID = onlinePlayer.getUniqueId();

                if (plugin.isHiddenFrom(joiningUUID, onlineUUID)) {
                    onlinePlayer.hidePlayer(plugin, joiningPlayer);
                }

                if (plugin.isHiddenFrom(onlineUUID, joiningUUID)) {
                    joiningPlayer.hidePlayer(plugin, onlinePlayer);
                }
            }
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player quittingPlayer = event.getPlayer();
        UUID quittingUUID = quittingPlayer.getUniqueId();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.equals(quittingPlayer)) continue;

            onlinePlayer.showPlayer(plugin, quittingPlayer);
        }
    }
}
