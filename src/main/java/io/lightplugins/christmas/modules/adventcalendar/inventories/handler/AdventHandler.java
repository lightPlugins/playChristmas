package io.lightplugins.christmas.modules.adventcalendar.inventories.handler;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.util.handler.ActionHandler;
import io.lightplugins.christmas.util.handler.ClickItemHandler;
import io.lightplugins.christmas.util.handler.RequirementHandler;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AdventHandler {

    private final ConfigurationSection REWARD_ARGS;
    private final ConfigurationSection REQUIREMENT_ARGS;
    private final ConfigurationSection CLICK_ITEM_ARGS;
    private ClickItemHandler clickItemHandler;
    private ItemStack clickItem;
    private ItemMeta clickItemMeta;
    private final Player player;

    private List<String> actionList;
    private List<String> requirementList;

    private final List<ActionHandler> actionHandler = new ArrayList<>();
    private final List<RequirementHandler> requirementHandler = new ArrayList<>();

    public AdventHandler(ConfigurationSection rewardSection, ConfigurationSection itemSection, Player player) {
        this.REWARD_ARGS = rewardSection.getConfigurationSection("rewards");
        this.REQUIREMENT_ARGS = rewardSection.getConfigurationSection("requirements");
        this.player = player;
        this.CLICK_ITEM_ARGS = itemSection.getConfigurationSection("args");
        this.actionList = rewardSection.getStringList("rewards");
        this.requirementList = rewardSection.getStringList("requirements");

        readClickItem();
        loadActions();
        loadRequirements();
        validate();

    }

    private void readClickItem() {

        this.clickItemHandler = new ClickItemHandler(CLICK_ITEM_ARGS, player);
        clickItem = clickItemHandler.getGuiItem();

        if(clickItem == null) {
            LightMaster.instance.getDebugPrinting().configError("Click item not found for reward: " + REWARD_ARGS.getName());
            LightMaster.instance.getDebugPrinting().configError("Using default item: STONE");
            clickItem = new ItemStack(Material.STONE);
        }

        clickItemMeta = clickItem.getItemMeta();
    }

    private void validate() {

        if(actionList == null || actionList.isEmpty()) {
            LightMaster.instance.getLogger().warning("No reward actions found for this reward.");
            this.actionList = new ArrayList<>();
        }

        if(requirementList == null || requirementList.isEmpty()) {
            LightMaster.instance.getLogger().warning("No requirements found for this reward.");
            this.requirementList = new ArrayList<>();
        }
    }

    private void loadActions() {
        actionList.forEach(action -> {
            actionHandler.add(new ActionHandler(player, action));
        });
    }

    private void loadRequirements() {
        requirementList.forEach(requirement -> {
            requirementHandler.add(new RequirementHandler(player, requirement));
        });
    }



}
