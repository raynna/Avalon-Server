package com.rs.java.game.npc.combat.impl.dung;

import java.util.LinkedList;
import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.dungeoneering.RuneboundBehemoth;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class RuneboundBehemothCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ "Runebound behemoth" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final RuneboundBehemoth boss = (RuneboundBehemoth) npc;
		final DungeonManager manager = boss.getManager();

		boolean trample = false;
		for (Entity t : npc.getPossibleTargets()) {
			if (Utils.colides(t.getX(), t.getY(), t.getSize(), npc.getX(), npc.getY(), npc.getSize())) {
				trample = true;
				delayHit(npc, t, 0, getRegularHit(npc, npc.getMaxHit()));
				if (t instanceof Player)
					((Player) t).getPackets().sendGameMessage("The beast tramples you.");
			}
		}
		if (trample) {
			npc.animate(new Animation(14426));
			return 5;
		}

		if (Utils.random(15) == 0) {// Special attack
			final List<WorldTile> explosions = new LinkedList<WorldTile>();
			boss.setNextForceTalk(new ForceTalk("Raaaaaaaaaaaaaaaaaaaaaaaaaawr!"));
			WorldTasksManager.schedule(new WorldTask() {

				int cycles;

				@Override
				public void run() {
					cycles++;
					if (cycles == 1) {
						boss.gfx(new Graphics(2769));
					} else if (cycles == 4) {
						boss.gfx(new Graphics(2770));
					} else if (cycles == 5) {
						boss.gfx(new Graphics(2771));
						for (Entity t : boss.getPossibleTargets()) {
							tileLoop: for (int i = 0; i < 4; i++) {
								WorldTile tile = Utils.getFreeTile(t, 2);
								if (!manager.isAtBossRoom(tile))
									continue tileLoop;
								explosions.add(tile);
								World.sendProjectileToTile(boss, tile, 2414);
							}
						}
					} else if (cycles == 8) {
						for (WorldTile tile : explosions)
							World.sendGraphics(boss, new Graphics(2399), tile);
						for (Entity t : boss.getPossibleTargets()) {
							tileLoop: for (WorldTile tile : explosions) {
								if (t.getX() != tile.getX() || t.getY() != tile.getY())
									continue tileLoop;
								t.applyHit(new Hit(boss, (int) Utils.random(boss.getMaxHit() * .6, boss.getMaxHit()), HitLook.REGULAR_DAMAGE));
							}
						}
						boss.resetTransformation();
						stop();
						return;
					}
				}
			}, 0, 0);
			return 8;
		}
		int[] possibleAttacks = new int[]
		{ 0, 1, 2 };
		if (target instanceof Player) {
			Player player = (Player) target;
			if (player.getPrayer().isMeleeProtecting())
				possibleAttacks = new int[]
				{ 1, 2 };
			else if (player.getPrayer().isRangeProtecting())
				possibleAttacks = new int[]
				{ 0, 1 };
			else if (player.getPrayer().isMageProtecting())
				possibleAttacks = new int[]
				{ 0, 2 };
		}
		boolean distanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0);
		int attack = possibleAttacks[Utils.random(possibleAttacks.length)];
		if (attack == 0 && distanced)
			attack = possibleAttacks[1];
		switch (attack) {
		case 0://melee
			boss.animate(new Animation(14423));
			delayHit(npc, target, 0, npc.meleeHit(npc, npc.getMaxHit()));
			break;
		case 1://green exploding blob attack (magic)
			boss.animate(new Animation(14427));
			//boss.setNextGraphics(new Graphics(2413));
			World.sendElementalProjectile(npc, target, 2414);
			delayHit(npc, target, 1, npc.magicHit(npc, npc.getMaxHit()));
			target.gfx(new Graphics(2417, 80, 0));
			break;
		case 2://green blob attack (range)
			boss.animate(new Animation(14424));
			boss.gfx(new Graphics(2394));
			World.sendElementalProjectile(npc, target, 2395);
			delayHit(npc, target, 1, npc.rangedHit(npc, npc.getMaxHit()));
			target.gfx(new Graphics(2396, 80, 0));
			break;
		}
		return 6;
	}
}
