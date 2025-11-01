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
    private final Map<UUID, Set<UUID>> visibilityCache = new HashMap<>();

    public PlayerVisibilityListener(GhostPlayersPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();
        UUID joiningUUID = joiningPlayer.getUniqueId();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Set<UUID> hiddenFromJoining = new HashSet<>();
            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

            for (Player onlinePlayer : onlinePlayers) {
                if (onlinePlayer == joiningPlayer) continue;

                UUID onlineUUID = onlinePlayer.getUniqueId();

                if (plugin.isHiddenFrom(joiningUUID, onlineUUID)) {
                    onlinePlayer.hidePlayer(plugin, joiningPlayer);
                    hiddenFromJoining.add(onlineUUID);
                } else {
                    onlinePlayer.showPlayer(plugin, joiningPlayer);
                }

                if (plugin.isHiddenFrom(onlineUUID, joiningUUID)) {
                    joiningPlayer.hidePlayer(plugin, onlinePlayer);
                } else {
                    joiningPlayer.showPlayer(plugin, onlinePlayer);
                }
            }

            visibilityCache.put(joiningUUID, hiddenFromJoining);
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player quittingPlayer = event.getPlayer();
        UUID quittingUUID = quittingPlayer.getUniqueId();

        visibilityCache.remove(quittingUUID);

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player onlinePlayer : onlinePlayers) {
            if (onlinePlayer == quittingPlayer) continue;
            onlinePlayer.showPlayer(plugin, quittingPlayer);
        }
    }
}
