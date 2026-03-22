package raynna.game.npc.combat.impl;

import raynna.game.*;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.player.Player;
import raynna.game.player.Skills;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcCombatDefinition;
import raynna.game.world.projectile.ProjectileManager;
import raynna.game.world.projectile.Projectile;

public class KarilCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2028 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NpcCombatDefinition defs = npc.getCombatDefinitions();
		npc.animate(new Animation(defs.getAttackAnim()));
		Hit rangeHit = npc.rangedHit(target, defs.getMaxHit());
		ProjectileManager.send(Projectile.BOLT, defs.getAttackProjectile(), npc, target, () -> {
			applyRegisteredHit(npc, target, rangeHit);
			if (rangeHit.getDamage() != 0 && target instanceof Player && Utils.random(3) == 0) {
				target.gfx(new Graphics(401, 0, 100));
				Player targetPlayer = (Player) target;
				int drain = (int) (targetPlayer.getSkills().getRealLevel(Skills.AGILITY) * 0.2);
				int currentLevel = targetPlayer.getSkills().getLevel(Skills.AGILITY);
				targetPlayer.getSkills().set(Skills.AGILITY, currentLevel < drain ? 0 : currentLevel - drain);
			}
		});
		return npc.getAttackSpeed();
	}
}
