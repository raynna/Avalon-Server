package com.rs.java.game.item.plugins.weapons;

import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;

public class AbyssalVineWhip extends ItemPlugin {

    private final int ATTACK_LEVEL_REQUIREMENT = 75, SLAYER_LEVEL_REQUIREMENT = 80;

    @Override
    public Object[] getKeys() {
        return new Object[]{"item_group.abyssal_vine_whip", "item.whip_vine"};
    }

    @Override
    public boolean processItem(Player player, Item item, int slotId, String option) {
        switch (option) {
            case "split":
                if (!player.getInventory().hasFreeSlots()) {
                    player.getPackets().sendGameMessage("You don't have enough inventory space to split the abyssal vine whip.");
                    return true;
                }
                player.getInventory().replaceItem("item.abyssal_whip", 1, slotId);
                player.getInventory().addItem("item.whip_vine", 1);
                player.message("You split your abyssal vine whip.");
                return true;
            case "drop":
                if (item.isItem("item.whip_vine"))
                    return false;
                player.getInventory().dropItem(slotId, item, true);
                return true;
        }
        return false;
    }


    @Override
    public boolean processItemOnItem(Player player, Item itemUsed, Item itemUsedWith, int fromSlot, int toSlot) {
        if (!usingItems("item.abyssal_whip", "item.whip_vine", itemUsed, itemUsedWith)) {
            return false;
        }
        if (!player.getSkills().hasRequirements(
                Skills.ATTACK, ATTACK_LEVEL_REQUIREMENT,
                Skills.SLAYER, SLAYER_LEVEL_REQUIREMENT)) {
            player.message("You need an attack level of " + ATTACK_LEVEL_REQUIREMENT +
                    " and a slayer level of " + SLAYER_LEVEL_REQUIREMENT + " in order to create abyssal vine whip.");
            return false;
        }
        player.getInventory().replaceItem("item.abyssal_vine_whip", 1, toSlot);
        player.getInventory().deleteItem(fromSlot, itemUsed);
        player.message("You attach vine whip with your abyssal whip to create an abyssal vine whip.");
        return true;
    }
}