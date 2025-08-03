package com.rs.java.game.npc.nomad;

import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

@SuppressWarnings("serial")
public class FlameVortex extends NPC {

	private long explodeTime;

	public FlameVortex(WorldTile tile) {
		this(9441, tile, -1, true, true);
	}

	public FlameVortex(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		explodeTime = Utils.currentTimeMillis() + 60000;
		animate(new Animation(12720));
	}

	@Override
	public void processNPC() {
		if (explodeTime == -1)
			return;
		Player target = getTargetToCheck();
		if (target != null
				&& ((target.getX() == getX() && target.getY() == getY()) || (target.getNextRunDirection() != -1
						&& target.getX() - Utils.DIRECTION_DELTA_X[target.getNextRunDirection()] == getX()
						&& target.getY() - Utils.DIRECTION_DELTA_Y[target.getNextRunDirection()] == getY()))) {
			explode(target, 400);
		} else if (explodeTime < Utils.currentTimeMillis())
			explode(target != null && withinDistance(target, 1) ? target : null, Utils.random(400, 701));
	}

	public void explode(final Player target, final int damage) {
		explodeTime = -1;
		final NPC npc = this;
		WorldTasksManager.schedule(new WorldTask() {

			private boolean secondLoop;

			@Override
			public void run() {
				if (!secondLoop) {
					animate(new Animation(12722));
					if (target != null) {
						target.applyHit(new Hit(npc, damage, HitLook.REGULAR_DAMAGE));
						target.setRunEnergy(0);
						target.setNextForceTalk(new ForceTalk("Aiiiiiieeeee!"));
					}
					secondLoop = true;
				} else {
					finish();
					stop();
				}
			}
		}, 0, 0);
	}

	public Player getTargetToCheck() {
		List<Integer> playerIndexes = World.getRegion(getRegionId()).getPlayerIndexes();
		if (playerIndexes != null) {
			for (int npcIndex : playerIndexes) {
				Player player = World.getPlayers().get(npcIndex);
				if (player == null || player.isDead() || !player.isActive())
					continue;
				return player;
			}
		}
		return null;
	}

}
