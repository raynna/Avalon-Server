package com.rs.java.game.item.plugins.skilling;

import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemId;
import com.rs.java.game.item.ItemPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.skills.runecrafting.RunecraftingPouches;
import com.rs.kotlin.rscm.Rscm;

import java.util.Arrays;

public class RunecraftingPouch extends ItemPlugin {

    @Override
    public Object[] getKeys() {
        return new Object[] {"item.small_pouch", "item.medium_pouch", "item.large_pouch", "item.giant_pouch"};
    }

    @Override
    public boolean processItem(Player player, Item item, int slotId, String option) {
        return switch (option) {
            case "check" -> {
                RunecraftingPouches.checkPouch(player, item);
                yield true;
            }
            case "fill" -> {
                RunecraftingPouches.fillPouch(player, item);
                yield true;
            }
            case "empty" -> {
                RunecraftingPouches.emptyPouch(player, item);
                yield true;
            }
            default -> false;
        };
    }
}
