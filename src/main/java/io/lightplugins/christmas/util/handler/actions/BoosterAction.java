package io.lightplugins.christmas.util.handler.actions;

import io.lightplugins.christmas.util.interfaces.LightAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BoosterAction implements LightAction {

    @Override
    public void execute(Player player, String[] actionDataArray) {

        if (actionDataArray.length < 2) {
            player.sendMessage("Config error: No message to send.");
            return;
        }

        String[] splitBooster = actionDataArray[1].split(":");


        if(splitBooster.length < 2) {
            player.sendMessage("Config error: No booster type or multiplier provided.");
            return;
        }

        try {
            int boosterAmount = Integer.parseInt(splitBooster[1]);

            String boosterType = splitBooster[0];
            player.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                    "rbridge booster add " + boosterType + " " + player.getName() + " " + boosterAmount);



        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number format for booster multiplier.", e);
        }


    }
}
