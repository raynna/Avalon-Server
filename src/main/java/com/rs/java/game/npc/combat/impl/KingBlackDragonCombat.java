package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.DragonFire;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.utils.Utils;
import com.rs.kotlin.Rscm;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

public class KingBlackDragonCombat extends CombatScript {

    private static final int DRAGON_SLAM_ANIMATION = 80, DRAGON_HEADBUTT_ANIMATION = 91, DRAGONFIRE_BREATH_ANIMATION = 84, DRAGON_DEATH_ANIMATION = 92;
    private static final int DRAGONFIRE_TOXIC_PROJECTILE = 394, DRAGONFIRE_NORMAL_PROJECTILE = 393, DRAGONFIRE_ICY_PROJECTILE = 395, DRAGONFIRE_SHOCKING_PROJECTILE = 396;

    private final static int FIREBREATH_SOUND = Rscm.INSTANCE.sound("sound.dragonfire_breath");

    enum KingBlackDragonAttack { MELEE, DRAGONFIRE }

    @Override
    public Object[] getKeys() {
        return new Object[]{50};
    }

    @Override
    public int attack(NPC npc, Entity target) {
        final Player player = target instanceof Player ? (Player) target : null;

        KingBlackDragonAttack attack;
        boolean inMelee = npc.isWithinMeleeRange(target);
        if (inMelee) {
            attack = Utils.randomWeighted(KingBlackDragonAttack.MELEE, 50, KingBlackDragonAttack.DRAGONFIRE, 50); //50/50 melee & dragonfire if close
        } else {
            attack = KingBlackDragonAttack.DRAGONFIRE;
        }

        switch (attack) {
            case MELEE:
                npc.animate(new Animation(Utils.roll(1, 2) ? DRAGON_SLAM_ANIMATION : DRAGON_HEADBUTT_ANIMATION));
				Hit melee = npc.meleeHit(target, 250);
				delayHit(npc, target, 0, melee);
                break;

            case DRAGONFIRE:
                if (player == null) break;

                npc.animate(new Animation(DRAGONFIRE_BREATH_ANIMATION));
                npc.playSound(FIREBREATH_SOUND, 1);
                boolean special = Utils.randomBoolean();

                int projectileId;
                int specialType;

                if (!special) {
                    specialType = -1;
                    projectileId = DRAGONFIRE_NORMAL_PROJECTILE;
                } else {
                    specialType = Utils.random(2);
                    projectileId = switch (specialType) {
                        case 0 -> DRAGONFIRE_TOXIC_PROJECTILE;
                        case 1 -> DRAGONFIRE_SHOCKING_PROJECTILE;
                        default -> DRAGONFIRE_ICY_PROJECTILE;
                    };
                }

                boolean accuracyRoll = NpcCombatCalculations.getAccuracyRoll(npc, NpcAttackStyle.MAGIC, target);

                int damage = DragonFire.applyDragonfireMitigation(
                        player,
                        accuracyRoll,
                        DragonFire.DragonType.KING_BLACK_DRAGON,
                        special
                );

                Hit dragonfire = npc.regularHit(target, damage);

                ProjectileManager.send(Projectile.DRAGONFIRE, projectileId, npc, target, () -> {
                            applyRegisteredHit(npc, target, dragonfire);
                            if (special && damage > 0) {
                                attemptSpecialEffect(player, specialType);
                            }
                            DragonFire.handleDragonfireShield(player);
                        }
                );
                break;

        }

        return npc.getCombatData().attackSpeedTicks;
    }

    private void attemptSpecialEffect(Player player, int type) {

        boolean shield = DragonFire.hasDragonShield(player);
        boolean prayer = player.getPrayer().isMageProtecting();

        int chance = (shield || prayer) ? 8 : 2;

        if (!Utils.roll(1, chance))
            return;

        switch (type) {
            case 0 -> {
                if (!player.getNewPoison().isPoisoned())
                    player.getNewPoison().startPoison(40);
            }
            case 1 -> {
                for (Skills.SkillData skills : Skills.SkillData.values()) {
                    if (skills.getId() == Skills.PRAYER || skills.getId() == Skills.HITPOINTS || skills.getId() == Skills.SUMMONING)
                        continue;
                    player.getSkills().drainLevel(skills.getId(), 2);
                }
            }
            case 2 -> player.addFreezeDelay(10, false);
        }
    }

}
