package com.rs.java.game.npc.combat;

import com.rs.java.game.*;
import com.rs.java.game.minigames.godwars.zaros.Nex;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.npc.fightcaves.FightCavesNPC;
import com.rs.java.game.npc.fightkiln.HarAkenTentacle;
import com.rs.java.game.npc.pest.PestPortal;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.combat.Combat;
import com.rs.java.utils.MapAreas;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.AttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;
import com.rs.kotlin.game.player.combat.CombatUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @Improved Andreas - AvalonPK
 */

public final class NPCCombat {

    private NPC npc;
    private int combatDelay;
    private int attackDelay;
    private Entity target;

    public int getAttackDelay() {
        return attackDelay;
    }

    public void setAttackDelay(int attackDelay) {
        this.attackDelay = attackDelay;
    }

    public NPCCombat(NPC npc) {
        this.npc = npc;
    }

    private static final int DEFAULT_AGRO_DISTANCE = 8;
    private static final int MAX_FAR_ATTACK_DISTANCE = 16;
    private static final int NEX_FORCE_MOVEMENT_ANIMATION = 17408;

    private static final boolean DEBUG_COMBAT = false;

    private void debug(String msg) {
        if (!DEBUG_COMBAT) return;
        System.out.println("[NPCCombat][" + npc.getId() + "] " + msg);
    }

    /*
     * returns if under combat
     */
    public boolean process() {
        if (npc.isLocked()) {
            debug("NPC locked");
            return true;
        }
        if (attackDelay > 0) {
            attackDelay--;
            debug("Attack delay ticking: " + attackDelay);
        }

        if (target != null) {
            debug("Processing target: " + target);
            npc.setNextFaceEntity(target);

            if (!checkAll()) {
                debug("checkAll() returned FALSE → combat stopped");
                return false;
            }

            if (attackDelay <= 0) {
                debug("Attempting attack");
                combatAttack();
                return true;
            }

            return true;
        }

        return false;
    }


    private boolean combatAttack() {
        Entity target = getValidTarget();
        if (target == null) {
            debug("combatAttack: target invalid");
            return false;
        }
        if (target instanceof Familiar familiar) {
            Player player = familiar.getOwner();
            if (player != null) {
                target = player;
                npc.setTarget(target);
            }
            if (target == familiar.getOwner()) {
                npc.setTarget(target);
            }
        }
        int maxDistance = getMaxAttackDistance(target);

        if (!canReachTarget(target, maxDistance)) {
            debug("combatAttack: cannot reach target (LOS or distance)");
            attackDelay = 1;
            return true;
        }

        debug("combatAttack: ATTACKING");
        attackDelay = npc.getAttackSpeed();
        target.setAttackedBy(npc);
        npc.setLastAttackTimer(16);
        target.setInCombat(16);
        target.setPjTimer(12);

        return CombatScriptsHandler.specialAttack(npc, target) > 0;
    }



    private Entity getValidTarget() {
        Entity target = this.target;
        if (!isTargetValid(target)) return null;

        if (target instanceof Familiar familiar) {
            Player owner = familiar.getOwner();
            if (owner != null) {
                target = owner;
                npc.setTarget(target);
            }
        }
        return target;
    }

    private boolean isTargetValid(Entity target) {
        if (target == null) return false;
        if (npc.isDead() || npc.hasFinished()) return false;
        if (target.isDead() || target.hasFinished()) return false;
        if (npc.getPlane() != target.getPlane()) return false;
        return true;
    }

    private int getMaxAttackDistance(Entity target) {
        NpcCombatDefinition defs = npc.getCombatDefinitions();
        AttackStyle attackStyle = defs.getAttackStyle();
        if (attackStyle == AttackStyle.MELEE) {
            return 0;
        }
        if (npc instanceof HarAkenTentacle) return 12;
        if (npc instanceof FightCavesNPC) return 14;
        int distance = defs.getAttackRange() != -1 ? defs.getAttackRange() : 7;
        if (target.hasWalkSteps()) distance += 1;
        return distance;
    }

    private boolean canReachTarget(Entity target, int maxDistance) {
        if (!(npc instanceof Nex) && !npc.clipedProjectile(target, maxDistance == 0 && !forceCheckClipAsRange(target)))
            return false;

        int size = npc.getSize();
        int distanceX = target.getX() - npc.getX();
        int distanceY = target.getY() - npc.getY();
        return distanceX <= size + maxDistance && distanceX >= -1 - maxDistance
                && distanceY <= size + maxDistance && distanceY >= -1 - maxDistance;
    }

    public boolean checkAll() {
        Entity target = this.target;
        if (npc instanceof Familiar && target instanceof NPC && ((NPC) target).isCantInteract())
            return false;
        if (!isTargetValid(target)) {
            debug("checkAll: target no longer valid → REMOVED");
            removeTarget();
            return false;
        }

        if (!canAttackTarget(target)) {
            debug("checkAll: canAttackTarget = false → REMOVED");
            removeTarget();
            return false;
        }

        if (isWithinRespawnRange() && npc.isForceWalking()) {
            debug("checkAll: reset forcewalk");
            npc.resetForcewalk();
        }

        if (!isWithinRespawnRange()) {
            debug("checkAll: OUTSIDE respawn range → force walking home");
            combatDelay = 1;
            npc.forceWalkRespawnTile();
            npc.setNextFaceEntity(target);
            return true;
        }

        if (handleCollisionMovement(target)) {
            debug("checkAll: collision movement handled");
            return true;
        }

        debug("checkAll: normal follow");
        handleFollow(target);
        return true;
    }


    private boolean canAttackTarget(Entity target) {
        if (npc instanceof Familiar)
            return ((Familiar) npc).canAttack(target);

        if (!npc.isForceMultiAttacked() && !target.isAtMultiArea()) {
            if (target.getAttackedBy() != npc && target.isInCombat()) {
                return false;
            }
        }
        return true;
    }

    private boolean isWithinRespawnRange() {
        int size = npc.getSize();
        int maxDistance = npc.getForceTargetDistance() > 0
                ? npc.getForceTargetDistance()
                : npc.getCombatDefinitions().getMaxDistFromSpawn() == -1 ? 64 : npc.getCombatDefinitions().getMaxDistFromSpawn();

        // Distance from respawn tile
        int npcDistX = npc.getX() - npc.getRespawnTile().getX();
        int npcDistY = npc.getY() - npc.getRespawnTile().getY();

        // If NPC has a defined MapArea, enforce area rules
        if (!(npc instanceof Familiar)) {
            if (npc.getMapAreaNameHash() != -1) {
                boolean npcInside = MapAreas.isAtArea(npc.getMapAreaNameHash(), npc);
                boolean tgtInside = MapAreas.isAtArea(npc.getMapAreaNameHash(), target);

                if (!npcInside) return false; // NPC wandered outside
                if (!npc.canBeAttackFromOutOfArea() && !tgtInside) return false; // Target outside and not allowed
                return true;
            }
        }

        // Fallback: distance-based leash
        boolean npcInRange = !(npcDistX > size + maxDistance || npcDistX < -1 - maxDistance
                || npcDistY > size + maxDistance || npcDistY < -1 - maxDistance);

        return npcInRange;
    }


    private boolean handleCollisionMovement(Entity target) {
        int size = npc.getSize();
        int targetSize = target.getSize();
        if (npc.isCantFollowUnderCombat()) {
            attackDelay = Math.max(attackDelay, 1);
            return true;
        }
        if (Utils.colides(npc.getX(), npc.getY(), size, target.getX(), target.getY(), targetSize)
                && !target.hasWalkSteps()) {

            if (npc.isFrozen() || npc.isLocked()) {
                attackDelay = Math.max(attackDelay, 1);
                return true;
            }
            attackDelay = Math.max(attackDelay, 1);
            if (!npc.hasWalkSteps()) {
                npc.resetWalkSteps();
                return attemptWalkAroundTarget(target, size);
            }
            return true;
        }

        if (npc.getCombatDefinitions().getAttackStyle() == AttackStyle.MELEE
                && targetSize == 1
                && Math.abs(npc.getX() - target.getX()) == 1
                && Math.abs(npc.getY() - target.getY()) == 1
                && !target.hasWalkSteps()
                && size == 1) {
            npc.resetWalkSteps();
            if (npc.isFrozen() || npc.isLocked()) {
                attackDelay = Math.max(attackDelay, 1);
                return true;
            }
            if (!npc.addWalkSteps(target.getX(), npc.getY(), 1))
                npc.addWalkSteps(npc.getX(), target.getY(), 1);
            return true;
        }

        return false;
    }

    private boolean attemptWalkAroundTarget(Entity target, int size) {
        int targetSize = target.getSize();
        int radius = size + targetSize;
        if (npc.isCantFollowUnderCombat()) {
            return false;
        }
        List<WorldTile> candidates = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int tileX = target.getX() + dx;
                int tileY = target.getY() + dy;

                if (Utils.colides(tileX, tileY, size, target.getX(), target.getY(), targetSize))
                    continue;

                candidates.add(new WorldTile(tileX, tileY, target.getPlane()));
            }
        }

        candidates.sort(Comparator.comparingInt(tile -> Utils.getDistance(tile.getX(), tile.getY(), npc.getX(), npc.getY())));

        for (WorldTile tile : candidates) {
            if (World.isTileFree(tile.getPlane(), tile.getX(), tile.getY(), size)) {
                if (npc.isFrozen() || npc.isLocked()) {
                    return true;
                }
                if (npc.addWalkStepsInteract(tile.getX(), tile.getY(), 1, size, true))
                    return true;
            }
        }

        return false;
    }



    private void handleFollow(Entity target) {
        if (npc.isCantFollowUnderCombat()) {
            debug("handleFollow: NPC cannot follow in combat");
            npc.setNextFaceEntity(target);
            return;
        }
        NpcCombatDefinition defs  = npc.getCombatDefinitions();
        AttackStyle attackStyle = defs.getAttackStyle();
        int size = npc.getSize();
        int targetSize = target.getSize();

        int attackRange = defs.getAttackRange() != -1 ? defs.getAttackRange() : 7;
        int maxAttackDistance = npc.isForceFollowClose() ? 0 : (attackStyle == AttackStyle.MELEE ? 0 : attackRange);

        // Allow following up to chase distance (forceTargetDistance or default agro leash)
        int chaseDistance = npc.getForceTargetDistance() > 0
                ? npc.getForceTargetDistance()
                : npc.getCombatDefinitions().getMaxDistFromSpawn() == -1 ? 16 : npc.getCombatDefinitions().getMaxDistFromSpawn();

        // Always attempt to follow target if inside chase distance
        boolean inChaseRange = Utils.isOnRange(
                npc.getX(), npc.getY(), size,
                target.getX(), target.getY(), targetSize,
                chaseDistance
        );

        if (!inChaseRange) {
            removeTarget();
            return;
        }
        if (npc.isFrozen() || npc.isLocked()) {
            return;
        }

        // If not in attack range, move closer
        boolean inAttackRange = Utils.isOnRange(
                npc.getX(), npc.getY(), size,
                target.getX(), target.getY(), targetSize,
                maxAttackDistance
        );

        if (!npc.hasWalkSteps()) {
            if (!inAttackRange || !npc.clipedProjectile(target, maxAttackDistance == 0 && !forceCheckClipAsRange(target))) {
                if (npc.isIntelligentRouteFinder()) {
                    npc.calcFollow(target, npc.getRun() ? 2 : 1, true, true);
                } else {
                    npc.addWalkStepsInteract(target.getX(), target.getY(), npc.getRun() ? 2 : 1, size, true);
                }
            }
        }
    }



    void performBlockAnimation(Entity target) {
        int anim;

        if (target instanceof Player player) {
            anim = CombatUtils.INSTANCE
                    .getBlockAnimation(player);
        } else {
            anim = Combat.getDefenceEmote(target);
        }

        if (anim > 0)
            target.setNextAnimation(anim);
    }


    public Entity getTarget() {
        return target;
    }

    public boolean hasTarget() { return this.target != null; }

    public void setTarget(Entity target) {
        this.target = target;
        npc.setNextFaceEntity(target);
        if (!checkAll()) {
            removeTarget();
            return;
        }
    }

    private boolean forceCheckClipAsRange(Entity target) {
        return target instanceof PestPortal;
    }

    public void addCombatDelay(int delay) {
        combatDelay += delay;
    }

    public void setCombatDelay(int delay) {
        combatDelay = delay;
    }

    public boolean underCombat() {
        return target != null;
    }

    public void removeTarget() {
        this.target = null;
        npc.setNextFaceEntity(null);
    }

    public void reset() {
        combatDelay = 0;
        target = null;
    }

}
