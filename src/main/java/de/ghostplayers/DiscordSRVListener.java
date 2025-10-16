package de.ghostplayers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class DiscordSRVListener implements Listener {

    private final GhostPlayersPlugin plugin;

    public DiscordSRVListener(GhostPlayersPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerJoinLowest(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (plugin.getAllHiddenPlayers().contains(playerUUID) || plugin.getHiddenFromAll().contains(playerUUID)) {
            event.joinMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerQuitLowest(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (plugin.getAllHiddenPlayers().contains(playerUUID) || plugin.getHiddenFromAll().contains(playerUUID)) {
            event.quitMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerDeathLowest(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUniqueId();

        if (plugin.getAllHiddenPlayers().contains(playerUUID) || plugin.getHiddenFromAll().contains(playerUUID)) {
            event.deathMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerAdvancementLowest(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (plugin.getAllHiddenPlayers().contains(playerUUID) || plugin.getHiddenFromAll().contains(playerUUID)) {
            event.message(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onAsyncPlayerChatLowest(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        UUID senderUUID = sender.getUniqueId();

        if (plugin.getAllHiddenPlayers().contains(senderUUID) || plugin.getHiddenFromAll().contains(senderUUID)) {
            event.setCancelled(true);
        }
    }
}
