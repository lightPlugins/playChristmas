package io.lightplugins.christmas.modules.adventcalendar.api.models;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class AdventPlayer {

    private final Player player;
    private final List<Date> claimedDates = new ArrayList<>();
    private final File playerDataFile;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public AdventPlayer(Player player, File playerDataFile) {
        this.player = player;
        this.playerDataFile = playerDataFile;

        loadClaimedDates();
    }

    private void loadClaimedDates() {
        // Load claimed dates from file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        if(config.contains("claimed-dates")) {
            claimedDates.forEach(date -> {
                try {
                    claimedDates.add(dateFormat.parse(config.getString("claimed-dates." + date)));
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing date: " + date + " for player: " + player.getName(), e);
                }
            });
        }
    }

    public void createNewPlayerFile() {
        // Create new player data file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        config.set("claimed-dates", new ArrayList<>());

        try {
            config.save(playerDataFile);
        } catch (Exception e) {
            throw new RuntimeException("Error saving player data file for player: " + player.getName(), e);
        }
    }

    public boolean hasClaimed(Date date) {
        return claimedDates.stream().anyMatch(claimedDate -> claimedDate.equals(date));
    }

    public void addClaimedDate(Date date) {
        if(!hasClaimed(date)) {
            return;
        }
        claimedDates.add(date);
    }






}
