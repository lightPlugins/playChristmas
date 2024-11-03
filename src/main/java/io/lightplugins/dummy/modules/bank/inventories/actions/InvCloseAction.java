package io.lightplugins.dummy.modules.bank.inventories.actions;

import io.lightplugins.dummy.util.interfaces.LightAction;
import org.bukkit.entity.Player;

public class InvCloseAction implements LightAction {

    @Override
    public void execute(Player player, String[] actionDataArray) {
        player.closeInventory();
    }
}
