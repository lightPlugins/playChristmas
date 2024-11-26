package io.lightplugins.christmas.modules.secretsanta.config;

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
    public String addPartnerSuccess() { return config.getString("addPartnerSuccess"); }
    public String addPartnerNotFound() { return config.getString("addPartnerNotFound"); }
    public String wrongSyntax() { return config.getString("wrongSyntax"); }

    // Titles
    public String addPartnerTitleUpper() { return config.getString("titles.addPartner.upper"); }
    public String addPartnerTitleLower() { return config.getString("titles.addPartner.lower"); }

    // Messages
    public String successVoted() { return config.getString("successVoted"); }
    public String alreadyVoted() { return config.getString("alreadyVoted"); }
    public String voteNotStarted() { return config.getString("voteNotStarted"); }


}
