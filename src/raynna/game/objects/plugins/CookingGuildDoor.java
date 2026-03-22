package raynna.game.objects.plugins;

import raynna.core.cache.defintions.ItemDefinitions;
import raynna.game.WorldObject;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;
import raynna.game.player.Skills;

public class CookingGuildDoor extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2712 };
	}

	@Override
	public int getDistance() {
		return 0;
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		if (player.getY() <= object.getY()) {
			if (player.getSkills().getRealLevel(Skills.COOKING) < 32) {
				player.getPackets().sendGameMessage("You need a level of 32 cooking to enter this guild.");
				return false;
			}
			if (!player.getEquipment().containsOneItem(1949)) {
				player.getPackets().sendGameMessage("You need to wear "
						+ ItemDefinitions.getItemDefinitions(1949).getName() + " to enter this guild.");
				return false;
			}
		}
		DoorsAndGates.handleDoorTemporary(player, object, 1200);
		return true;
	}
}
