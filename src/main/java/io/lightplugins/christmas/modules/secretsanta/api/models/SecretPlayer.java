package io.lightplugins.christmas.modules.secretsanta.api.models;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
    private UUID partnerUUD;
    private OfflinePlayer partner;
    private int votes;
    private boolean inCheckChat;

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
    // check if player has a partner, if not partner is null
    public boolean hasPartner() { return partner != null; }
    // get the amount of votes a player has for his gift
    public Integer getVotes() { return votes; }
    // set the chat check status
    public void setChatCheck(boolean status) { this.inCheckChat = status; }

    /**
     * Initialize a new player data file and return the SecretPlayer instance
     * @return SecretPlayer instance
     */
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
        } catch (IOException e) {
            throw new RuntimeException("Error creating new player data file for player", e);
        }
    }
    /**
     * Load the gift of the player
     */
    private void loadGift() {
        // Load gift from file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);
        ItemStack gift = config.getItemStack("gift-data");

        if(gift != null) {
            this.gift = gift.clone();
        }
    }

    private void loadPartner() {
        // Load partner from file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        if(config.contains("gift-partner")) {
            String configUUID = config.getString("gift-partner");
            if(configUUID == null) {
                this.partnerUUD = null;
                return;
            }

            this.partnerUUD = UUID.fromString(configUUID);
            this.partner = Bukkit.getOfflinePlayer(this.partnerUUD);
        }
    }
    /**
     * Set the gift of the player
     * @param gift ItemStack to set as gift
     */
    public void setGift(ItemStack gift) {
        // Save gift clone to file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);
        config.set("gift-data", gift.clone());
        this.gift = gift.clone();

        try {
            config.save(playerDataFile);
        } catch (Exception e) {
            throw new RuntimeException("Error saving gift data to file", e);
        }
    }

    /**
     * Set the partner of the player
     * @param partnerUUID UUID of the partner
     */
    public void setPartner(UUID partnerUUID) {
        // Save a partner to file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        config.set("gift-partner", partnerUUID.toString());
        this.partnerUUD = partnerUUID;

        try {
            config.save(playerDataFile);
            this.partner = Bukkit.getOfflinePlayer(partnerUUID);
        } catch (IOException e) {
            throw new RuntimeException("Error saving partner data to file", e);
        }
    }

    /**
     * Add a vote to the player
     */
    public void addVote() {
        // Save votes to file
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        votes = config.getInt("votes") + 1;
        config.set("votes", votes);

        try {
            config.save(playerDataFile);
        } catch (IOException e) {
            throw new RuntimeException("Error saving votes data to file", e);
        }
    }
    /**
     * Get the gift of the player
     * IMPORTANT: Clone the ItemStack before using it !!!
     * @return cloned ItemStack gift
     */
    public ItemStack getGift() {
        return this.gift.clone();
    }

}
