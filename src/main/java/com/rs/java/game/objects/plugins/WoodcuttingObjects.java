package com.rs.java.game.objects.plugins;

import com.rs.java.game.WorldObject;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;
import com.rs.kotlin.game.player.skills.woodcutting.TreeDefinition;
import com.rs.kotlin.game.player.skills.woodcutting.Woodcutting;

import java.util.Arrays;

//TODO: Not done
public class WoodcuttingObjects extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return TreeDefinition.Companion.getAllObjectIds().toArray();
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		TreeDefinition def = TreeDefinition.Companion.forObjectId(object.getId());
		if (def == null)
			return false;

		player.getActionManager().setAction(
				new Woodcutting(player, object, def)
		);
		player.faceObject(object);
		return true;
	}
}
