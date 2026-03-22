package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.WorldTile;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;
import raynna.game.player.Skills;
import raynna.game.player.actions.skills.thieving.Thieving;
import raynna.game.player.content.AxeHut;

public class AxeHutGate extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] {"object.axe_hut_gate" };
	}

    @Override
	public boolean processObject(Player player, WorldObject object) {
		if (player.getY() == 3962) {
			AxeHut.GateNorthOut(player, object);
			return true;
		} else if (player.getY() == 3958) {
			AxeHut.GateSouthOut(player, object);
			return true;
		} else if (player.getY() == 3963 || player.getY() == 3957) {
			player.getPackets().sendGameMessage("This gate is locked from the inside.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processObject2(Player player, WorldObject object) {
		if (player.getSkills().getRealLevel(Skills.THIEVING) < 60) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You need atleast an level of 60 thieving to picklock this door.");
			return false;
		}
		if (!player.getInventory().containsItem("item.lockpick", 1)) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You need a Lockpick to picklock this door.");
			return false;
		}
		if (player.getY() == 3963) {
			player.setNextFaceWorldTile(new WorldTile(player.getX(), player.getY() - 1, 0));
			Thieving.pickNorthGate(player, object);
			return true;
		} else if (player.getY() == 3957) {
			player.setNextFaceWorldTile(new WorldTile(player.getX(), player.getY() + 1, 0));
			Thieving.pickSouthGate(player, object);
			return true;
		} else if (player.getY() == 3958 || player.getY() == 3962) {
			player.getPackets().sendGameMessage("You can't picklock from the inside.");
			return false;
		}
		return true;
	}
}
