package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.player.Player;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class RevenantCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 13465, 13466, 13467, 13468, 13469, 13470, 13471, 13472, 13473, 13474, 13475, 13476, 13477,
				13478, 13479, 13480, 13481 };
	}

	public int getMagicAnimation(NPC npc) {
		switch (npc.getId()) {
		case 13465:
			return 7500;
		case 13466:
		case 13467:
		case 13468:
		case 13469:
			return 7499;
		case 13470:
		case 13471:
			return 7506;
		case 13472:
			return 7503;
		case 13473:
			return 7507;
		case 13474:
			return 7496;
		case 13475:
			return 7497;
		case 13476:
			return 7515;
		case 13477:
			return 7498;
		case 13478:
			return 7505;
		case 13479:
			return 7515;
		case 13480:
			return 7508;
		case 13481:
		default:
			// melee emote, better than 0
			return npc.getCombatDefinitions().getAttackEmote();
		}
	}

	public int getRangeAnimation(NPC npc) {
		switch (npc.getId()) {
		case 13465:
			return 7501;
		case 13466:
		case 13467:
		case 13468:
		case 13469:
			return 7513;
		case 13470:
		case 13471:
			return 7519;
		case 13472:
			return 7516;
		case 13473:
			return 7520;
		case 13474:
			return 7521;
		case 13475:
			return 7510;
		case 13476:
			return 7501;
		case 13477:
			return 7512;
		case 13478:
			return 7518;
		case 13479:
			return 7514;
		case 13480:
			return 7522;
		case 13481:
		default:
			// melee emote, better than 0
			return npc.getCombatDefinitions().getAttackEmote();
		}
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int timesHealed = 0;
		// Revenants will heal when they reach below 50% of their maximum
		// hitpoints
		// and will heal a certain percentage of hitpoints.
		if (timesHealed < 15) {// can only heal 15 times.
			if (npc.getHitpoints() < npc.getMaxHitpoints() * 0.30 && Utils.random(1) == 0) {
				npc.heal(npc.getMaxHitpoints() / 4);
				timesHealed++;
			}
		}
		int attackStyle = Utils.random(3);
		if (attackStyle == 2) { // checks if can melee
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			int size = npc.getSize();
			if ((distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1))
				attackStyle = Utils.random(2);
		}
		if (attackStyle != 2 && target instanceof Player) {
			Player targetPlayer = (Player) target;
			targetPlayer.getPackets().sendSound(202, 0, 1);
		}
		switch (attackStyle) {
		case 0: // magic
			int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MAGE, target);
			delayHit(npc, 2, target, getMagicHit(npc, damage));
			World.sendElementalProjectile(npc, target, 1276);
			if (damage > 0) {
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						target.gfx(new Graphics(1277, 0, 100));
						if (Utils.random(5) == 0) { // 1/5 prob freezing while
													// maging 5 seconds
							target.gfx(new Graphics(363));
							target.setFreezeDelay(5000);
						}
					}

				}, 2);
			}
			npc.animate(new Animation(getMagicAnimation(npc)));
			break;
		case 1: // range
			delayHit(npc, 2, target,
					getRangeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.RANGE, target)));
			World.sendSlowBowProjectile(npc, target, 1278);
			npc.animate(new Animation(getRangeAnimation(npc)));
			break;
		case 2: // melee
			delayHit(npc, 0, target,
					getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
			npc.animate(new Animation(defs.getAttackEmote()));
			break;
		}
		return defs.getAttackDelay();
	}
}
