package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;
import raynna.game.player.travel.FairyRings;

public class FairyRingObject extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { "object_group.fairy_ring_use" }; }

	@Override
	public boolean processObject(Player player, WorldObject object) {
		FairyRings.Companion.openRingInterface(player, object);
		return true;
	}
}
