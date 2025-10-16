package de.ghostplayers;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;
import java.util.UUID;

public class ChatFilterListener implements Listener {

    private final GhostPlayersPlugin plugin;

    public ChatFilterListener(GhostPlayersPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (plugin.getAllHiddenPlayers().contains(playerUUID) || plugin.getHiddenFromAll().contains(playerUUID)) {
            Component originalMessage = event.joinMessage();
            event.joinMessage(null);

            if (originalMessage != null) {
                for (Player viewer : Bukkit.getOnlinePlayers()) {
                    if (viewer.hasPermission("ghostplayers.seehidden") || plugin.canPlayerSeeHidden(viewer.getUniqueId())) {
                        viewer.sendMessage(originalMessage);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (plugin.getAllHiddenPlayers().contains(playerUUID) || plugin.getHiddenFromAll().contains(playerUUID)) {
            Component originalMessage = event.quitMessage();
            event.quitMessage(null);

            if (originalMessage != null) {
                for (Player viewer : Bukkit.getOnlinePlayers()) {
                    if (viewer.hasPermission("ghostplayers.seehidden") || plugin.canPlayerSeeHidden(viewer.getUniqueId())) {
                        viewer.sendMessage(originalMessage);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUniqueId();

        if (plugin.getAllHiddenPlayers().contains(playerUUID) || plugin.getHiddenFromAll().contains(playerUUID)) {
            Component originalMessage = event.deathMessage();
            event.deathMessage(null);

            if (originalMessage != null) {
                for (Player viewer : Bukkit.getOnlinePlayers()) {
                    if (viewer.hasPermission("ghostplayers.seehidden") || plugin.canPlayerSeeHidden(viewer.getUniqueId())) {
                        viewer.sendMessage(originalMessage);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (plugin.getAllHiddenPlayers().contains(playerUUID) || plugin.getHiddenFromAll().contains(playerUUID)) {
            Component originalMessage = event.message();
            event.message(null);

            if (originalMessage != null) {
                for (Player viewer : Bukkit.getOnlinePlayers()) {
                    if (viewer.hasPermission("ghostplayers.seehidden") || plugin.canPlayerSeeHidden(viewer.getUniqueId())) {
                        viewer.sendMessage(originalMessage);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        UUID senderUUID = sender.getUniqueId();

        if (plugin.getAllHiddenPlayers().contains(senderUUID) || plugin.getHiddenFromAll().contains(senderUUID)) {
            event.viewers().clear();
            event.viewers().add(sender);

            for (Player viewer : Bukkit.getOnlinePlayers()) {
                if (viewer.hasPermission("ghostplayers.seehidden") || plugin.canPlayerSeeHidden(viewer.getUniqueId())) {
                    event.viewers().add(viewer);
                }
            }
        }
    }
}
