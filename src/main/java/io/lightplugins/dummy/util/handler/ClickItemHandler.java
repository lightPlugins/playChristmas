package io.lightplugins.dummy.util.handler;

import io.lightplugins.dummy.Light;
import io.lightplugins.dummy.util.NumberFormatter;
import io.lightplugins.dummy.util.SkullUtil;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static org.bukkit.Registry.ENCHANTMENT;

/**
 * This class handles the creation and management of GUI items for players.
 * It processes configuration data to generate items with specific properties,
 * applies placeholders, and handles actions associated with the items.
 * <p>
 * The main functionalities include:
 * - Applying placeholders to item properties.
 * - Translating placeholders in item display names and lore.
 * - Constructing ItemStack objects based on configuration data.
 * - Reading and processing actions associated with the items.
 * <p>
 * Author: lightPlugins
 * Copyright: © 2023 [lightStudios]. All rights reserved.
 * You may not use, distribute, or modify this code without explicit permission.
 */

@Getter
public class ClickItemHandler {

    private final ConfigurationSection GUI_ITEM_ARGS;
    private final ConfigurationSection PLACEHOLDERS;
    private final Player player;
    private String item;
    private String displayName;
    private List<String> lore = new ArrayList<>();
    private final Map<String, String> placeholders = new HashMap<>();
    private int modelData;
    private String headData;
    private final List<String> actionsSection;
    private final List<ActionHandler> actionHandlers = new ArrayList<>();

    /**
     * Constructs a new ClickItemHandler.
     *
     * @param section the configuration section containing item and action data
     * @param player the player for whom the item is to be handled
     */
    public ClickItemHandler(ConfigurationSection section, Player player) {

        this.GUI_ITEM_ARGS = section.getConfigurationSection("args");
        this.actionsSection = section.getStringList("args.click-actions");
        this.PLACEHOLDERS = section.getConfigurationSection("args.placeholders");
        this.player = player;

        applyPlaceholders();
        readItemVariables();
        translatePlaceholders();
        translateLore();
        loadActions();

    }

    /**
     * Applies placeholders to the placeholders map.
     * Iterates through all keys in the PLACEHOLDERS configuration section,
     * retrieves the corresponding value, and sets the placeholder using
     * PlaceholderAPI for the given player.
     */
    private void applyPlaceholders() {

        if(PLACEHOLDERS == null) {
            return;
        }

        for(String placeholder : PLACEHOLDERS.getKeys(true)) {
            placeholders.put(
                    placeholder,
                    PlaceholderAPI.setPlaceholders(
                            player,
                            Objects.requireNonNull(PLACEHOLDERS.getString(placeholder))
                    ));
        }
    }

    /**
     * Translates placeholders in the displayName and headData fields.
     * Iterates through all keys in the placeholders map and replaces
     * occurrences of each placeholder in the displayName and headData
     * with their corresponding values.
     */
    private void translatePlaceholders() {
        for(String key : placeholders.keySet()) {
            this.displayName = displayName.replace("#" + key + "#", placeholders.get(key));
            this.headData = headData.replace("#" + key + "#", placeholders.get(key));
        }
    }

    /**
     * Translates placeholders in the lore list.
     * Iterates through each line in the lore list and replaces
     * occurrences of each placeholder with their corresponding values.
     * The translated lines are then processed through the colorTranslation
     * method and added to the translatedLore list.
     */
    private void translateLore() {
        List<String> translatedLore = new ArrayList<>();
        for(String line : lore) {
            for(String key : placeholders.keySet()) {
                line = line.replace("#" + key + "#", placeholders.get(key));
            }
            translatedLore.add(Light.instance.colorTranslation.loreLineTranslation(line, player));
        }
        this.lore = translatedLore;
    }

    /**
     * Retrieves and sets the item content from the GUI_ITEM_ARGS configuration section.
     * This includes the item type, display name, lore, and head data.
     */
    private void readItemVariables() {

        this.item = GUI_ITEM_ARGS.getString("item");
        this.displayName = GUI_ITEM_ARGS.getString("display-name");
        this.lore = GUI_ITEM_ARGS.getStringList("lore");
        this.headData = GUI_ITEM_ARGS.getString("head-data");

    }

    /**
     * Constructs an ItemStack based on the configuration and player data.
     *
     * @return the constructed ItemStack
     */
    public ItemStack getGuiItem() {

        ItemStack itemStack = new ItemStack(Material.STONE, 1);

        String[] splitItem = item.split(" ");
        Material material = Material.getMaterial(splitItem[0].toUpperCase());

        if(NumberFormatter.isNumber(splitItem[1])) {
            itemStack.setAmount(Integer.parseInt(splitItem[1]));
        }

        if(material != null) {
            itemStack.setType(material);
        } else {
            Light.getDebugPrinting().configError("Invalid material: " + splitItem[0] + " in file: " + GUI_ITEM_ARGS.getCurrentPath());
            Light.getDebugPrinting().configError("Material must be a valid material.");
            Light.getDebugPrinting().configError("It is set to the backup material -> Stone");
            return new ItemStack(Material.STONE, 1);
        }

        UUID uuid = UUID.fromString(headData);
        Player skullPlayer = Bukkit.getPlayer(uuid);
        OfflinePlayer offlinePlayer = Bukkit.getPlayer(headData);

        if(skullPlayer != null) {
            itemStack = SkullUtil.getPlayerSkull(skullPlayer);
        } else if(offlinePlayer != null) {
            itemStack = SkullUtil.getPlayerSkull(offlinePlayer.getPlayer());
        } else {
            Light.getDebugPrinting().configError("Invalid head data: " + headData + " in file: " + GUI_ITEM_ARGS.getCurrentPath());
            Light.getDebugPrinting().configError("Head data must be a valid UUID or name.");
            Light.getDebugPrinting().configError("It is set to the backup material -> Stone");
            return new ItemStack(Material.STONE, 1);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null) {
            return null;
        }

        itemMeta.setDisplayName(Light.instance.colorTranslation.loreLineTranslation(displayName, player));

        for(String split : splitItem) {

            // Check if the split contains model-data
            if(split.equalsIgnoreCase("model-data")) {
                String[] splitModelData = split.split(":");
                if(NumberFormatter.isNumber(splitModelData[1])) {
                    itemMeta.setCustomModelData(Integer.parseInt(splitModelData[1]));
                    this.modelData = Integer.parseInt(splitModelData[1]);
                } else {
                    Light.getDebugPrinting().configError("Invalid model data: " + splitModelData[1] + " in file: "
                            + GUI_ITEM_ARGS.getCurrentPath());
                    Light.getDebugPrinting().configError("Model data must be a number.");
                    Light.getDebugPrinting().configError("Syntax: model-data:12345");
                }
            }

            // Check if the item should glow
            if(split.equalsIgnoreCase("glow")) {
                itemMeta.addEnchant(Enchantment.FLAME, 1, true);
            }

            // Check if the split contains item flags
            try {
                ItemFlag itemFlag = ItemFlag.valueOf(split.toUpperCase());
                itemMeta.addItemFlags(itemFlag);
                // Debug printing for testing purposes -> remove in production !
                Light.getDebugPrinting().print("Added item flag: " + itemFlag.name() + " to item: " + itemMeta.getDisplayName());
            } catch (IllegalArgumentException ignored) { }
        }

        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Reads and processes actions from the actionsSection list.
     * Iterates through each action, replaces placeholders with their corresponding values,
     * and adds the processed action to the actionHandlers list.
     */
    private void loadActions() {
        actionsSection.forEach(action -> {
            for(String key : placeholders.keySet()) {
                action = action.replace("#" + key + "#", placeholders.get(key));
            }
            actionHandlers.add(new ActionHandler(player, action));
        });
    }
}
