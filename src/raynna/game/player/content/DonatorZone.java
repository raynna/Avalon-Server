package raynna.game.player.content;

import java.util.List;

import raynna.game.ForceTalk;
import raynna.game.World;
import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.game.player.Player;
import raynna.game.player.actions.combat.Magic;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.util.Utils;

public class DonatorZone {

	public static void enterDonatorzone(final Player player) {
		Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2582, 3910, 0));
		for (int regionId : player.getMapRegionsIds()) {
			List<Integer> npcIndexes = World.getRegion(regionId).getNPCsIndexes();
			if (npcIndexes != null) {
				for (int npcIndex : npcIndexes) {
					final NPC n = World.getNPCs().get(npcIndex);
					if (n == null || n.getId() != 5445)
						continue;
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							final int random = Utils.getRandom(3);
							if (random == 0)
								n.setNextForceTalk(new ForceTalk(""));
							else if (random == 1)
								n.setNextForceTalk(new ForceTalk(""));
							else if (random == 2)
								n.setNextForceTalk(new ForceTalk(""));
							else if (random == 3)
								n.setNextForceTalk(new ForceTalk(""));
						}
					}, 4);
				}
			}
		}
	}
}
