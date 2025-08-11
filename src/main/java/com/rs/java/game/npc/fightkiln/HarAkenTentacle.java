package com.rs.java.game.npc.fightkiln;

import java.util.ArrayList;
import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;

@SuppressWarnings("serial")
public class HarAkenTentacle extends NPC {

	private HarAken aken;

	public HarAkenTentacle(int id, WorldTile tile, HarAken aken) {
		super(id, tile, -1, true, true);
		setForceMultiArea(true);
		setCantFollowUnderCombat(true);
		setForceAgressive(true);
		setForceAgressiveDistance(32);
		setForceTargetDistance(32);
		animate(new Animation(id == 15209 ? 16238 : 16241));
		this.aken = aken;
	}

	@Override
	public void sendDeath(Entity source) {
		aken.removeTentacle(this);
		super.sendDeath(source);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>(1);
		List<Integer> playerIndexes = World.getRegion(getRegionId()).getPlayerIndexes();
		if (playerIndexes != null) {
			for (int npcIndex : playerIndexes) {
				Player player = World.getPlayers().get(npcIndex);
				if (player == null || player.isDead() || player.hasFinished() || !player.isActive())
					continue;
				possibleTarget.add(player);
			}
		}
		return possibleTarget;
	}
}
