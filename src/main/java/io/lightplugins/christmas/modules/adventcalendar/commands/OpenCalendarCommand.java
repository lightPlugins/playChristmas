package io.lightplugins.christmas.modules.adventcalendar.commands;

import io.lightplugins.christmas.modules.adventcalendar.LightAdventCalendar;
import io.lightplugins.christmas.modules.adventcalendar.inventories.constructor.AdventCalendarInv;
import io.lightplugins.christmas.util.SubCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class OpenCalendarCommand extends SubCommand {

    @Override
    public List<String> getName() {
        return List.of("open");
    }

    @Override
    public String getDescription() {
        return "Open the advent calendar";
    }

    @Override
    public String getSyntax() {
        return "/advent open";
    }

    @Override
    public int maxArgs() {
        return 1;
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public TabCompleter registerTabCompleter() {
        return ((sender, command, alias, args) -> {
            if (args.length == 1) {
                return List.of("open");
            }
            return null;
        });
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) throws ExecutionException, InterruptedException {

        AdventCalendarInv adventCalendarInv = LightAdventCalendar.instance.getInventoryManager().generateInventoryFromFileManager(
                LightAdventCalendar.instance.getAdventCalendar(),
                LightAdventCalendar.instance.getAdventRewards(),
                player);

        adventCalendarInv.openInventory();

        return true;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException {
        return false;
    }
}