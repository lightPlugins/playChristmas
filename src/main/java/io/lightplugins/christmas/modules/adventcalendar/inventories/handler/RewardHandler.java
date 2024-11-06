package io.lightplugins.christmas.modules.adventcalendar.inventories.handler;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.util.handler.ClickItemHandler;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

@Getter
public class RewardHandler {

    private final ConfigurationSection REWARD_ARGS;
    private final ConfigurationSection REQUIREMENT_ARGS;
    private final ConfigurationSection CLICK_ITEM_ARGS;
    private ItemStack clickItem;
    private ItemMeta clickItemMeta;
    private final Player player;
    //            HashMap<id, data>
    private List<String> rewardActions;
    private List<String> requirements;

    private List<RewardHandler> rewardHandlers;

    public RewardHandler(ConfigurationSection rewardSection, ConfigurationSection itemSection, Player player) {
        this.REWARD_ARGS = rewardSection.getConfigurationSection("rewards");
        this.REQUIREMENT_ARGS = rewardSection.getConfigurationSection("requirements");
        this.player = player;

        this.CLICK_ITEM_ARGS = itemSection.getConfigurationSection("args");

        this.rewardActions = rewardSection.getStringList("rewards");
        this.requirements = rewardSection.getStringList("requirements");

        validate();

    }

    private void readClickItem() {

    }

    private void validate() {

        if(rewardActions == null || rewardActions.isEmpty()) {
            LightMaster.instance.getLogger().warning("No reward actions found for this reward.");
            this.rewardActions = new ArrayList<>();
        }

        if(requirements == null || requirements.isEmpty()) {
            LightMaster.instance.getLogger().warning("No requirements found for this reward.");
            this.requirements = new ArrayList<>();
        }




    }



}
