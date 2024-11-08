package io.lightplugins.christmas.modules.adventcalendar;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.adventcalendar.api.models.AdventPlayer;
import io.lightplugins.christmas.modules.adventcalendar.commands.OpenCalendarCommand;
import io.lightplugins.christmas.modules.adventcalendar.commands.ReloadCommand;
import io.lightplugins.christmas.modules.adventcalendar.commands.ResetCalendarCommand;
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

import java.io.File;
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
    private final ArrayList<AdventPlayer> adventPlayerData = new ArrayList<>();

    public SettingParams settingParams;
    public MessageParams messageParams;

    private FileManager settings;
    private FileManager language;
    private FileManager adventCalendar;
    private FileManager adventRewards;

    private MultiFileManager playerDataFiles;


    @Override
    public void enable() {
        LightMaster.instance.getDebugPrinting().print(
                "Starting " + this.moduleName + " module...");
        instance = this;
        registerPlaceHolder();
        LightMaster.instance.getDebugPrinting().print(
                "Creating default files for module " + this.moduleName + " module...");
        initFiles();
        settingParams = new SettingParams(this);
        LightMaster.instance.getDebugPrinting().print(
                "Selecting module language for module " + this.moduleName + "...");
        selectLanguage();
        messageParams = new MessageParams(language);
        LightMaster.instance.getDebugPrinting().print(
                "Registering subcommands for module " + this.moduleName + "...");
        initSubCommands();
        registerEvents();
        this.inventoryManager = new InventoryManager();
        this.isModuleEnabled = true;
        LightMaster.instance.getDebugPrinting().print(
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

        if(!adventPlayerData.isEmpty()) {
            adventPlayerData.clear();
        }

        try {
            // load player data multi file manager
            this.playerDataFiles = new MultiFileManager(
                    "plugins/" + LightMaster.instance.getPluginName() + "/" + moduleName + "/storage/player-data/");
        } catch (IOException e) {
            throw new RuntimeException("Error reading player data files for module: " + moduleName, e);
        }

        for(File file : playerDataFiles.getYamlFiles()) {
            adventPlayerData.add(new AdventPlayer(file));
        }
        LightMaster.instance.getDebugPrinting().print(
                "Successfully loaded §e" + adventPlayerData.size() + "§r player data account(s).");
    }

    private void selectLanguage() {
        this.language = LightMaster.instance.selectLanguage(settingParams.getModuleLanguage(), moduleName);
    }

    private void initSubCommands() {
        PluginCommand ecoCommand = Bukkit.getPluginCommand("advent");
        subCommands.add(new OpenCalendarCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new ResetCalendarCommand());
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
