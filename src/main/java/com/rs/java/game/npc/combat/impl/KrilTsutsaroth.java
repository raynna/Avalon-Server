package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.Equipment;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.prayer.AncientPrayer;
import com.rs.java.game.player.prayer.NormalPrayer;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class KrilTsutsaroth extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]{6203}; // K'ril Tsutsaroth ID
	}

	private static final int SPECIAL_ANIMATION = 14968;
	private static final int MELEE_ANIMATION = 14963;
	private static final int MAGIC_ANIMATION = 14962;
	private static final int MAGIC_GFX = 1210;
	private static final int MAGIC_PROJECTILE = 1211;
	private static final int MELEE_LEFT_SWING = 14374;
	private static final int MELEE_RIGHT_SWING = 14375;

	enum KrilTsutsarothAttack { MELEE, MAGIC, SPECIAL }

	@Override
	public int attack(NPC npc, Entity target) {
		maybeShout(npc);
		KrilTsutsarothAttack attack = Utils.randomWeighted(KrilTsutsarothAttack.MELEE, 66, KrilTsutsarothAttack.MAGIC, 33);
		boolean special = Utils.roll(1, 9);
		if (target instanceof Player player) {
			if (special && player.getPrayer().hasProtectFromMelee()) {
				performSpecialAttack(npc, target);
				return npc.getCombatData().attackSpeedTicks;
			}
		}
		switch (attack) {
			case MELEE -> performMeleeAttack(npc, target);
			case MAGIC -> performMagicFlameAttack(npc);
		}
		return npc.getCombatData().attackSpeedTicks;
	}

	private void performMagicFlameAttack(NPC npc) {
		npc.animate(MAGIC_ANIMATION);
		npc.gfx(MAGIC_GFX);

		for (Entity t : npc.getPossibleTargets()) {
			int damage = Utils.random(100, 300);
			Hit magicHit = npc.magicHit(t, damage);
			ProjectileManager.send(Projectile.KRIL_TSUTSAROTH, MAGIC_PROJECTILE, npc, t, () -> {
				applyRegisteredHit(npc, t, magicHit);
			});
		}
	}

	public void performSpecialAttack(NPC npc, Entity target) {
		if (target instanceof Player p2) {
			npc.animate(SPECIAL_ANIMATION);
			npc.setNextForceTalk(new ForceTalk("YARRRRRRR!"));

			int damage = Utils.random(350, 490);
			Hit specialHit = npc.regularHit(p2, damage);
			delayHit(npc, target, 1, specialHit);
			if (p2.getPrayer().hasPrayerPoints()) {
				int drainAmount = p2.getEquipment().getItem(Equipment.SLOT_SHIELD).isItem("item.spectral_spirit_shield") ? 4 : 2;
				p2.getPrayer().drainPrayer(p2.getPrayer().getPrayerPoints() / drainAmount);
				p2.getPackets().sendGameMessage(
						"K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.");
			}
		}
	}

	private void performMeleeAttack(NPC npc, Entity target) {
		int[] attackAnims = { MELEE_LEFT_SWING, MELEE_RIGHT_SWING };
		int anim = attackAnims[Utils.random(attackAnims.length)];
		boolean poison = Utils.roll(1, 4);
		if (poison)
			target.getNewPoison().startPoison(80);
		npc.animate(anim);
		Hit meleeHit = npc.meleeHit(target, 460, NpcAttackStyle.SLASH);
		delayHit(npc, target, 0, meleeHit);
	}

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
}
