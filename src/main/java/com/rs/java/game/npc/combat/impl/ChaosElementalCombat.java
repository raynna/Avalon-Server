package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.*;
import com.rs.java.game.item.Item;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.player.Player;
import com.rs.core.packets.packet.ButtonHandler;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class ChaosElementalCombat extends CombatScript {

	private static final int CHAOS_ELEMENTAL_ID = 3200;

	private static final Animation ATTACK_ANIMATION = new Animation(5443);
	private static final Animation TELEPORT_ANIMATION = new Animation(5442);
	private static final Graphics SPECIAL_HIT = new Graphics(552, 100);

	private static final int PROJECTILE_DEFAULT = 1279;
	private static final int PROJECTILE_SPECIAL = 1273;

	private static final int MAX_MAGIC_HIT = 220;
	private static final int MAX_MELEE_HIT = 240;

	enum ChaosElementalAttack { SPECIAL, MAGIC, RANGE, MELEE }

	enum SpecialTypes { DISARM, TELEPORT }

	private static final SpecialTypes[] SPECIAL_TYPES = {
			SpecialTypes.DISARM,
			SpecialTypes.TELEPORT
	};

	@Override
	public Object[] getKeys() {
		return new Object[]{ CHAOS_ELEMENTAL_ID };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		boolean inMelee = npc.isWithinMeleeRange(target);

		ChaosElementalAttack attack;
		if (inMelee) {
			attack = Utils.randomWeighted(
					ChaosElementalAttack.MELEE, 45,
					ChaosElementalAttack.MAGIC, 25,
					ChaosElementalAttack.RANGE, 20,
					ChaosElementalAttack.SPECIAL, 10
			);
		} else {
			attack = Utils.randomWeighted(
					ChaosElementalAttack.MAGIC, 55,
					ChaosElementalAttack.RANGE, 35,
					ChaosElementalAttack.SPECIAL, 10
			);
		}

		npc.animate(ATTACK_ANIMATION);
		switch (attack) {
			case MELEE:
				Hit meleeHit = npc.meleeHit(target, MAX_MELEE_HIT);
				delayHit(npc, target, 0, meleeHit);
				break;
			case RANGE:
				Hit rangeHit = npc.rangedHit(target, MAX_MAGIC_HIT);
				ProjectileManager.send(Projectile.CHAOS_ELEMENTAL, PROJECTILE_DEFAULT, npc, target, () -> {
					applyRegisteredHit(npc, target, rangeHit);
				});
				break;
			case MAGIC:
				Hit magicHit = npc.magicHit(target, MAX_MAGIC_HIT);
				ProjectileManager.send(Projectile.CHAOS_ELEMENTAL, PROJECTILE_DEFAULT, npc, target, () -> {
					applyRegisteredHit(npc, target, magicHit);
				});
				break;
			case SPECIAL:
				SpecialTypes special = SPECIAL_TYPES[Utils.random(SPECIAL_TYPES.length)];
				switch (special) {
					case TELEPORT -> performTeleportAttack(npc, target);
					case DISARM -> performDisarmAttack(npc, target);
				}
				break;
		}
		return npc.getAttackSpeed();
	}

	/** Special attack: Disarm a random equipped item from the player. */
	private void performDisarmAttack(NPC npc, Entity target) {
		ProjectileManager.send(Projectile.CHAOS_ELEMENTAL, PROJECTILE_SPECIAL, npc, target, () -> {
			if (target instanceof Player player) {
				target.gfx(SPECIAL_HIT);
				if (player.getInventory().hasFreeSlots()) {
					int slot = Utils.random(10);
					Item item = player.getEquipment().getItem(slot);
					if (item != null) {
						player.message("The Chaos Elemental has disarmed your " + item.getName() + ".");
						ButtonHandler.sendTakeOff(player, slot, -1);
					}
				}
			}
		});
	}

	/** Special attack: Teleport the target randomly nearby. */
	private void performTeleportAttack(NPC npc, Entity target) {
		ProjectileManager.send(Projectile.CHAOS_ELEMENTAL, PROJECTILE_SPECIAL, npc, target, () -> {

			if (!(target instanceof Player))
				return;

			target.gfx(SPECIAL_HIT);
			npc.animate(TELEPORT_ANIMATION);

			target.moveRandom(6);
		});
	}

}
