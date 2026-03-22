package raynna.game.npc.combat.impl.dung;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.dungeoneering.FleshspoilerHaasghenahk;
import raynna.game.player.Player;
import raynna.util.Utils;

public class FleshspoilerHaasghenahkCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 11925, 11895 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final FleshspoilerHaasghenahk boss = (FleshspoilerHaasghenahk) npc;

		for (Entity t : npc.getPossibleTargets()) {
			if (Utils.colides(t.getX(), t.getY(), t.getSize(), npc.getX(), npc.getY(), npc.getSize()))
				delayHit(npc, t, 0, getRegularHit(npc, boss.getMaxHit()));
		}
		if (boss.isSecondStage())
			return 0;
		boolean magicOnly = boss.canUseMagicOnly();
		if (magicOnly || Utils.random(5) == 0) {
			if (magicOnly) {
				if (target instanceof Player) {
					Player player = (Player) target;
					if (player.getPrayer().isMageProtecting() && Utils.random(3) == 0)
						boss.setUseMagicOnly(false);
				}
			}
			npc.animate(new Animation(14463));
			delayHit(npc, target, 1, npc.meleeHit(npc, boss.getMaxHit()));
		} else {
			npc.animate(new Animation(13320));
			delayHit(npc, target, 0, npc.meleeHit(npc, boss.getMaxHit()));
		}
		return 6;
	}
}
