package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.dungeonnering.Dreadnaut;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.utils.Utils;

public class DreadnautCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 12848 };//GFX 2859 Poop bubbles that drain prayer
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Dreadnaut boss = (Dreadnaut) npc;

		if (!Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0))
			return 0;

		if (Utils.random(5) == 0) {
			npc.animate(new Animation(14982));
			npc.gfx(new Graphics(2865));
			Hit hit = npc.meleeHit(target, boss.getMaxHit());
			if (hit.getDamage() > 0) {
				target.gfx(new Graphics(2866, 75, 0));
				sendReductionEffect(boss, target, hit.getDamage());
			}
			if (target instanceof Player player) {
                player.getPackets().sendGameMessage("You have been injured and are unable to use protection prayers.");
				player.setPrayerDelay(8000);
			}
			delayHit(npc, target, 1, hit);
		} else {
			npc.animate(new Animation(14973));
			npc.gfx(new Graphics(2856));

			for (Entity t : boss.getPossibleTargets()) {
				if (!t.withinDistance(target, 2))
					continue;
				int damage = boss.getMaxHit();
				World.sendProjectileToTile(boss, t, 2857);
				if (damage > 0) {
					sendReductionEffect(boss, t, damage);
					boss.addSpot(new WorldTile(t));
				} else
					t.gfx(new Graphics(2858, 75, 0));
				delayHit(npc, t, 1, npc.meleeHit(npc, damage));
			}
		}
		return 5;
	}

	private void sendReductionEffect(Dreadnaut boss, Entity target, int damage) {
		if (!boss.canReduceMagicLevel() || !(target instanceof Player))
			return;
		Player player = (Player) target;
		player.getSkills().set(Skills.MAGIC, (int) (player.getSkills().getLevel(Skills.MAGIC) - (damage * .10)));
	}
}
