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
            case "allow":
                handleAllow(sender, args);
                break;
            case "disallow":
                handleDisallow(sender, args);
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
            plugin.hidePlayerFromAll(targetUUID);

            if (targetPlayer.isOnline()) {
                Player target = (Player) targetPlayer;
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.getUniqueId().equals(targetUUID)) continue;
                    if (!plugin.isHiddenFrom(targetUUID, onlinePlayer.getUniqueId())) continue;
                    onlinePlayer.hidePlayer(plugin, target);
                }
            }

            sender.sendMessage(Component.text("Player " + args[1] + " is now hidden from everyone (including future joins)!")
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
        Set<UUID> hiddenFromAll = plugin.getHiddenFromAll();
        Set<UUID> canSeeHidden = plugin.getCanSeeHidden();

        if (hiddenPlayers.isEmpty() && hiddenFromAll.isEmpty() && canSeeHidden.isEmpty()) {
            sender.sendMessage(Component.text("No players are currently hidden.")
                    .color(NamedTextColor.YELLOW));
            return;
        }

        sender.sendMessage(Component.text("=== Hidden Players ===")
                .color(NamedTextColor.GOLD));

        if (!hiddenFromAll.isEmpty()) {
            sender.sendMessage(Component.text("Hidden from ALL:")
                    .color(NamedTextColor.AQUA));
            for (UUID hiddenUUID : hiddenFromAll) {
                OfflinePlayer hiddenPlayer = Bukkit.getOfflinePlayer(hiddenUUID);
                String hiddenName = hiddenPlayer.getName() != null ? hiddenPlayer.getName() : "Unknown";
                sender.sendMessage(Component.text("  - " + hiddenName)
                        .color(NamedTextColor.GRAY));
            }
        }

        if (!hiddenPlayers.isEmpty()) {
            sender.sendMessage(Component.text("Hidden from specific players:")
                    .color(NamedTextColor.AQUA));
            for (Map.Entry<UUID, Set<UUID>> entry : hiddenPlayers.entrySet()) {
                OfflinePlayer hiddenPlayer = Bukkit.getOfflinePlayer(entry.getKey());
                String hiddenName = hiddenPlayer.getName() != null ? hiddenPlayer.getName() : "Unknown";

                if (!entry.getValue().isEmpty()) {
                    StringBuilder viewers = new StringBuilder();
                    for (UUID viewerUUID : entry.getValue()) {
                        OfflinePlayer viewer = Bukkit.getOfflinePlayer(viewerUUID);
                        String viewerName = viewer.getName() != null ? viewer.getName() : "Unknown";
                        if (viewers.length() > 0) viewers.append(", ");
                        viewers.append(viewerName);
                    }
                    sender.sendMessage(Component.text("  - " + hiddenName + ": hidden from " + viewers)
                            .color(NamedTextColor.GRAY));
                }
            }
        }

        if (!canSeeHidden.isEmpty()) {
            sender.sendMessage(Component.text("Players who can see hidden:")
                    .color(NamedTextColor.AQUA));
            for (UUID viewerUUID : canSeeHidden) {
                OfflinePlayer viewer = Bukkit.getOfflinePlayer(viewerUUID);
                String viewerName = viewer.getName() != null ? viewer.getName() : "Unknown";
                sender.sendMessage(Component.text("  - " + viewerName)
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

        if (plugin.getHiddenFromAll().contains(targetUUID)) {
            sender.sendMessage(Component.text("Player " + args[1] + " is hidden from EVERYONE")
                    .color(NamedTextColor.AQUA));
            return;
        }

        Set<UUID> visibleTo = plugin.getVisibleTo(targetUUID);

        if (visibleTo.isEmpty()) {
            sender.sendMessage(Component.text("Player " + args[1] + " is not hidden from anyone.")
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

    private void handleAllow(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /ghost allow <player>")
                    .color(NamedTextColor.RED));
            return;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
        UUID targetUUID = targetPlayer.getUniqueId();

        plugin.addCanSeeHidden(targetUUID);

        if (targetPlayer.isOnline()) {
            Player target = (Player) targetPlayer;
            for (Player hiddenPlayer : Bukkit.getOnlinePlayers()) {
                if (plugin.getAllHiddenPlayers().contains(hiddenPlayer.getUniqueId()) ||
                    plugin.getHiddenFromAll().contains(hiddenPlayer.getUniqueId())) {
                    target.showPlayer(plugin, hiddenPlayer);
                }
            }
        }

        sender.sendMessage(Component.text("Player " + args[1] + " can now see all hidden players!")
                .color(NamedTextColor.GREEN));
    }

    private void handleDisallow(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /ghost disallow <player>")
                    .color(NamedTextColor.RED));
            return;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);
        UUID targetUUID = targetPlayer.getUniqueId();

        plugin.removeCanSeeHidden(targetUUID);

        if (targetPlayer.isOnline()) {
            Player target = (Player) targetPlayer;
            for (Player hiddenPlayer : Bukkit.getOnlinePlayers()) {
                if (plugin.isHiddenFrom(hiddenPlayer.getUniqueId(), targetUUID)) {
                    target.hidePlayer(plugin, hiddenPlayer);
                }
            }
        }

        sender.sendMessage(Component.text("Player " + args[1] + " can no longer see hidden players!")
                .color(NamedTextColor.GREEN));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("=== GhostPlayers Commands ===")
                .color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/ghost add <player> - Hide player from everyone (including future joins)")
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
        sender.sendMessage(Component.text("/ghost allow <player> - Allow player to see all hidden players")
                .color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("/ghost disallow <player> - Disallow player from seeing hidden players")
                .color(NamedTextColor.GRAY));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("ghostplayers.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return Arrays.asList("add", "remove", "list", "visible", "allow", "disallow");
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("add") ||
                                  args[0].equalsIgnoreCase("remove") ||
                                  args[0].equalsIgnoreCase("visible") ||
                                  args[0].equalsIgnoreCase("allow") ||
                                  args[0].equalsIgnoreCase("disallow"))) {
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
