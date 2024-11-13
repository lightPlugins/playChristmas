package io.lightplugins.christmas.util.manager;

import io.lightplugins.christmas.modules.adventcalendar.inventories.constructor.AdventCalendarInv;
import io.lightplugins.christmas.modules.secretsanta.inventories.constructor.*;
import io.lightplugins.christmas.util.constructor.InvConstructor;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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

    public AdventCalendarInv generateInventoryFromFileManager(FileManager file, FileManager extraFile, Player player) {

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

        return new AdventCalendarInv(
                invConstructor,
                extraFile.getConfig().getConfigurationSection("rewards"),
                extraFile.getConfig().getConfigurationSection("gui-items"),
                player);

    }

    public SecretSantaMainInv generateSecretSantaMainInv(FileManager file, Player player) {

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

        return new SecretSantaMainInv(
                invConstructor,
                clickContent,
                player);

    }


    public SecretSantaRatingInv generateSecretSantaRatingInv(FileManager file, Player player) {

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

        return new SecretSantaRatingInv(
                invConstructor,
                clickContent,
                player);

    }

    public SecretSantaSuggestionInv generateSecretSantaSuggestionInv(FileManager file, Player player) {

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

        return new SecretSantaSuggestionInv(
                invConstructor,
                clickContent,
                player);

    }

    public SecretSantaGiftEditInv generateSecretSantaGiftEditInv(FileManager file, Player player) {

        FileConfiguration conf = file.getConfig();
        InvConstructor invConstructor = new InvConstructor();

        String guiName = file.getConfig().getName().replace(".yml", "");
        String title = conf.getString("gui-title");
        int row = conf.getInt("rows");
        List<String> pattern = conf.getStringList("pattern");
        ConfigurationSection clickContent = conf.getConfigurationSection("contents");
        ConfigurationSection extrasContent = conf.getConfigurationSection("extras");

        invConstructor.setGuiName(guiName);
        invConstructor.setGuiTitle(title);
        invConstructor.setRows(row);
        invConstructor.setPattern(pattern);
        invConstructor.setClickItemHandlersSection(clickContent);

        return new SecretSantaGiftEditInv(
                invConstructor,
                extrasContent,
                player);

    }

    public SecretSantaGiftEditApplyInv generateSecretSantaGiftEditApplyInv(FileManager file, Player player) {

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

        return new SecretSantaGiftEditApplyInv(
                invConstructor,
                clickContent,
                player);

    }
}
