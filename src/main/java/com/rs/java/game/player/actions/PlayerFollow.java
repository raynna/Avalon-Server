package com.rs.java.game.player.actions;

import com.rs.java.game.Entity;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.Player;
import com.rs.java.game.route.RouteFinder;
import com.rs.java.game.route.strategy.EntityStrategy;
import com.rs.java.utils.Utils;

public class PlayerFollow extends Action {

	private transient Player target;
	private transient Entity npcTarget;

	public PlayerFollow(Player target) {
		this.target = target;
	}

	public PlayerFollow(Entity npcTarget) {
		this.npcTarget = npcTarget;
	}

	@Override
	public boolean start(Player player) {
		player.setNextFaceEntity(target);
		if (checkAll(player))
			return true;
		player.setNextFaceEntity(null);
		return false;
	}

	private boolean checkAll(Player player) {
		Entity followTarget = target != null ? target : npcTarget;

		if (followTarget == null || player.isDead() || player.hasFinished()
				|| followTarget.isDead() || followTarget.hasFinished())
			return false;

		if (player.getPlane() != followTarget.getPlane())
			return false;

		int distanceX = player.getX() - followTarget.getX();
		int distanceY = player.getY() - followTarget.getY();
		int size = player.getSize();
		int maxDistance = 16;

		if (distanceX > size + maxDistance || distanceX < -1 - maxDistance
				|| distanceY > size + maxDistance || distanceY < -1 - maxDistance)
			return false;

		player.setNextFaceEntity(followTarget);

		WorldTile prev = followTarget.getPreviousTile();
		if (prev != null && !player.hasWalkSteps()) {
			boolean added = player.addWalkSteps(prev.getX(), prev.getY(), 25, true);
			if (added) {
				return true;
			}
		}

		if (!player.clipedProjectile(followTarget, true) ||
				!Utils.isOnRange(player.getX(), player.getY(), size,
						followTarget.getX(), followTarget.getY(),
						followTarget.getSize(), 0)) {

			int steps = RouteFinder.findRoute(
					RouteFinder.WALK_ROUTEFINDER,
					player.getX(), player.getY(),
					player.getPlane(), player.getSize(),
					new EntityStrategy(followTarget),
					true
			);

			if (steps == -1)
				return false;

			if (steps > 0) {
				player.resetWalkSteps();
				int[] bufferX = RouteFinder.getLastPathBufferX();
				int[] bufferY = RouteFinder.getLastPathBufferY();
				for (int step = steps - 1; step >= 0; step--) {
					if (!player.addWalkSteps(bufferX[step], bufferY[step], 25, true))
						break;
				}
			}
		}

		return true;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		return 0;
	}

	@Override
	public void stop(final Player player) {
		player.setNextFaceEntity(null);
	}
}
