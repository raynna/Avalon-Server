package raynna.game.objects.plugins;

import raynna.game.Hit;
import raynna.game.WorldObject;
import raynna.game.WorldTile;
import raynna.game.Hit.HitLook;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;
import raynna.game.player.Skills;
import raynna.util.Utils;

public class GodWarsBoulder extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { 35390 };
	}

	@Override
	public int getDistance() {
		return 0;
	}

	@Override
	public boolean processObject2(Player player, WorldObject object) {
		if (player.getSkills().getRealLevel(Skills.AGILITY) < 60) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You need at least level 60 agility to clear this obstacle.");
			return false;
		} else {
			if (player.getY() > 3709) {
				player.setNextWorldTile(new WorldTile(2907, 3708, 0));
			} else if (player.getY() < 3709) {
				player.setNextWorldTile(new WorldTile(2907, 3712, 0));
			}
			if (Utils.random(1, 50) > 40) {
				player.applyHit(new Hit(player, 70, HitLook.REGULAR_DAMAGE));
				player.message("You scrape yourself against the rock while trying to pass!");
				return false;
			} else {
				player.getPackets().sendGameMessage("You slide past the rock with great skill.");
				return true;
			}
		}
	}
}