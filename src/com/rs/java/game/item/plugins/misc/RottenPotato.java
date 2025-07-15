package com.rs.java.game.item.plugins.misc;

import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemPlugin;
import com.rs.java.game.player.Player;

public class RottenPotato extends ItemPlugin {

    @Override
    public Object[] getKeys() {
        return new Object[]{5733};
    }

    @Override
    public boolean processItem(Player player, Item item, int slotId, String option) {
        switch (option) {
            case "eat":
                player.getDialogueManager().startDialogue("Potato_Op1", item);
                return true;
            case "heal":
                player.heal(player.getMaxHitpoints(), true, true);
                return true;
            case "cm-tool":
                player.getDialogueManager().startDialogue("Potato_CMTool", item);
                return true;
            case "commands":
                player.getDialogueManager().startDialogue("Potato_Commands", item);
                return true;
            case "drop":
                player.getInventory().deleteItem(item);
                player.getPackets().sendGameMessage("Too late! It's already gone.", true);
                return true;
            case "examine":
                player.getPackets().sendGameMessage("Yuk!", true);
                return true;
        }
        return false;
    }
}
