package io.lightplugins.dummy.util;

import io.lightplugins.dummy.Light;
import org.bukkit.Bukkit;

public class DebugPrinting {

    public void print(String message) {
        Bukkit.getConsoleSender().sendMessage(Light.consolePrefix + message);
    }
    public void configError(String message) {
        Bukkit.getConsoleSender().sendMessage(Light.consolePrefix + " [CONFIG-ERROR] " + message);
    }
}
