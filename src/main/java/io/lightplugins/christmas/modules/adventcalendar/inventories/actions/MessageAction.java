package io.lightplugins.christmas.modules.adventcalendar.inventories.actions;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.util.interfaces.LightAction;
import org.bukkit.entity.Player;

public class MessageAction implements LightAction {

    @Override
    public void execute(Player player, String[] actionDataArray) {

        if (actionDataArray.length < 2) {
            player.sendMessage("Config error: No message to send.");
            return;
        }
        LightMaster.instance.getMessageSender().sendPlayerMessage(actionDataArray[1], player);
    }
}
