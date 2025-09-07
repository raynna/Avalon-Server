package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.npc.glacior.Glacor;
import com.rs.java.game.player.Player;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class GlacorCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 14301 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		Glacor glacor = (Glacor) npc;
		NpcCombatDefinition defs = npc.getCombatDefinitions();
		if (Utils.random(4) == 0)
			glacor.setRangeAttack(!glacor.isRangeAttack());
		if (target instanceof Player) {
			Player player = (Player) target;
			if (glacor.getEffect() == 1)
				player.getPrayer().drainPrayer((int) (player.getPrayer().getPrayerPoints() * .1));
			switch (Utils.getRandom(5)) {
			case 0:
			case 1:
			case 2:
				sendDistancedAttack(glacor, target);
				break;
			case 3:
				if (Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(),
						target.getSize(), 0)) {
					npc.animate(new Animation(9955));
					delayHit(npc, target, 0,
                            getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 350, NpcAttackStyle.CRUSH, target)));
				} else
					sendDistancedAttack(glacor, target);
				break;
			case 4:
				final WorldTile tile = new WorldTile(target);
				npc.animate(new Animation(9955));
				World.sendProjectileToTile(npc, tile, 2314);
				glacor.setRangeAttack(true);
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						for (Entity e : npc.getPossibleTargets()) {
							if (e instanceof Player) {
								Player player = (Player) e;
								if (player.withinDistance(tile, 0))
									player.applyHit(new Hit(npc, player.getHitpoints() / 2, HitLook.RANGE_DAMAGE));
								player.getPackets().sendGraphics(new Graphics(2315), tile);
							}
						}
					}
				}, 3);
				break;
			}
		}
		return npc.getAttackSpeed();
	}

	private void sendDistancedAttack(Glacor npc, final Entity target) {
		boolean isRangedAttack = npc.isRangeAttack();
		if (isRangedAttack) {
			delayHit(npc, target, 2, getRangeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 294, NpcAttackStyle.RANGED, target)));
			World.sendElementalProjectile(npc, target, 962);
		} else {
			delayHit(npc, target, 2, getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 264, NpcAttackStyle.MAGIC, target)));
			World.sendElementalProjectile(npc, target, 634);
			if (Utils.random(5) == 0) {
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						target.gfx(new Graphics(369));
						target.setFreezeDelay(10000); // ten seconds
					}
				});
			}
		}
		npc.animate(new Animation(isRangedAttack ? 9968 : 9967));
	}

}
