package io.lightplugins.christmas.util;
import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.adventcalendar.LightAdventCalendar;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageSender {

    public void sendPlayerMessage(String message, Player player) {
        Bukkit.getScheduler().runTask(LightMaster.instance, () -> {
            Audience audience = (Audience) player;
            Component component = LightMaster.instance.colorTranslation.universalColor(
                    LightAdventCalendar.instance.getMessageParams().prefix() + message, player);
            audience.sendMessage(component);
        });
    }
}
