package com.rs.java.game.npc.combat.impl.dung;

import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.dungeoneering.BulwarkBeast;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class BulwarkBeastCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ "Bulwark beast" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		((BulwarkBeast) npc).refreshBar();

		final NpcCombatDefinition defs = npc.getCombatDefinitions();

		if (Utils.random(15) == 0) {
			List<Entity> targets = npc.getPossibleTargets();
			npc.animate(new Animation(13007));
			for (Entity t : targets) {
				if (Utils.isOnRange(t.getX(), t.getY(), t.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0)) {
					t.gfx(new Graphics(2400));
					delayHit(npc, t, 1, getRegularHit(npc, 1 + Utils.random((int) (npc.getMaxHit() * 0.7))));
				}
			}
			return npc.getAttackSpeed();
		}

		//mage, range, melee
		int attackStyle = Utils.random(Utils.isOnRange(target.getX(), target.getY(), target.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0) ? 3 : 2);
		switch (attackStyle) {
		case 0:
			npc.animate(new Animation(13004));
			npc.gfx(new Graphics(2397));
			ProjectileManager.sendWithGraphic(
					Projectile.ELEMENTAL_SPELL, 280,
					npc, target,
					new Graphics(281)
			);
			delayHit(npc, target, 2, npc.magicHit(target, npc.getMaxHit()));
			break;
		case 1:
			npc.animate(new Animation(13006));
			npc.gfx(new Graphics(2394));
			List<Entity> targets = npc.getPossibleTargets();
			for (Entity t : targets) {
				World.sendProjectileToTile(npc, t, 2395);
				t.gfx(new Graphics(2396, 75, 0));
				delayHit(npc, t, 1, npc.rangedHit(npc, defs.getMaxHit()));
			}
			break;
		case 2:
			npc.animate(new Animation(defs.getAttackAnim()));
			delayHit(npc, target, 0, npc.meleeHit(npc, defs.getMaxHit()));
			break;
		}
		return npc.getAttackSpeed();
	}
}
