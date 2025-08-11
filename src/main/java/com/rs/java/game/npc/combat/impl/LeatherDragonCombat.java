package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.meta.DragonFireShieldMetaData;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.player.Equipment;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;

public class LeatherDragonCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Green dragon", "Blue dragon", "Red dragon", "Black dragon", "Brutal green dragon", 742,
				14548 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1)
			return 0;
		if (Utils.getRandom(3) != 0) {
			npc.animate(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target,
					getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
		} else {
			int damage = Utils.getRandom(650);
			npc.animate(new Animation(12259));
			npc.gfx(new Graphics(1, 0, 100));
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
				damage *= 0.0;
				player.getPackets()
						.sendGameMessage("Your potion fully protects you from the heat of the dragon's breath.");
			} else if (player.getPrayer().isMageProtecting()) {
				damage *= 0.1;
				player.getPackets()
						.sendGameMessage("Your prayer protects you from some of the heat of the dragon's breath!");
			} else if (player.getEquipment().getShieldId() != 11283 && player.getEquipment().getShieldId() != 11284
					&& player.getEquipment().getShieldId() != 1540
					&& player.getSuperAntifire() < Utils.currentTimeMillis()
					&& player.getAntifire() < Utils.currentTimeMillis() && !player.getPrayer().isMageProtecting())
				player.getPackets().sendGameMessage("You are hit by the dragon's fiery breath!", true);
			delayHit(npc, 1, target, getRegularHit(npc, damage));

			if (player.getEquipment().containsOneItem(11284, 11283)) {
				Item shield = player.getEquipment().getItem(Equipment.SLOT_SHIELD);

				// If shield is uncharged (11284), convert to charged version (11283)
				if (shield != null && shield.getId() == 11284) {
					shield.setId(11283);
					shield.setMetadata(new DragonFireShieldMetaData(0));
					player.getAppearence().generateAppearenceData();
				}

				// Ensure the shield has charge metadata
				if (shield.getMetadata() == null || !(shield.getMetadata() instanceof DragonFireShieldMetaData)) {
					shield.setMetadata(new DragonFireShieldMetaData(0));
				}

				// Increment charges
				((DragonFireShieldMetaData) shield.getMetadata()).increment(1);

				// Play animation & feedback
				player.animate(new Animation(6695));
				player.gfx(new Graphics(1164, 1, 100));
				player.getPackets().sendGameMessage("Your dragonfire shield absorbs the dragon breath.");
			}
		}
		return defs.getAttackDelay();
	}
}
