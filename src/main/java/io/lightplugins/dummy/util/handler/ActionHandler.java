package io.lightplugins.dummy.util.handler;

import io.lightplugins.dummy.modules.bank.inventories.actions.InvCloseAction;
import io.lightplugins.dummy.modules.bank.inventories.actions.MessageAction;
import io.lightplugins.dummy.util.interfaces.LightAction;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * This class handles actions for a player based on provided action data.
 * Author: lightPlugins
 * Copyright: © 2023 [lightStudios]. All rights reserved.
 * You may not use, distribute, or modify this code without explicit permission.
 */

public class ActionHandler {

    private static final Map<String, LightAction> actions = new HashMap<>();

    static {
        initializeActions();
    }

    private final Player player;
    private final String[] actionDataArray;

    public ActionHandler(Player player, String actionData) {
        this.player = player;
        this.actionDataArray = actionData.split(";");
    }

    private static void initializeActions() {
        actions.put("message", new MessageAction());
        actions.put("close", new InvCloseAction());
    }

    public void handleAction() {
        if (actionDataArray == null) {
            return;
        }

        LightAction lightAction = actions.get(actionDataArray[0]);

        if (lightAction != null) {
            lightAction.execute(player, actionDataArray);
        }
    }
}