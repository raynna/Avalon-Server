package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class KetZekCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Ket-Zek", 15207 };
	}// anims: DeathEmote: 9257 DefEmote: 9253 AttackAnim: 9252 gfxs: healing:
		// 444 - healer

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		int distanceX = target.getX() - npc.getX();
		int distanceY = target.getY() - npc.getY();
		int size = npc.getSize();
		int hit = 0;
		if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1) {
			commenceMagicAttack(npc, target, hit);
			return npc.getAttackSpeed();
		}
		int attackStyle = Utils.getRandom(1);
		switch (attackStyle) {
		case 0:
			hit = NpcCombatCalculations.getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.CRUSH, target);
			npc.animate(new Animation(defs.getAttackAnim()));
			delayHit(npc, target, 0, getMeleeHit(npc, hit));
			break;
		case 1:
			commenceMagicAttack(npc, target, hit);
			break;
		}
		return npc.getAttackSpeed();
	}

	private void commenceMagicAttack(final NPC npc, final Entity target, int hit) {
		hit = NpcCombatCalculations.getRandomMaxHit(npc, npc.getCombatDefinitions().getMaxHit() - 50, NpcAttackStyle.MAGIC, target);
		npc.animate(new Animation(16136));
		// npc.setNextGraphics(new Graphics(1622, 0, 96 << 16));
		World.sendElementalProjectile(npc, target, 2984);
		delayHit(npc, target, 2, getMagicHit(npc, hit));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				target.gfx(new Graphics(2983, 0, 96 << 16));
			}
		}, 2);
	}
}
