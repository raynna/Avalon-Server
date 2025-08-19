package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.meta.DragonFireShieldMetaData;
import com.rs.java.game.item.meta.ItemMetadata;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.player.Equipment;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;

public class LeatherDragonCombat extends CombatScript {

	private static final int[] DRAGON_SHIELDS = {11283, 11284, 1540};

	@Override
	public Object[] getKeys() {
		return new Object[] {"Green dragon", "Blue dragon", "Red dragon", "Black dragon",
				"Brutal green dragon", 742, 14548};
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		if (!isWithinMeleeRange(npc, target)) return 0;

		final NPCCombatDefinitions defs = npc.getCombatDefinitions();

		if (Utils.getRandom(3) != 0) {
			performMeleeAttack(npc, target, defs);
		} else {
			performDragonfireAttack(npc, target, defs);
		}

		return defs.getAttackDelay();
	}

	private boolean isWithinMeleeRange(NPC npc, Entity target) {
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		return distanceX <= size && distanceX >= -1 && distanceY <= size && distanceY >= -1;
	}

	private void performMeleeAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
		npc.animate(new Animation(defs.getAttackEmote()));
		delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
	}

	private void performDragonfireAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
		if (!(target instanceof Player player)) return;

		int damage = Utils.getRandom(650);
		npc.animate(new Animation(12259));
		npc.gfx(new Graphics(1, 0, 100));

		damage = applyDragonfireMitigation(player, damage);
		delayHit(npc, 1, player, getRegularHit(npc, damage));

		handleDragonfireShield(player);
	}

	private int applyDragonfireMitigation(Player player, int damage) {
		// Super antifire always takes priority
		if (player.getSuperAntifire() > 0) {
			player.getPackets().sendGameMessage("Your potion fully protects you from the heat of the dragon's breath.");
			return 0;
		}

		boolean shield = playerHasDragonShield(player);
		boolean antifire = player.getAntifire() > 0;
		boolean prayer = player.getPrayer().isMageProtecting();

		if (shield || antifire) {
			damage *= 0.1;
			String source = shield ? "shield" : "potion";

			if (prayer) {
				player.getPackets().sendGameMessage(
						"Your " + source + " and prayer fully protect you from the heat of the dragon's breath.");
				return 0;
			} else {
				player.getPackets().sendGameMessage(
						"Your " + source + " protects you from most of the dragon's breath.");
				return damage;
			}
		}

		if (prayer) {
			player.getPackets().sendGameMessage("Your prayer protects you from some of the heat of the dragon's breath!");
			damage *= 0.1;
			return damage;
		}

		player.getPackets().sendGameMessage("You are hit by the dragon's fiery breath!", true);
		return damage;
	}


	private boolean playerHasDragonShield(Player player) {
		int shieldId = player.getEquipment().getShieldId();
		for (int id : DRAGON_SHIELDS) {
			if (shieldId == id) return true;
		}
		return false;
	}

	private void handleDragonfireShield(Player player) {
		if (!player.getEquipment().containsOneItem(11283, 11284)) return;

		Item shield = player.getEquipment().getItem(Equipment.SLOT_SHIELD);
		if (shield == null) return;

		ItemMetadata meta = shield.getMetadata();
		if (meta == null) {
			if (shield.getId() == 11283) {
				shield.setMetadata(new DragonFireShieldMetaData(0));
			}
			if (shield.getId() == 11284) {
				shield.setId(11283);
				shield.setMetadata(new DragonFireShieldMetaData(0));
				player.getEquipment().refresh(Equipment.SLOT_SHIELD);
				player.getAppearence().generateAppearenceData();
			}
		}

		if (shield.getMetadata() instanceof DragonFireShieldMetaData) {
			if ((int) shield.getMetadata().getValue() < shield.getMetadata().getMaxValue()) {
				shield.getMetadata().increment(1);
				player.animate(new Animation(6695));
				player.gfx(new Graphics(1164, 1, 100));
				player.getPackets().sendGameMessage("Your dragonfire shield absorbs the dragon breath.");
				player.getEquipment().refresh(Equipment.SLOT_SHIELD);
			}
		}
	}
}
