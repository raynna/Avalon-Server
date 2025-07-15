package com.rs.java.game.item.plugins.misc;

import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.DwarfMultiCannon;

public class DwarfCannon extends ItemPlugin {

    @Override
    public Object[] getKeys() {
        return new Object[]{6};//TODO ADD ALL DWARF CANNON ITEMIDSS
    }

    @Override
    public boolean processItem(Player player, Item item, int slotId, String option) {
        switch (option) {
            case "set-up":
                DwarfMultiCannon.setUp(player, item.getId() == 6 ? 0 : item.getId() == 8 ? 1 : 2);
                return true;
        }
        return false;
    }
}