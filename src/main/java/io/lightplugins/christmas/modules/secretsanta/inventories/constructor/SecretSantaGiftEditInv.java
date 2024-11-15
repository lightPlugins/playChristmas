package io.lightplugins.christmas.modules.secretsanta.inventories.constructor;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.secretsanta.LightSecretSanta;
import io.lightplugins.christmas.modules.secretsanta.api.models.SecretPlayer;
import io.lightplugins.christmas.util.constructor.InvConstructor;
import io.lightplugins.christmas.util.handler.ActionHandler;
import io.lightplugins.christmas.util.handler.ClickItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * AdventCalendarInv
 * This class is used to create the Advent Calendar GUI
 * Every day in December has a reward that the player can claim
 * by clicking on the available item in the GUI.
 * <p>
 * Author: lightPlugins
 * Copyright: © 2023 [lightStudios]. All rights reserved.
 * You may not use, distribute, or modify this code without explicit permission.
 *  </p>
 */

public class SecretSantaGiftEditInv {

    private final ChestGui gui = new ChestGui(6, "Init");
    private final InvConstructor invConstructor;
    private final ConfigurationSection extraSection;
    private final Player player;
    private BukkitTask bukkitTask;
    private final int refreshRate = 20;
    private final List<Player> clickCooldown = new ArrayList<>();
    private final List<Player> failCooldown = new ArrayList<>();
    private final int cooldownTime = 3;
    private final int failCooldownTime = 20;
    private ItemStack clickedItemStack;
    private final SecretPlayer secretPlayer;

    /**
     * Constructor for the AdventCalendarInv
     * @param invConstructor InvConstructor - The constructor for the inventory
     * @param extraSection ConfigurationSection - The section for the rewards
     * @param player Player - The player to open the inventory for
     */

    public SecretSantaGiftEditInv(
            InvConstructor invConstructor,
            ConfigurationSection extraSection,
            Player player) {

        this.invConstructor = invConstructor;
        this.extraSection = extraSection;
        this.player = player;

        this.secretPlayer = LightSecretSanta.instance.getSecretPlayerData().stream()
                .filter(playerData -> playerData.getPlayerDataFile().getName().replace(".yml", "")
                        .equalsIgnoreCase(player.getUniqueId().toString()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("SecretPlayer not found for player: " + player.getUniqueId()));
    }

    /**
     * Open the inventory
     * This method is used to open the Advent Calendar GUI
     */
    public void openInventory() {



        gui.setRows(invConstructor.getRows());
        gui.setTitle(LightMaster.instance.getColorTranslation().loreLineTranslation(invConstructor.getGuiTitle(), player));
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        // bukkitTask = Bukkit.getScheduler().runTaskTimer(LightMaster.instance, this::refreshGui, 0, refreshRate);

        gui.setOnClose(event -> {
            if(bukkitTask != null) {
                bukkitTask.cancel();
            }
        });

        gui.addPane(getExtraPane());
        gui.addPane(getPatternPane());

        // handle the click actions from the players inventory

        gui.setOnBottomClick(event -> {
            event.setCancelled(true);
            if(event.getCurrentItem() != null &! secretPlayer.hasGift()) {
                clickedItemStack = event.getCurrentItem().clone();
                LightMaster.instance.getDebugPrinting().print("Gift stack in BottomClick before timer: " + this.clickedItemStack);
                // create a runnable
                Bukkit.getScheduler().runTaskTimer(LightMaster.instance, () -> {
                    // open the SecretSantaGiftEditApplyInv
                    LightMaster.instance.getDebugPrinting().print("Gift stack in BottomClick after Timer: " + this.clickedItemStack);

                }, 0, 10);

            }
            update();
        });

        gui.show(player);
    }

    /**
     * Get the extra pane
     * @return StaticPane for the extra items (day rewards)
     */
    @NotNull
    private StaticPane getExtraPane() {

        StaticPane staticPane = new StaticPane(0, 0, 9, 5);

        ClickItemHandler noGiftStack = new ClickItemHandler(
                Objects.requireNonNull(extraSection.getConfigurationSection("no-gift")), player);
        ClickItemHandler giftStack = new ClickItemHandler(
                Objects.requireNonNull(extraSection.getConfigurationSection("gift")), player);

        ClickItemHandler accept = new ClickItemHandler(
                Objects.requireNonNull(extraSection.getConfigurationSection("accept")), player);

        if(secretPlayer == null) {
            LightMaster.instance.getDebugPrinting().print("SecretPlayer is null !");
            return staticPane;
        }
        int slotID = 22;
        Slot slot = Slot.fromIndex(slotID);

        ItemStack guiItemStack;

        guiItemStack = secretPlayer.hasGift() ? secretPlayer.getGift() :
                (clickedItemStack != null ? clickedItemStack : noGiftStack.getGuiItem());

        staticPane.addItem(new GuiItem(guiItemStack, inventoryClickEvent -> {

            if(!inventoryClickEvent.isLeftClick()) {
                return;
            }

            // execute the actions from the clickItemHandler -> file
            if(secretPlayer.hasGift()) {

                // the player can change the gift for X money -> system

                giftStack.getActionHandlers().forEach(ActionHandler::handleAction);
                player.getInventory().addItem(secretPlayer.getGift());
            } else {

                // the player can add a gift to the system
                // open SecretSantaGiftEditApplyInv if he clicks an item on the players inventory
                // SecretSantaGiftEditApplyInv -> sending clicked ItemStack
                noGiftStack.getActionHandlers().forEach(ActionHandler::handleAction);
            }
        }), slot);

        if(clickedItemStack != null &! secretPlayer.hasGift()) {
            staticPane.addItem(new GuiItem(accept.getItemStack(), inventoryClickEvent -> {

                if(!inventoryClickEvent.isLeftClick()) {
                    return;
                }

                // removes the selected Item from the Inventory and
                // add this item to the storage file of the player
                // and marked as a gift.
                // TODO: Add the item to the storage file of the player
                LightMaster.instance.getDebugPrinting().print("Gift stack before in SaveGiftClick: " + this.clickedItemStack);
                player.getInventory().remove(this.clickedItemStack);
                secretPlayer.setGift(clickedItemStack);
                LightMaster.instance.getDebugPrinting().print("Gift stack after in SaveGiftClick: " + clickedItemStack);

                // execute the actions from the clickItemHandler -> file
                accept.getActionHandlers().forEach(ActionHandler::handleAction);

            }), accept.getSlot());
        }

        return staticPane;
    }

    /**
     * Get the pattern pane
     * @return PatternPane for the deco items
     */
    @NotNull
    private PatternPane getPatternPane() {

        String[] patternList = invConstructor.getPattern().toArray(new String[0]);
        Pattern pattern = new Pattern(patternList);
        PatternPane patternPane = new PatternPane(0, 0, 9, invConstructor.getRows(), pattern);

        for(String patternIdentifier : invConstructor.getClickItemHandlersSection().getKeys(false)) {

            ClickItemHandler clickItemHandler = new ClickItemHandler(
                    Objects.requireNonNull(invConstructor.getClickItemHandlersSection().getConfigurationSection(
                            patternIdentifier)), player);

            ItemStack itemStack = clickItemHandler.getGuiItem();

            patternPane.bindItem(patternIdentifier.charAt(0), new GuiItem(itemStack, inventoryClickEvent -> {

                if(!inventoryClickEvent.isLeftClick()) {
                    return;
                }

                // AntiSpam protection for general actions
                if(clickCooldown.contains(player)) {
                    return;
                }

                Bukkit.getScheduler().runTaskLater(LightMaster.instance, () -> {
                    clickCooldown.remove(player);
                }, cooldownTime);

                // execute the actions from the clickItemHandler -> file
                clickItemHandler.getActionHandlers().forEach(ActionHandler::handleAction);



                clickCooldown.add(player);
            }));
        }


        return patternPane;
    }

    /**
     * Update the GUI
     * This method is used to update the GUI after a player has claimed a reward
     */
    private void update() {

        gui.getPanes().forEach(Pane::clear);

        gui.addPane(getPatternPane());
        gui.addPane(getExtraPane());


        gui.update();
    }
}
