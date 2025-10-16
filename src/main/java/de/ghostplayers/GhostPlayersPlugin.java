package de.ghostplayers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GhostPlayersPlugin extends JavaPlugin {

    private final Map<UUID, Set<UUID>> hiddenPlayers = new HashMap<>();
    private final Set<UUID> hiddenFromAll = new HashSet<>();
    private final Set<UUID> canSeeHidden = new HashSet<>();
    private File dataFile;
    private FileConfiguration dataConfig;

    @Override
    public void onEnable() {
        setupDataFile();
        loadHiddenPlayers();

        getCommand("ghost").setExecutor(new GhostCommand(this));

        Bukkit.getPluginManager().registerEvents(new DiscordSRVListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerVisibilityListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatFilterListener(this), this);
        Bukkit.getPluginManager().registerEvents(new TabCompleteListener(this), this);

        getLogger().info("GhostPlayers Plugin enabled!");
    }

    @Override
    public void onDisable() {
        saveHiddenPlayers();
        getLogger().info("GhostPlayers Plugin disabled!");
    }

    private void setupDataFile() {
        dataFile = new File(getDataFolder(), "hidden-players.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Could not create hidden-players.yml!");
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void loadHiddenPlayers() {
        hiddenPlayers.clear();
        hiddenFromAll.clear();
        canSeeHidden.clear();

        if (dataConfig.contains("hidden-players")) {
            for (String hiddenUUID : dataConfig.getConfigurationSection("hidden-players").getKeys(false)) {
                UUID hidden = UUID.fromString(hiddenUUID);
                List<String> viewerList = dataConfig.getStringList("hidden-players." + hiddenUUID);
                Set<UUID> viewers = new HashSet<>();
                for (String viewerUUID : viewerList) {
                    viewers.add(UUID.fromString(viewerUUID));
                }
                hiddenPlayers.put(hidden, viewers);
            }
        }

        if (dataConfig.contains("hidden-from-all")) {
            List<String> hiddenFromAllList = dataConfig.getStringList("hidden-from-all");
            for (String hiddenUUID : hiddenFromAllList) {
                hiddenFromAll.add(UUID.fromString(hiddenUUID));
            }
        }

        if (dataConfig.contains("can-see-hidden")) {
            List<String> canSeeList = dataConfig.getStringList("can-see-hidden");
            for (String viewerUUID : canSeeList) {
                canSeeHidden.add(UUID.fromString(viewerUUID));
            }
        }

        getLogger().info("Loaded " + hiddenPlayers.size() + " hidden player configurations");
    }

    public void saveHiddenPlayers() {
        dataConfig.set("hidden-players", null);
        dataConfig.set("hidden-from-all", null);
        dataConfig.set("can-see-hidden", null);

        for (Map.Entry<UUID, Set<UUID>> entry : hiddenPlayers.entrySet()) {
            List<String> viewerList = new ArrayList<>();
            for (UUID viewer : entry.getValue()) {
                viewerList.add(viewer.toString());
            }
            dataConfig.set("hidden-players." + entry.getKey().toString(), viewerList);
        }

        List<String> hiddenFromAllList = new ArrayList<>();
        for (UUID hidden : hiddenFromAll) {
            hiddenFromAllList.add(hidden.toString());
        }
        dataConfig.set("hidden-from-all", hiddenFromAllList);

        List<String> canSeeList = new ArrayList<>();
        for (UUID viewer : canSeeHidden) {
            canSeeList.add(viewer.toString());
        }
        dataConfig.set("can-see-hidden", canSeeList);

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            getLogger().severe("Could not save hidden-players.yml!");
            e.printStackTrace();
        }
    }

    public void hidePlayer(UUID hidden, UUID viewer) {
        hiddenPlayers.computeIfAbsent(hidden, k -> new HashSet<>()).add(viewer);
        saveHiddenPlayers();
    }

    public void showPlayer(UUID hidden, UUID viewer) {
        Set<UUID> viewers = hiddenPlayers.get(hidden);
        if (viewers != null) {
            viewers.remove(viewer);
            if (viewers.isEmpty()) {
                hiddenPlayers.remove(hidden);
            }
            saveHiddenPlayers();
        }
    }

    public void hidePlayerFromAll(UUID hidden) {
        hiddenFromAll.add(hidden);
        hiddenPlayers.remove(hidden);
        saveHiddenPlayers();
    }

    public void showPlayerToAll(UUID hidden) {
        hiddenPlayers.remove(hidden);
        hiddenFromAll.remove(hidden);
        saveHiddenPlayers();
    }

    public boolean isHiddenFrom(UUID hidden, UUID viewer) {
        if (canSeeHidden.contains(viewer)) {
            return false;
        }
        Player viewerPlayer = Bukkit.getPlayer(viewer);
        if (viewerPlayer != null && viewerPlayer.hasPermission("ghostplayers.seehidden")) {
            return false;
        }
        if (hiddenFromAll.contains(hidden)) {
            return true;
        }
        Set<UUID> viewers = hiddenPlayers.get(hidden);
        return viewers != null && viewers.contains(viewer);
    }

    public Set<UUID> getVisibleTo(UUID hidden) {
        return hiddenPlayers.getOrDefault(hidden, new HashSet<>());
    }

    public Set<UUID> getAllHiddenPlayers() {
        return new HashSet<>(hiddenPlayers.keySet());
    }

    public Map<UUID, Set<UUID>> getHiddenPlayersMap() {
        return new HashMap<>(hiddenPlayers);
    }

    public Set<UUID> getHiddenFromAll() {
        return new HashSet<>(hiddenFromAll);
    }

    public void addCanSeeHidden(UUID viewer) {
        canSeeHidden.add(viewer);
        saveHiddenPlayers();
    }

    public void removeCanSeeHidden(UUID viewer) {
        canSeeHidden.remove(viewer);
        saveHiddenPlayers();
    }

    public Set<UUID> getCanSeeHidden() {
        return new HashSet<>(canSeeHidden);
    }

    public boolean canPlayerSeeHidden(UUID viewer) {
        return canSeeHidden.contains(viewer);
    }
}
