package com.rs.java.game.npc.combat.impl.dung;

import java.util.LinkedList;
import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombatDefinitions;
import com.rs.java.game.npc.dungeonnering.LakkTheRiftSplitter;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class LakkTheRiftSplitterCombat extends CombatScript {

	private static final String[] MESSAGES =
	{ "A flame portal will flush you out!", "Taste miasma!", "This will cut you down to size!" };

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 9898 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final LakkTheRiftSplitter boss = (LakkTheRiftSplitter) npc;

		DungeonManager manager = boss.getManager();

		boolean smash = false;
		for (Player player : manager.getParty().getTeam()) {
			if (Utils.colides(player.getX(), player.getY(), player.getSize(), npc.getX(), npc.getY(), npc.getSize())) {
				smash = true;
				player.setPrayerDelay(1000);
				delayHit(npc, player, 0, getRegularHit(npc, getRandomMaxHit(npc, (int) (npc.getMaxHit() * .85), NPCCombatDefinitions.MELEE, player)));
				delayHit(npc, player, 0, getRegularHit(npc, getRandomMaxHit(npc, (int) (npc.getMaxHit() * .60), NPCCombatDefinitions.MELEE, player)));
			}
		}
		if (smash) {
			npc.animate(new Animation(14383));
			return 5;
		}

		if (Utils.random(4) == 0) {
			final int type = Utils.random(3);
			switch (type) {
			case 0:
			case 1:
			case 2:
				final List<WorldTile> boundary = new LinkedList<WorldTile>();
				for (int x = -1; x < 2; x++) {//3x3 area
					for (int y = -1; y < 2; y++) {
						boundary.add(target.transform(x, y, 0));
					}
				}
				if (boss.doesBoundaryOverlap(boundary)) {
					regularMagicAttack(target, npc);
					return 5;
				}
				//npc.playSoundEffect(VOICES[type]);
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						boss.setNextForceTalk(new ForceTalk(MESSAGES[type]));
						boss.animate(new Animation(14398));
						boss.addPortalCluster(type, boundary.toArray(new WorldTile[1]));
					}
				}, 1);
				return 5;
			}
		}

		//melee or magic
		boolean onRange = Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0);
		boolean melee = onRange && Utils.random(2) == 0;
		if (melee) {
			npc.animate(new Animation(14375));
			delayHit(npc, target, 0, getMeleeHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
		} else
			regularMagicAttack(target, npc);
		return 5;
	}

	private void regularMagicAttack(Entity target, NPC npc) {
		npc.animate(new Animation(14398));
		World.sendElementalProjectile(npc, target, 2579);
		if (target instanceof Player) {
			Player player = (Player) target;
			int damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MAGE, player);
			if (player.getPrayer().getPrayerPoints() > 0 && player.getPrayer().isMageProtecting()) {
				player.getPrayer().drainPrayer((int) (damage * .5));
				player.getPackets().sendGameMessage("Your prayer points feel drained.");
			} else
				delayHit(npc, player, 1, getMagicHit(npc, damage));
		}
		target.gfx(new Graphics(2580, 75, 0));
	}
}
