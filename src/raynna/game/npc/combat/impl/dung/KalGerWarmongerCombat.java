package raynna.game.npc.combat.impl.dung;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.ForceTalk;
import raynna.game.Graphics;
import raynna.game.Hit;
import raynna.game.Hit.HitLook;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.dungeoneering.KalGerWarmonger;
import raynna.game.player.Player;
import raynna.game.player.content.dungeoneering.DungeonManager;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;

public class KalGerWarmongerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
				{ "Kal'Ger the Warmonger" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final KalGerWarmonger boss = (KalGerWarmonger) npc;
		final DungeonManager manager = boss.getManager();
		if (boss.getType() == 0 || boss.isMaximumPullTicks())
			return 0;
		if (boss.isUsingMelee()) {
			boolean smash = false;
			
			for (Player player : manager.getParty().getTeam()) {
				if (Utils.colides(player.getX(), player.getY(), player.getSize(), boss.getX(), boss.getY(), 5)) {
					smash = true;
					break;
				}
			}
			if (smash) {
				boss.animate(new Animation(14968));
				boss.gfx(new Graphics(2867));
				for (Player player : manager.getParty().getTeam()) {
					if (!manager.isAtBossRoom(player))
						continue;
					player.getPackets().sendCameraShake(3, 25, 50, 25, 50);//everyone's camera shakes
					if (Utils.inCircle(player, boss, 5))//5 square radius (imperfect circle)
						player.applyHit(new Hit(boss, Utils.random(300, boss.getMaxHit()), HitLook.REGULAR_DAMAGE));
				}
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						boss.setPullTicks(0);
						for (Player player : manager.getParty().getTeam())//we obv need to reset the camera ^.^
							player.getPackets().sendStopCameraShake();
					}
				});
			} else if (!Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0))
				return 0;
		}
		boss.setPullTicks(0);
		if (boss.getAnnoyanceMeter() == 8) {//This part is essentially done
			//boss.playSoundEffect(2986);
			boss.setNextForceTalk(new ForceTalk("GRRRR!"));
		} else if (boss.getAnnoyanceMeter() == 10) {
			//boss.playSoundEffect(3012);
			boss.setNextForceTalk(new ForceTalk("ENOUGH!"));
		}
		if (boss.getType() == 1) {//NO WEAPONS HUR
			npc.animate(new Animation(14392));
			delayHit(npc, target, 0, npc.meleeHit(npc, boss.getMaxHit()));
		} else if (boss.getType() == 2) {//LONG
			npc.animate(new Animation(14416));
			delayHit(npc, target, 0, npc.meleeHit(npc, boss.getMaxHit(), NpcAttackStyle.STAB));
		} else if (boss.getType() == 3) {//STAFF
			npc.animate(new Animation(14996));
			npc.gfx(new Graphics(2874));
			for (Entity t : boss.getPossibleTargets()) {
				World.sendElementalProjectile(boss, t, 2875);
				t.gfx(new Graphics(2873));
				delayHit(npc, t, 0, npc.magicHit(npc, boss.getMaxHit()));
			}
		} else if (boss.getType() == 4) {//2H
			npc.animate(new Animation(14450));
			delayHit(npc, target, 0, npc.meleeHit(npc, boss.getMaxHit(), NpcAttackStyle.SLASH));
		} else if (boss.getType() == 5) {//BOW
			npc.animate(new Animation(14537));
			npc.gfx(new Graphics(2885));
			for (Entity t : boss.getPossibleTargets()) {
				World.sendElementalProjectile(boss, t, 2886);
				delayHit(npc, t, 2, npc.rangedHit(npc, boss.getMaxHit()));
			}
		} else if (boss.getType() == 6) {//MAUL
			npc.animate(new Animation(14963));
			delayHit(npc, target, 0, npc.meleeHit(npc, boss.getMaxHit(), NpcAttackStyle.CRUSH));
			return 3;//SUPER OP MODE!
		}
		return 4;
	}
}
