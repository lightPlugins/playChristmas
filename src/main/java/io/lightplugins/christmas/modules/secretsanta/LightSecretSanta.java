package io.lightplugins.christmas.modules.secretsanta;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.secretsanta.commands.OpenMainInv;
import io.lightplugins.christmas.modules.secretsanta.config.MessageParams;
import io.lightplugins.christmas.modules.secretsanta.config.SettingParams;
import io.lightplugins.christmas.util.SubCommand;
import io.lightplugins.christmas.util.interfaces.LightModule;
import io.lightplugins.christmas.util.manager.CommandManager;
import io.lightplugins.christmas.util.manager.FileManager;
import io.lightplugins.christmas.util.manager.InventoryManager;
import lombok.Getter;
import org.bukkit.command.PluginCommand;

import java.util.ArrayList;

@Getter
public class LightSecretSanta implements LightModule {

    public static LightSecretSanta instance;
    private boolean isModuleEnabled = false;
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public static SettingParams settingParams;
    public static MessageParams messageParams;

    public InventoryManager inventoryManager;

    private FileManager settings;
    private FileManager language;

    private FileManager secretSantaMainInv;
    private FileManager secretSantaGiftInv;
    private FileManager secretSantaSuggestionInv;
    private FileManager secretSantaRatingInv;

    @Override
    public void enable() {
        LightMaster.instance.getDebugPrinting().print(
                "Starting " + getName() + " module...");
        instance = this;
        LightMaster.instance.getDebugPrinting().print(
                "Creating default files for module " + getName() + " module...");
        initFiles();
        settingParams = new SettingParams(this);
        LightMaster.instance.getDebugPrinting().print(
                "Selecting module language for module " + getName() + "...");
        selectLanguage();
        messageParams = new MessageParams(language);
        LightMaster.instance.getDebugPrinting().print(
                "Registering subcommands for module " + getName() + "...");
        initSubCommands();
        registerEvents();
        this.inventoryManager = new InventoryManager();
        this.isModuleEnabled = true;
        LightMaster.instance.getDebugPrinting().print(
                "Successfully started module " + getName() + "!");
    }

    @Override
    public void disable() { }

    @Override
    public void reload() { initFiles(); }

    @Override
    public boolean isEnabled() {
        return isModuleEnabled;
    }

    @Override
    public String getName() {
        return "secret-santa";
    }

    private void selectLanguage() {
        this.language = LightMaster.instance.selectLanguage(settingParams.getModuleLanguage(), getName());
    }

    private void initFiles() {

        this.settings = new FileManager(
                LightMaster.instance, getName() + "/settings.yml", true);

        this.secretSantaMainInv = new FileManager(
                LightMaster.instance, getName() + "/inventories/secretsanta-main.yml", false);
        this.secretSantaGiftInv = new FileManager(
                LightMaster.instance, getName() + "/inventories/secretsanta-gift.yml", false);
        this.secretSantaSuggestionInv = new FileManager(
                LightMaster.instance, getName() + "/inventories/secretsanta-suggestion.yml", false);
        this.secretSantaRatingInv = new FileManager(
                LightMaster.instance, getName() + "/inventories/secretsanta-rating.yml", false);

    }

    private void initSubCommands() {
        PluginCommand pluginCommand = LightMaster.instance.getCommand("secretsanta");
        subCommands.add(new OpenMainInv());
        new CommandManager(pluginCommand, subCommands);
    }

    private void registerEvents() {

    }

}
