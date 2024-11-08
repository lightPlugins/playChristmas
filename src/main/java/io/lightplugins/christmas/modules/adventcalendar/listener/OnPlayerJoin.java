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
    public void onJoin (PlayerJoinEvent event) {

        Player player = event.getPlayer();

        // create new player storage file
        try {
            File file = LightAdventCalendar.instance.getPlayerDataFiles().createFile(player.getUniqueId().toString());
            new AdventPlayer(player, file);
        } catch (IOException e) {
            throw new RuntimeException("Error creating player data file for player: " + player.getName(), e);
        }

    }
}
