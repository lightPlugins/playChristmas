package io.lightplugins.christmas.util.handler.requirements;

import io.lightplugins.christmas.util.interfaces.LightRequirement;
import org.bukkit.entity.Player;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateRequirement implements LightRequirement {

    @Override
    public boolean checkRequirement(Player player, String[] requirementDataArray) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date date = simpleDateFormat.parse(requirementDataArray[1]);
            return date.before(new Date());

        } catch (ParseException e) {
            throw new RuntimeException("Error while parsing the date for requirement: " + requirementDataArray[1], e);
        }
    }
}
