package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.dungeoneering.DungeonBoss;
import com.rs.java.game.npc.dungeoneering.GluttonousBehemoth;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.game.player.content.dungeoneering.RoomReference;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class GluttonousBehemothCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ "Gluttonous behemoth" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		DungeonBoss boss = (DungeonBoss) npc;
		DungeonManager manager = boss.getManager();
		boolean lessThanHalf = npc.getHitpoints() < npc.getMaxHitpoints() * .5;
		if (lessThanHalf && npc.getTemporaryAttributtes().get("GLUTTONOUS_HEALING") == null) {
			RoomReference reference = manager.getCurrentRoomReference(npc);
			WorldObject food1 = manager.getObject(reference, 49283, 0, 11);
			WorldObject food2 = manager.getParty().getTeam().size() <= 1 ? null : manager.getObject(reference, 49283, 11, 11);
			WorldObject food = food1;
			if (food1 != null) {
				for (Player player : manager.getParty().getTeam()) {
					if (player.withinDistance(food1, food1.getDefinitions().getSizeX() + 1)) {
						food = null;
						break;
					}
				}
			}
			if (food == null && food2 != null) {
				food = food2;
				for (Player player : manager.getParty().getTeam()) {
					if (player.withinDistance(food2, food1.getDefinitions().getSizeX() + 1)) {
						food = null;
						break;
					}
				}
			}
			if (food != null) {
				npc.getTemporaryAttributtes().put("GLUTTONOUS_HEALING", true);
				food = null;
				((GluttonousBehemoth) npc).setHeal(food);
				return 0;
			}
		}
		boolean stomp = false;
		for (Player player : manager.getParty().getTeam()) {
			if (Utils.colides(player.getX(), player.getY(), player.getSize(), npc.getX(), npc.getY(), npc.getSize())) {
				stomp = true;
				delayHit(npc, player, 0, getRegularHit(npc, npc.getMaxHit()));
			}
		}
		if (stomp) {
			npc.animate(new Animation(13718));
			return npc.getAttackSpeed();
		}
		int attackStyle = Utils.getRandom(2);
		if (attackStyle == 2) {
			if (!Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0))
				attackStyle = Utils.getRandom(1);
			else {
				npc.animate(new Animation(defs.getAttackAnim()));
				delayHit(npc, target, 0, npc.meleeHit(npc, npc.getMaxHit()));
				return npc.getAttackSpeed();
			}
		}
		if (attackStyle == 0) {
			npc.animate(new Animation(13719));
			World.sendElementalProjectile(npc, target, 2612);
			Hit hit = npc.magicHit(target, npc.getMaxHit());
			delayHit(npc, target, 2, hit);
			if (hit.getDamage() != 0) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						target.gfx(new Graphics(2613));
					}
				}, 1);
			}
		} else if (attackStyle == 1) {
			npc.animate(new Animation(13721));
			World.sendElementalProjectile(npc, target, 2610);
			Hit hit = npc.rangedHit(target, npc.getMaxHit());
			delayHit(npc, target, 2, hit);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					target.gfx(new Graphics(2611));
				}
			}, 1);
		}
		return npc.getAttackSpeed();
	}
}
