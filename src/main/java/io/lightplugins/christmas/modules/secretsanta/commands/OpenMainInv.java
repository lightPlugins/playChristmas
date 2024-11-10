package io.lightplugins.christmas.modules.secretsanta.commands;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.secretsanta.LightSecretSanta;
import io.lightplugins.christmas.modules.secretsanta.inventories.constructor.SecretSantaInv;
import io.lightplugins.christmas.util.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class OpenMainInv extends SubCommand {
    @Override
    public List<String> getName() {
        return List.of("open");
    }

    @Override
    public String getDescription() {
        return "Open the main Secret Santa GUI";
    }

    @Override
    public String getSyntax() {
        return "/wichteln open <playername>";
    }

    @Override
    public int maxArgs() {
        return 1;
    }

    @Override
    public String getPermission() {
        return "playnayz.christmas.secretsanta.command.open";
    }

    @Override
    public TabCompleter registerTabCompleter() {
        return (sender, command, alias, args) -> {

            if(args.length == 1) {
                return List.of("open");
            }

            if(args.length == 2) {
                return Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
            }

            return null;
        };
    }

    @Override
    public boolean performAsPlayer(Player player, String[] args) throws ExecutionException, InterruptedException {

        SecretSantaInv secretSantaInv = LightSecretSanta.instance.getInventoryManager().generateFromSingleFile(
                LightSecretSanta.instance.getSecretSantaMainInv(),
                player
        );

        secretSantaInv.openInventory();

        return false;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException {

        OfflinePlayer target = Bukkit.getServer().getPlayer(args[1]);

        if(target == null) {
            sender.sendMessage("Player not found");
            LightMaster.instance.getDebugPrinting().print("Target player not found: " + args[1]);
            return false;
        }

        if(target.isOnline()) {
            SecretSantaInv secretSantaInv = LightSecretSanta.instance.getInventoryManager().generateFromSingleFile(
                    LightSecretSanta.instance.getSecretSantaMainInv(),
                    target.getPlayer()
            );

            secretSantaInv.openInventory();
        }

        return false;
    }
}
