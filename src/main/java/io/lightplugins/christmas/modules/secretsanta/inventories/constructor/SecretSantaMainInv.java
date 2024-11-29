package io.lightplugins.christmas.modules.secretsanta.inventories.constructor;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.secretsanta.LightSecretSanta;
import io.lightplugins.christmas.modules.secretsanta.api.models.SecretPlayer;
import io.lightplugins.christmas.util.SoundUtil;
import io.lightplugins.christmas.util.constructor.InvConstructor;
import io.lightplugins.christmas.util.handler.ActionHandler;
import io.lightplugins.christmas.util.handler.ClickItemHandler;
import net.playnayz.core.Core;
import net.playnayz.core.gameplayer.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

public class SecretSantaMainInv {

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
    private final SecretPlayer secretPlayer;

    /**
     * Constructor for the AdventCalendarInv
     * @param invConstructor InvConstructor - The constructor for the inventory
     * @param extraSection ConfigurationSection - The section for the rewards
     * @param player Player - The player to open the inventory for
     */

    public SecretSantaMainInv(
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
        //gui.update();
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        // bukkitTask = Bukkit.getScheduler().runTaskTimer(LightMaster.instance, this::refreshGui, 0, refreshRate);

        gui.setOnClose(event -> {
            if(bukkitTask != null) {
                bukkitTask.cancel();
            }
        });

        gui.addPane(getGiftAddPane());
        gui.addPane(getPartnerPane());
        gui.addPane(getPatternPane());


        gui.show(player);
    }

    @NotNull
    private StaticPane getGiftAddPane() {

        StaticPane staticPane = new StaticPane(0, 0, 9, 5);

        ClickItemHandler noPartnerGiftAdd = new ClickItemHandler(
                Objects.requireNonNull(extraSection.getConfigurationSection("missing-partner-gift")), player);
        ClickItemHandler partnerGiftAdd = new ClickItemHandler(
                Objects.requireNonNull(extraSection.getConfigurationSection("partner-gift")), player);

        ItemStack guiItem = secretPlayer.hasPartner() ? partnerGiftAdd.getGuiItem() : noPartnerGiftAdd.getGuiItem();

        staticPane.addItem(new GuiItem(guiItem, inventoryClickEvent -> {

            if(!inventoryClickEvent.isLeftClick()) {
                return;
            }

            if(failCooldown.contains(player)) {
                return;
            }

            if(secretPlayer.hasPartner()) {
                partnerGiftAdd.getActionHandlers().forEach(ActionHandler::handleAction);
                return;
            }

            noPartnerGiftAdd.getActionHandlers().forEach(ActionHandler::handleAction);
            SoundUtil.onFail(player);
            failCooldown.add(player);
            Bukkit.getScheduler().runTaskLater(LightMaster.instance, () -> {
                failCooldown.remove(player);
            }, failCooldownTime);


        }), secretPlayer.hasPartner() ? partnerGiftAdd.getSlot() : noPartnerGiftAdd.getSlot());

        return staticPane;
    }

    /**
     * Get the extra pane
     * @return StaticPane for the extra items (day rewards)
     */
    @NotNull
    private StaticPane getPartnerPane() {

        StaticPane staticPane = new StaticPane(0, 0, 9, 5);

        SecretPlayer currentSecretPlayer = null;
        for(SecretPlayer secretPlayer : LightSecretSanta.instance.getSecretPlayerData()) {
            if(secretPlayer.getPlayerUUID().equals(player.getUniqueId())) {
                currentSecretPlayer = secretPlayer;
            }
        }

        if(currentSecretPlayer == null) {
            throw new RuntimeException("This player does not have a SecretSanta file!");
        }

        ClickItemHandler partnerStack = new ClickItemHandler(
                Objects.requireNonNull(extraSection.getConfigurationSection("partner")), player);

        ClickItemHandler noPartnerStack = new ClickItemHandler(
                Objects.requireNonNull(extraSection.getConfigurationSection("no-partner")), player);

        if(currentSecretPlayer.hasPartner()) {
            // partnerStack.setHeadData(secretPlayer.getPartner().getName());
            partnerStack.setHeadData(currentSecretPlayer.getPartner().getUniqueId().toString());
            partnerStack.replaceLoreLine("#partner#", currentSecretPlayer.getPartner().getName());
        }

        ItemStack guiItem = secretPlayer.hasPartner() ? partnerStack.getGuiItem() : noPartnerStack.getGuiItem();

        staticPane.addItem(new GuiItem(guiItem, inventoryClickEvent -> {

            if(inventoryClickEvent.isLeftClick()) {
                if(!secretPlayer.hasPartner()) {
                    secretPlayer.setChatCheck(true);
                    player.closeInventory();
                    String upperTitle = LightSecretSanta.messageParams.addPartnerTitleUpper();
                    String lowerTitle = LightSecretSanta.messageParams.addPartnerTitleLower();
                    LightMaster.instance.getMessageSender().sendTitle(upperTitle, lowerTitle, player);
                    SoundUtil.onAttention(player);
                } else {
                    SoundUtil.onFail(player);
                }
            }

            if(inventoryClickEvent.isRightClick()) {
                if(secretPlayer.hasPartner()) {
                    // TODO: get the current amount of coins from the player
                    //       and remove the coins to the partner (if he had enough)
                    GamePlayer gamePlayer = Core.instance.getGamePlayerManager().getGamePlayer(player);
                    if(gamePlayer == null) {
                        LightMaster.instance.getDebugPrinting().print("GamePlayer from NayzCore is null for player: " + player.getName());
                        return;
                    }
                    int currentCoins = gamePlayer.getCoins();
                    int price = 1500;

                    if(currentCoins < price) {
                        LightMaster.instance.getMessageSender().sendPlayerMessage(
                                LightSecretSanta.messageParams.notEnoughMoneyToChangePartner()
                                        .replace("#money#", String.valueOf(price)), player);
                        SoundUtil.onFail(player);
                        return;
                    }
                    gamePlayer.setCoins(currentCoins - price);
                    secretPlayer.setChatCheck(true);
                    player.closeInventory();
                    String upperTitle = LightSecretSanta.messageParams.addPartnerTitleUpper();
                    String lowerTitle = LightSecretSanta.messageParams.addPartnerTitleLower();
                    LightMaster.instance.getMessageSender().sendTitle(upperTitle, lowerTitle, player);
                    SoundUtil.onAttention(player);
                } else {
                    SoundUtil.onFail(player);
                }
            }

        }), secretPlayer.hasPartner() ? partnerStack.getSlot() : noPartnerStack.getSlot());

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
        gui.addPane(getGiftAddPane());
        gui.addPane(getPatternPane());
        gui.addPane(getPartnerPane());


        gui.update();
    }
}
