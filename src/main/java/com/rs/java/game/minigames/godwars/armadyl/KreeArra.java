package com.rs.java.game.minigames.godwars.armadyl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rs.core.thread.CoresManager;
import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.player.Player;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;

@SuppressWarnings("serial")
public class KreeArra extends NPC {

	public KreeArra(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setNoDistanceCheck(true);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playerIndexes != null) {
				for (int npcIndex : playerIndexes) {
					Player player = World.getPlayers().get(npcIndex);
					if (player == null || player.isDead() || player.hasFinished() || !player.isActive()
							|| !player.withinDistance(this, 64)
							|| ((!isAtMultiArea() || !player.isAtMultiArea()) && player.getAttackedBy() != this
									&& player.getAttackedByDelay() > System.currentTimeMillis())
							|| !clipedProjectile(player, false))
						continue;
					possibleTarget.add(player);
				}
			}
		}
		return possibleTarget;
	}

	/*
	 * gotta override else setRespawnTask override doesnt work
	 */
	@Override
	public void sendDeath(final Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		animate(-1);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					animate(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					drop();
					reset();
					setLocation(getRespawnTile());
					finish();
					setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	@Override
	public void setRespawnTask() {
		if (!hasFinished()) {
			reset();
			setLocation(getRespawnTile());
			finish();
		}
		final NPC npc = this;
		CoresManager.getSlowExecutor().schedule(() -> {
            setFinished(false);
            World.addNPC(npc);
            npc.setLastRegionId(0);
            World.updateEntityRegion(npc);
            loadMapRegions();
            checkMultiArea();
            // GodWarsBosses.respawnArmadylMinions();
        }, getCombatDefinitions().getRespawnDelay() * 600L, TimeUnit.MILLISECONDS);
	}
}
