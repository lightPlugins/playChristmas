package io.lightplugins.christmas.modules.sledhunt;

import io.lightplugins.christmas.util.interfaces.LightModule;
import lombok.Getter;

@Getter
public class LightSledHunt implements LightModule {

    private static LightSledHunt instance;

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
        return "sled-hunt";
    }
}
