package io.lightplugins.christmas.util.handler.actions;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.util.interfaces.LightAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CoinsAddAction implements LightAction {
    @Override
    public void execute(Player player, String[] actionDataArray) {

        int amountToAdd = Integer.parseInt(actionDataArray[1]);

        if(amountToAdd < 0 || amountToAdd > 1000000) {
            LightMaster.instance.getDebugPrinting().configError(
                    "Invalid amount to add to player's coins in reward action.");
            return;
        }

        player.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                "rbridge coins add " + player.getName() + " " + amountToAdd);


    }
}
