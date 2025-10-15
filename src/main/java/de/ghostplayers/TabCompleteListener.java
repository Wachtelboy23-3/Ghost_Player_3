package de.ghostplayers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TabCompleteListener implements Listener {

    private final GhostPlayersPlugin plugin;

    public TabCompleteListener(GhostPlayersPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabComplete(TabCompleteEvent event) {
        if (!(event.getSender() instanceof Player)) {
            return;
        }

        Player sender = (Player) event.getSender();
        UUID senderUUID = sender.getUniqueId();

        List<String> completions = new ArrayList<>(event.getCompletions());
        List<String> filteredCompletions = new ArrayList<>();

        for (String completion : completions) {
            boolean shouldHide = false;

            for (UUID hiddenUUID : plugin.getAllHiddenPlayers()) {
                if (plugin.isHiddenFrom(hiddenUUID, senderUUID)) {
                    Player hiddenPlayer = plugin.getServer().getPlayer(hiddenUUID);
                    if (hiddenPlayer != null) {
                        String hiddenName = hiddenPlayer.getName();
                        if (completion.equalsIgnoreCase(hiddenName) ||
                            completion.toLowerCase().startsWith(hiddenName.toLowerCase())) {
                            shouldHide = true;
                            break;
                        }
                    }
                }
            }

            if (!shouldHide) {
                filteredCompletions.add(completion);
            }
        }

        event.setCompletions(filteredCompletions);
    }
}
