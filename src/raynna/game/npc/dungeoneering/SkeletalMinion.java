package raynna.game.npc.dungeoneering;

import raynna.game.Entity;
import raynna.game.WorldTile;
import raynna.game.player.content.dungeoneering.DungeonManager;

@SuppressWarnings("serial")
public class SkeletalMinion extends DungeonNPC {

	private NecroLord boss;

	public SkeletalMinion(NecroLord boss, int id, WorldTile tile, DungeonManager manager, double multiplier) {
		super(id, tile, manager, multiplier);
		setForceAgressive(true);
		this.boss = boss;
	}

	@Override
	public void drop() {

	}

	@Override
	public int getMaxHit() {
		return super.getMaxHit() * 2;
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		boss.removeSkeleton(this);
	}
}
