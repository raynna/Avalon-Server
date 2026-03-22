package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;
import raynna.game.player.skills.woodcutting.TreeDefinition;
import raynna.game.player.skills.woodcutting.Woodcutting;

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
