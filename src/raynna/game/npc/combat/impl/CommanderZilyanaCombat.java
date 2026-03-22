package raynna.game.npc.combat.impl;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.ForceTalk;
import raynna.game.Graphics;
import raynna.game.Hit;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;
import raynna.game.npc.worldboss.WorldBossNPC;

public class CommanderZilyanaCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]{6247}; // Commander Zilyana
	}

	private final static int MELEE_ANIMATION = 6964;
	private final static int MAGIC_ANIMATION = 6967;
	private final static int MAGIC_GFX = 1194;

	enum CommanderZilyanaAttack { MELEE, MAGIC }

	@Override
	public int attack(NPC npc, Entity target) {
		maybeShout(npc);
		CommanderZilyanaAttack attack = Utils.randomOf(CommanderZilyanaAttack.MAGIC, CommanderZilyanaAttack.MELEE);
		switch (attack) {
			case MELEE -> performMeleeAttack(npc, target);
			case MAGIC -> performMagicAttack(npc, target);
		}
		return npc.getCombatData().attackSpeedTicks;
	}

	private void performMagicAttack(NPC npc, Entity target) {
		npc.animate(MAGIC_ANIMATION);
		int damage = Utils.random(100, 200);
		Hit magicHit = npc.magicHit(target, damage);
		if (magicHit.getDamage() > 0) {
			delayHit(npc, target, 0, magicHit);
			target.gfx(MAGIC_GFX);
		}
	}

	private void performMeleeAttack(NPC npc, Entity target) {
		npc.animate(MELEE_ANIMATION);
		Hit meleeHit = npc.meleeHit(target, 270, NpcAttackStyle.SLASH);
		delayHit(npc, target, 0, meleeHit);
	}

	private void maybeShout(NPC npc) {
		if (Utils.random(4) != 0) return;

		switch (Utils.random(10)) {
			case 0 -> shout(npc, "Death to the enemies of the light!", 3247);
			case 1 -> shout(npc, "Slay the evil ones!", 3242);
			case 2 -> shout(npc, "Saradomin lend me strength!", 3263);
			case 3 -> shout(npc, "By the power of Saradomin!", 3262);
			case 4 -> shout(npc, "May Saradomin be my sword.", 3251);
			case 5 -> shout(npc, "Good will always triumph!", 3260);
			case 6 -> shout(npc, "Forward! Our allies are with us!", 3245);
			case 7 -> shout(npc, "Saradomin is with us!", 3266);
			case 8 -> shout(npc, "In the name of Saradomin!", 3250);
			case 9 -> shout(npc, "Attack! Find the Godsword!", 3258);
		}
	}

	private void shout(NPC npc, String text, int soundId) {
		npc.setNextForceTalk(new ForceTalk(text));
		npc.playSound(soundId, 2);
	}
}
