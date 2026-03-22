package raynna.game.npc.combat.impl;

import raynna.game.*;
import raynna.game.npc.NPC;
import raynna.game.npc.combat.CombatScript;
import raynna.game.npc.combat.NpcCombatCalculations;
import raynna.game.player.TickManager;
import raynna.util.Utils;
import raynna.game.npc.combatdata.NpcAttackStyle;
import raynna.game.world.projectile.Projectile;
import raynna.game.world.projectile.ProjectileManager;

public class KreeArraCombat extends CombatScript {

    private final static int MELEE_ANIMATION = 6977;
    private final static int RANGE_ANIMATION = 6976;
    private final static int RANGE_PROJECTILE = 1197;
    private final static int MAGIC_PROJECTILE = 1198;

    enum KreeArraAttack { MELEE, RANGE, MAGE }

    @Override
    public Object[] getKeys() {
        return new Object[]{6222};
    }

    @Override
    public int attack(NPC npc, Entity target) {
        if (npc.getTickManager().getTicksLeft(TickManager.TickKeys.LAST_ATTACKED_TICK) == 0) {
            boolean inMelee = npc.isWithinMeleeRange(target);
            if (!inMelee && !npc.hasWalkSteps()) {
                npc.setForceFollowClose(true);
            }
            if (inMelee) {
                npc.animate(MELEE_ANIMATION);
                Hit hit = npc.meleeHit(target, 260);
                delayHit(npc, target, 0, hit);
            }
            return npc.getAttackSpeed();
        } else {
            npc.setForceFollowClose(false);
            getRandomAttack(npc, target);
            return npc.getAttackSpeed();
        }
    }

    private void getRandomAttack(NPC npc, Entity target) {
		npc.animate(new Animation(RANGE_ANIMATION));
        KreeArraAttack attack;
        attack = Utils.randomOf(KreeArraAttack.RANGE, KreeArraAttack.MAGE);
        switch (attack) {
            case RANGE -> sendRangedAttack(npc, target);
            case MAGE -> sendMagicAttack(npc, target);
        }
    }

    private void sendRangedAttack(NPC npc, Entity target) {
        for (Entity t : npc.getPossibleTargets()) {
            Hit rangeHit = npc.rangedHit(t, 710);
            ProjectileManager.send(Projectile.KREE_ARRA, RANGE_PROJECTILE, npc, t, () -> {
                applyRegisteredHit(npc, target, rangeHit);
                target.moveRandom(1);
                for (int c = 0; c < 10; c++) {
                    int dir = Utils.random(Utils.DIRECTION_DELTA_X.length);
                    if (World.checkWalkStep(target.getPlane(), target.getX(), target.getY(), dir, 1)) {
                        t.setNextWorldTile(new WorldTile(target.getX() + Utils.DIRECTION_DELTA_X[dir],
                                target.getY() + Utils.DIRECTION_DELTA_Y[dir], target.getPlane()));
                        break;
                    }
                }
            });
        }
    }

    private void sendMagicAttack(NPC npc, Entity target) {
        for (Entity t : npc.getPossibleTargets()) {
            Hit magicHit = npc.magicHit(t, 210);
            ProjectileManager.send(Projectile.STORM_OF_ARMADYL, MAGIC_PROJECTILE, npc, t, () -> applyRegisteredHit(npc, target, magicHit));
        }
    }
}
