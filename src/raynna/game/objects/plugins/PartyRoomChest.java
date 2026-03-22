package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;
import raynna.game.player.content.PartyRoom;

public class PartyRoomChest extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2418 };
	}

	@Override
	public int getDistance() {
		return 0;
	}

	@Override
	public boolean processObject2(Player player, WorldObject object) {
		PartyRoom.openPartyChest(player);
		return true;
	}
}

