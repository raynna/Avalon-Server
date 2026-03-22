package raynna.game.npc.others;

import raynna.game.Hit;
import raynna.game.Hit.HitLook;
import raynna.game.WorldTile;
import raynna.game.npc.NPC;

@SuppressWarnings("serial")
public class ChaosElemental extends NPC {

	public ChaosElemental(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setCapDamage(1200);
		setForceMultiAttacked(true);
		setForceTargetDistance(32);
		setForceAgressiveDistance(16);
		setRandomWalkDistance(64);
	}

	@Override
	public void handleHit(Hit hit) {
		if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE
				&& hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		super.handleHit(hit);
	}
}
