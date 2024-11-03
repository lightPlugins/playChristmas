package io.lightplugins.christmas.modules.adventcalendar;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.adventcalendar.commands.DummyCommand;
import io.lightplugins.christmas.modules.adventcalendar.config.MessageParams;
import io.lightplugins.christmas.modules.adventcalendar.config.SettingParams;
import io.lightplugins.christmas.util.SubCommand;
import io.lightplugins.christmas.util.interfaces.LightModule;
import io.lightplugins.christmas.util.manager.CommandManager;
import io.lightplugins.christmas.util.manager.FileManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

@Getter
public class LightAdventCalendar implements LightModule {

    public static LightAdventCalendar instance;
    public boolean isModuleEnabled = false;

    public final String moduleName = "advent-calendar";
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public SettingParams settingParams;
    public MessageParams messageParams;

    private FileManager settings;
    private FileManager language;
    private FileManager dummyInventory;


    @Override
    public void enable() {
        LightMaster.instance.getDebugPrinting().print(LightMaster.instance.getConsolePrefix() +
                "Starting " + this.moduleName + " module...");
        instance = this;
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
        this.isModuleEnabled = true;
        LightMaster.instance.getDebugPrinting().print(LightMaster.instance.getConsolePrefix() +
                "Successfully started module " + this.moduleName + "!");

    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() { enable(); }

    @Override
    public boolean isEnabled() {
        return isModuleEnabled;
    }

    @Override
    public String getName() {
        return moduleName;
    }

    public FileConfiguration getSettings() { return this.settings.getConfig(); }

    public FileConfiguration getLanguage() { return this.language.getConfig(); }

    private void initFiles() {
        this.settings = new FileManager(
                LightMaster.instance, moduleName + "/settings.yml", true);
        this.dummyInventory = new FileManager(
                LightMaster.instance, moduleName + "/inventories/_example.yml", false);
    }

    private void selectLanguage() {
        this.language = LightMaster.instance.selectLanguage(settingParams.getModuleLanguage(), moduleName);
    }

    private void initSubCommands() {
        PluginCommand ecoCommand = Bukkit.getPluginCommand("lightdummy");
        subCommands.add(new DummyCommand());
        new CommandManager(ecoCommand, subCommands);
    }
}
