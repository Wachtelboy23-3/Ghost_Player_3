package de.ghostplayers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GhostPlayersPlugin extends JavaPlugin {

    private final Map<UUID, Set<UUID>> hiddenPlayers = new HashMap<>();
    private File dataFile;
    private FileConfiguration dataConfig;

    @Override
    public void onEnable() {
        setupDataFile();
        loadHiddenPlayers();

        getCommand("ghost").setExecutor(new GhostCommand(this));

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
        if (!dataConfig.contains("hidden-players")) {
            return;
        }

        for (String hiddenUUID : dataConfig.getConfigurationSection("hidden-players").getKeys(false)) {
            UUID hidden = UUID.fromString(hiddenUUID);
            List<String> viewerList = dataConfig.getStringList("hidden-players." + hiddenUUID);
            Set<UUID> viewers = new HashSet<>();
            for (String viewerUUID : viewerList) {
                viewers.add(UUID.fromString(viewerUUID));
            }
            hiddenPlayers.put(hidden, viewers);
        }

        getLogger().info("Loaded " + hiddenPlayers.size() + " hidden player configurations");
    }

    public void saveHiddenPlayers() {
        dataConfig.set("hidden-players", null);

        for (Map.Entry<UUID, Set<UUID>> entry : hiddenPlayers.entrySet()) {
            List<String> viewerList = new ArrayList<>();
            for (UUID viewer : entry.getValue()) {
                viewerList.add(viewer.toString());
            }
            dataConfig.set("hidden-players." + entry.getKey().toString(), viewerList);
        }

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
        Set<UUID> allPlayers = new HashSet<>();
        Bukkit.getOnlinePlayers().forEach(p -> allPlayers.add(p.getUniqueId()));
        hiddenPlayers.put(hidden, allPlayers);
        saveHiddenPlayers();
    }

    public void showPlayerToAll(UUID hidden) {
        hiddenPlayers.remove(hidden);
        saveHiddenPlayers();
    }

    public boolean isHiddenFrom(UUID hidden, UUID viewer) {
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
}
