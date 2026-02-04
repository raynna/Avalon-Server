package com.rs.java.game.npc.combat.impl.dung;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.dungeoneering.Rammernaut;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.TickManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class RammernautCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ 9767 };
	}

	public static int getChargeCount(NPC npc) {

		Integer charge = (Integer) npc.getTemporaryAttributtes().get("RAMMERNAUT_CHARGE");

		return charge == null ? 0 : charge;

	}

	public static void setChargeCount(NPC npc, int count) {
		npc.getTemporaryAttributtes().put("RAMMERNAUT_CHARGE", count);

	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		int chargeCount = getChargeCount(npc);

		if (chargeCount > 1 && target instanceof Player) {
			((Rammernaut) npc).setChargeTarget((Player) target);
			setChargeCount(npc, 0);
			return 0;
		}

		if (!Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) {
			setChargeCount(npc, chargeCount + 1);
			return 3;
		}
		setChargeCount(npc, Utils.random(10) == 0 ? 2 : 0); //1 in 10 change charging next att

		if (Utils.random(5) == 0) {
			npc.animate(new Animation(13705));
			for (Entity entity : npc.getPossibleTargets()) {
				if (!Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), entity.getX(), entity.getY(), entity.getSize(), 0))
					continue;
				((Rammernaut) npc).applyStunHit(entity, npc.getMaxHit());
			}
			return npc.getAttackSpeed();
		}

		if (((Rammernaut) npc).isRequestSpecNormalAttack() && target instanceof Player player) {
			((Rammernaut) npc).setRequestSpecNormalAttack(false);
            player.getPackets().sendGameMessage("Your prayers have been disabled.");
			player.getTickManager().addSeconds(TickManager.TickKeys.DISABLED_PROTECTION_PRAYER_TICK, 8);
			player.getPackets().sendGameMessage("Your defence been reduced.");
			player.getSkills().drainLevel(Skills.DEFENCE, Utils.random(3) + 1);

		}

		//default melee attack can be protected with prayer
		npc.animate(new Animation(defs.getAttackAnim()));
		delayHit(npc, target, 0, npc.meleeHit(npc, npc.getMaxHit()));
		return npc.getAttackSpeed();
	}
}
