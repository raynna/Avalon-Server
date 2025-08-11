package com.rs.java.game.item.plugins.skilling;

import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemId;
import com.rs.java.game.item.ItemPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.skills.crafting.LeatherCrafting;
import com.rs.java.game.player.actions.skills.crafting.LeatherCrafting.*;

public class Leather extends ItemPlugin {

    @Override
    public Object[] getKeys() {
        return new Object[]{"leather"};
    }

    @Override
    public boolean processItem(Player player, Item item, int slotId, String option) {
        switch (option) {
            case "craft":
                if (!player.getInventory().containsOneItem(ItemId.NEEDLE_1733) && !player.getToolbelt().contains(ItemId.NEEDLE_1733)) {
                    player.message("You need a needle to craft this item.");
                    return true;
                }
                Craft craft = Craft.forId(item.getId());
                if (craft != null) {
                    player.getDialogueManager().startDialogue("CraftingD", craft, false);
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean processItemOnItem(Player player, Item item, Item item2, int itemUsed, int usedWith) {
        Craft craft = LeatherCrafting.isCrafting(item, item2);
        if (craft == null)
            craft = LeatherCrafting.isCrafting(item2, item);
        if (craft != null) {
            player.getDialogueManager().startDialogue("CraftingD", craft);
            return true;
        }
        return true;
    }
}