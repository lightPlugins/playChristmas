package io.lightplugins.christmas.modules.adventcalendar.config;

import io.lightplugins.christmas.modules.adventcalendar.LightAdventCalendar;

public class SettingParams {

    private final LightAdventCalendar lightAdventCalendar;

    public SettingParams(LightAdventCalendar lightBank) {
        this.lightAdventCalendar = lightBank;
    }

    public String getModuleLanguage() {
        return lightAdventCalendar.getSettings().getConfig().getString("module-language");
    }
}
