package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.dungeonnering.BalLakThePummeler;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class BalLakThePummelerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ "Bal'lak the Pummeller" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final BalLakThePummeler boss = (BalLakThePummeler) npc;
		final DungeonManager manager = boss.getManager();

		final NpcCombatDefinition defs = npc.getCombatDefinitions();

		boolean smash = Utils.random(5) == 0 && boss.getPoisionPuddles().size() == 0;
		for (Player player : manager.getParty().getTeam()) {
			if (Utils.colides(player.getX(), player.getY(), player.getSize(), npc.getX(), npc.getY(), npc.getSize())) {
				smash = true;
				Hit hit = getRegularHit(npc, defs.getMaxHit());
				delayHit(npc, player, 0, hit);
			}
		}
		if (smash) {
			npc.animate(new Animation(14384));
			npc.setNextForceTalk(new ForceTalk("Rrrraargh!"));
			//npc.playSoundEffect(3038);
			final WorldTile center = manager.getRoomCenterTile(boss.getReference());
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					for (int i = 0; i < 3; i++)
						boss.addPoisionBubble(Utils.getFreeTile(center, 6));
				}
			}, 1);
			return npc.getAttackSpeed();
		}

		if (Utils.random(5) == 0) {
			boss.animate(new Animation(14383));
			for (Entity t : boss.getPossibleTargets()) {
				if (!Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), t.getX(), t.getY(), t.getSize(), 0))
					continue;
				Hit hit = npc.meleeHit(npc, defs.getMaxHit());
				Hit hit2 = npc.meleeHit(npc, defs.getMaxHit());
				if (t instanceof Player) {
					Player player = (Player) t;
					if ((hit.getDamage() > 0 || hit2.getDamage() > 0)) {
						player.setPrayerDelay(1000);
						player.getPackets().sendGameMessage("You are injured and currently cannot use protection prayers.");
					}
				}
				delayHit(npc, t, 0, hit, hit2);
			}
			return npc.getAttackSpeed();
		}

		switch (Utils.random(2)) {
		case 0://reg melee left

			final boolean firstHand = Utils.random(2) == 0;

			boss.animate(new Animation(firstHand ? defs.getAttackAnim() : defs.getAttackAnim() + 1));
			delayHit(npc, target, 0, npc.meleeHit(npc, (int) (npc.getMaxHit() * 0.8)));
			delayHit(npc, target, 2, npc.meleeHit(npc, (int) (npc.getMaxHit() * 0.8)));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					boss.animate(new Animation(firstHand ? defs.getAttackAnim() + 1 : defs.getAttackAnim()));
				}

			}, 1);
			break;
		case 1://magic attack multi
			boss.animate(new Animation(14380));
			boss.gfx(new Graphics(2441));
			for (Entity t : npc.getPossibleTargets()) {
				World.sendProjectileToTile(npc, t, 2872);
				delayHit(npc, t, 1, npc.magicHit(npc, (int) (boss.getMaxHit() * 0.6)));
			}
			return npc.getAttackSpeed() - 2;
		}

		return npc.getAttackSpeed();
	}
}
