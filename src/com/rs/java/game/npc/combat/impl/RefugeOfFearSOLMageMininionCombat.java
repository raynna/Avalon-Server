package com.rs.java.game.npc.combat.impl;

import java.util.Random;

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

public class RefugeOfFearSOLMageMininionCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 15172 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		boolean player = target instanceof Player; // shouldn't have player as
													// target unless bind spell.
		startAttack(npc, target, player, player ? 4 : new Random().nextInt(4));
		return defs.getAttackDelay();
	}

	private void startAttack(final NPC npc, final Entity entity, boolean player, int attack) {
		switch (attack) {
		case 0: // Vengeance other - to be casted on one of the minions.
			if (entity.temporaryAttribute().get("vengeance_activated") == Boolean.TRUE) {
				startAttack(npc, entity, player, new Random().nextInt(3) + 1);
				return;
			}
			npc.animate(new Animation(4411));
			entity.gfx(new Graphics(725, 0, 96));
			break;
		case 1: // Heal other - to be casted on one of the minions.
		case 2: // ?
		case 3: // ?
		case 4: // Entangle - to be casted on the player.
			if (!player) {
				startAttack(npc, entity, player, new Random().nextInt(4));
				return;
			}
			final Player p = (Player) entity;
			npc.gfx(new Graphics(177, 0, 96));
			npc.animate(new Animation(710));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					super.stop();
					p.gfx(new Graphics(179, 0, 96));
				}
			}, 2);
			p.addFreezeDelay(20000, true);
			World.sendElementalProjectile(npc, p, 178);
			break;
		}
	}
}
