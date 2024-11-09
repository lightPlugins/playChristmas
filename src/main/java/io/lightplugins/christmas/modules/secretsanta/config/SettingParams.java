package io.lightplugins.christmas.modules.secretsanta.config;

import io.lightplugins.christmas.modules.secretsanta.LightSecretSanta;

public class SettingParams {

    private final LightSecretSanta lightSecretSanta;

    public SettingParams(LightSecretSanta lightSecretSanta) {
        this.lightSecretSanta = lightSecretSanta;
    }

    public String getModuleLanguage() {
        return lightSecretSanta.getSettings().getConfig().getString("module-language");
    }
}
