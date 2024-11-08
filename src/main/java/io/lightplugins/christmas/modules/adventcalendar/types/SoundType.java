package io.lightplugins.christmas.modules.adventcalendar.types;

import org.bukkit.Sound;

public enum SoundType {


    /*
        Admin Command Perissions
     */

    SUCCESS(Sound.ENTITY_PLAYER_LEVELUP),
    ;

    private final Sound sound;
    SoundType(Sound sound) { this.sound = sound; }
    public Sound getSound() {
        return sound;
    }

}
