package io.lightplugins.christmas.modules.adventcalendar.api.manager;

import io.lightplugins.christmas.util.handler.ActionHandler;
import io.lightplugins.christmas.util.handler.RequirementHandler;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AdventManager {

    /**
     * Reads and processes extra requirements from the extraRequirementsSection list.
     * Iterates through each requirement, replaces placeholders with their corresponding values,
     * and adds the processed requirement to the extraRequirementHandlers list.
     */
    public List<RequirementHandler> getDayRequirements(List<String> requirementList, Player player) {

        // Iterates through each requirement List and create RequirementHandlers and return a list oft them
        return requirementList.stream()
                .map(requirement -> new RequirementHandler(player, requirement))
                .collect(Collectors.toList());


    }

    /**
     * Reads and processes extra actions from the extraActionsSection list.
     * Iterates through each action, replaces placeholders with their corresponding values,
     * and adds the processed action to the extraActionHandlers list.
     */
    public List<ActionHandler> getDayActions(List<String> actions, Player player) {

        return actions.stream()
                .map(requirement -> new ActionHandler(player, requirement))
                .collect(Collectors.toList());
    }
}
