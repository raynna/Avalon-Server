package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.dungeonnering.DungeonBoss;
import com.rs.java.game.npc.dungeonnering.IcyBones;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class IcyBonesCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ "Icy Bones" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		DungeonBoss boss = (DungeonBoss) npc;
		DungeonManager manager = boss.getManager();

		if (Utils.random(10) == 0) {
			npc.animate(new Animation(13791, 20));
			npc.gfx(new Graphics(2594));
			boolean mage = Utils.random(2) == 0;
			if (mage && Utils.random(3) == 0) {
				target.gfx(new Graphics(2597));
				target.setFreezeDelay(8);
			}
			if (mage)
				delayHit(npc, target, 2, npc.magicHit(npc, npc.getMaxHit()));
			else
				delayHit(npc, target, 2, npc.rangedHit(npc, npc.getMaxHit()));
			World.sendElementalProjectile(npc, target, 2595);
			return npc.getAttackSpeed();
		}
		if (Utils.random(3) == 0 && Utils.isOnRange(target.getX(), target.getY(), target.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0) && ((IcyBones) npc).sendSpikes()) {
			npc.gfx(new Graphics(2596));
			npc.animate(new Animation(13790));
			delayHit(npc, target, 0, npc.meleeHit(npc, npc.getMaxHit()));
			return npc.getAttackSpeed();
		}
		boolean onRange = false;
		for (Player player : manager.getParty().getTeam()) {
			if (Utils.isOnRange(player.getX(), player.getY(), player.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0)) {
				Hit hit = npc.meleeHit(target, npc.getMaxHit());
				if (hit.getDamage() != 0 && player.getPrayer().isMeleeProtecting())
					player.getPackets().sendGameMessage("Your prayer offers only partial protection against the attack.");
				delayHit(npc, player, 0, hit);
				onRange = true;
			}
		}
		if (onRange) {
			npc.animate(new Animation(defs.getAttackAnim(), 20));
			npc.gfx(new Graphics(defs.getAttackGfx()));
			return npc.getAttackSpeed();
		}
		return 0;
	}
}
