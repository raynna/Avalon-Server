package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.Player;
import com.rs.core.packets.packet.ButtonHandler;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class ChaosElementalCombat extends CombatScript {

	private static final int CHAOS_ELEMENTAL_ID = 3200;

	private static final Animation ATTACK_ANIMATION = new Animation(5443);
	private static final Animation TELEPORT_ANIMATION = new Animation(5442);
	private static final Graphics DISARM_GRAPHIC = new Graphics(999);

	private static final int PROJECTILE_DEFAULT = 1279;
	private static final int PROJECTILE_DISARM = 552;

	private static final int MAX_MAGIC_HIT = 220;
	private static final int MAX_MELEE_HIT = 240;

	private static final int SPECIAL_DISARM = 9;
	private static final int SPECIAL_TELEPORT = 10;

	@Override
	public Object[] getKeys() {
		return new Object[]{ CHAOS_ELEMENTAL_ID };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackOption = Utils.getRandom(10);

		if (attackOption <= 8) {
			performOffensiveAttack(npc, target, defs);
		} else if (attackOption == SPECIAL_DISARM) {
			performDisarmAttack(npc, target);
		} else if (attackOption == SPECIAL_TELEPORT) {
			performTeleportAttack(npc, target);
		}

		return defs.getAttackDelay();
	}


	/** Standard offensive attack: magic, ranged, or melee. */
	private void performOffensiveAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
		npc.animate(ATTACK_ANIMATION);

		int attackType = Utils.random(9); // 0–8
		switch (attackType) {
			case 0: case 1: case 2: case 3: case 4: // Magic
				int magicHit = NpcCombatCalculations.getRandomMaxHit(npc, MAX_MAGIC_HIT, NPCCombatDefinitions.MAGE, target);
				delayHit(npc, 2, target, getMagicHit(npc, magicHit));
				ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, PROJECTILE_DEFAULT, npc, target);
				break;

			case 5: case 6: case 7: case 8: // Range
				int rangeHit = NpcCombatCalculations.getRandomMaxHit(npc, MAX_MAGIC_HIT, NPCCombatDefinitions.RANGE, target);
				delayHit(npc, 2, target, getMagicHit(npc, rangeHit));
				ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, PROJECTILE_DEFAULT, npc, target);
				break;

			default: // Fallback melee
				int meleeHit = NpcCombatCalculations.getRandomMaxHit(npc, MAX_MELEE_HIT, NPCCombatDefinitions.MELEE, target);
				delayHit(npc, 2, target, getMeleeHit(npc, meleeHit));
				ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, PROJECTILE_DEFAULT, npc, target);
				break;
		}
	}

	/** Special attack: Disarm a random equipped item from the player. */
	private void performDisarmAttack(NPC npc, Entity target) {
		if (target instanceof Player player) {
			if (player.getInventory().hasFreeSlots()) {
				int slot = Utils.random(10);
				if (player.getEquipment().getItem(slot) != null) {
					ButtonHandler.sendTakeOff(player, slot, -1);
					player.message("The Chaos Elemental has disarmed your item (slot " + slot + ").");
				}
			}
		}
		npc.gfx(DISARM_GRAPHIC);
		npc.animate(ATTACK_ANIMATION);
		ProjectileManager.sendSimple(Projectile.ELEMENTAL_SPELL, PROJECTILE_DISARM, npc, target);
	}

	/** Special attack: Teleport the target randomly nearby. */
	private void performTeleportAttack(NPC npc, Entity target) {
		int baseX = npc.getX() + Utils.random(20);
		int baseY = npc.getY() + Utils.random(10);
		int direction = Utils.random(Utils.DIRECTION_DELTA_X.length);

		for (int i = 0; i < 10; i++) {
			if (World.checkWalkStep(target.getPlane(), baseX, baseY, direction, 1)) {
				WorldTile destination = new WorldTile(baseX, baseY, target.getPlane());
				npc.animate(TELEPORT_ANIMATION);
				target.setNextWorldTile(destination);
				break;
			}
		}
	}
}
