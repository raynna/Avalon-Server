package raynna.game.objects.plugins;

import raynna.game.WorldObject;
import raynna.game.objects.ObjectPlugin;
import raynna.game.player.Player;
import raynna.game.player.Skills;

public class Alter extends ObjectPlugin {

	@Override
	public Object[] getKeys() {
		return new Object[] { "object_group.altar_pray", "object_group.chaos_altar_pray_at", "object_group.gorilla_statue_pray_at"};
	}

    @Override
	public boolean processObject(Player player, WorldObject object) {
        if (!object.hasAnyOption("Pray", "Pray-at")) {
            return true;
        }
        if (object.isObject("object.ancient_altar")) {
            player.getDialogueManager().startDialogue("AncientAltar");
            return true;
        }
        if (player.getPrayer().hasFullPrayerPoints()) {
            player.getPackets().sendGameMessage("You already have full prayer.");
            return true;
        }
        player.lock(1);
        player.animate("animation.pray_altar");
        player.getPrayer().restorePrayer(player.getSkills().getRealLevel(Skills.PRAYER));
        player.message("You recharge your Prayer points");
		return true;
	}
}
