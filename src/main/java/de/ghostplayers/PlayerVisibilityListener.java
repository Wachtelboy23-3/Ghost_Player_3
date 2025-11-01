package de.ghostplayers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

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
            Set<UUID> hiddenFromAll = plugin.getHiddenFromAll();
            Map<UUID, Set<UUID>> hiddenPlayers = plugin.getHiddenPlayersMap();
            Set<UUID> canSeeHidden = plugin.getCanSeeHidden();
            boolean joiningCanSeeHidden = canSeeHidden.contains(joiningUUID) ||
                                         (joiningPlayer.hasPermission("ghostplayers.seehidden"));

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer == joiningPlayer) continue;

                UUID onlineUUID = onlinePlayer.getUniqueId();
                boolean onlineCanSeeHidden = canSeeHidden.contains(onlineUUID) ||
                                           (onlinePlayer.hasPermission("ghostplayers.seehidden"));

                if (hiddenFromAll.contains(joiningUUID)) {
                    onlinePlayer.hidePlayer(plugin, joiningPlayer);
                } else if (hiddenPlayers.containsKey(joiningUUID) &&
                          hiddenPlayers.get(joiningUUID).contains(onlineUUID) &&
                          !onlineCanSeeHidden) {
                    onlinePlayer.hidePlayer(plugin, joiningPlayer);
                } else {
                    onlinePlayer.showPlayer(plugin, joiningPlayer);
                }

                if (hiddenFromAll.contains(onlineUUID)) {
                    joiningPlayer.hidePlayer(plugin, onlinePlayer);
                } else if (hiddenPlayers.containsKey(onlineUUID) &&
                          hiddenPlayers.get(onlineUUID).contains(joiningUUID) &&
                          !joiningCanSeeHidden) {
                    joiningPlayer.hidePlayer(plugin, onlinePlayer);
                } else {
                    joiningPlayer.showPlayer(plugin, onlinePlayer);
                }
            }
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player quittingPlayer = event.getPlayer();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer == quittingPlayer) continue;
            onlinePlayer.showPlayer(plugin, quittingPlayer);
        }
    }
}
