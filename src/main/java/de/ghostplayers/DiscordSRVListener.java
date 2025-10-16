package de.ghostplayers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class DiscordSRVListener implements Listener {

    private final GhostPlayersPlugin plugin;

    public DiscordSRVListener(GhostPlayersPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean isPlayerHidden(UUID playerUUID) {
        return plugin.getAllHiddenPlayers().contains(playerUUID) || plugin.getHiddenFromAll().contains(playerUUID);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerJoinLowest(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (isPlayerHidden(playerUUID)) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerQuitLowest(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (isPlayerHidden(playerUUID)) {
            event.setQuitMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerDeathLowest(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUniqueId();

        if (isPlayerHidden(playerUUID)) {
            event.setDeathMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerAdvancementLowest(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (isPlayerHidden(playerUUID)) {
            event.message(null);
        }
    }
}
