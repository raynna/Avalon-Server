package com.rs.java.game.item.plugins.weapons;

import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemPlugin;
import com.rs.java.game.item.meta.ItemMetadata;
import com.rs.java.game.item.meta.PolyporeStaffMetaData;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import kotlin.Metadata;

public class PolyporeStaff extends ItemPlugin {

    @Override
    public Object[] getKeys() {
        return new Object[]{22494, 22496};
    }

    @Override
    public boolean processItem(Player player, Item item, int slotId, String option) {
        switch (option) {
            case "check":
                if (item.getMetadata() == null) {
                    item.setMetadata(new PolyporeStaffMetaData(3000));
                }
                if (item.getMetadata() instanceof PolyporeStaffMetaData) {
                    ItemMetadata data = item.getMetadata();
                    player.message("Your " + item.getName() + " currently has " + data.getValue() + " charges left.");
                }
                return true;
            case "clean":
                if (item.getMetadata() == null)
                    return true;
                player.getInventory().deleteItem(item);
                player.getInventory().addItem(new Item("item.polypore_stick"));
                player.message("You have cleaned your polypore staff.");
                return true;
        }
        return false;
    }

    @Override
    public boolean processItemOnItem(Player player, Item itemUsed, Item itemUsedWith, int fromSlot, int toSlot) {
        if (!usingItems(Item.getId("item.fire_rune"), Item.getId("item.polypore_spore"), itemUsed, itemUsedWith)) {
            return false;
        }
        if (!player.getSkills().hasRequirements(
                Skills.FARMING, 80)) {
            player.message("You need a farming level of " + 80 +
                    " in order to charge your polypore staff.");
            return false;
        }

        player.getInventory().replaceItem(21371, 1, toSlot);
        player.getInventory().deleteItem(fromSlot, itemUsed);
        int charges = 0;
        player.message("You charge your polypore staff with " + charges + " charges.");
        return true;
    }
}