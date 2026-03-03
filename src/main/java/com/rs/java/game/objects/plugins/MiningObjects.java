package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;
import com.rs.kotlin.game.player.skills.mining.Mining;
import com.rs.kotlin.game.player.skills.mining.RockDefinition;
import com.rs.kotlin.game.player.skills.woodcutting.TreeDefinition;
import com.rs.kotlin.game.player.skills.woodcutting.Woodcutting;

//TODO: Not done
public class MiningObjects extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return RockDefinition.Companion.getAllObjectIds().toArray();
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		RockDefinition def = RockDefinition.Companion.forObjectId(object.getId());
		if (def == null)
			return false;

		player.getActionManager().setAction(
				new Mining(player, object, def)
		);
		player.faceObject(object);
		return true;
	}
}
