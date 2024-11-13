package io.lightplugins.christmas.util.handler;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.util.handler.actions.*;
import io.lightplugins.christmas.util.interfaces.LightAction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        actions.put("send-message", new MessageAction());
        actions.put("close-inventory", new InvCloseAction());
        actions.put("give-item", new GiveItemAction());
        actions.put("player-cmd", new PlayerCmdAction());
        actions.put("console-cmd", new ConsoleCmdAction());
        actions.put("open-inventory", new InvOpenAction());
    }

    public String[] getActions() {
        return actionDataArray;
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

    public List<String> getRewardNames() {
        List<String> rewardNames = new ArrayList<>();

        switch (actionDataArray[0]) {
            case "give-item" -> rewardNames.add(rewardItem());
            case "dummy" -> rewardNames.add("dummy");
        }

        return rewardNames;
    }

    private String rewardItem() {
        if (actionDataArray.length < 2) {
            return "Invalid item data";
        }
        String itemName = actionDataArray[1];
        String[] itemNameArray = itemName.split(" ");
        itemName = itemName.substring(0, 1).toUpperCase()
                + itemName.substring(1).toLowerCase().split(" ")[0];

        return "<#ffdc73>" + itemNameArray[1] + "<gray> x <#ffdc73>" + itemName;
    }
}