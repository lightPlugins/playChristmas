package io.lightplugins.christmas.modules.adventcalendar.commands;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.adventcalendar.LightAdventCalendar;
import io.lightplugins.christmas.modules.adventcalendar.api.models.AdventPlayer;
import io.lightplugins.christmas.util.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ResetCalendarCommand extends SubCommand {
    @Override
    public List<String> getName() {
        return List.of("reset");
    }

    @Override
    public String getDescription() {
        return "Reset the advent calendar for a specific player";
    }

    @Override
    public String getSyntax() {
        return "/advent reset <playername>";
    }

    @Override
    public int maxArgs() {
        return 2;
    }

    @Override
    public String getPermission() {
        return "playchristmas.advent.command.reset";
    }

    @Override
    public TabCompleter registerTabCompleter() {

        return (sender, command, alias, args) -> {

            if (args.length == 1) {
                return getName();
            }
            if (args.length == 2) {
                return List.of("<playername>", "all");
            }
            return null;
        };
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) throws ExecutionException, InterruptedException {

        boolean isAll = args[1].equalsIgnoreCase("all");

        OfflinePlayer target = Bukkit.getServer().getPlayer(args[1]);

        if(target == null &! isAll) {
            LightMaster.instance.getMessageSender().sendPlayerMessage(
                    LightAdventCalendar.instance.getMessageParams().playerNotFound()
                            .replace("#player#", args[1]), player);
            return false;
        }

        if(isAll) {
            for(AdventPlayer adventPlayer : LightAdventCalendar.instance.getAdventPlayerData()) {
                adventPlayer.resetPlayer();
            }
            LightMaster.instance.getMessageSender().sendPlayerMessage(
                    LightAdventCalendar.instance.getMessageParams().resetAll(), player);
            return true;
        }

        String uuid = target.getUniqueId().toString();

        for(AdventPlayer adventPlayer : LightAdventCalendar.instance.getAdventPlayerData()) {

            if(adventPlayer.getPlayerUUID().equalsIgnoreCase(uuid)) {
                adventPlayer.resetPlayer();
                LightMaster.instance.getMessageSender().sendPlayerMessage(
                        LightAdventCalendar.instance.getMessageParams().resetPlayer()
                                .replace("#player#", target.getName()), player);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException {
        return false;
    }
}
