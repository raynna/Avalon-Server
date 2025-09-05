package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.prayer.AncientPrayer;
import com.rs.java.game.player.prayer.NormalPrayer;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class KrilTsutsaroth extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]{6203}; // K'ril Tsutsaroth ID
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();

		maybeShout(npc);

		int style = Utils.random(3);
		switch (style) {
			case 0 -> performMagicFlameAttack(npc);
			case 1 -> performMeleeAttack(npc, target, defs, false);
			case 2 -> performMeleeAttack(npc, target, defs, true);
		}

		return npc.getCombatData().attackSpeedTicks;
	}

	// --------------------------
	// Shouts
	// --------------------------
	private void maybeShout(NPC npc) {
		if (Utils.random(4) != 0) {
			return;
		}

		int roll = Utils.random(8);
		switch (roll) {
			case 0 -> shout(npc, "Attack them, you dogs!", 3282);
			case 1 -> shout(npc, "Forward!", 3276);
			case 2 -> shout(npc, "Death to Saradomin's dogs!", 3277);
			case 3 -> shout(npc, "Kill them, you cowards!", 3290);
			case 4 -> shout(npc, "The Dark One will have their souls!", 3229);
			case 5 -> shout(npc, "Zamorak curse them!", 3270);
			case 6 -> shout(npc, "Rend them limb from limb!", 3273);
			case 7 -> shout(npc, "Flay them all!", 3279);
		}
	}

	private void shout(NPC npc, String text, int soundId) {
		npc.setNextForceTalk(new ForceTalk(text));
		npc.playSound(soundId, 2);
	}

	// --------------------------
	// Magic flame attack
	// --------------------------
	private void performMagicFlameAttack(NPC npc) {
		npc.animate(new Animation(14962));
		npc.gfx(new Graphics(1210));

		for (Entity t : npc.getPossibleTargets()) {
			Hit magicHit = getMagicHit(npc,
					NpcCombatCalculations.getRandomMaxHit(npc, 300, NpcAttackStyle.MAGIC, t)
			);
			delayHit(npc, t, 1, magicHit);

			ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, 1211, npc, t);

			if (Utils.random(4) == 0) {
				t.getPoison().makePoisoned(168);
			}
		}
	}

	// --------------------------
	// Melee attacks
	// --------------------------
	private void performMeleeAttack(NPC npc, Entity target, NPCCombatDefinitions defs, boolean smiteStyle) {
		int damage = 463;
		int anim = 14963;

		if (smiteStyle && target instanceof Player p2) {
			if ((p2.getPrayer().isActive(NormalPrayer.PROTECT_FROM_MELEE)
					|| p2.getPrayer().isActive(AncientPrayer.DEFLECT_MELEE))
					&& Utils.random(2) == 0) {

				p2.getPrayer().drainPrayer(damage / 2);
				damage = 597;
				anim = 14968;

				npc.setNextForceTalk(new ForceTalk("YARRRRRRR!"));
				p2.getPackets().sendGameMessage(
						"K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.");
			}
		}

		int[] attackAnims = {14374, 14375};
		if (!smiteStyle) {
			anim = attackAnims[Utils.random(attackAnims.length)];
		}

		npc.animate(new Animation(anim));

		Hit meleeHit = getMeleeHit(npc,
				NpcCombatCalculations.getRandomMaxHit(npc, damage, NpcAttackStyle.SLASH, target)
		);
		delayHit(npc, target, 0, meleeHit);
	}
}
