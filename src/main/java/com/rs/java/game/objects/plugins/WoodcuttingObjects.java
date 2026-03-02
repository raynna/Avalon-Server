package com.rs.java.game.objects.plugins;

import com.rs.core.cache.defintions.ObjectDefinitions;
import com.rs.java.game.WorldObject;
import com.rs.java.game.objects.ObjectPlugin;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.skills.woodcutting.Woodcutting;
import com.rs.java.game.player.actions.skills.woodcutting.Woodcutting.TreeDefinitions;
import com.rs.kotlin.game.player.skills.woodcutting.TreeDefinition;

import java.util.Arrays;

//TODO: Not done
public class WoodcuttingObjects extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return Arrays.stream(TreeDefinition.values())
				.flatMapToInt(def -> Arrays.stream(def.getObjectIds()))
				.boxed()
				.toArray();
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		TreeDefinition def = TreeDefinition.forObjectId(object.getId());
		if (def == null)
			return false;

		player.getActionManager().setAction(
				new com.rs.kotlin.game.player.skills.woodcutting.Woodcutting(player, object, def)
		);
		return true;
	}
}
