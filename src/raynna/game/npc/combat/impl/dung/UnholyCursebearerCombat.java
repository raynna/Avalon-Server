package raynna.game.npc.combat.impl.dung;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.Hit;
import raynna.game.Hit.HitLook;
import raynna.game.World;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.npc.dungeoneering.DungeonBoss;
import raynna.game.player.Player;
import raynna.game.player.Skills;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;

public class UnholyCursebearerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]
		{ "Unholy cursebearer" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		int attackStyle = Utils.isOnRange(target.getX(), target.getY(), target.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0) ? Utils.random(2) : 0;
		if (target instanceof Player && target.getTemporaryAttributtes().get("UNHOLY_CURSEBEARER_ROT") == null) {
			target.getTemporaryAttributtes().put("UNHOLY_CURSEBEARER_ROT", 1);
			final Player player = (Player) target;
			player.getPackets().sendGameMessage("An undead rot starts to work at your body.");
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					Integer value = (Integer) target.getTemporaryAttributtes().get("UNHOLY_CURSEBEARER_ROT");
					if (player.hasFinished() || npc.hasFinished() || !((DungeonBoss) npc).getManager().isAtBossRoom(player) || value == null) {
						target.getTemporaryAttributtes().remove("UNHOLY_CURSEBEARER_ROT");
						stop();
						return;
					}
					int damage = 20 * value;
					for (int stat = 0; stat < 7; stat++) {
						if (stat == Skills.HITPOINTS)
							continue;
						int drain = Utils.random(5) + 1;
						if (stat == Skills.PRAYER)
							player.getPrayer().drainPrayer(drain * 10);
						player.getSkills().drainLevel(stat, drain);
					}
					int maxDamage = player.getMaxHitpoints() / 10;
					if (damage > maxDamage)
						damage = maxDamage;
					if (value == 6)
						player.getPackets().sendGameMessage("The undead rot can now be cleansed by the unholy font.");
					player.applyHit(new Hit(npc, damage, HitLook.REGULAR_DAMAGE));
					player.gfx(new Graphics(2440));
					target.getTemporaryAttributtes().put("UNHOLY_CURSEBEARER_ROT", value + 1);
				}

			}, 0, 12);
		}
		switch (attackStyle) {
		case 0:
			boolean multiTarget = Utils.random(2) == 0;
			npc.animate(new Animation(multiTarget ? 13176 : 13175));
			if (multiTarget) {
				npc.gfx(new Graphics(2441));
				for (Entity t : npc.getPossibleTargets()) {
					World.sendElementalProjectile(npc, t, 88);
					delayHit(npc, t, 1, getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, (int) (npc.getMaxHit() * 0.6), NpcAttackStyle.MAGIC, t)));
				}
			} else {
				World.sendElementalProjectile(npc, target, 88);
				delayHit(npc, target, 1, getMagicHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, npc.getMaxHit(), NpcAttackStyle.MAGIC, target)));
			}
			break;
		case 1:
			npc.animate(new Animation(defs.getAttackAnim()));
			delayHit(npc, target, 0, getMeleeHit(npc, NpcCombatCalculations.getRandomMaxHit(npc, npc.getMaxHit(), NpcAttackStyle.CRUSH, target)));
			break;
		}
		return npc.getAttackSpeed();
	}
}
