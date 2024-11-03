package io.lightplugins.christmas.util.handler;

import io.lightplugins.christmas.LightMaster;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequirementHandler {

    private String requirementType;
    private String requirementData;
    private List<ActionHandler> failActions;

    public RequirementHandler(
            String requirementType,
            String requirementData,
            List<ActionHandler> failActions) {

        this.requirementType = requirementType;
        this.requirementData = requirementData;
        this.failActions = failActions;
    }

    public boolean checkRequirements() {

        if(failActions == null) {
            LightMaster.instance.getDebugPrinting().print("No fail actions found for requirement: "
                    + requirementType + " -> with data: " + requirementData);
            return false;
        }

        if(!handleRequirement()) {
            for(ActionHandler actionHandler : failActions) {
                actionHandler.handleAction();
            }
            return true;
        }
        return false;
    }


    private Boolean handleRequirement() {

        return switch (requirementType) {
            case "permission" -> checkPermission();
            case "money" -> checkMoney();
            case "item" -> checkItem();
            case "level" -> checkLevel();
            case "experience" -> checkExperience();
            case "custom" -> checkCustom();
            default -> false;
        };
    }

    private Boolean checkPermission() {
        return true;
    }

    private Boolean checkMoney() {
        return true;
    }

    private Boolean checkItem() {
        return true;
    }

    private Boolean checkLevel() {
        return true;
    }

    private Boolean checkExperience() {
        return true;
    }

    private Boolean checkCustom() {
        return true;
    }

}
