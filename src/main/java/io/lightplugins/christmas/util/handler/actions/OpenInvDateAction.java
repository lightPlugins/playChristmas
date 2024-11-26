package io.lightplugins.christmas.util.handler.actions;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.secretsanta.LightSecretSanta;
import io.lightplugins.christmas.modules.secretsanta.inventories.constructor.SecretSantaGiftEditInv;
import io.lightplugins.christmas.modules.secretsanta.inventories.constructor.SecretSantaMainInv;
import io.lightplugins.christmas.modules.secretsanta.inventories.constructor.SecretSantaRatingInv;
import io.lightplugins.christmas.modules.secretsanta.inventories.constructor.SecretSantaSuggestionInv;
import io.lightplugins.christmas.util.interfaces.LightAction;
import org.bukkit.entity.Player;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OpenInvDateAction implements LightAction {

    @Override
    public void execute(Player player, String[] actionDataArray) {

        if(actionDataArray.length < 2) {
            LightMaster.instance.getDebugPrinting().configError("Invalid action data for 'open-date-inventory' action");
            LightMaster.instance.getDebugPrinting().configError("Syntax: open-date-inventory;inventory-name;dd:MM:yyyy HH:mm");
            return;
        }

        String[] split = actionDataArray[1].split("@");

        try {

            SimpleDateFormat simpleDateFormatCurrent = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date targetDate = simpleDateFormatCurrent.parse(split[1]);
            String targetFormatedDate = simpleDateFormatCurrent.format(targetDate);


            if(targetDate.after(new Date())) {
                LightMaster.instance.getMessageSender().sendPlayerMessage(
                        LightSecretSanta.messageParams.voteNotStarted()
                                .replace("#date#", targetFormatedDate), player);
                return;
            }

            if(split[0].equalsIgnoreCase("secretsanta-gift-edit")) {
                // SecretSantaGiftEdit
                SecretSantaGiftEditInv secretSantaGiftEditInv = LightMaster.instance.getInventoryManager().generateSecretSantaGiftEditInv(
                        LightSecretSanta.instance.getSecretSantaGiftEditor(),
                        player);
                secretSantaGiftEditInv.openInventory();
                return;
            }

            if(split[0].equalsIgnoreCase("secretsanta-suggestion")) {
                // SecretSantaSuggestion
                SecretSantaSuggestionInv secretSantaSuggestionInv = LightMaster.instance.getInventoryManager().generateSecretSantaSuggestionInv(
                        LightSecretSanta.instance.getSecretSantaSuggestionInv(),
                        player);
                secretSantaSuggestionInv.openInventory();
                return;
            }

            if(split[0].equalsIgnoreCase("secretsanta-rating")) {
                // SecretSantaRating
                SecretSantaRatingInv secretSantaRatingInv = LightMaster.instance.getInventoryManager().generateSecretSantaRatingInv(
                        LightSecretSanta.instance.getSecretSantaRatingInv(),
                        player);
                secretSantaRatingInv.openInventory();

                return;
            }

            if(split[0].equalsIgnoreCase("secretsanta-main")) {
                // SecretSantaMain
                SecretSantaMainInv secretSantaMainInv = LightMaster.instance.getInventoryManager().generateSecretSantaMainInv(
                        LightSecretSanta.instance.getSecretSantaMainInv(),
                        player);
                secretSantaMainInv.openInventory();
            }

        } catch (ParseException e) {
            throw new RuntimeException("Error while parsing the date for action: " + actionDataArray[1], e);
        }




    }

}
