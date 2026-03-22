package raynna.game.npc.combat.impl.dung;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.npc.combatdata.NpcAttackStyle;

public class TomeOfLexicus extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 9856, 9857, 9858 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		int type = npc.getId() - 9856;
		switch (type) {
		case 0:
			npc.animate(new Animation(13479));
			delayHit(npc, target, 0, getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, npc.getMaxHit(), NpcAttackStyle.MAGIC, target)));
			break;
		case 1:
		case 2:
			boolean range_style = type == 1;
			npc.animate(new Animation(13480));
			npc.gfx(new Graphics(range_style ? 2408 : 2424));
			World.sendElementalProjectile(npc, target, range_style ? 2409 : 2425);
			if (range_style)
				delayHit(npc, target, 1, getRangeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, npc.getMaxHit(), NpcAttackStyle.RANGED, target)));
			else
				delayHit(npc, target, 1, getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, npc.getMaxHit(), NpcAttackStyle.MAGIC, target)));
			target.gfx(new Graphics(range_style ? 2410 : 2426, 75, 0));
			break;
		}
		return 4;
	}
}
