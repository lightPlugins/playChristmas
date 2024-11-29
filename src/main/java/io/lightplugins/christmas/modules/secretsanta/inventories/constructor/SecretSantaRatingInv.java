package io.lightplugins.christmas.modules.secretsanta.inventories.constructor;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.secretsanta.LightSecretSanta;
import io.lightplugins.christmas.modules.secretsanta.api.models.SecretPlayer;
import io.lightplugins.christmas.modules.secretsanta.config.MessageParams;
import io.lightplugins.christmas.util.MessageSender;
import io.lightplugins.christmas.util.SoundUtil;
import io.lightplugins.christmas.util.constructor.InvConstructor;
import io.lightplugins.christmas.util.handler.ActionHandler;
import io.lightplugins.christmas.util.handler.ClickItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

public class SecretSantaRatingInv {

    private final ChestGui gui = new ChestGui(6, "Init");
    private final InvConstructor invConstructor;
    private final ConfigurationSection rewardSection;
    private final Player player;
    private BukkitTask bukkitTask;
    private final int refreshRate = 20;
    private final List<Player> clickCooldown = new ArrayList<>();
    private final List<Player> failCooldown = new ArrayList<>();
    private final int cooldownTime = 3;
    private final int failCooldownTime = 20;

    /**
     * Constructor for the AdventCalendarInv
     * @param invConstructor InvConstructor - The constructor for the inventory
     * @param extraSection ConfigurationSection - The section for the rewards
     * @param player Player - The player to open the inventory for
     */

    public SecretSantaRatingInv(
            InvConstructor invConstructor,
            ConfigurationSection extraSection,
            Player player) {

        this.invConstructor = invConstructor;
        this.rewardSection = extraSection;
        this.player = player;
    }

    /**
     * Open the inventory
     * This method is used to open the Advent Calendar GUI
     */
    public void openInventory() {

        gui.setRows(invConstructor.getRows());
        gui.setTitle(LightMaster.instance.getColorTranslation().loreLineTranslation(invConstructor.getGuiTitle(), player));
        //gui.update();
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        // bukkitTask = Bukkit.getScheduler().runTaskTimer(LightMaster.instance, this::refreshGui, 0, refreshRate);

        gui.setOnClose(event -> {
            if(bukkitTask != null) {
                bukkitTask.cancel();
            }
        });

        gui.addPane(getGiftPage());
        gui.addPane(getExtraPane());
        gui.addPane(getPatternPane());

        gui.show(player);
    }

    /**
     * Get the extra pane
     * @return StaticPane for the extra items (day rewards)
     */
    @NotNull
    private StaticPane getExtraPane() {

        StaticPane staticPane = new StaticPane(0, 0, 9, 5);

        for(String rewardKey : rewardSection.getKeys(false)) {

        }

        return staticPane;
    }

    @NotNull
    private PaginatedPane getGiftPage() {

        // get all gifts and add these to an PaginatedPane
        PaginatedPane paginatedPane = new PaginatedPane(1, 1, 8, gui.getRows());
        List<GuiItem> allGifts = new ArrayList<>();

        // add all gifts to the list

        SecretPlayer currentSecretPlayer = null;
        for(SecretPlayer secretPlayer : LightSecretSanta.instance.getSecretPlayerData()) {
            if(secretPlayer.getPlayerUUID().equals(player.getUniqueId())) {
                currentSecretPlayer = secretPlayer;
            }
        }

        if(currentSecretPlayer == null) {
            throw new RuntimeException("This player does not have a SecretSanta file!");
        }

        for(SecretPlayer secretPlayer : LightSecretSanta.instance.getSecretPlayerData()) {

            if(secretPlayer.getGift() == null) {
                continue;
            }

            ItemStack is = secretPlayer.getGift();
            ItemMeta im = is.getItemMeta();

            if(im == null) {
                throw new RuntimeException("This Gift does not have an ItemMeta!");
            }

            List<String> currentLore = im.getLore();

            if(currentLore == null) {
                currentLore = new ArrayList<>();
            }

            currentLore.add(" ");
            currentLore.add(LightMaster.instance.colorTranslation.loreLineTranslation(
                    "<#ffdc73>Aktuelle Votes: <gray>" + secretPlayer.getVotes(), player));

            im.setLore(currentLore);
            is.setItemMeta(im);

            SecretPlayer finalCurrentSecretPlayer = currentSecretPlayer;
            allGifts.add(new GuiItem(is, inventoryClickEvent -> {

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

                clickCooldown.add(player);

                if(!finalCurrentSecretPlayer.hasVoted()) {

                    String formatedMaterialName =
                            secretPlayer.getGift().getType().toString().toLowerCase().replace("_", " ");
                    finalCurrentSecretPlayer.setVoted();
                    secretPlayer.addVote();
                    LightMaster.instance.getMessageSender().sendPlayerMessage(
                            LightSecretSanta.messageParams.successVoted()
                                    .replace("#gift#", formatedMaterialName), player);
                    SoundUtil.onSuccess(player);
                    update();
                    return;
                }

                LightMaster.instance.getMessageSender().sendPlayerMessage(
                        LightSecretSanta.messageParams.alreadyVoted(), player);
                SoundUtil.onFail(player);

            }));
        }

        paginatedPane.populateWithGuiItems(allGifts);
        return paginatedPane;
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

        gui.addPane(getGiftPage());
        gui.addPane(getExtraPane());
        gui.addPane(getPatternPane());

        gui.update();
    }
}
