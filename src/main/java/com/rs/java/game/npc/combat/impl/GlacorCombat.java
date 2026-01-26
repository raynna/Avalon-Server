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
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class GlacorCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 14301 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		Glacor glacor = (Glacor) npc;
		if (Utils.roll(1, 4))
			glacor.setRangeAttack(!glacor.isRangeAttack());
		if (target instanceof Player player) {
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
					Hit meleeHit = npc.meleeHit(target, 350);
					delayHit(npc, target, 0, meleeHit);
				} else
					sendDistancedAttack(glacor, target);
				break;
			case 4:
				final WorldTile tile = new WorldTile(target);
				npc.animate(new Animation(9955));
				ProjectileManager.sendToTile(Projectile.ELEMENTAL_SPELL, 2314, npc, tile, () -> {
					for (Entity e : npc.getPossibleTargets()) {
						if (e instanceof Player t) {
                            if (t.withinDistance(tile, 0))
								t.applyHit(new Hit(npc, t.getHitpoints() / 2, HitLook.RANGE_DAMAGE));
							t.getPackets().sendGraphics(new Graphics(2315), tile);
						}
					}
				});
				glacor.setRangeAttack(true);
				break;
			}
		}
		return npc.getAttackSpeed();
	}

	private void sendDistancedAttack(Glacor npc, final Entity target) {
		boolean isRangedAttack = npc.isRangeAttack();
		npc.animate(new Animation(isRangedAttack ? 9968 : 9967));
		if (isRangedAttack) {
			Hit rangeHit = npc.rangedHit(target, 294);
			ProjectileManager.send(Projectile.ELEMENTAL_SPELL, 962, npc, target, () -> {
				applyRegisteredHit(npc, target, rangeHit);
			});
		} else {
			Hit mageHit = npc.rangedHit(target, 264);
			ProjectileManager.send(Projectile.ELEMENTAL_SPELL, 634, npc, target, () -> {
				applyRegisteredHit(npc, target, mageHit);
				if (Utils.roll(1, 5)) {
					target.gfx(new Graphics(369));
					target.setFreezeDelay(10000); // ten seconds
				}
			});
		}
	}

}
