package io.lightplugins.christmas.modules.adventcalendar.config;

import io.lightplugins.christmas.util.manager.FileManager;
import org.bukkit.configuration.file.FileConfiguration;

public class MessageParams {

    private final FileConfiguration config;

    public MessageParams(FileManager selectedLanguage) {
        this.config = selectedLanguage.getConfig();
    }

    public int version() { return config.getInt("version"); }

    public String prefix() { return config.getString("prefix"); }
    public String noPermission() { return config.getString("noPermission"); }
    public String moduleReload() { return config.getString("moduleReload"); }
    public String reloadAll() { return config.getString("reloadAll"); }
    public String wrongSyntax() { return config.getString("wrongSyntax"); }
    public String resetPlayer() {return config.getString("resetPlayer"); }
    public String resetAll() {return config.getString("resetAll"); }
    public String playerNotFound() {return config.getString("playerNotFound"); }
    public String requirementFail() {return config.getString("requirementFail"); }
    public String alreadyClaimed() {return config.getString("alreadyClaimed"); }
    public String successClaim() {return config.getString("successClaim"); }

}
