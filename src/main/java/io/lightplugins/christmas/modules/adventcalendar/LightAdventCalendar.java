package io.lightplugins.christmas.modules.adventcalendar;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.adventcalendar.commands.OpenAdventCalendarCommand;
import io.lightplugins.christmas.modules.adventcalendar.config.MessageParams;
import io.lightplugins.christmas.modules.adventcalendar.config.SettingParams;
import io.lightplugins.christmas.modules.adventcalendar.listener.OnPlayerJoin;
import io.lightplugins.christmas.util.SubCommand;
import io.lightplugins.christmas.util.SubPlaceholder;
import io.lightplugins.christmas.util.interfaces.LightModule;
import io.lightplugins.christmas.util.manager.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;

import java.io.IOException;
import java.util.ArrayList;

@Getter
public class LightAdventCalendar implements LightModule {

    public static LightAdventCalendar instance;
    public InventoryManager inventoryManager;
    public boolean isModuleEnabled = false;

    public final String moduleName = "advent-calendar";
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();
    private final ArrayList<SubPlaceholder> subPlaceholders = new ArrayList<>();

    public SettingParams settingParams;
    public MessageParams messageParams;

    private FileManager settings;
    private FileManager language;
    private FileManager adventCalendar;
    private FileManager adventRewards;

    private MultiFileManager playerDataFiles;


    @Override
    public void enable() {
        LightMaster.instance.getDebugPrinting().print(LightMaster.instance.getConsolePrefix() +
                "Starting " + this.moduleName + " module...");
        instance = this;
        registerPlaceHolder();
        LightMaster.instance.getDebugPrinting().print(LightMaster.instance.getConsolePrefix() +
                "Creating default files for module " + this.moduleName + " module...");
        initFiles();
        settingParams = new SettingParams(this);
        LightMaster.instance.getDebugPrinting().print(LightMaster.instance.getConsolePrefix() +
                "Selecting module language for module " + this.moduleName + "...");
        selectLanguage();
        messageParams = new MessageParams(language);
        LightMaster.instance.getDebugPrinting().print(LightMaster.instance.getConsolePrefix() +
                "Registering subcommands for module " + this.moduleName + "...");
        initSubCommands();
        registerEvents();
        this.inventoryManager = new InventoryManager();
        this.isModuleEnabled = true;
        LightMaster.instance.getDebugPrinting().print(LightMaster.instance.getConsolePrefix() +
                "Successfully started module " + this.moduleName + "!");

    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {
        initFiles();
    }

    @Override
    public boolean isEnabled() {
        return isModuleEnabled;
    }

    @Override
    public String getName() {
        return moduleName;
    }

    private void initFiles() {
        this.settings = new FileManager(
                LightMaster.instance, moduleName + "/settings.yml", true);
        this.adventCalendar = new FileManager(
                LightMaster.instance, moduleName + "/inventories/advent-calendar.yml", false);
        this.adventRewards = new FileManager(
                LightMaster.instance, moduleName + "/rewards/advent-rewards.yml", false);

        try {
            // load player data multi file manager
            this.playerDataFiles = new MultiFileManager(
                    "plugins/lightInventory/" + moduleName + "/storage/player-data/");
        }catch (IOException e) {
            throw new RuntimeException("Error reading player data files for module: " + moduleName, e);
        }
    }

    private void selectLanguage() {
        this.language = LightMaster.instance.selectLanguage(settingParams.getModuleLanguage(), moduleName);
    }

    private void initSubCommands() {
        PluginCommand ecoCommand = Bukkit.getPluginCommand("advent");
        subCommands.add(new OpenAdventCalendarCommand());
        new CommandManager(ecoCommand, subCommands);
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new OnPlayerJoin(), LightMaster.instance);
    }

    private void registerPlaceHolder() {
        // subPlaceholders.add(new TestPlaceholder());
        new PlaceholderManager(subPlaceholders);

    }
}
