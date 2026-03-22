package raynna.game.npc.others;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.util.Utils;

@SuppressWarnings("serial")
public class Hybrid extends NPC {

	public Hybrid(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setForceTargetDistance(8);
		setIntelligentRouteFinder(true);
	}

	int food = 28;
	int meleeForm = 0;

	@Override
	public void processNPC() {
		if (isDead())
			return;
		if (getId() == 1919 || getId() == 19002) {
			if (meleeForm > 7) {
				if (getId() == 19002)
					transformIntoNPC(Utils.random(1) == 1 ? 19001 : 19000);
				else
					transformIntoNPC(Utils.random(1) == 1 ? 6367 : 3229);
			}
			meleeForm++;
		}
		if (getId() == 6367 || getId() == 3229)
			meleeForm = 0;
		if (getHitpoints() < 450 && food > 0) {
			heal(240);
			food--;
			animate(new Animation(829));
			return;
		}
		super.processNPC();
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		food = 28;
	}

}
