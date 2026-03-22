package raynna.game.npc.combat.impl.dung;

import java.util.LinkedList;
import java.util.List;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.ForceTalk;
import raynna.game.Graphics;
import raynna.game.Hit;
import raynna.game.Hit.HitLook;
import raynna.game.World;
import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.dungeoneering.Blink;
import raynna.game.player.Player;
import raynna.game.player.content.dungeoneering.DungeonManager;
import raynna.game.player.prayer.NormalPrayer;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.util.Utils;

public class BlinkCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 12865 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final Blink boss = (Blink) npc;
		DungeonManager manager = boss.getManager();

		if (Utils.random(10) == 0 || boss.isSpecialRequired()) {
			boss.setSpecialRequired(false);
			boss.setNextForceTalk(new ForceTalk("H...Here it comes..."));
			//boss.playSoundEffect(2989);
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					boss.animate(new Animation(14956));
					boss.setNextForceTalk(new ForceTalk("Kapow!!"));
					//boss.playSoundEffect(3002);
					for (Entity t : boss.getPossibleTargets()) {
						if (t instanceof Player)
							((Player) t).getPackets().sendGameMessage("You are hit by a powerful magical blast.");
						t.gfx(new Graphics(2855, 0, 50));
						delayHit(boss, t, 0, new Hit(boss, (int) Utils.random(boss.getMaxHit() * .6D, boss.getMaxHit()), HitLook.MAGIC_DAMAGE));
					}
				}
			}, 5);
			return 8;
		}

		boolean atDistance = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0);
		if (atDistance || Utils.random(3) == 0) {
			boolean rangeAttack = Utils.random(3) == 0;

			if (rangeAttack) {
				if (manager.getParty().getTeam().size() > 1 || Utils.random(3) == 0) {
					WorldTile beginningTile = boss.getNextPath();
					boss.animate(new Animation(14949));
					boss.setNextFaceEntity(null);
					boss.setNextFaceWorldTile(beginningTile);//Faces the direction it throws into
					World.sendProjectileToTile(boss, beginningTile, 2853);
					WorldTasksManager.schedule(new WorldTask() {

						private List<WorldTile> knifeTargets;
						private int cycles;

						@Override
						public void run() {
							cycles++;
							if (cycles == 1) {
								knifeTargets = new LinkedList<WorldTile>();
								for (Entity t : boss.getPossibleTargets()) {
									WorldTile center = new WorldTile(t);
									for (int i = 0; i < 3; i++)
										knifeTargets.add(i == 0 ? center : Utils.getFreeTile(center, 1));
								}
							} else if (cycles == 2) {
								for (WorldTile tile : knifeTargets) {
									//outdated method projectile
									int delay = 3;
									entityLoop: for (Entity t : boss.getPossibleTargets()) {
										if (!t.matches(tile))
											continue entityLoop;
										delayHit(boss, t, delay, npc.rangedHit(boss, boss.getMaxHit()));
									}
								}
								stop();
								return;
							}
						}
					}, 0, 0);
				} else {
					boss.animate(new Animation(14949));
					World.sendProjectileToTile(boss, target, 2853);
					delayHit(boss, target, 1, npc.rangedHit(boss, boss.getMaxHit()));
				}
			} else {
				if (Utils.random(7) == 0) {
					boss.setNextForceTalk(new ForceTalk("Magicinyaface!"));
					//boss.playSoundEffect(3022);//MAGIC IN YA FACE
				}
				boss.animate(new Animation(14956));
				boss.gfx(new Graphics(2854));
				target.gfx(new Graphics(2854, 5, 0));
				int damage = boss.getMaxHit();
				if (target instanceof Player) {
					if (((Player) target).getPrayer().isActive(NormalPrayer.PROTECT_FROM_MAGIC))
						damage *= .5D;
				}
				delayHit(boss, target, 1, npc.magicHit(boss, damage));
			}
			return 5;
		} else {
			boss.animate(new Animation(12310));
			delayHit(boss, target, 0, npc.meleeHit(boss, boss.getMaxHit()));
			return 4;
		}
	}
}
