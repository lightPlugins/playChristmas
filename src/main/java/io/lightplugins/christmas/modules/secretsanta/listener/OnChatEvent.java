package io.lightplugins.christmas.modules.secretsanta.listener;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.secretsanta.LightSecretSanta;
import io.lightplugins.christmas.modules.secretsanta.api.models.SecretPlayer;
import io.lightplugins.christmas.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class OnChatEvent implements Listener {

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        SecretPlayer secretPlayer = LightSecretSanta.instance.getSecretPlayerData().stream()
                .filter(playerData -> playerData.getPlayerDataFile().getName().replace(".yml", "")
                        .equalsIgnoreCase(player.getUniqueId().toString()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("SecretPlayer not found for player: " + player.getUniqueId()));

        if(secretPlayer.isInCheckChat()) {
            event.setCancelled(true);
            String message = event.getMessage();
            for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {

                if(offlinePlayer.getName() == null) {
                    continue;
                }

                if(offlinePlayer.getName().equalsIgnoreCase(message)) {
                    secretPlayer.setPartner(offlinePlayer.getUniqueId());
                    LightMaster.instance.getMessageSender().sendPlayerMessage(
                            LightSecretSanta.messageParams.addPartnerSuccess()
                                    .replace("#partner#", offlinePlayer.getName()),
                            player
                    );
                    SoundUtil.onSuccess(player);
                    secretPlayer.setChatCheck(false);
                    return;
                }
            }

            LightMaster.instance.getMessageSender().sendPlayerMessage(
                    LightSecretSanta.messageParams.addPartnerNotFound()
                            .replace("#partner#", message),
                    player
            );
            SoundUtil.onFail(player);
            secretPlayer.setChatCheck(false);
        }
    }
}
