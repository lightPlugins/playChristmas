package io.lightplugins.christmas.modules.adventcalendar.api.models;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class AdventModel {

    private UUID playerUUID;
    private String playerName;
    private List<String> claimedDates;

}
