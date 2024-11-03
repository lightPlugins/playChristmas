package io.lightplugins.dummy.modules.bank;

import io.lightplugins.dummy.Light;
import io.lightplugins.dummy.modules.bank.commands.DummyCommand;
import io.lightplugins.dummy.modules.bank.config.MessageParams;
import io.lightplugins.dummy.modules.bank.config.SettingParams;
import io.lightplugins.dummy.util.SubCommand;
import io.lightplugins.dummy.util.interfaces.LightModule;
import io.lightplugins.dummy.util.manager.CommandManager;
import io.lightplugins.dummy.util.manager.FileManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class LightBank implements LightModule {

    public static LightBank instance;
    public boolean isModuleEnabled = false;

    public final String moduleName = "bank";
    public final String adminPerm = "lighteconomy." + moduleName + ".admin";
    public final String tablePrefix = "lightbank_";
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    @Getter
    public static SettingParams settingParams;
    @Getter
    public static MessageParams messageParams;

    private FileManager settings;
    private FileManager language;
    @Getter
    private FileManager dummyInventory;


    @Override
    public void enable() {
        Light.getDebugPrinting().print(Light.consolePrefix +
                "Starting " + this.moduleName + " module...");
        instance = this;
        Light.getDebugPrinting().print(Light.consolePrefix +
                "Creating default files for module " + this.moduleName + " module...");
        initFiles();
        settingParams = new SettingParams(this);
        Light.getDebugPrinting().print(Light.consolePrefix +
                "Selecting module language for module " + this.moduleName + "...");
        selectLanguage();
        messageParams = new MessageParams(language);
        Light.getDebugPrinting().print(Light.consolePrefix +
                "Registering subcommands for module " + this.moduleName + "...");
        initSubCommands();
        this.isModuleEnabled = true;
        Light.getDebugPrinting().print(Light.consolePrefix +
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
                Light.instance, moduleName + "/settings.yml", true);
        this.dummyInventory = new FileManager(
                Light.instance, moduleName + "/inventories/_example.yml", false);
    }

    private void selectLanguage() {
        this.language = Light.instance.selectLanguage(settingParams.getModuleLanguage(), moduleName);
    }

    private void initSubCommands() {
        PluginCommand ecoCommand = Bukkit.getPluginCommand("lightdummy");
        subCommands.add(new DummyCommand());
        new CommandManager(ecoCommand, subCommands);
    }
}
