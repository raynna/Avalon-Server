package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.familiar.Familiar;
import raynna.game.player.Player;
import raynna.game.npc.combatdata.AttackStyle;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

import static raynna.game.npc.combat.NpcCombatCalculations.getRandomMaxHit;

public class SpiritWolfCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6829, 6828 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		if (usingSpecial) {// priority over regular attack
			familiar.submitSpecial(familiar.getOwner());
			npc.animate(new Animation(8293));
			npc.gfx(new Graphics(1334));
			World.sendElementalProjectile(npc, target, 1333);
			if (target instanceof NPC) {
				if (!(((NPC) target).getCombatDefinitions().getAttackStyle() == AttackStyle.MELEE))
					target.setAttackedByDelay(3000);// three seconds
				else
					familiar.getOwner().getPackets().sendGameMessage("Your familiar cannot scare that monster.");
			} else if (target instanceof Player)
				familiar.getOwner().getPackets().sendGameMessage("Your familiar cannot scare a player.");
			else if (target instanceof Familiar)
				familiar.getOwner().getPackets().sendGameMessage("Your familiar cannot scare other familiars.");
		} else {
			npc.animate(new Animation(6829));
			delayHit(npc, target, 1, getMagicHit(npc, getRandomMaxHit(npc, 40, NpcAttackStyle.MAGIC, target)));
		}
		return npc.getAttackSpeed();
	}

}
