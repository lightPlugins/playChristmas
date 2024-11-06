package io.lightplugins.christmas.util.handler.actions;

import io.lightplugins.christmas.util.interfaces.LightAction;
import org.bukkit.entity.Player;

public class InvCloseAction implements LightAction {

    @Override
    public void execute(Player player, String[] actionDataArray) {
        player.closeInventory();
    }
}
