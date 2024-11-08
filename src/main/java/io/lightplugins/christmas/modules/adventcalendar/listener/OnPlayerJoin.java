package io.lightplugins.christmas.modules.adventcalendar.listener;

import io.lightplugins.christmas.modules.adventcalendar.LightAdventCalendar;
import io.lightplugins.christmas.modules.adventcalendar.api.models.AdventPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;

public class OnPlayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        // load or create player data file on player join

        Player player = event.getPlayer();

        // create new player storage file if not already existing
        try {
            File file = LightAdventCalendar.instance.getPlayerDataFiles().createFile(player.getUniqueId().toString());

            for(AdventPlayer adventPlayer : LightAdventCalendar.instance.getAdventPlayerData()) {
                if(adventPlayer.hasPlayerDataFile(player.getUniqueId().toString())) {
                    return;
                }
            }
            new AdventPlayer(file);
        } catch (IOException e) {
            throw new RuntimeException("Error creating player data file for player: " + player.getName(), e);
        }
    }
}
