package com.rs.java.game.npc.others;

import java.util.concurrent.TimeUnit;

import com.rs.core.thread.CoresManager;
import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.WorldObject;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.player.actions.skills.construction.House;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;
import com.rs.java.utils.Logger;
import com.rs.java.utils.Utils;

@SuppressWarnings("serial")
public class Guard extends NPC {

	private House house;

	public Guard(int id, House house, WorldObject object) {
		super(id, object, -1, true, false);
		setDirection(Utils.getAngle(Utils.DOOR_ROTATION_DIR_Y[object.getRotation()], Utils.DOOR_ROTATION_DIR_X[object.getRotation()]));
		setForceAgressive(true);
		setForceMultiArea(true);
		setForceTargetDistance(7);
		this.house = house;
	}

	@Override
	public void processNPC() {
		Entity target = getCombat().getTarget();
		if (target != null && !withinDistance(target, 7)) {
			getCombat().reset();
			resetCombat();
			WorldTile tile = getRespawnTile();
			addWalkSteps(tile.getX(), tile.getY());
			setNextFaceEntity(null);
		}
		super.processNPC();
	}

	@Override
	public void sendDeath(final Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		animate(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					animate(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
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
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (!house.isLoaded() || !house.isChallengeMode())
						return;
					spawn();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, getCombatDefinitions().getRespawnDelay() * 600, TimeUnit.MILLISECONDS);
	}

}
