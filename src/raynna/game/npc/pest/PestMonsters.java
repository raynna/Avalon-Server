package raynna.game.npc.pest;

import java.util.ArrayList;
import java.util.List;

import raynna.game.Entity;
import raynna.game.World;
import raynna.game.WorldTile;
import raynna.game.minigames.pest.PestControl;
import raynna.game.npc.NPC;
import raynna.game.player.Player;
import raynna.util.Utils;

@SuppressWarnings("serial")
public class PestMonsters extends NPC {

	protected PestControl manager;
	protected int portalIndex;

	public PestMonsters(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned,
			int index, PestControl manager) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		this.manager = manager;
		this.portalIndex = index;
		setForceMultiArea(true);
		setForceAgressive(true);
		setForceTargetDistance(70);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (!getCombat().underCombat())
			checkAgressivity();
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		List<Integer> playerIndexes = World.getRegion(getRegionId()).getPlayerIndexes();
		if (playerIndexes != null) {
			for (int playerIndex : playerIndexes) {
				Player player = World.getPlayers().get(playerIndex);
				if (player == null || player.isDead() || player.hasFinished() || !player.isActive()
						|| !player.withinDistance(this, 10))
					continue;
				possibleTarget.add(player);
			}
		}
		if (possibleTarget.isEmpty() || Utils.getRandom(3) != 0) {
			possibleTarget.clear();
			possibleTarget.add(manager.getKnight());
		}
		return possibleTarget;
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		manager.getPestCounts()[portalIndex]--;
	}
}
