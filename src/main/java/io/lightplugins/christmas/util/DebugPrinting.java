package io.lightplugins.christmas.util;

import io.lightplugins.christmas.LightMaster;
import org.bukkit.Bukkit;

public class DebugPrinting {

    public void print(String message) {
        Bukkit.getConsoleSender().sendMessage(LightMaster.instance.getConsolePrefix() + message);
    }
    public void configError(String message) {
        Bukkit.getConsoleSender().sendMessage(LightMaster.instance.getConsolePrefix() + " [CONFIG-ERROR] " + message);
    }
}
