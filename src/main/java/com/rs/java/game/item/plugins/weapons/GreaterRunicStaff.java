package com.rs.java.game.item.plugins.weapons;

import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.ItemPlugin;
import com.rs.java.game.item.meta.GreaterRunicStaffMetaData;
import com.rs.java.game.item.meta.ItemMetadata;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.GreaterRunicStaff.*;
import com.rs.kotlin.game.player.combat.magic.Spell;
import com.rs.kotlin.game.player.combat.magic.Spellbook;

import java.util.Map;

public class GreaterRunicStaff extends ItemPlugin {

    @Override
    public Object[] getKeys() {
        return new Object[]{24201, 24202, 24203};
    }

    @Override
    public boolean processItem(Player player, Item item, int slotId, String option) {
        player.temporaryAttribute().put("GREATER_RUNIC_STAFF", item);
        player.temporaryAttribute().put("INTERACT_STAFF_FROM_INVENTORY", true);
        switch (option) {
            case "set spell":
                player.getRunicStaff().openChooseSpell(player, item);
                return true;
            case "charge":
                player.getDialogueManager().startDialogue("GreaterRunicStaffD");
                return true;
            case "check charges"://TODO for some reason wield and check charge is opposite order
                if (player.getSwitchItemCache().contains(slotId))
                    return true;
                player.getSwitchItemCache().add(slotId);
                return true;
            case "wield":
                if (item.getId() == 24201) {
                    if (player.getSwitchItemCache().contains(slotId))
                        return true;
                    player.getSwitchItemCache().add(slotId);
                    return true;
                }
                if (item.getMetadata() == null) {
                    item.setMetadata(new GreaterRunicStaffMetaData(0, 0));
                }
                if (item.getMetadata() instanceof GreaterRunicStaffMetaData data) {
                    Spell spell = Spellbook.getSpellById(player, data.getSpellId());
                    if (spell == null) {
                        player.message("You dont have any spell selected");
                        return true;
                    }
                    int charges = data.getCharges();
                    player.message("You currently have " + charges + " " + spell.getName() + " charges left.");
                }
                return true;
            case "clear spell":
                player.getRunicStaff().clearSpell(false, false);
                return true;
            case "empty charge":
                player.getRunicStaff().clearCharges(false, false);
                return true;
        }
        return false;
    }

    @Override
    public boolean processDestroy(Player player, Item item, int slotId) {
        if (item.getId() == 24203) {
            for (Map.Entry<Integer, Item[]> charges : player.getStaffCharges().entrySet()) {
                if (charges.getValue() == null)
                    continue;
                for (Item staffRunes : charges.getValue()) {
                    if (item == null)
                        continue;
                    World.updateGroundItem(staffRunes, new WorldTile(player), player, player.isAtWild() ? 0 : 60, 0);
                }
            }
            player.message("All your runes in your runic staff were dropped.");
        }
        if (player.getRunicStaff().getSpellId() > 0) {
            player.getRunicStaff().setStaffValues(-1, null);
            player.getPackets().sendGameMessage("You clear your greater runic staff spell.");
            player.getStaffCharges().clear();
        }
        player.getInventory().dropItem(slotId, item, false);
        return true;
    }
}
