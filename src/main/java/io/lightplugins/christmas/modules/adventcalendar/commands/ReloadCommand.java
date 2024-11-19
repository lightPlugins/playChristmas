package io.lightplugins.christmas.modules.adventcalendar.commands;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.adventcalendar.LightAdventCalendar;
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
        return "Reload the advent calendar";
    }

    @Override
    public String getSyntax() {
        return "/advent reload";
    }

    @Override
    public int maxArgs() {
        return 1;
    }

    @Override
    public String getPermission() {
        return "playchristmas.advent.command.reload";
    }

    @Override
    public TabCompleter registerTabCompleter() {
            return((sender, command, alias, args) -> {

                if (args.length == 1) {
                    return List.of("reload");
                }

                return null;
        });
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) throws ExecutionException, InterruptedException {

        LightAdventCalendar.instance.reload();
        LightMaster.instance.getMessageSender().sendPlayerMessage(
                LightAdventCalendar.instance.getMessageParams().moduleReload()
                        .replace("#module#", "Adventskalender"), player);

        return true;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException {
        return false;
    }
}
