package raynna.game.npc.combat;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.Graphics;
import raynna.game.Hit;
import raynna.game.npc.NPC;
import raynna.game.npc.combatdata.AttackStyle;
import raynna.game.npc.combatdata.CombatData;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.npc.combatdata.NpcCombatDefinition;
import raynna.game.world.projectile.Projectile;
import raynna.game.world.projectile.ProjectileManager;

public class Default extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Default" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NpcCombatDefinition defs = npc.getCombatDefinitions();
		CombatData data = npc.getCombatData();
		AttackStyle definitionAttackStyle = defs.getAttackStyle();
		switch (definitionAttackStyle) {
			case AttackStyle.MELEE -> {
				NpcAttackStyle attackStyle = NpcAttackStyle.fromList(npc.getCombatData().attackStyles);
				if (attackStyle == null) {
					System.out.println("[NPC COMBAT] Unhandled melee attack style for NPC: "
							+ npc.getId() + " styles=" + data.attackStyles);
					return npc.getAttackSpeed();
				}
				switch (attackStyle) {
					case MAGICAL_MELEE -> {
						Hit magicalMelee = npc.magicalMelee(target, npc.getMaxHit());
						delayHit(npc, target, 0, magicalMelee);
					}
					case STAB, SLASH, CRUSH -> {
						Hit meleeHit = npc.meleeHit(target, npc.getMaxHit(), attackStyle);
						delayHit(npc, target, 0, meleeHit);
					}
					default -> {
						System.out.println("[NPC COMBAT] Unhandled melee subtype: "
								+ attackStyle + ", attackStyles: " + npc.getCombatData().attackStyles + ", npc=" + npc.getId());
					}
				}
			}
			case AttackStyle.RANGE -> {
				Hit rangeHit = npc.rangedHit(target, npc.getMaxHit());
				if (defs.getAttackProjectile() != -1) {
					ProjectileManager.send(Projectile.ARROW, defs.getAttackProjectile(), npc, target, () -> {
						applyRegisteredHit(npc, target, rangeHit);
					});
				} else {
					delayHit(npc, target, npc.getHitDelay(npc, target), rangeHit);
				}
			}

			case AttackStyle.MAGIC -> {
				Hit mageHit = npc.magicHit(target, npc.getMaxHit());
				if (defs.getAttackProjectile() != -1) {
					ProjectileManager.send(Projectile.ELEMENTAL_SPELL, defs.getAttackProjectile(), npc, target, () -> {
						applyRegisteredHit(npc, target, mageHit);
					});
				} else {
					delayHit(npc, target, npc.getHitDelay(npc, target), mageHit);
				}
			}
			default -> {
				System.out.println("[NPC COMBAT] Unhandled attack style: "
						+ definitionAttackStyle + ", styles: " + npc.getCombatData().attackStyles +", npc=" + npc.getId());
			}
		}

		if (defs.getAttackGfx() != -1) {
			npc.gfx(new Graphics(defs.getAttackGfx()));
		}
		npc.animate(new Animation(defs.getAttackAnim()));
		if (defs.getAttackSound() != -1)
			npc.playSound(defs.getAttackSound(), 1);
		return npc.getAttackSpeed();
	}
}
