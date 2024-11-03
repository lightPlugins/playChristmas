package io.lightplugins.christmas.modules.secretsanta;

import io.lightplugins.christmas.modules.secretsanta.config.MessageParams;
import io.lightplugins.christmas.modules.secretsanta.config.SettingParams;
import io.lightplugins.christmas.util.interfaces.LightModule;
import io.lightplugins.christmas.util.manager.FileManager;
import lombok.Getter;

@Getter
public class LightSecretSanta implements LightModule {

    private static LightSecretSanta instance;

    public static SettingParams settingParams;
    public static MessageParams messageParams;

    private FileManager settings;
    private FileManager language;

    @Override
    public void enable() {
        instance = this;
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "secret-santa";
    }
}
