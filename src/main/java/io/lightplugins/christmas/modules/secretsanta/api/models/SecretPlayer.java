package io.lightplugins.christmas.modules.secretsanta.api.models;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.util.Base64Converter;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@Getter
public class SecretPlayer {

    private final File playerDataFile;
    private ItemStack gift;
    private UUID partner;
    private int votes;

    public SecretPlayer(File playerDataFile) {
        this.playerDataFile = playerDataFile;

        loadPartner();
        loadGift();
    }

    // check if player has a data file
    public boolean hasPlayerDataFile(String uuid) {
        return playerDataFile.getName().equalsIgnoreCase(uuid + ".yml");
    }
    // check if player has a gift, if not gift is null
    public boolean hasGift() { return gift != null; }
    public Integer getVotes() { return votes; }

    public SecretPlayer initNewPlayer() {

        // Generate data into the player storage file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);
        try {
            // partner uuid
            config.set("gift-partner", null);
            // serialized itemstack
            config.set("gift", null);
            config.set("gift-data", new ArrayList<>());
            config.set("votes", 0);
            config.save(playerDataFile);

            return this;
        } catch (Exception e) {
            throw new RuntimeException("Error creating new player data file for player", e);
        }
    }

    private void loadGift() {
        // Load gift from file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        if(config.contains("gift")) {
            // deserialize itemstack
            String base64 = config.getString("gift");
            this.gift = Base64Converter.itemStackFromBase64(base64);

        }
    }

    private void loadPartner() {
        // Load partner from file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        if(config.contains("gift-partner")) {
            String configUUID = config.getString("gift-partner");
            if(configUUID == null) {
                this.partner = null;
                return;
            }
            this.partner = UUID.fromString(configUUID);
        }
    }

    public void setGift(ItemStack gift) {
        // Save gift to file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        String base64 = Base64Converter.itemStackToBase64(gift);

        LightMaster.instance.getDebugPrinting().print("Gift stack in SecretPlayer: " + gift);

        config.set("gift", base64);
        config.set("gift-data", gift);
        this.gift = gift;

        try {
            config.save(playerDataFile);
            LightMaster.instance.getDebugPrinting().print("config stack: " + config.getItemStack("gift"));
        } catch (Exception e) {
            throw new RuntimeException("Error saving gift data to file", e);
        }
    }

    public void setPartner(UUID partner) {
        // Save partner to file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        config.set("gift-partner", partner.toString());
        this.partner = partner;

        try {
            config.save(playerDataFile);
        } catch (Exception e) {
            throw new RuntimeException("Error saving partner data to file", e);
        }
    }

    public void addVote() {
        // Save votes to file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        votes = config.getInt("votes") + 1;
        config.set("votes", votes);

        try {
            config.save(playerDataFile);
        } catch (Exception e) {
            throw new RuntimeException("Error saving votes data to file", e);
        }
    }

}
