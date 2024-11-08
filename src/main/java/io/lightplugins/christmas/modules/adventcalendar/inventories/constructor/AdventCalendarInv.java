package io.lightplugins.christmas.modules.adventcalendar.inventories.constructor;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.adventcalendar.LightAdventCalendar;
import io.lightplugins.christmas.modules.adventcalendar.api.manager.AdventManager;
import io.lightplugins.christmas.modules.adventcalendar.api.models.AdventPlayer;
import io.lightplugins.christmas.util.SoundUtil;
import io.lightplugins.christmas.util.constructor.InvConstructor;
import io.lightplugins.christmas.util.handler.ActionHandler;
import io.lightplugins.christmas.util.handler.ClickItemHandler;
import io.lightplugins.christmas.util.handler.RequirementHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class AdventCalendarInv {

    private final ChestGui gui = new ChestGui(6, "Init");
    private final InvConstructor invConstructor;
    private final ConfigurationSection rewardSection;
    private final ConfigurationSection extraItemSection;
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
     * @param extraItemSection ConfigurationSection - The section for the extra items
     * @param player Player - The player to open the inventory for
     */

    public AdventCalendarInv(
            InvConstructor invConstructor,
            ConfigurationSection extraSection,
            ConfigurationSection extraItemSection,
            Player player) {

        this.invConstructor = invConstructor;
        this.rewardSection = extraSection;
        this.extraItemSection = extraItemSection;
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

        StaticPane staticPane = new StaticPane(0, 0, 9, 6);

        for(String rewardKey : rewardSection.getKeys(false)) {

            ClickItemHandler readyClickHandler = new ClickItemHandler(
                    Objects.requireNonNull(extraItemSection.getConfigurationSection("ready")), player);

            ClickItemHandler notReadyClickHandler = new ClickItemHandler(
                    Objects.requireNonNull(extraItemSection.getConfigurationSection("not-ready")), player);

            ClickItemHandler alreadyClaimedClickHandler = new ClickItemHandler(
                    Objects.requireNonNull(extraItemSection.getConfigurationSection("already-claimed")), player);


            List<RequirementHandler> requirementHandlers = new AdventManager().getDayRequirements(
                    rewardSection.getStringList(rewardKey + ".requirements"), player);

            // The final ClickItemHandler used for the click event.
            ClickItemHandler finalClickHandler;

            boolean allRequirementsMet = true;
            Date date = null;
            for (RequirementHandler requirementHandler : requirementHandlers) {
                date = requirementHandler.getDateRequirement();
                if (!requirementHandler.checkRequirements()) {
                    allRequirementsMet = false;
                    break;
                }
            }

            boolean hasClaimed = false;
            if(date != null) {
                for(AdventPlayer adventPlayer : LightAdventCalendar.instance.getAdventPlayerData()) {

                    if(!adventPlayer.hasPlayerDataFile(player.getUniqueId().toString())) {
                        continue;
                    }

                    if(adventPlayer.hasClaimed(date)) {
                        hasClaimed = true;
                        break;
                    }
                }
            }

            if(hasClaimed) {
                finalClickHandler = alreadyClaimedClickHandler;
            } else if(allRequirementsMet) {
                finalClickHandler = readyClickHandler;
            } else {
                finalClickHandler = notReadyClickHandler;
            }

            finalClickHandler.setExtraSection(rewardSection.getConfigurationSection(rewardKey));

            int day = Integer.parseInt(rewardKey);
            Slot slot = Slot.fromIndex(finalClickHandler.getExtraSlot());

            // replace placeholders in lore
            List<String> itemLore = finalClickHandler.getItemMeta().getLore();
            List<String> translatedLore = new ArrayList<>();

            if(itemLore != null) {
                for (String loreLine : itemLore) {
                    translatedLore.add(loreLine.replace("#day#", String.valueOf(day))
                            .replace("#date#", new SimpleDateFormat("dd.MM.yyyy HH.mm").format(date)));
                }
            }

            ItemMeta im = finalClickHandler.getItemMeta();

            if (im != null) {
                im.setLore(translatedLore);
                im.setDisplayName(im.getDisplayName()
                        .replace("#day#", String.valueOf(day))
                        .replace("#date#", new SimpleDateFormat("dd.MM.yyyy").format(date)));
                finalClickHandler.getItemStack().setItemMeta(im);
                finalClickHandler.getItemStack().setAmount(day);
            }

            ClickItemHandler tempClickHandler = finalClickHandler;
            Date finalDate = date;
            boolean finalHasClaimed = hasClaimed;
            staticPane.addItem(new GuiItem(finalClickHandler.getItemStack(), inventoryClickEvent -> {

                if(!inventoryClickEvent.isLeftClick()) {
                    return;
                }

                if(clickCooldown.contains(player)) {
                    return;
                }

                Bukkit.getScheduler().runTaskLater(LightMaster.instance, () -> {
                    clickCooldown.remove(player);
                }, cooldownTime);

                boolean allMet = true;
                for (RequirementHandler requirementHandler : requirementHandlers) {
                    if (!requirementHandler.checkRequirements()) {
                        allMet = false;
                        break;
                    }
                }

                // Check if all requirements are met before claiming
                if(!allMet) {
                    if(!failCooldown.contains(player)) {
                        LightMaster.instance.getMessageSender().sendPlayerMessage(
                                LightAdventCalendar.instance.getMessageParams().requirementFail(), player);
                        SoundUtil.onFail(player);
                        // AntiSpam protection for fail actions
                        failCooldown.add(player);
                        Bukkit.getScheduler().runTaskLater(LightMaster.instance, () -> {
                            failCooldown.remove(player);
                        }, failCooldownTime);
                        return;
                    }
                    return;
                }

                //  Check if the player has already claimed the reward
                if(finalHasClaimed) {
                    if(!failCooldown.contains(player)) {
                        LightMaster.instance.getMessageSender().sendPlayerMessage(
                                LightAdventCalendar.instance.getMessageParams().alreadyClaimed(), player);
                        SoundUtil.onFail(player);
                        // AntiSpam protection for fail actions
                        failCooldown.add(player);
                        Bukkit.getScheduler().runTaskLater(LightMaster.instance, () -> {
                            failCooldown.remove(player);
                        }, failCooldownTime);
                        return;
                    }
                    return;
                }

                // Claim the reward and add the date to the claimed dates
                LightAdventCalendar.instance.getAdventPlayerData().stream()
                        .filter(adventPlayer -> adventPlayer.hasPlayerDataFile(player.getUniqueId().toString()))
                        .forEach(adventPlayer -> adventPlayer.addClaimedDate(finalDate));

                tempClickHandler.getExtraActionHandlers().forEach(ActionHandler::handleAction);
                LightMaster.instance.getMessageSender().sendPlayerMessage(
                        LightAdventCalendar.instance.getMessageParams().successClaim()
                                .replace("#day#", String.valueOf(day)), player);
                SoundUtil.onSuccess(player);

                clickCooldown.add(player);
                // force update the GUI
                update();
            }), slot);
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
