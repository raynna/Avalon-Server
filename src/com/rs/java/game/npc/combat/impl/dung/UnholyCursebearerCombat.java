package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.dungeonnering.DungeonBoss;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class UnholyCursebearerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ "Unholy cursebearer" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utils.isOnRange(target.getX(), target.getY(), target.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0) ? Utils.random(2) : 0;
		if (target instanceof Player && target.getTemporaryAttributtes().get("UNHOLY_CURSEBEARER_ROT") == null) {
			target.getTemporaryAttributtes().put("UNHOLY_CURSEBEARER_ROT", 1);
			final Player player = (Player) target;
			player.getPackets().sendGameMessage("An undead rot starts to work at your body.");
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					Integer value = (Integer) target.getTemporaryAttributtes().get("UNHOLY_CURSEBEARER_ROT");
					if (player.hasFinished() || npc.hasFinished() || !((DungeonBoss) npc).getManager().isAtBossRoom(player) || value == null) {
						target.getTemporaryAttributtes().remove("UNHOLY_CURSEBEARER_ROT");
						stop();
						return;
					}
					int damage = 20 * value;
					for (int stat = 0; stat < 7; stat++) {
						if (stat == Skills.HITPOINTS)
							continue;
						int drain = Utils.random(5) + 1;
						if (stat == Skills.PRAYER)
							player.getPrayer().drainPrayer(drain * 10);
						player.getSkills().drainLevel(stat, drain);
					}
					int maxDamage = player.getMaxHitpoints() / 10;
					if (damage > maxDamage)
						damage = maxDamage;
					if (value == 6)
						player.getPackets().sendGameMessage("The undead rot can now be cleansed by the unholy font.");
					player.applyHit(new Hit(npc, damage, HitLook.REGULAR_DAMAGE));
					player.gfx(new Graphics(2440));
					target.getTemporaryAttributtes().put("UNHOLY_CURSEBEARER_ROT", value + 1);
				}

			}, 0, 12);
		}
		switch (attackStyle) {
		case 0:
			boolean multiTarget = Utils.random(2) == 0;
			npc.animate(new Animation(multiTarget ? 13176 : 13175));
			if (multiTarget) {
				npc.gfx(new Graphics(2441));
				for (Entity t : npc.getPossibleTargets()) {
					World.sendElementalProjectile(npc, t, 88);
					delayHit(npc, 1, t, getMagicHit(npc, getRandomMaxHit(npc, (int) (npc.getMaxHit() * 0.6), NPCCombatDefinitions.MAGE, t)));
				}
			} else {
				World.sendElementalProjectile(npc, target, 88);
				delayHit(npc, 1, target, getMagicHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MAGE, target)));
			}
			break;
		case 1:
			npc.animate(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
			break;
		}
		return npc.getAttackSpeed();
	}
}
