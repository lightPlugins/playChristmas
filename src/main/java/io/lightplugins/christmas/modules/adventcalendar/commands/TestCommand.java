package io.lightplugins.christmas.modules.adventcalendar.commands;

import io.lightplugins.christmas.util.SubCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TestCommand extends SubCommand {

    @Override
    public List<String> getName() {
        return List.of("day", "tag");
    }

    @Override
    public String getDescription() {
        return "Das und das";
    }

    @Override
    public String getSyntax() {
        return "/advent day";
    }

    @Override
    public int maxArgs() {
        return 1;
    }

    @Override
    public String getPermission() {
        return "test.test";
    }

    @Override
    public TabCompleter registerTabCompleter() {

        return (sender, command, alias, args) -> {

            if(args.length == 1) {

                return List.of("day", "tag");
            }

            return null;
        };
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) throws ExecutionException, InterruptedException {



        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException {



        return false;
    }
}
