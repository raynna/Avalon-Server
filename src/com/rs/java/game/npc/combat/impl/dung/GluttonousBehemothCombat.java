package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.WorldObject;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.dungeonnering.DungeonBoss;
import com.rs.java.game.npc.dungeonnering.GluttonousBehemoth;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.game.player.content.dungeoneering.RoomReference;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class GluttonousBehemothCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ "Gluttonous behemoth" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
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
				delayHit(npc, 0, player, getRegularHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MELEE, player)));
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
				npc.animate(new Animation(defs.getAttackEmote()));
				delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
				return npc.getAttackSpeed();
			}
		}
		if (attackStyle == 0) {
			npc.animate(new Animation(13719));
			World.sendElementalProjectile(npc, target, 2612);
			int damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MAGE, target);
			delayHit(npc, 2, target, getMagicHit(npc, damage));
			if (damage != 0) {
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
			delayHit(npc, 2, target, getRangeHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.RANGE, target)));
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
