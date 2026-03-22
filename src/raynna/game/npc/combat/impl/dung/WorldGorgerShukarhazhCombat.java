package raynna.game.npc.combat.impl.dung;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.npc.dungeoneering.WorldGorgerShukarhazh;
import raynna.game.player.Player;
import raynna.game.player.content.dungeoneering.DungeonManager;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;

public class WorldGorgerShukarhazhCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 12478 };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		final WorldGorgerShukarhazh boss = (WorldGorgerShukarhazh) npc;
		final DungeonManager manager = boss.getManager();

		boolean smash = false;
		for (Player player : manager.getParty().getTeam()) {
			if (Utils.colides(player.getX(), player.getY(), player.getSize(), npc.getX(), npc.getY(), npc.getSize())) {
				smash = true;
				player.getPackets().sendGameMessage("The creature crushes you as you move underneath it.");
				delayHit(npc, player, 0, getRegularHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, npc.getMaxHit(), NpcAttackStyle.CRUSH, player)));
			}
		}
		if (smash) {
			npc.animate(new Animation(14894));
			return 6;
		}

		if (Utils.random(manager.getParty().getTeam().size() > 1 ? 20 : 5) == 0 && Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) {
			npc.animate(new Animation(14892));
			delayHit(npc, target, 0, getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, npc.getMaxHit(), NpcAttackStyle.CRUSH, target)));
		} else {
			npc.animate(new Animation(14893));
			npc.gfx(new Graphics(2846, 0, 100));
			target.gfx(new Graphics(2848, 75, 100));
			delayHit(npc, target, 2, getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, npc.getMaxHit(), NpcAttackStyle.MAGIC, target)));
		}
		return 6;
	}
}
