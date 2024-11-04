package io.lightplugins.christmas.util.manager;

import io.lightplugins.christmas.modules.adventcalendar.inventories.constructor.AdventCalendarInv;
import io.lightplugins.christmas.util.constructor.InvConstructor;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

@Getter
public class InventoryManager {

    /**
     * Generates an inventory from a given file and player.
     * Including click actions and item handlers.
     *
     * @param file the file containing the inventory configuration
     * @param player the player for whom the inventory is being generated
     * @return an InvCreator instance for the generated inventory
     */

    public AdventCalendarInv generateInventoryFromFileManager(FileManager file, Player player) {

        FileConfiguration conf = file.getConfig();
        InvConstructor invConstructor = new InvConstructor();

        String guiName = file.getConfig().getName().replace(".yml", "");
        String title = conf.getString("gui-title");
        int row = conf.getInt("rows");
        List<String> pattern = conf.getStringList("pattern");
        ConfigurationSection clickContent = conf.getConfigurationSection("contents");

        invConstructor.setGuiName(guiName);
        invConstructor.setGuiTitle(title);
        invConstructor.setRows(row);
        invConstructor.setPattern(pattern);
        invConstructor.setClickItemHandlersSection(clickContent);

        return new AdventCalendarInv(invConstructor, player);

    }

    public AdventCalendarInv generateInventoryFromFile(File file, Player player) {

        FileConfiguration conf = YamlConfiguration.loadConfiguration(file);
        InvConstructor invConstructor = new InvConstructor();

        String guiName = file.getName().replace(".yml", "");
        String title = conf.getString("gui-title");
        int row = conf.getInt("rows");
        List<String> pattern = conf.getStringList("pattern");
        ConfigurationSection clickContent = conf.getConfigurationSection("contents");

        invConstructor.setGuiName(guiName);
        invConstructor.setGuiTitle(title);
        invConstructor.setRows(row);
        invConstructor.setPattern(pattern);
        invConstructor.setClickItemHandlersSection(clickContent);

        return new AdventCalendarInv(invConstructor, player);

    }
}
