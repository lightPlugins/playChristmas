package io.lightplugins.christmas.util.interfaces;

public interface LightModule {

    void enable();

    void disable();

    void reload();

    boolean isEnabled();

    String getName();
}
