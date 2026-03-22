package raynna.game.npc.nomad;

import java.util.List;

import raynna.game.Animation;
import raynna.game.ForceTalk;
import raynna.game.Hit;
import raynna.game.Hit.HitLook;
import raynna.game.World;
import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.game.player.Player;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.util.Utils;

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
