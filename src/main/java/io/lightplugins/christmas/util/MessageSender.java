package io.lightplugins.christmas.util;
import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.adventcalendar.LightAdventCalendar;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
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

    public void sendTitle(String title, String subtitle, Player player) {
        Bukkit.getScheduler().runTask(LightMaster.instance, () -> {
            Audience audience = (Audience) player;
            Component titleComponent = LightMaster.instance.colorTranslation.universalColor(title, player);
            Component subtitleComponent = LightMaster.instance.colorTranslation.universalColor(subtitle, player);
            Title titleMessage = Title.title(titleComponent, subtitleComponent);
            audience.showTitle(titleMessage);
        });
    }
}
