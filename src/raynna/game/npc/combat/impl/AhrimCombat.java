package raynna.game.npc.combat.impl;

import raynna.game.*;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.player.Player;
import raynna.game.player.Skills;
import raynna.util.Utils;
import raynna.game.world.projectile.Projectile;
import raynna.game.world.projectile.ProjectileManager;

public class AhrimCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2025 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		npc.animate(npc.getAttackAnimation());
		npc.gfx(2728);
		npc.playSound(npc.getCombatDefinitions().getAttackSound(), 1);
		Hit mageHit = npc.magicHit(target, npc.getMaxHit());
		ProjectileManager.send(Projectile.ELEMENTAL_SPELL, npc.getCombatDefinitions().getAttackProjectile(), npc, target, () -> {
			applyRegisteredHit(npc, target, mageHit);
		});
		if (mageHit.getDamage() != 0 && target instanceof Player player && Utils.roll(1, 3)) {
			target.gfx(400, 100);
            int currentLevel = player.getSkills().getLevel(Skills.STRENGTH);
			player.getSkills().set(Skills.STRENGTH, currentLevel < 5 ? 0 : currentLevel - 5);
		}
		return npc.getAttackSpeed();
	}
}
