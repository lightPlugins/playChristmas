package io.lightplugins.christmas.modules.adventcalendar.models;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RewardModel {

    private final String id;
    private final String data;

    private final List<Date> dateChecker = new ArrayList<>();


    public RewardModel(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public void readRewards() {

        switch (id) {
            case "give-item":
                // read reward actions

                break;
            default:
                break;
        }
    }




}
