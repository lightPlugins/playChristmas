package io.lightplugins.christmas.modules.secretsanta.commands;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.secretsanta.LightSecretSanta;
import io.lightplugins.christmas.modules.secretsanta.permissions.Permissions;
import io.lightplugins.christmas.util.SubCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReloadCommand extends SubCommand {

    @Override
    public List<String> getName() {
        return List.of("reload");
    }

    @Override
    public String getDescription() {
        return "Reload the config files";
    }

    @Override
    public String getSyntax() {
        return "/wichteln reload";
    }

    @Override
    public int maxArgs() {
        return 1;
    }

    @Override
    public String getPermission() {
        return Permissions.RELOAD_COMMAND.getPerm();
    }

    @Override
    public TabCompleter registerTabCompleter() {
        return (sender, command, alias, args) -> {

            if(args.length == 1) {
                return List.of("reload");
            }
            return null;
        };
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) throws ExecutionException, InterruptedException {
        LightSecretSanta.instance.reload();
        LightMaster.instance.getMessageSender().sendPlayerMessage(LightSecretSanta.messageParams.moduleReload(), player);
        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException {
        LightSecretSanta.instance.reload();
        LightMaster.instance.getDebugPrinting().print("Reloaded SecretSanta config files successfully.");
        return false;
    }
}
