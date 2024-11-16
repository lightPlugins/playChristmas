package io.lightplugins.christmas.modules.secretsanta.permissions;

import org.bukkit.Sound;

public enum Permissions {


    /*
        Admin Command Perissions
     */

    RELOAD_COMMAND("playchristmas.secretsanta.command.reload"),
    ;

    private final String perm;
    Permissions(String perm) { this.perm = perm; }
    public String getPerm() {
        return perm;
    }

}
