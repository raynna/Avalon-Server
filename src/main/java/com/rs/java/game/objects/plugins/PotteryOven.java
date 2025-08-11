package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.item.Item;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.skills.smithing.JewllerySmithing;

public class PotteryOven extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2643, 11601, "Pottery Oven" };
	}

	@Override
	public int getDistance() {
		return 0;
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		player.getDialogueManager().startDialogue("PotterOvenD", object);
		return true;
	}
	
	@Override
	public boolean processItemOnObject(Player player, WorldObject object, Item item) {
        if (item.getId() == 2357 && object.getId() == 2643) {
        	JewllerySmithing.openInterface(player);
        }
		return true;
	}
}
