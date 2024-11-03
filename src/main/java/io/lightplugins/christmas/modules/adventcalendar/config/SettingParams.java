package io.lightplugins.christmas.modules.adventcalendar.config;

import io.lightplugins.christmas.modules.adventcalendar.LightAdventCalendar;

public class SettingParams {

    private final LightAdventCalendar lightBank;

    public SettingParams(LightAdventCalendar lightBank) {
        this.lightBank = lightBank;
    }

    public String getModuleLanguage() {
        return lightBank.getSettings().getString("module-language");
    }
}
