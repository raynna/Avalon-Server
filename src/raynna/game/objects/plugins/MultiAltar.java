package raynna.game.objects.plugins;

import raynna.game.Animation;
import raynna.game.WorldObject;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;
import raynna.game.player.Skills;

public class MultiAltar extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 18254 };
	}

	@Override
	public int getDistance() {
		return 0;
	}

	@Override
	public boolean processObject(Player player, WorldObject object) {
		final int maxPrayer = player.getSkills().getRealLevel(Skills.PRAYER) * 10;
		if (player.getPrayer().getPrayerPoints() < maxPrayer) {
			player.lock(1);
			player.getPackets().sendGameMessage("You pray to the gods...", true);
			player.getPrayer().restorePrayer(maxPrayer);
			player.getPackets().sendGameMessage("...and recharged your prayer.", true);
			player.animate(new Animation(645));
		} else
			player.getPackets().sendGameMessage("You already have full prayer.");
		player.getDialogueManager().startDialogue("MultiAltar");
		return true;
	}
}
