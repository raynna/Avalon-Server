package raynna.game.npc.combat.impl.dung;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.ForceTalk;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.npc.dungeoneering.FrozenAdventurer;
import raynna.game.npc.dungeoneering.ToKashBloodChiller;
import raynna.game.player.Player;
import raynna.game.player.content.dungeoneering.DungeonManager;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;

public class ToKashBloodChillerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 10024 };
	}

	@Override
	public int attack(final NPC npc, Entity target) {
		final ToKashBloodChiller boss = (ToKashBloodChiller) npc;
		final DungeonManager manager = boss.getManager();

		boolean perfectDamage = false;

		if (target instanceof Player) {
			Player player = (Player) target;
			if (player.getAppearance().isNPC())
				perfectDamage = true;
		}

		if (perfectDamage) {
			((Player) target).getAppearance().transformIntoNPC(-1);
			delayHit(npc, target, 0, getRangeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, (int) Utils.random(boss.getMaxHit() * 0.90, boss.getMaxHit()), NpcAttackStyle.MAGIC, target)));
		}

		boolean special = boss.canSpecialAttack() && Utils.random(10) == 0;

		if (special) {
			npc.setNextForceTalk(new ForceTalk("Sleep now, in the bitter cold..."));
			//npc.playSoundEffect(2896);
			boss.setSpecialAttack(true);
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					npc.setNextForceTalk(new ForceTalk("DEEP FREEZE!"));
					npc.animate(new Animation(14396));
					npc.gfx(new Graphics(2544));
					for (Entity t : boss.getPossibleTargets())
						setSpecialFreeze((Player) t, boss, manager);
				}
			}, 3);
			return 8;
		} else {
			boolean meleeAttack = perfectDamage || Utils.random(3) == 0;

			if (meleeAttack) {
				npc.animate(new Animation(14392));
				delayHit(npc, target, 0, getRangeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 200, NpcAttackStyle.CRUSH, target)));
			} else {
				npc.animate(new Animation(14398));
				World.sendElementalProjectile(npc, target, 2546);
				delayHit(npc, target, 1, getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, 200, NpcAttackStyle.MAGIC, target)));
			}
			return meleeAttack ? 4 : 5;
		}
	}

	public static void setSpecialFreeze(final Player player, final ToKashBloodChiller boss, DungeonManager dungManager) {
		player.resetWalkSteps();
		player.stopAll();
		player.gfx(new Graphics(2545));
		player.addFreezeDelay(10000, false);
		player.getAppearance().transformIntoNPC(10022);
		FrozenAdventurer npc = new FrozenAdventurer(10023, player, -1, false);
		npc.setPlayer(player);
		player.getPackets().sendGameMessage("You have been frozen solid!");
		WorldTasksManager.schedule(new WorldTask() {

			int counter = 0;

			@Override
			public void run() {
				boss.setSpecialAttack(false);
				for (Entity t : boss.getPossibleTargets()) {
					Player player = (Player) t;
					counter++;
					removeSpecialFreeze(player);
				}
				if (counter == 0)
					return;
				boss.setNextForceTalk(new ForceTalk("I will shatter your soul!"));
				boss.gfx(new Graphics(2549, 5, 100));
			}
		}, 5 * dungManager.getParty().getTeam().size());
	}

	public static void removeSpecialFreeze(Player player) {
		player.unlock();
		player.getAppearance().transformIntoNPC(-1);
		player.gfx(new Graphics(2548));
		player.getPackets().sendGameMessage("The ice encasing you shatters violently.");
	}
}
