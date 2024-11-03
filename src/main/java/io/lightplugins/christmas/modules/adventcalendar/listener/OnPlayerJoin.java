package io.lightplugins.christmas.modules.adventcalendar.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoin implements Listener {

    @EventHandler
    public void onJoin (PlayerJoinEvent event) {

        Player player = event.getPlayer();

    }
}
