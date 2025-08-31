package com.rs.java.game.item.plugins.weapons;

import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;

public class AbyssalVineWhip extends ItemPlugin {

    private final int ATTACK_LEVEL_REQUIREMENT = 75, SLAYER_LEVEL_REQUIREMENT = 80;

    @Override
    public Object[] getKeys() {
        return new Object[]{21371, 21369};
    }

    @Override
    public boolean processItem(Player player, Item item, int slotId, String option) {
        switch (option) {
            case "split":
                if (!player.getInventory().hasFreeSlots()) {
                    player.getPackets().sendGameMessage("You don't have enough inventory space to split the abyssal vine whip.");
                    return true;
                }
                player.getInventory().replaceItem(4151, 1, slotId);
                player.getInventory().addItem(21369, 1);
                player.message("You split your abyssal vine whip.");
                return true;
            case "drop":
                if (item.getId() == 21369)
                    return false;//script false, continues to regular drop method
                player.getInventory().dropItem(slotId, item, false);
                World.updateGroundItem(new Item(21369, 1), new WorldTile(player), player, player.inPkingArea() ? 0 : 60, 2);
                World.updateGroundItem(new Item(4151, 1), new WorldTile(player), player, player.inPkingArea() ? 0 : 60, 2);
                return true;
        }
        return false;
    }

    @Override
    public boolean processItemOnItem(Player player, Item itemUsed, Item itemUsedWith, int fromSlot, int toSlot) {
        if (!usingItems(4151, 21369, itemUsed, itemUsedWith)) {
            return false;
        }
        if (!player.getSkills().hasRequirements(
                Skills.ATTACK, ATTACK_LEVEL_REQUIREMENT,
                Skills.SLAYER, SLAYER_LEVEL_REQUIREMENT)) {
            player.message("You need an attack level of " + ATTACK_LEVEL_REQUIREMENT +
                    " and a slayer level of " + SLAYER_LEVEL_REQUIREMENT + " in order to create abyssal vine whip.");
            return false;
        }
        player.getInventory().replaceItem(21371, 1, toSlot);
        player.getInventory().deleteItem(fromSlot, itemUsed);
        player.message("You attach vine whip with your abyssal whip to create an abyssal vine whip.");
        return true;
    }
}