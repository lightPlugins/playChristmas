package io.lightplugins.christmas;

import com.zaxxer.hikari.HikariDataSource;
import io.lightplugins.christmas.modules.adventcalendar.LightAdventCalendar;
import io.lightplugins.christmas.modules.secretsanta.LightSecretSanta;
import io.lightplugins.christmas.modules.sledhunt.LightSledHunt;
import io.lightplugins.christmas.util.ColorTranslation;
import io.lightplugins.christmas.util.DebugPrinting;
import io.lightplugins.christmas.util.MessageSender;
import io.lightplugins.christmas.util.SubPlaceholder;
import io.lightplugins.christmas.util.database.SQLDatabase;
import io.lightplugins.christmas.util.database.impl.MySQLDatabase;
import io.lightplugins.christmas.util.database.impl.SQLiteDatabase;
import io.lightplugins.christmas.util.database.model.ConnectionProperties;
import io.lightplugins.christmas.util.database.model.DatabaseCredentials;
import io.lightplugins.christmas.util.interfaces.LightModule;
import io.lightplugins.christmas.util.manager.FileManager;
import io.lightplugins.christmas.util.manager.InventoryManager;
import io.lightplugins.christmas.util.manager.MultiFileManager;
import io.lightplugins.christmas.util.manager.PlaceholderManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
public class LightMaster extends JavaPlugin {

    public static LightMaster instance;

    // Modules of the plugin
    private LightAdventCalendar lightAdventCalendar;
    private LightSecretSanta lightSecretSanta;
    private LightSledHunt lightSledHunt;

    private final Map<String, LightModule> modules = new LinkedHashMap<>();
    private final ArrayList<SubPlaceholder> subPlaceholders = new ArrayList<>();

    private MessageSender messageSender;
    public FileManager database;
    public ColorTranslation colorTranslation;
    private DebugPrinting debugPrinting;
    private SQLDatabase pluginDatabase;

    public boolean isPlaceholderAPI = false;

    public HikariDataSource ds;

    public final String consolePrefix = "§r[play§eChristmas§r] §r";
    public final String pluginName = "playChristmas";

    private FileManager settings;
    @Getter
    private final InventoryManager inventoryManager = new InventoryManager();


    public void onLoad() {

        instance = this;
        debugPrinting = new DebugPrinting();
        colorTranslation = new ColorTranslation();
        checkForHooks();
        database = createNewFile("settings.yml", true);

        if(!this.initDatabase()) {
            getDebugPrinting().print("§4Could not connect to the database. Please check your database.yml");
            getDebugPrinting().print(pluginName + "§4 is shutting down... ");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void onEnable() {

        debugPrinting.print("Loading " + pluginName + " modules...");
        messageSender = new MessageSender();

        initModules();
        loadModules();
        registerPlaceHolders();


    }

    public void onDisable() {

        Iterator<LightModule> iterator = this.modules.values().iterator();

        while(iterator.hasNext()) {
            this.unloadModule(iterator.next());
            iterator.remove();
        }

        if(this.pluginDatabase != null) {
            SQLDatabase sqlDatabase = this.pluginDatabase;
            sqlDatabase.close();
            getDebugPrinting().print("§4Database is closed");
        }

        super.onDisable();

    }

    private void loadModules() {
        this.loadModule(lightAdventCalendar, true);
        this.loadModule(lightSecretSanta, true);
        this.loadModule(lightSledHunt, true);
    }

    private void loadModule(LightModule lightModule, boolean enable) {
        if(lightModule.isEnabled()) {
            debugPrinting.print("Module §e" + lightModule.getName() + "§r already loaded.");
            return;
        }
        debugPrinting.print("Module §e" + lightModule.getName() + "§r is" +
                (enable ? "§a activated" : "§c deactivated"));
        if(enable) { lightModule.enable();  }

    }

    private void unloadModule(LightModule lightModule) {
        if(!lightModule.isEnabled()) {
            return;
        }
        lightModule.disable();
        debugPrinting.print("Successfully unloaded module: §e" + lightModule.getName());
    }

    private void initModules() {
        // new instance of each module
        this.lightAdventCalendar = new LightAdventCalendar();
        this.lightSecretSanta = new LightSecretSanta();
        this.lightSledHunt = new LightSledHunt();

        // add modules to the map
        this.modules.put(this.lightAdventCalendar.getName(), this.lightAdventCalendar);
        this.modules.put(this.lightSecretSanta.getName(), this.lightSecretSanta);
        this.modules.put(this.lightSledHunt.getName(), this.lightSledHunt);
    }

    public FileManager selectLanguage(String languageName, String moduleName) {

        return switch (languageName) {
            case "de" -> {
                debugPrinting.print(
                        "Selected language for module " + moduleName + ": " + languageName);
                yield new FileManager(LightMaster.instance, moduleName +
                        "/language/de.yml", true);
            }
            case "pl" -> {
                debugPrinting.print(
                        "Selected language for module " + moduleName + ": " + languageName);
                yield new FileManager(LightMaster.instance, moduleName +
                        "/language/pl.yml", true);
            }
            default -> {
                debugPrinting.print(
                        "Selected language for module " + moduleName + ": " + languageName);
                yield new FileManager(LightMaster.instance, moduleName +
                        "/language/en.yml", true);
            }
        };
    }

    private void checkForHooks() {
        debugPrinting.print("Checks for third party hooks");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getDebugPrinting().print("PlaceholderAPI is present. Hooking into PlaceholderAPI");
            isPlaceholderAPI = true;
        }

    }

    private void registerPlaceHolders() {

        //  this.subPlaceholders.add(new EcoTopPlaceholder());

        if(this.subPlaceholders.isEmpty()) {
            getDebugPrinting().print("No SubPlaceholders found. Skipping Placeholder registration.");
            return;
        }

        PlaceholderManager placeholderManager = new PlaceholderManager(this.subPlaceholders);
        placeholderManager.register();
    }

    private boolean initDatabase() {
        try {
            String databaseType = database.getConfig().getString("storage.type");
            ConnectionProperties connectionProperties = ConnectionProperties.fromConfig(database.getConfig());

            if ("sqlite".equalsIgnoreCase(databaseType)) {
                this.pluginDatabase = new SQLiteDatabase(this, connectionProperties);
                getDebugPrinting().print("Using SQLite (local) database.");
            } else if ("mysql".equalsIgnoreCase(databaseType)) {
                DatabaseCredentials credentials = DatabaseCredentials.fromConfig(database.getConfig());
                this.pluginDatabase = new MySQLDatabase(this, credentials, connectionProperties);
                getDebugPrinting().print("Using MySQL (remote*) database.");
            } else {
                this.getLogger().warning(String.format("Error! Unknown database type: %s. Disabling plugin.", databaseType));
                this.getServer().getPluginManager().disablePlugin(this);
                return false;
            }

            this.pluginDatabase.connect();
        } catch (Exception e) {
            this.getLogger().warning("Could not maintain Database Connection. Disabling plugin.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @NotNull
    public List<FileConfiguration> getFileConfigs(String path) throws IOException {

        final List<FileConfiguration> fileConfigs = new ArrayList<>();

        getMultiFiles(path).forEach(singleFile -> {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(singleFile);
            fileConfigs.add(cfg);
        });

        return fileConfigs;
    }

    @NotNull
    public List<File> getMultiFiles(String path) throws IOException {
        // Add files from the MultiFileManager to the existing files list
        return new ArrayList<>(readMultiFiles(path).getYamlFiles());
    }

    @NotNull
    public MultiFileManager readMultiFiles(String directoryPath) throws IOException {
        return new MultiFileManager(directoryPath);
    }

    @NotNull
    public FileManager createNewFile(String fileName, boolean loadDefaultsOneReload) {
        return new FileManager(this, fileName, loadDefaultsOneReload);
    }

    public SQLDatabase getConnection() {
        return this.pluginDatabase;
    }
}