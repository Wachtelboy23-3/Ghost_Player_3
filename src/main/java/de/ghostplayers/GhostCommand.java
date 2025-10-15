package de.ghostplayers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class GhostCommand implements CommandExecutor, TabCompleter {

    private final GhostPlayersPlugin plugin;

    public GhostCommand(GhostPlayersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ghostplayers.admin")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!")
                    .color(NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "add":
                handleAdd(sender, args);
                break;
            case "remove":
                handleRemove(sender, args);
                break;
            case "list":
                handleList(sender);
                break;
            case "visible":
                handleVisible(sender, args);
                break;
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void handleAdd(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /ghost add <player> [viewer]")
                    .color(NamedTextColor.RED));
            return;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
        UUID targetUUID = targetPlayer.getUniqueId();

        if (args.length == 2) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                plugin.hidePlayer(targetUUID, onlinePlayer.getUniqueId());
                if (onlinePlayer.getUniqueId().equals(targetUUID)) continue;
                if (targetPlayer.isOnline()) {
                    onlinePlayer.hidePlayer(plugin, (Player) targetPlayer);
                }
            }
            sender.sendMessage(Component.text("Player " + args[1] + " is now hidden from everyone!")
                    .color(NamedTextColor.GREEN));
        } else {
            OfflinePlayer viewerPlayer = Bukkit.getOfflinePlayer(args[2]);
            UUID viewerUUID = viewerPlayer.getUniqueId();

            plugin.hidePlayer(targetUUID, viewerUUID);

            if (targetPlayer.isOnline() && viewerPlayer.isOnline()) {
                Player viewer = (Player) viewerPlayer;
                viewer.hidePlayer(plugin, (Player) targetPlayer);
            }

            sender.sendMessage(Component.text("Player " + args[1] + " is now hidden from " + args[2] + "!")
                    .color(NamedTextColor.GREEN));
        }
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /ghost remove <player> [viewer]")
                    .color(NamedTextColor.RED));
            return;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
        UUID targetUUID = targetPlayer.getUniqueId();

        if (args.length == 2) {
            plugin.showPlayerToAll(targetUUID);

            if (targetPlayer.isOnline()) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.showPlayer(plugin, (Player) targetPlayer);
                }
            }

            sender.sendMessage(Component.text("Player " + args[1] + " is now visible to everyone!")
                    .color(NamedTextColor.GREEN));
        } else {
            OfflinePlayer viewerPlayer = Bukkit.getOfflinePlayer(args[2]);
            UUID viewerUUID = viewerPlayer.getUniqueId();

            plugin.showPlayer(targetUUID, viewerUUID);

            if (targetPlayer.isOnline() && viewerPlayer.isOnline()) {
                Player viewer = (Player) viewerPlayer;
                viewer.showPlayer(plugin, (Player) targetPlayer);
            }

            sender.sendMessage(Component.text("Player " + args[1] + " is now visible to " + args[2] + "!")
                    .color(NamedTextColor.GREEN));
        }
    }

    private void handleList(CommandSender sender) {
        Map<UUID, Set<UUID>> hiddenPlayers = plugin.getHiddenPlayersMap();

        if (hiddenPlayers.isEmpty()) {
            sender.sendMessage(Component.text("No players are currently hidden.")
                    .color(NamedTextColor.YELLOW));
            return;
        }

        sender.sendMessage(Component.text("=== Hidden Players ===")
                .color(NamedTextColor.GOLD));

        for (Map.Entry<UUID, Set<UUID>> entry : hiddenPlayers.entrySet()) {
            OfflinePlayer hiddenPlayer = Bukkit.getOfflinePlayer(entry.getKey());
            String hiddenName = hiddenPlayer.getName() != null ? hiddenPlayer.getName() : "Unknown";

            if (entry.getValue().isEmpty()) {
                sender.sendMessage(Component.text("- " + hiddenName + ": hidden from no one")
                        .color(NamedTextColor.GRAY));
            } else {
                StringBuilder viewers = new StringBuilder();
                for (UUID viewerUUID : entry.getValue()) {
                    OfflinePlayer viewer = Bukkit.getOfflinePlayer(viewerUUID);
                    String viewerName = viewer.getName() != null ? viewer.getName() : "Unknown";
                    if (viewers.length() > 0) viewers.append(", ");
                    viewers.append(viewerName);
                }
                sender.sendMessage(Component.text("- " + hiddenName + ": hidden from " + viewers)
                        .color(NamedTextColor.GRAY));
            }
        }
    }

    private void handleVisible(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /ghost visible <player>")
                    .color(NamedTextColor.RED));
            return;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
        UUID targetUUID = targetPlayer.getUniqueId();

        Set<UUID> visibleTo = plugin.getVisibleTo(targetUUID);

        if (visibleTo.isEmpty()) {
            sender.sendMessage(Component.text("Player " + args[1] + " is not hidden from anyone or not found in configuration.")
                    .color(NamedTextColor.YELLOW));
            return;
        }

        StringBuilder viewers = new StringBuilder();
        for (UUID viewerUUID : visibleTo) {
            OfflinePlayer viewer = Bukkit.getOfflinePlayer(viewerUUID);
            String viewerName = viewer.getName() != null ? viewer.getName() : "Unknown";
            if (viewers.length() > 0) viewers.append(", ");
            viewers.append(viewerName);
        }

        sender.sendMessage(Component.text("Player " + args[1] + " is hidden from: " + viewers)
                .color(NamedTextColor.AQUA));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("=== GhostPlayers Commands ===")
                .color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/ghost add <player> - Hide player from everyone")
                .color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/ghost add <player> <viewer> - Hide player from specific viewer")
                .color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/ghost remove <player> - Show player to everyone")
                .color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/ghost remove <player> <viewer> - Show player to specific viewer")
                .color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/ghost list - List all hidden players")
                .color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/ghost visible <player> - Check who can't see a player")
                .color(NamedTextColor.GRAY));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("ghostplayers.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return Arrays.asList("add", "remove", "list", "visible");
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("add") ||
                                  args[0].equalsIgnoreCase("remove") ||
                                  args[0].equalsIgnoreCase("visible"))) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        }

        if (args.length == 3 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        }

        return new ArrayList<>();
    }
}
