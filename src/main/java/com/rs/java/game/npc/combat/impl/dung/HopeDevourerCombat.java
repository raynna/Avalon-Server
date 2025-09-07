package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.Graphics;
import com.rs.java.game.NewForceMovement;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.dungeonnering.HopeDevourer;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.game.player.prayer.NormalPrayer;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public class HopeDevourerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 12886 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final HopeDevourer boss = (HopeDevourer) npc;
		final DungeonManager manager = boss.getManager();

		boolean stomp = false;
		for (Player player : manager.getParty().getTeam()) {
			if (Utils.colides(player.getX(), player.getY(), player.getSize(), npc.getX(), npc.getY(), npc.getSize())) {
				stomp = true;
				delayHit(npc, player, 0, getRegularHit(npc, npc.getMaxHit()));
			}
		}
		if (stomp) {
			npc.animate(new Animation(14459));
			return 6;
		}

		if (Utils.random(10) == 0) {
			npc.setNextForceTalk(new ForceTalk("Grrrrrrrrrroooooooooaaaarrrrr"));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					npc.animate(new Animation(14460));
					npc.gfx(new Graphics(2844, 30, 0));
					int healedDamage = 0;
					for (Entity t : npc.getPossibleTargets()) {
						Player player = (Player) t;
						int damage = (int) Utils.random(npc.getMaxHit() * .85, npc.getMaxHit());
						if (damage > 0 && player.getPrayer().hasProtectionPrayerActive()) {
							healedDamage += damage;
							player.setPrayerDelay(1000);
							t.gfx(new Graphics(2845, 75, 0));
							delayHit(npc, t, 0, getMagicHit(npc, damage));
						}
					}
					npc.heal(healedDamage);
				}
			}, 2);
			return 8;
		}

		if (!Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0))
			return 0;

		if (Utils.random(5) == 0) {
			npc.animate(new Animation(14458));
			final int damage = (int) Utils.random(npc.getMaxHit() * .85, npc.getMaxHit());
			if (target instanceof Player) {
				Player player = (Player) target;
				player.getSkills().set(Skills.DEFENCE, (int) (player.getSkills().getLevel(Skills.DEFENCE) - (damage * .05)));
			}
			delayHit(npc, target, 0, getMeleeHit(npc, damage));
			WorldTasksManager.schedule(new WorldTask() {
				private int ticks;
				private WorldTile tile;

				@Override
				public void run() {
					ticks++;
					if (ticks == 1) {
						if (target instanceof Player) {
							Player player = (Player) target;
							player.lock(2);
							player.stopAll();
						}
						byte[] dirs = Utils.getDirection(npc.getDirection());
						for (int distance = 2; distance >= 0; distance--) {
							tile = new WorldTile(new WorldTile(target.getX() + (dirs[0] * distance), target.getY() + (dirs[1] * distance), target.getPlane()));
							if (World.isFloorFree(tile.getPlane(), tile.getX(), tile.getY()) && manager.isAtBossRoom(tile))
								break;
							else if (distance == 0)
								tile = new WorldTile(target);
						}
						target.faceEntity(boss);
						target.animate(new Animation(10070));
						target.setNextForceMovement(new NewForceMovement(target, 0, tile, 2, target.getDirection()));
					} else if (ticks == 2) {
						target.setNextWorldTile(tile);
						stop();
						return;
					}
				}
			}, 0, 0);
		} else {
			npc.animate(new Animation(14457));
			int damage = (int) Utils.random(npc.getMaxHit() * .75, npc.getMaxHit());
			if (target instanceof Player) {
				Player player = (Player) target;
				if (player.getPrayer().isActive(NormalPrayer.PROTECT_FROM_MELEE)) {
					player.getPackets().sendGameMessage("Your prayer completely negates the attack.", true);
					damage = 0;
				}
			}
			delayHit(npc, target, 0, getMeleeHit(npc, damage));
		}
		return 6;
	}
}
