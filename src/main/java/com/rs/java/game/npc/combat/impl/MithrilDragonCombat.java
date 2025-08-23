package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.item.Item;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.prayer.AncientPrayer;
import com.rs.java.game.player.prayer.NormalPrayer;
import com.rs.java.utils.Utils;

public class MithrilDragonCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Mithril dragon" };
	}

	/**
	 * If in melee distance it should hit melee mostly, close range d-fire,
	 * mage, and range OCCASIONALLY If out of melee range it will auto range,
	 * mage, and longrange d fire | In rs its short range but honestly long
	 * range would look alot better
	 */
	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int damage = 0;
		boolean withinMeleeDist = npc.withinDistance(target, 2);

		if (withinMeleeDist) {
			switch (Utils.getRandom(5)) {
			case 0:// Melee - Putting 3x melee so the odds of it hitting a melee
					// attk is 3:5 like rs
			case 1:// Melee
			case 2:// Melee
				npc.animate(new Animation(14247));
				delayHit(npc, target, 0,
                        getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
				break;
			case 3:// Close range dragonfire ratio is about 1:5
				damage = Utils.getRandom(650);
				npc.animate(new Animation(14246));
				npc.gfx(new Graphics(2465));
				final Player player = target instanceof Player ? (Player) target : null;
				if (player.getEquipment().getShieldId() == 11283 || player.getEquipment().getShieldId() == 11284
						|| player.getEquipment().getShieldId() == 1540) {
					damage *= 0.1;
					player.getPackets().sendGameMessage("Your shield aborsbs most of the dragon fire!");
				} else if (player.getAntifire() > Utils.currentTimeMillis()) {
					damage *= 0.1;
					player.getPackets()
							.sendGameMessage("Your potion protects you from some of the heat of the dragon's breath!");
				} else if (player.getSuperAntifire() > Utils.currentTimeMillis()) {
					damage *= 0.1;
					player.getPackets()
							.sendGameMessage("Your potion fully protects you from the heat of the dragon's breath.");
				} else if (player.getPrayer().isActive(NormalPrayer.PROTECT_FROM_MISSILES) || player.getPrayer().isActive(AncientPrayer.DEFLECT_MISSILES)) {
					damage *= 0.1;
					player.getPackets()
							.sendGameMessage("Your prayer protects you from some of the heat of the dragon's breath!");
				} else if (player.getEquipment().getShieldId() != 11283 && player.getEquipment().getShieldId() != 11284
						&& player.getEquipment().getShieldId() != 1540
						&& player.getSuperAntifire() < Utils.currentTimeMillis()
						&& player.getAntifire() < Utils.currentTimeMillis() && !player.getPrayer().isActive(NormalPrayer.PROTECT_FROM_MISSILES)
						&& !player.getPrayer().isActive(AncientPrayer.DEFLECT_MISSILES))
					player.getPackets().sendGameMessage("You are hit by the dragon's fiery breath!", true);
				delayHit(npc, target, 1, getRegularHit(npc, damage));
				if (player.getEquipment().getShieldId() == 11283) {
					if (player.getDfsCharges() < 50) {
						player.animate(new Animation(6695));
						player.gfx(new Graphics(1164, 1, 100));
						player.setDfsCharges(player.getDfsCharges() + 1);
						player.addDFSDefence();
						player.getPackets().sendGameMessage("Your dragonfire shield absorbs the dragon breath");
					}
				}
				if (player.getEquipment().getShieldId() == 11284) {
					if (player.getDfsCharges() < 50) {
						player.getEquipment().getItems().set(5, new Item(11283));
						player.getAppearence().generateAppearenceData();
						player.animate(new Animation(6695));
						player.gfx(new Graphics(1164, 1, 100));
						player.setDfsCharges(player.getDfsCharges() + 1);
						player.addDFSDefence();
						player.getPackets().sendGameMessage("Your dragonfire shield absorbs the dragon breath");
					}
				}
				break;
			case 4:// Ranged Attack 1:5
				damage = Utils.getRandom(250);
				npc.animate(new Animation(14252));
				World.sendFastBowProjectile(npc, target, 12);
				delayHit(npc, target, 1, getRangeHit(npc, damage));
				break;
			case 5:// Magical Attack 1:5
				damage = Utils.getRandom(250);
				npc.animate(new Animation(14252));
				World.sendProjectile(npc, target, 2706, 28, 18, 50, 50, 0);
				delayHit(npc, target, 1, getMagicHit(npc, damage));
				break;
			}
			return defs.getAttackDelay();

		} else {

			switch (Utils.getRandom(3)) {
			/**
			 * When long dist the ratios are about 3:5 range - 1:5 dfire/mage
			 * The mith arrow is not same one in rs that drags use as the mage
			 * attk aswell but it looks fine so w/e
			 */
			case 0: // Range
				damage = Utils.getRandom(250);
				npc.animate(new Animation(14252));
				World.sendFastBowProjectile(npc, target, 12);
				delayHit(npc, target, 1, getRangeHit(npc, damage));
				break;
			case 1: // Longrange Dfire
				damage = Utils.getRandom(650);
				npc.animate(new Animation(14246));
				npc.gfx(new Graphics(2465));
				final Player player = target instanceof Player ? (Player) target : null;
				if (player.getEquipment().getShieldId() == 11283 || player.getEquipment().getShieldId() == 11284
						|| player.getEquipment().getShieldId() == 1540) {
					damage *= 0.1;
					player.getPackets().sendGameMessage("Your shield aborsbs most of the dragon fire!");
				} else if (player.getAntifire() > Utils.currentTimeMillis()) {
					damage *= 0.1;
					player.getPackets()
							.sendGameMessage("Your potion protects you from some of the heat of the dragon's breath!");
				} else if (player.getSuperAntifire() > Utils.currentTimeMillis()) {
					damage *= 0.1;
					player.getPackets()
							.sendGameMessage("Your potion fully protects you from the heat of the dragon's breath.");
				} else if (player.getPrayer().isActive(NormalPrayer.PROTECT_FROM_MISSILES) || player.getPrayer().isActive(AncientPrayer.DEFLECT_MISSILES)) {
					damage *= 0.1;
					player.getPackets()
							.sendGameMessage("Your prayer protects you from some of the heat of the dragon's breath!");
				} else if (player.getEquipment().getShieldId() != 11283 && player.getEquipment().getShieldId() != 11284
						&& player.getEquipment().getShieldId() != 1540
						&& player.getSuperAntifire() < Utils.currentTimeMillis()
						&& player.getAntifire() < Utils.currentTimeMillis() && !player.getPrayer().isActive(NormalPrayer.PROTECT_FROM_MISSILES)
						&& !player.getPrayer().isActive(AncientPrayer.DEFLECT_MISSILES))
					player.getPackets().sendGameMessage("You are hit by the dragon's fiery breath!", true);
				delayHit(npc, target, 1, getRegularHit(npc, damage));
				if (player.getEquipment().getShieldId() == 11283) {
					if (player.getDfsCharges() < 50) {
						player.animate(new Animation(6695));
						player.gfx(new Graphics(1164, 1, 100));
						player.setDfsCharges(player.getDfsCharges() + 1);
						player.addDFSDefence();
						player.getPackets().sendGameMessage("Your dragonfire shield absorbs the dragon breath");
					}
				}
				if (player.getEquipment().getShieldId() == 11284) {
					player.getEquipment().getItems().set(5, new Item(11283));
					player.getAppearence().generateAppearenceData();
					if (player.getDfsCharges() < 50) {
						player.animate(new Animation(6695));
						player.gfx(new Graphics(1164, 1, 100));
						player.setDfsCharges(player.getDfsCharges() + 1);
						player.addDFSDefence();
						player.getPackets().sendGameMessage("Your dragonfire shield absorbs the dragon breath");
					}
				}
				break;
			case 2: // Mage Hit
				damage = Utils.getRandom(250);
				npc.animate(new Animation(14252));
				World.sendProjectile(npc, target, 2706, 28, 18, 50, 50, 0);
				delayHit(npc, target, 1, getMagicHit(npc, damage));
				break;
			case 3: // Range
				damage = Utils.getRandom(250);
				npc.animate(new Animation(14252));
				World.sendFastBowProjectile(npc, target, 12);
				delayHit(npc, target, 1, getRangeHit(npc, damage));
				break;
			}
			return defs.getAttackDelay();
		}

	}

}
