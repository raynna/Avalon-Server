package raynna.game.npc.others;

import java.util.concurrent.TimeUnit;

import raynna.core.thread.CoresManager;
import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.game.player.Player;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.util.Logger;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcCombatDefinition;

@SuppressWarnings("serial")
public class LivingRock extends NPC {

	private Entity source;
	private long deathTime;

	public LivingRock(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setForceTargetDistance(4);
	}

	@Override
	public void sendDeath(final Entity source) {
		final NpcCombatDefinition defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		animate(-1);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					animate(new Animation(defs.getDeathAnim()));
				} else if (loop >= defs.getDeathDelay()) {
					drop();
					reset();
					transformIntoRemains(source);
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public void transformIntoRemains(Entity source) {
		this.source = source;
		deathTime = Utils.currentTimeMillis();
		final int remainsId = getId() + 5;
		transformIntoNPC(remainsId);
		setRandomWalk(0);
		CoresManager.getSlowExecutor().schedule(() -> {
            try {
                if (remainsId == getId())
                    takeRemains();
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, 3, TimeUnit.MINUTES);

	}

	public boolean canMine(Player player) {
		return Utils.currentTimeMillis() - deathTime > 60000 || player == source;
	}

	public void takeRemains() {
		setNPC(getId() - 5);
		setLocation(getRespawnTile());
		setRandomWalk(getDefinitions().walkMask);
		finish();
		if (!isSpawned())
			setRespawnTask();
	}

}
