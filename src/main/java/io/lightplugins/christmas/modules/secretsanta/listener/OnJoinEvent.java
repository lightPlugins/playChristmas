package io.lightplugins.christmas.modules.secretsanta.listener;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.secretsanta.LightSecretSanta;
import io.lightplugins.christmas.modules.secretsanta.api.models.SecretPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;

public class OnJoinEvent implements Listener {

    @EventHandler
    private void onServerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        // create new player storage file if not already existing
        try {

            for(SecretPlayer secretPlayer : LightSecretSanta.instance.getSecretPlayerData()) {
                if(secretPlayer.hasPlayerDataFile(player.getUniqueId().toString())) {
                    LightMaster.instance.getDebugPrinting().print("Player data file already exists for player: " + player.getName());
                    return;
                }
            }

            File file = LightSecretSanta.instance.getPlayerDataFiles().createFile(player.getUniqueId().toString());

            LightMaster.instance.getDebugPrinting().print("Creating secret player data file for player: " + player.getName());
            LightSecretSanta.instance.getSecretPlayerData().add(new SecretPlayer(file).initNewPlayer());
        } catch (IOException e) {
            throw new RuntimeException("Error creating player data file for player: " + player.getName(), e);
        }
    }
}
