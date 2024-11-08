package io.lightplugins.christmas.util.handler.actions;

import io.lightplugins.christmas.util.interfaces.LightAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerCmdAction implements LightAction {

    @Override
    public void execute(Player player, String[] actionDataArray) {

        String command = actionDataArray[1];
        Bukkit.getServer().dispatchCommand(player, command);
    }
}
