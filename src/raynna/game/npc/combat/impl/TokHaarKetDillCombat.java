package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.player.Player;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

import static raynna.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

public class TokHaarKetDillCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "TokHaar-Ket-Dill" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NpcCombatDefinition defs = npc.getCombatDefinitions();
		if (Utils.random(6) == 0) {
			delayHit(npc, target, 0, getRegularHit(npc, Utils.random(defs.getMaxHit() + 1)));
			target.gfx(new Graphics(2999));
			if (target instanceof Player) {
				Player playerTarget = (Player) target;
				playerTarget.getPackets().sendGameMessage("The TokHaar-Ket-Dill slams it's tail to the ground.");
			}
		} else {
			delayHit(npc, target, 0,
                    getMeleeHit(npc, getRandomMaxHit(npc, defs.getMaxHit(), NpcAttackStyle.CRUSH, target)));
		}
		npc.animate(new Animation(defs.getAttackAnim()));
		return npc.getAttackSpeed();
	}
}
