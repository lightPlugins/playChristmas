package io.lightplugins.christmas.modules.adventcalendar.commands;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.PacketType;
import io.lightplugins.christmas.util.SubCommand;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class OpenAdventCalendarCommand extends SubCommand {

    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

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
        try {
            // Create a new PacketContainer for the enchanting inventory
            PacketContainer openWindowPacket = protocolManager.createPacket(PacketType.Play.Server.OPEN_WINDOW);
            openWindowPacket.getIntegers().write(0, 1); // Window ID
            openWindowPacket.getStrings().write(0, "minecraft:enchanting_table");
            openWindowPacket.getChatComponents().write(0, WrappedChatComponent.fromText("Custom Enchanting"));

            // Send the packet to the player
            protocolManager.sendServerPacket(player, openWindowPacket);

            // Set the item to be enchanted
            ItemStack itemToEnchant = new ItemStack(Material.DIAMOND_SWORD);
            player.getOpenInventory().getTopInventory().setItem(0, itemToEnchant);

            // Create enchantment offers
            PacketContainer enchantmentPacket = protocolManager.createPacket(PacketType.Play.Server.WINDOW_DATA);
            enchantmentPacket.getIntegers().write(0, 1); // Window ID
            enchantmentPacket.getIntegers().write(1, 0); // Enchantment slot
            enchantmentPacket.getIntegers().write(2, 1); // Enchantment level
            enchantmentPacket.getIntegers().write(3, 30); // Enchantment cost

            // Send the enchantment offers to the player
            protocolManager.sendServerPacket(player, enchantmentPacket);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean performAsConsole(ConsoleCommandSender sender, String[] args) throws ExecutionException, InterruptedException {
        return false;
    }
}