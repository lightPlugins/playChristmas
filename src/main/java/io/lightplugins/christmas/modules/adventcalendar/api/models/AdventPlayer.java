package io.lightplugins.christmas.modules.adventcalendar.api.models;

import io.lightplugins.christmas.LightMaster;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AdventPlayer {

    private final List<Date> claimedDates = new ArrayList<>();
    private final File playerDataFile;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public AdventPlayer(File playerDataFile) {
        this.playerDataFile = playerDataFile;

        initNewPlayer();
        loadClaimedDates();

    }

    public boolean hasPlayerDataFile(String uuid) {
        return playerDataFile.getName().equalsIgnoreCase(uuid + ".yml");
    }


    private void initNewPlayer() {

        if(hasPlayerDataFile(playerDataFile.getName().replace(".yml", ""))) {
            return;
        }

        LightMaster.instance.getDebugPrinting().print("Player has no claimed dates, initializing new player data file");
        // Generate data into the player storage file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);
        try {
            config.set("claimed-dates", new ArrayList<>());
            config.save(playerDataFile);
        } catch (Exception e) {
            throw new RuntimeException("Error saving player data file for player", e);
        }
    }

    private void loadClaimedDates() {
        // Load claimed dates from file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        if(config.contains("claimed-dates")) {
            config.getStringList("claimed-dates").forEach(date -> {
                try {
                    claimedDates.add(dateFormat.parse(date));
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing date: " + date, e);
                }
            });
        }
    }

    public boolean hasClaimed(Date date) {
        return claimedDates.stream().anyMatch(claimedDate -> claimedDate.equals(date));
    }

    public void addClaimedDate(Date date) {
        if (hasClaimed(date)) {
            LightMaster.instance.getDebugPrinting().print("Player has already claimed date: " + date);
            return;
        }
        claimedDates.add(date);

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);
        List<String> formattedDates = claimedDates.stream()
                .map(dateFormat::format)
                .collect(Collectors.toList());
        config.set("claimed-dates", formattedDates);

        try {
            config.save(playerDataFile);
        } catch (Exception e) {
            throw new RuntimeException("Error saving claimed date: " + date, e);
        }
    }

    public String getPlayerUUID() {
        return playerDataFile.getName().replace(".yml", "");
    }

    public void resetPlayer() {
        claimedDates.clear();
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);
        config.set("claimed-dates", new ArrayList<>());
        try {
            config.save(playerDataFile);
        } catch (Exception e) {
            throw new RuntimeException("Error saving player data file for player", e);
        }
    }
}
