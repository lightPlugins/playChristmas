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


    //

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

            if (hasClaimed) {
                finalClickHandler = alreadyClaimedClickHandler;
            } else if (allRequirementsMet) {
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

                if(!allMet) {
                    if(!failCooldown.contains(player)) {
                        LightMaster.instance.getMessageSender().sendPlayerMessage(
                                LightAdventCalendar.instance.getMessageParams().requirementFail(), player);
                        SoundUtil.onFail(player);
                        failCooldown.add(player);
                        Bukkit.getScheduler().runTaskLater(LightMaster.instance, () -> {
                            failCooldown.remove(player);
                        }, failCooldownTime);
                        return;
                    }
                    return;
                }

                if(finalHasClaimed) {
                    if(!failCooldown.contains(player)) {
                        LightMaster.instance.getMessageSender().sendPlayerMessage(
                                LightAdventCalendar.instance.getMessageParams().alreadyClaimed(), player);
                        SoundUtil.onFail(player);
                        failCooldown.add(player);
                        Bukkit.getScheduler().runTaskLater(LightMaster.instance, () -> {
                            failCooldown.remove(player);
                        }, failCooldownTime);
                        return;
                    }
                    return;
                }


                LightAdventCalendar.instance.getAdventPlayerData().stream()
                        .filter(adventPlayer -> adventPlayer.hasPlayerDataFile(player.getUniqueId().toString()))
                        .forEach(adventPlayer -> adventPlayer.addClaimedDate(finalDate));

                tempClickHandler.getExtraActionHandlers().forEach(ActionHandler::handleAction);
                LightMaster.instance.getMessageSender().sendPlayerMessage(
                        LightAdventCalendar.instance.getMessageParams().successClaim()
                                .replace("#day#", String.valueOf(day)), player);
                SoundUtil.onSuccess(player);

                clickCooldown.add(player);
                update();
            }), slot);
        }

        return staticPane;
    }

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

                // Anti-Spam protection
                if(clickCooldown.contains(player)) {
                    return;
                }

                Bukkit.getScheduler().runTaskLater(LightMaster.instance, () -> {
                    clickCooldown.remove(player);
                }, cooldownTime);

                clickItemHandler.getActionHandlers().forEach(ActionHandler::handleAction);
                clickCooldown.add(player);
            }));
        }


        return patternPane;
    }

    private void update() {

        gui.getPanes().forEach(Pane::clear);

        gui.addPane(getPatternPane());
        gui.addPane(getExtraPane());

        gui.update();
    }

    // currently toggled - enable this for refreshing the GUI
    private void refreshGui() {
        // gui.setTitle(invConstructor.getGuiTitle());

        // Clear existing items in the pattern pane
        PatternPane patternPane = (PatternPane) gui.getPanes().getFirst();
        patternPane.clear();

        // Re-add items to the pattern pane
        for (String patternIdentifier : invConstructor.getClickItemHandlersSection().getKeys(false)) {
            ClickItemHandler clickItemHandler = new ClickItemHandler(
                    Objects.requireNonNull(invConstructor.getClickItemHandlersSection().getConfigurationSection(
                            patternIdentifier)), player);

            ItemStack itemStack = clickItemHandler.getGuiItem();
            patternPane.bindItem(patternIdentifier.charAt(0), new GuiItem(itemStack));
            // TODO: Add Click Event after item refreshing
            // Maybe update only the Items, not the PatternPane
            clickItemHandler.getActionHandlers().forEach(ActionHandler::handleAction);
        }

        gui.update();
        LightMaster.instance.getDebugPrinting().print("GUI refreshed");
    }

}
