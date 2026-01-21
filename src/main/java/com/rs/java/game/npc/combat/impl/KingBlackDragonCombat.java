package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.DragonFire;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class KingBlackDragonCombat extends CombatScript {

	private static final int DRAGON_SLAM_ANIMATION = 80, DRAGON_HEADBUTT_ANIMATION = 91, DRAGONFIRE_BREATH_ANIMATION = 84, DRAGON_DEATH_ANIMATION = 92;
	private static final int DRAGONFIRE_GFX = 1, DRAGONFIRE_TOXIC_PROJECTILE = 393, DRAGONFIRE_NORMAL_PROJECTILE = 394, DRAGONFIRE_ICY_PROJECTILE = 395, DRAGONFIRE_SHOCKING_PROJECTILE = 396;


	@Override
	public Object[] getKeys() {
		return new Object[]{50};
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final Player player = target instanceof Player ? (Player) target : null;

		int size = npc.getSize();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();

		boolean inMelee = distanceX <= size && distanceX >= -1 && distanceY <= size && distanceY >= -1;

		int attackStyle;
		if (inMelee) {
			attackStyle = Utils.getRandom(2) == 0 ? 0 : 1; // 1/3 melee, 2/3 breath
		} else {
			attackStyle = 1;
		}

		switch (attackStyle) {
			case 0: // Melee
				int meleeHit = NpcCombatCalculations.getRandomMaxHit(npc, 250, NpcAttackStyle.CRUSH, target);
				npc.animate(new Animation(Utils.roll(1, 2) ? DRAGON_SLAM_ANIMATION : DRAGON_HEADBUTT_ANIMATION));
				delayHit(npc, target, 0, getMeleeHit(npc, meleeHit));
				break;

			case 1: // Breath attacks
				if (player != null) {
					npc.animate(new Animation(DRAGONFIRE_BREATH_ANIMATION));
					int breathTypeRoll = Utils.random(4);

					int baseDamage = 500;
					int projectileId;
					boolean applySpecialEffect = false;

					switch (breathTypeRoll) {
						case 0: // Normal dragonfire
							projectileId = DRAGONFIRE_NORMAL_PROJECTILE;
							baseDamage = 650;
							break;
						case 1: // Toxic (poison)
							projectileId = DRAGONFIRE_TOXIC_PROJECTILE;
							applySpecialEffect = true;
							break;
						case 2: // Shocking
							projectileId = DRAGONFIRE_SHOCKING_PROJECTILE;
							applySpecialEffect = true;
							break;
						default: // Icy
							projectileId = DRAGONFIRE_ICY_PROJECTILE;
							applySpecialEffect = true;
							break;
					}

					int rawDamage = Utils.random(baseDamage);
					int damage = DragonFire.applyDragonfireMitigation(player, rawDamage, DragonFire.DragonType.KING_BLACK_DRAGON, applySpecialEffect);

					Hit dragonfire = npc.regularHit(target, damage);
					ProjectileManager.send(Projectile.DRAGONFIRE, projectileId, npc, target, () -> {
						applyRegisteredHit(npc, target, dragonfire);
						DragonFire.handleDragonfireShield(player);
					});

					if (applySpecialEffect) {
						applySpecialBreathEffect(player, breathTypeRoll - 1);
					}
				}
				break;
		}

		return npc.getCombatData().attackSpeedTicks;
	}

	private void applySpecialBreathEffect(Player player, int type) {
		switch (type) {
			case 0: // Toxic
				player.getNewPoison().startPoison(30);
				break;
			case 1: // Shocking
				for (Skills.SkillData skills : Skills.SkillData.values()) {
					if (skills.getId() == Skills.PRAYER || skills.getId() == Skills.HITPOINTS || skills.getId() == Skills.SUMMONING)
						continue;
					player.getSkills().drainLevel(skills.getId(), 2);
				}
				break;
			case 2: // Icy
				player.addFreezeDelay(10, false);
				break;
		}
	}
}
