package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.skills.runecrafting.Altars;
import com.rs.java.game.player.actions.skills.runecrafting.OuraniaAltar;

public class RunecraftingAltars extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2478, 2479, 2480, 2481, 2482, 2483, 2484, 2485, 2486, 2487, 2488, 17010, 30624 
				/*Enter Altar Ids*/ , 2452, 2453, 2454, 2455, 2456, 2457, 2458, 30624, 2464, 2462, 2459, 2460, 2461, 26847};
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		if (object.getId() == 26847) {
			OuraniaAltar.craftRune(player);
			return true;
		}
		Altars.handleAltar(player, object.getId());
		return true;
	}
}
