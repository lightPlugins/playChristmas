package io.lightplugins.christmas.util.constructor;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.util.handler.ActionHandler;
import io.lightplugins.christmas.util.handler.ClickItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InvCreator {

    private final ChestGui gui = new ChestGui(6, "Init");
    private final InvConstructor invConstructor;
    private final Player player;
    private BukkitTask bukkitTask;
    private final int refreshRate = 20;
    private final List<Player> clickCooldown = new ArrayList<>();
    private final int cooldownTime = 3;

    public InvCreator(InvConstructor invConstructor, Player player) {
        this.invConstructor = invConstructor;
        this.player = player;
    }

    public void openInventory() {

        gui.setRows(invConstructor.getRows());
        gui.setTitle(invConstructor.getGuiTitle());
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        bukkitTask = Bukkit.getScheduler().runTaskTimer(LightMaster.instance, this::refreshGui, 0, refreshRate);

        gui.setOnClose(event -> {
            if(bukkitTask != null) {
                bukkitTask.cancel();
            }
        });

        gui.addPane(getPatternPane());
        gui.show(player);
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
        gui.setTitle(invConstructor.getGuiTitle());

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