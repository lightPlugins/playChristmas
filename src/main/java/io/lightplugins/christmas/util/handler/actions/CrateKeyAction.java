package io.lightplugins.christmas.util.handler.actions;

import io.lightplugins.christmas.util.interfaces.LightAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CrateKeyAction implements LightAction {


    @Override
    public void execute(Player player, String[] actionDataArray) {

        if (actionDataArray.length < 2) {
            player.sendMessage("Config error: No message to send.");
            return;
        }

        String[] splitCrate = actionDataArray[1].split(":");

        if(splitCrate.length < 2) {
            player.sendMessage("Config error: No crate type or amount provided.");
            return;
        }

        try {
            int crateAmount = Integer.parseInt(splitCrate[1]);

            String crateType = splitCrate[0];
            player.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    "rbridge cratekey add " + crateType + " " + player.getName() + " " + crateAmount);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number format for crate amount.", e);
        }



    }
}
