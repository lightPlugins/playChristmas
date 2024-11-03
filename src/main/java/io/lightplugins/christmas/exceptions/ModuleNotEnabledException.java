package io.lightplugins.christmas.exceptions;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.util.interfaces.LightModule;

public class ModuleNotEnabledException extends Exception {

    public ModuleNotEnabledException(LightModule module) {
        super(LightMaster.instance.getConsolePrefix() + "The Module §e" + module.getName() + "§r is not enabled");
    }
}
