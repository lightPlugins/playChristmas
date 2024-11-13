package io.lightplugins.christmas.modules.secretsanta.api.models;

import io.lightplugins.christmas.LightMaster;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

@Getter
public class SecretPlayer {

    private final File playerDataFile;
    private ItemStack gift;
    private UUID partner;

    public SecretPlayer(File playerDataFile) {
        this.playerDataFile = playerDataFile;

        initNewPlayer();
        loadPartner();
        loadGift();
    }

    // check if player has a data file
    public boolean hasPlayerDataFile(String uuid) {
        return playerDataFile.getName().equalsIgnoreCase(uuid + ".yml");
    }
    // check if player has a gift, if not gift is null
    public boolean hasGift() { return gift != null; }

    private void initNewPlayer() {

        if(hasPlayerDataFile(playerDataFile.getName().replace(".yml", ""))) {
            return;
        }

        LightMaster.instance.getDebugPrinting().print("Player has no secretsanta file, initializing new player data file");
        // Generate data into the player storage file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);
        try {
            // partner uuid
            config.set("gift-partner", "");
            // serialized itemstack
            config.set("gift", new ArrayList<>());
            config.save(playerDataFile);
        } catch (Exception e) {
            throw new RuntimeException("Error creating new player data file for player", e);
        }
    }

    private void loadGift() {
        // Load gift from file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        if(config.contains("gift")) {
            // deserialize itemstack
            gift = config.getItemStack("gift");

        }
    }

    private void loadPartner() {
        // Load partner from file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        if(config.contains("gift-partner")) {
            String uuid = config.getString("gift-partner");
            partner = UUID.fromString(uuid == null ? "NaN" : uuid);
        }
    }

    public void setGift(ItemStack gift) {
        // Save gift to file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        config.set("gift", gift);

        try {
            config.save(playerDataFile);
        } catch (Exception e) {
            throw new RuntimeException("Error saving gift data to file", e);
        }
    }

}
