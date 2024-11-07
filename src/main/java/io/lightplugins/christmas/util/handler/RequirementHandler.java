package io.lightplugins.christmas.util.handler;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.util.handler.requirements.DateRequirement;
import io.lightplugins.christmas.util.interfaces.LightAction;
import io.lightplugins.christmas.util.interfaces.LightRequirement;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RequirementHandler {

    private static final Map<String, LightRequirement> requirements = new HashMap<>();

    static {
        initializeRequirements();
    }

    private final Player player;
    private final String[] requirementDataArray;

    public RequirementHandler(Player player, String requirementData) {
        this.player = player;
        this.requirementDataArray = requirementData.split(";");
    }

    public static void initializeRequirements() {
        requirements.put("date", new DateRequirement());

    }

    public boolean checkRequirements() {
        if (requirementDataArray == null) {
            return true;
        }

        LightRequirement lightRequirement = requirements.get(requirementDataArray[0]);

        if (lightRequirement != null) {
            return lightRequirement.checkRequirement(player, requirementDataArray);
        }

        LightMaster.instance.getDebugPrinting().print("Something went wrong with the requirements.");
        LightMaster.instance.getDebugPrinting().print("-> RequirementHandler -> LightRequirement is null");
        LightMaster.instance.getDebugPrinting().print("Activate fallback system. All requirements are now FALSE.");
        return false;
    }


}
