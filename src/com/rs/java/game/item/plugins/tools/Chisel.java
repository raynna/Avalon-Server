package com.rs.java.game.item.plugins.tools;

import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemId;
import com.rs.java.game.item.ItemPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.skills.crafting.*;
import com.rs.java.game.player.actions.skills.fletching.*;
import com.rs.java.game.player.actions.skills.crafting.GemCutting.*;
import com.rs.java.game.player.actions.skills.fletching.Fletching.*;

public class Chisel extends ItemPlugin {

    @Override
    public Object[] getKeys() {
        return new Object[]{ItemId.CHISEL_1755};
    }

    @Override
    public boolean processItemOnItem(Player player, Item item, Item item2, int fromSlot, int toSlot) {
        Gem gem = GemCutting.getUncut(item, item2);//cutting gems in to cut gems
        if (gem != null) {
            GemCutting.cut(player, gem);
            return true;
        }
        FletchingData fletchingData = Fletching.findFletchingData(item, item2);//cutting gems to bolt tips
        if (fletchingData != null) {
            player.getDialogueManager().startDialogue("FletchingD", fletchingData);
            return true;
        }
        return false;
    }
}
