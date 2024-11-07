package io.lightplugins.christmas.modules.adventcalendar.inventories.constructor;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.adventcalendar.LightAdventCalendar;
import io.lightplugins.christmas.modules.adventcalendar.api.manager.AdventManager;
import io.lightplugins.christmas.util.constructor.InvConstructor;
import io.lightplugins.christmas.util.handler.ActionHandler;
import io.lightplugins.christmas.util.handler.ClickItemHandler;
import io.lightplugins.christmas.util.handler.RequirementHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

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
    private final int cooldownTime = 3;

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

            LightMaster.instance.getDebugPrinting().print("Reward Key: " + rewardKey);
            for(String key : extraItemSection.getKeys(false)) {
                LightMaster.instance.getDebugPrinting().print("Extra Item Key: " + key);
            }

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
            LightMaster.instance.getDebugPrinting().print("Checking requirements for reward: " + rewardKey);
            for (RequirementHandler requirementHandler : requirementHandlers) {
                if (!requirementHandler.checkRequirements()) {
                    LightMaster.instance.getDebugPrinting().print(requirementHandler.getRequirementDataArray()[1] + " 1 not met");
                    allRequirementsMet = false;
                    break;
                }
                LightMaster.instance.getDebugPrinting().print(requirementHandler.getRequirementDataArray()[1] + " 1 success met");
            }
            LightMaster.instance.getDebugPrinting().print("All requirements checked with result: " + allRequirementsMet);

            if (allRequirementsMet) {
                finalClickHandler = readyClickHandler;
            } else {
                finalClickHandler = notReadyClickHandler;
            }

            finalClickHandler.setExtraSection(rewardSection.getConfigurationSection(rewardKey));

            int day = Integer.parseInt(rewardKey);
            Slot slot = Slot.fromIndex(finalClickHandler.getExtraSlot());

            LightMaster.instance.getDebugPrinting().print("Slot: " + finalClickHandler.getExtraSlot());

            ClickItemHandler tempClickHandler = finalClickHandler;
            staticPane.addItem(new GuiItem(finalClickHandler.getGuiItem(), inventoryClickEvent -> {

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
                    LightMaster.instance.getMessageSender().sendPlayerMessage("requirements-not-met", player);
                    return;
                }

                LightMaster.instance.getDebugPrinting().print("All requirements met for day: " + day);
                tempClickHandler.getExtraActionHandlers().forEach(singleAction -> {
                    LightMaster.instance.getDebugPrinting().print("Action executed for day: " + singleAction.getActions()[0] );
                    singleAction.handleAction();
                });
                clickCooldown.add(player);
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
