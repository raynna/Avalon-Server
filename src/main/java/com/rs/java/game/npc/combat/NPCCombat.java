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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @Improved Andreas - AvalonPK
 */

public final class NPCCombat {

    private NPC npc;
    private int combatDelay;
    private Entity target;

    public NPCCombat(NPC npc) {
        this.npc = npc;
    }

    private static final int DEFAULT_AGRO_DISTANCE = 8;
    private static final int MAX_FAR_ATTACK_DISTANCE = 16;
    private static final int NEX_FORCE_MOVEMENT_ANIMATION = 17408;

    public int getCombatDelay() {
        return combatDelay;
    }

    /*
     * returns if under combat
     */
    public boolean process() {
        if (combatDelay > 0)
            combatDelay--;
        if (target != null) {
            if (!checkAll()) {
                removeTarget();
                return false;
            }
            if (combatDelay <= 0)
                combatDelay = combatAttack();
            return true;
        }
        return false;
    }

    private int combatAttack() {
        Entity target = getValidTarget();
        if (target == null) return 0;

        int maxDistance = getMaxAttackDistance(target);
        if (!canReachTarget(target, maxDistance)) return 0;

        return CombatScriptsHandler.specialAttack(npc, target);
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
        int attackStyle = npc.getCombatDefinitions().getAttackStyle();
        if (attackStyle == NPCCombatDefinitions.MELEE || attackStyle == NPCCombatDefinitions.SPECIAL2)
            return 0;
        if (npc instanceof HarAkenTentacle) return 12;
        if (npc instanceof FightCavesNPC && attackStyle == NPCCombatDefinitions.SPECIAL) return 14;
        int distance = 7;
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
        if (!isTargetValid(target)) return false;
        if (!canAttackTarget(target)) return false;
        if (isWithinRespawnRange() && npc.isForceWalking())
            npc.resetForcewalk();
        if (!isWithinRespawnRange()) {
            combatDelay = 1;
            npc.forceWalkRespawnTile();
            npc.setNextFaceEntity(target);
            return true;
        }

        if (handleCollisionMovement(target)) return true;

        handleFollowAndFlinch(target);
        return true;
    }

    private boolean canAttackTarget(Entity target) {
        if (npc instanceof Familiar) return ((Familiar) npc).canAttack(target);

        if (!npc.isForceMultiAttacked() && (!target.isAtMultiArea() || !npc.isAtMultiArea())) {
            if (npc.getAttackedBy() != target && npc.getAttackedByDelay() > Utils.currentTimeMillis()) return false;
            if (target.getAttackedBy() != npc && target.getAttackedByDelay() > Utils.currentTimeMillis()) return false;
        }
        return true;
    }

    private boolean isWithinRespawnRange() {
        int size = npc.getSize();
        int maxDistance = npc.getForceTargetDistance() > 0 ? npc.getForceTargetDistance() : DEFAULT_AGRO_DISTANCE;

        // Distance from respawn tile
        int npcDistX = npc.getX() - npc.getRespawnTile().getX();
        int npcDistY = npc.getY() - npc.getRespawnTile().getY();

        int tgtDistX = target.getX() - npc.getRespawnTile().getX();
        int tgtDistY = target.getY() - npc.getRespawnTile().getY();

        // If NPC has a defined MapArea, enforce area rules
        if (npc.getMapAreaNameHash() != -1) {
            boolean npcInside = MapAreas.isAtArea(npc.getMapAreaNameHash(), npc);
            boolean tgtInside = MapAreas.isAtArea(npc.getMapAreaNameHash(), target);

            if (!npcInside) return false; // NPC wandered outside
            if (!npc.canBeAttackFromOutOfArea() && !tgtInside) return false; // Target outside and not allowed
            return true;
        }

        // Fallback: distance-based leash
        boolean npcInRange = !(npcDistX > size + maxDistance || npcDistX < -1 - maxDistance
                || npcDistY > size + maxDistance || npcDistY < -1 - maxDistance);

        boolean targetInRange = !(tgtDistX > size + maxDistance || tgtDistX < -1 - maxDistance
                || tgtDistY > size + maxDistance || tgtDistY < -1 - maxDistance);

        return npcInRange && targetInRange;
    }


    private boolean handleCollisionMovement(Entity target) {
        int size = npc.getSize();
        int targetSize = target.getSize();

        if (Utils.colides(npc.getX(), npc.getY(), size, target.getX(), target.getY(), targetSize)
                && !target.hasWalkSteps()) {

            if (npc.isFrozen()) {
                combatDelay = 1;
                return true;
            }
            combatDelay = 1;

            npc.resetWalkSteps();
            return attemptWalkAroundTarget(target, size);
        }

        if (npc.getCombatDefinitions().getAttackStyle() == NPCCombatDefinitions.MELEE
                && targetSize == 1
                && Math.abs(npc.getX() - target.getX()) == 1
                && Math.abs(npc.getY() - target.getY()) == 1
                && !target.hasWalkSteps()
                && size == 1) {
            npc.resetWalkSteps();
            if (npc.isFrozen()) {
                combatDelay = 1;
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
                if (npc.addWalkStepsInteract(tile.getX(), tile.getY(), 1, size, true))
                    return true;
            }
        }

        return false;
    }



    private void handleFollowAndFlinch(Entity target) {
        int attackStyle = npc.getCombatDefinitions().getAttackStyle();
        int size = npc.getSize();
        int targetSize = target.getSize();

        int maxDistance = npc.isForceFollowClose() ? 0
                : (attackStyle == NPCCombatDefinitions.MELEE || attackStyle == NPCCombatDefinitions.SPECIAL2) ? 0 : 7;

        boolean needsMove = !npc.clipedProjectile(target, maxDistance == 0 && !forceCheckClipAsRange(target))
                || !Utils.isOnRange(npc.getX(), npc.getY(), size, target.getX(), target.getY(), targetSize, maxDistance);

        npc.resetWalkSteps();

        if (needsMove) {
            if (npc.isIntelligentRouteFinder())
                npc.calcFollow(target, npc.getRun() ? 2 : 1, true, true);
            else
                npc.addWalkStepsInteract(target.getX(), target.getY(), npc.getRun() ? 2 : 1, size, true);
        } else {
            if (npc.getAttackDelay() < Utils.currentTimeMillis() || npc.getAttackDelay() == 0)
                flinch();
            if (npc.getFlinchDelay() > Utils.currentTimeMillis())
                combatDelay = 1;
        }
    }


    void doDefenceEmote(Entity target) {
        target.setNextAnimationNoPriority(new Animation(Combat.getDefenceEmote(target)), target);
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

    public void flinch() {
        if (npc.getFlinchDelay() > Utils.currentTimeMillis())
            return;
        int attackSpeed = npc.getAttackSpeed() * 600;
        npc.setFlinch((attackSpeed / 2) - 1000);
        npc.setAttackDelay((attackSpeed / 2) + 4800);
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
