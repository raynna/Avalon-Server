package raynna.game.npc.fightpits;

import java.util.ArrayList;

import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.WorldTile;
import raynna.game.minigames.fightpits.FightPits;
import raynna.game.npc.NPC;
import raynna.game.player.Player;

@SuppressWarnings("serial")
public class FightPitsNPC extends NPC {

	public FightPitsNPC(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
		setForceMultiArea(true);
		setNoDistanceCheck(true);
	}

	@Override
	public void sendDeath(Entity source) {
		gfx(new Graphics(2924 + getSize()));
		super.sendDeath(source);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for (Player player : FightPits.arena)
			possibleTarget.add(player);
		return possibleTarget;
	}

}
