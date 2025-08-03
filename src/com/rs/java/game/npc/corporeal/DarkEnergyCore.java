package com.rs.java.game.npc.corporeal;

import java.util.ArrayList;

import com.rs.java.game.Entity;
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
public class DarkEnergyCore extends NPC {

	private CorporealBeast beast;
	private Entity target;

	public DarkEnergyCore(CorporealBeast beast) {
		super(8127, beast, -1, true, true);
		setForceMultiArea(true);
		this.beast = beast;
		changeTarget = 2;
	}

	private int changeTarget;
	private int delay;

	@Override
	public void processNPC() {
		if (isDead() || hasFinished())
			return;
		if (delay > 0) {
			delay--;
			return;
		}
		if (changeTarget > 0) {
			if (changeTarget == 1) {
				ArrayList<Entity> possibleTarget = beast.getPossibleTargets();
				if (possibleTarget.isEmpty()) {
					finish();
					beast.removeDarkEnergyCore();
					return;
				}
				target = possibleTarget.get(Utils.getRandom(possibleTarget.size() - 1));
				World.sendElementalProjectile(this, target, 1828);
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						setNextWorldTile(new WorldTile(target));
					}
				}, 1);
			}
			changeTarget--;
			return;
		}
		if (target == null || !target.withinDistance(beast, 1)|| target.getPlane() != getPlane()) {
			changeTarget = 5;
			return;
		}
		int damage = Utils.getRandom(50) + 50;
		target.applyHit(new Hit(this, Utils.random(1, 131), HitLook.REGULAR_DAMAGE));
		beast.applyHit(new Hit(this, damage, HitLook.HEALED_DAMAGE));
		delay = getPoison().isPoisoned() ? 20 : 2;
		if (target instanceof Player) {
			Player player = (Player) target;
			player.getPackets().sendGameMessage("The dark core creature steals some life from you for its master.");
		}
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		beast.removeDarkEnergyCore();
	}

}
