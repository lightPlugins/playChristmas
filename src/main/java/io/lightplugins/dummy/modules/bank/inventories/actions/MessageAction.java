package io.lightplugins.dummy.modules.bank.inventories.actions;

import io.lightplugins.dummy.Light;
import io.lightplugins.dummy.util.interfaces.LightAction;
import org.bukkit.entity.Player;

public class MessageAction implements LightAction {

    @Override
    public void execute(Player player, String[] actionDataArray) {

        if (actionDataArray.length < 2) {
            player.sendMessage("Config error: No message to send.");
            return;
        }

        Light.getMessageSender().sendPlayerMessage(actionDataArray[1], player);
    }
}
