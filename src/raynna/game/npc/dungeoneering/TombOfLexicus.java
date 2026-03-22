package raynna.game.npc.dungeoneering;

import raynna.game.Entity;
import raynna.game.WorldTile;
import raynna.game.player.content.dungeoneering.DungeonManager;

@SuppressWarnings("serial")
public class TombOfLexicus extends DungeonNPC {

	private LexicusRunewright boss;

	public TombOfLexicus(LexicusRunewright boss, int id, WorldTile tile, DungeonManager manager, double multiplier) {
		super(id, tile, manager, multiplier);
		setForceAgressive(true);
		this.boss = boss;
	}

	@Override
	public void drop() {

	}

	@Override
	public int getMaxHitpoints() {
		return 10;
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		boss.removeBook(this);
	}
}
