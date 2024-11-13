package io.lightplugins.christmas.util.handler.actions;

import io.lightplugins.christmas.LightMaster;
import io.lightplugins.christmas.modules.secretsanta.LightSecretSanta;
import io.lightplugins.christmas.modules.secretsanta.inventories.constructor.*;
import io.lightplugins.christmas.util.interfaces.LightAction;
import org.bukkit.entity.Player;

public class InvOpenAction implements LightAction {
    @Override
    public void execute(Player player, String[] actionDataArray) {

        if(actionDataArray[1].equalsIgnoreCase("secretsanta-gift-edit")) {
            // SecretSantaGiftEdit
            SecretSantaGiftEditInv secretSantaGiftEditInv = LightMaster.instance.getInventoryManager().generateSecretSantaGiftEditInv(
                    LightSecretSanta.instance.getSecretsantaGiftEditor(),
                    player);
            secretSantaGiftEditInv.openInventory();
            return;
        }

        if(actionDataArray[1].equalsIgnoreCase("secretsanta-suggestion")) {
            // SecretSantaSuggestion
            SecretSantaSuggestionInv secretSantaSuggestionInv = LightMaster.instance.getInventoryManager().generateSecretSantaSuggestionInv(
                    LightSecretSanta.instance.getSecretSantaSuggestionInv(),
                    player);
            secretSantaSuggestionInv.openInventory();
            return;
        }

        if(actionDataArray[1].equalsIgnoreCase("secretsanta-rating")) {
            // SecretSantaRating
            SecretSantaRatingInv secretSantaRatingInv = LightMaster.instance.getInventoryManager().generateSecretSantaRatingInv(
                    LightSecretSanta.instance.getSecretSantaRatingInv(),
                    player);
            secretSantaRatingInv.openInventory();

            return;
        }

        if(actionDataArray[1].equalsIgnoreCase("secretsanta-main")) {
            // SecretSantaMain
            SecretSantaMainInv secretSantaMainInv = LightMaster.instance.getInventoryManager().generateSecretSantaMainInv(
                    LightSecretSanta.instance.getSecretSantaMainInv(),
                    player);
            secretSantaMainInv.openInventory();
            return;
        }
    }
}
