package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.meta.DragonFireShieldMetaData;
import com.rs.java.game.item.meta.ItemMetadata;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.DragonFire;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.Equipment;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.utils.Utils;
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
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
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
				int meleeHit = NpcCombatCalculations.getRandomMaxHit(npc, 250, NPCCombatDefinitions.MELEE, target);
				npc.animate(new Animation(Utils.roll(1, 2) ? DRAGON_SLAM_ANIMATION : DRAGON_HEADBUTT_ANIMATION));
				delayHit(npc, 0, target, getMeleeHit(npc, meleeHit));
				break;

			case 1: // Breath attacks
				if (player != null) {
					int specialChance = inMelee ? 1 : 0;
					int breathTypeRoll = Utils.random(2 + specialChance); // 0 normal, 1-2 special

					int rawDamage;
					int projectileId;
					boolean applySpecialEffect = false;

					switch (breathTypeRoll) {
						case 0: // Normal dragonfire
							rawDamage = NpcCombatCalculations.getRandomMaxHit(npc, 650, NPCCombatDefinitions.MAGE, player);
							projectileId = DRAGONFIRE_NORMAL_PROJECTILE;
							break;
						case 1: // Toxic (poison)
							rawDamage = NpcCombatCalculations.getRandomMaxHit(npc, 500, NPCCombatDefinitions.MAGE, player);
							projectileId = DRAGONFIRE_TOXIC_PROJECTILE;
							applySpecialEffect = true;
							break;
						case 2: // Shocking
							rawDamage = NpcCombatCalculations.getRandomMaxHit(npc, 500, NPCCombatDefinitions.MAGE, player);
							projectileId = DRAGONFIRE_SHOCKING_PROJECTILE;
							applySpecialEffect = true;
							break;
						default: // Icy
							rawDamage = NpcCombatCalculations.getRandomMaxHit(npc, 500, NPCCombatDefinitions.MAGE, player);
							projectileId = DRAGONFIRE_ICY_PROJECTILE;
							applySpecialEffect = true;
							break;
					}

					int damage = DragonFire.applyDragonfireMitigation(player, rawDamage);

					npc.animate(new Animation(DRAGONFIRE_BREATH_ANIMATION));
					ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, projectileId, npc, target);
					delayHit(npc, Utils.getDistance(npc, target) > 2 ? 2 : 1, target, getRegularHit(npc, damage));
					DragonFire.handleDragonfireShield(player);

					if (applySpecialEffect) {
						applySpecialBreathEffect(player, breathTypeRoll - 1);
					}
				}
				break;
		}

		return defs.getAttackDelay();
	}

	private void applySpecialBreathEffect(Player player, int type) {
		switch (type) {
			case 0: // Toxic
				player.getNewPoison().startPoison(30);
				break;
			case 1: // Shocking
				for (Skills.SkillData skills : Skills.SkillData.values()) {
					player.getSkills().drainLevel(skills.getId(), 2);
				}
				break;
			case 2: // Icy
				player.addFreezeDelay(10, false);
				break;
		}
	}
}
