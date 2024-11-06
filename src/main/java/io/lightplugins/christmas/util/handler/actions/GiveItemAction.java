package io.lightplugins.christmas.util.handler.actions;

import io.lightplugins.christmas.util.interfaces.LightAction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveItemAction implements LightAction {

    @Override
    public void execute(Player player, String[] actionDataArray) {

        ItemStack itemStack = new ItemStack(Material.valueOf(actionDataArray[1]), Integer.parseInt(actionDataArray[2]));

    }
}
