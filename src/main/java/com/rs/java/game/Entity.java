package com.rs.java.game;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.rs.Settings;
import com.rs.core.cache.defintions.AnimationDefinitions;
import com.rs.core.cache.defintions.ObjectDefinitions;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.npc.nomad.Nomad;
import com.rs.java.game.npc.pet.Pet;
import com.rs.java.game.npc.qbd.TorturedSoul;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.TickManager;
import com.rs.java.game.player.actions.combat.ModernMagicks;
import com.rs.java.game.player.actions.combat.Poison;
import com.rs.java.game.player.content.UpdateMask;
import com.rs.java.game.player.prayer.NormalPrayer;
import com.rs.java.game.route.RouteFinder;
import com.rs.java.game.route.strategy.EntityStrategy;
import com.rs.java.game.route.strategy.ObjectStrategy;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.Rscm;
import com.rs.kotlin.game.player.NewPoison;
import com.rs.kotlin.game.player.interfaces.HealthOverlay;
import com.rs.kotlin.game.world.area.Area;
import com.rs.kotlin.game.world.area.AreaManager;

public abstract class Entity extends WorldTile {

    private static final long serialVersionUID = -3372926325008880753L;
    private final static AtomicInteger hashCodeGenerator = new AtomicInteger();

    // transient stuff
    private transient int index;
    private transient int lastRegionId; // the last region the entity was at
    private transient WorldTile lastLoadedMapRegionTile;
    private transient CopyOnWriteArrayList<Integer> mapRegionsIds; // called by
    // more than
    // 1thread
    // so
    // concurent
    private transient WorldTile previousTile;
    private transient int direction;
    private transient WorldTile lastWorldTile;
    private transient WorldTile nextWorldTile;
    private transient WorldTile predictedWorldTile;
    private transient int nextWalkDirection;
    private transient int nextRunDirection;
    private transient WorldTile nextFaceWorldTile;
    private transient boolean teleported;
    private transient ConcurrentLinkedQueue<Object[]> walkSteps;// called by
    // more
    // than 1thread
    // so concurent
    public transient ConcurrentLinkedQueue<Hit> receivedHits;
    public transient Map<Entity, Integer> receivedDamage;
    private transient boolean finished; // if removed
    // entity masks
    private transient UpdateMask updateMask;
    private transient Animation nextAnimation;
    private transient Animation pendingAnimation;
    private transient boolean forceAnimation;
    private transient Graphics nextGraphics1;
    private transient Graphics nextGraphics2;
    private transient Graphics nextGraphics3;
    private transient Graphics nextGraphics4;
    private transient ArrayList<Hit> nextHits;
    private Queue<Hit> hitOverflow = new LinkedList<>();
    private transient ForceMovement nextForceMovement;
    private transient ForceTalk nextForceTalk;
    private transient int nextFaceEntity;
    private transient int lastFaceEntity;

    private transient long castedSpellDelay;
    private transient boolean multiArea;
    private transient boolean isAtDynamicRegion;
    private transient long lastAnimationEnd;
    private transient boolean forceMultiArea;
    public transient long frozenBlocked;
    private transient long findTargetDelay;
    private transient Map<Object, Object> temporaryAttributes = new ConcurrentHashMap<>();
    private transient int hashCode;
    public boolean isTeleporting;

    // saving stuff
    private int hitpoints;
    private int increasedMaxHitpoints;
    private long pid;
    private int mapSize; // default 0, can be setted other value usefull on
    // static maps
    private boolean run;
    private Poison poison;
    private NewPoison newPoison;
    public transient int morriganHits;
    public transient int vineHits;

    // creates Entity and saved classes
    public Entity(WorldTile tile) {
        super(tile);
        poison = new Poison();
        newPoison = new NewPoison(this);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public boolean inArea(int a, int b, int c, int d) {
        return getX() >= a && getY() >= b && getX() <= c && getY() <= d;
    }

    public final void initEntity() {
        hashCode = hashCodeGenerator.getAndIncrement();
        mapRegionsIds = new CopyOnWriteArrayList<Integer>();
        walkSteps = new ConcurrentLinkedQueue<Object[]>();
        receivedHits = new ConcurrentLinkedQueue<Hit>();
        receivedDamage = new ConcurrentHashMap<Entity, Integer>();
        temporaryAttributes = new ConcurrentHashMap<>();
        nextHits = new ArrayList<>();
        hitOverflow = new LinkedList<>();
        nextWalkDirection = nextRunDirection - 1;
        lastFaceEntity = -1;
        nextFaceEntity = -2;
        poison.setEntity(this);
        if (tickManager == null) {
            tickManager = new TickManager(this);
        }
        tickManager.init();
        if (newPoison == null) {
            newPoison = new NewPoison(this);
        }
        newPoison.setEntity(this);
        if (tickTimers == null)
            tickTimers = new HashMap<>();
        if (intMap == null)
            intMap = new HashMap<>();
        if (longMap == null)
            longMap = new HashMap<>();
        if (booleanMap == null)
            booleanMap = new HashMap<>();
        if (stringKey == null)
            stringKey = new HashMap<>();
    }

    public int getClientIndex() {
        return index + (this instanceof Player ? 32768 : 0);
    }

    public final boolean inWilderness() {
        return (getX() >= 3011 && getX() <= 3132 && getY() >= 10052 && getY() <= 10175) || (getX() >= 2940 && getX() <= 3395 && getY() >= 3525 && getY() <= 4000) || (getX() >= 3264 && getX() <= 3279 && getY() >= 3279 && getY() <= 3672) || (getX() >= 2756 && getX() <= 2875 && getY() >= 5512 && getY() <= 5627) || (getX() >= 3158 && getX() <= 3181 && getY() >= 3679 && getY() <= 3697) || (getX() >= 2293 && getX() <= 2357 && getY() >= 3662 && getY() <= 3710) || (getX() >= 3280 && getX() <= 3183 && getY() >= 3885 && getY() <= 3888) || (getX() >= 3012 && getX() <= 3059 && getY() >= 10303 && getY() <= 10351) || (getX() >= 3078 && getX() <= 3134 && getY() >= 9923 && getY() <= 9999);
    }

    public abstract void handleHit(Hit hit);

    public abstract int getHitDelay(Entity attacker, Entity defender);

    public abstract void handleIncommingHit(Hit hit);

    public void reset(boolean attributes) {
        setHitpoints(getMaxHitpoints());
        receivedHits.clear();
        resetCombat();
        walkSteps.clear();
        poison.reset();
        resetReceivedDamage();
        if (attributes)
            temporaryAttributes.clear();
    }

    public void resetReceivedHits() {
        nextHits.clear();
        hitOverflow.clear();
        receivedHits.clear();
        receivedDamage.clear();
    }

    public void reset() {
        reset(true);
    }

    public void updatePlayerMask(UpdateMask newMask) {
        this.updateMask = newMask;
    }

    public void resetCombat() {
    }

    public void applyHit(Hit hit) {
        //if (isDead())
          //  hit.setDamage(0);
        Entity source = hit.getSource();
        if (source instanceof Player p2) {
            if (hit.getLook() == HitLook.REGULAR_DAMAGE) {
                System.out.println("applyHit: source is: " + p2.getUsername());
            }
        }
        if (source instanceof Player && source.dead && hit.isCombatLook()) {
            System.out.println("reset damage because source was dead & hit is " + hit.getLook().name());
            resetReceivedHits();
            return;
        }
        if (this instanceof Player player) {
            if (player.invulnerable)
                return;
            if (player.healMode) {
                if (hit.getDamage() >= 1)
                    hit.setHealHit();
            }
        }
        if (hit.getLook() == HitLook.REGULAR_DAMAGE) {
            System.out.println("added receivedHits");
        }
        receivedHits.add(hit);
    }

    public void applyBleed(Hit originalHit, double bleedPercent, int maxTickDamage, int initialDelay, int tickInterval) {
        if (originalHit == null || originalHit.getDamage() <= 0 || bleedPercent <= 0) return;

        int totalBleed = (int) Math.round(originalHit.getDamage() * bleedPercent);
        int remaining = totalBleed;
        int delay = initialDelay;

        while (remaining > 0) {
            int chunk = Math.min(maxTickDamage, remaining);
            remaining -= chunk;

            BleedHit bleedHit = new BleedHit(originalHit.getSource(), chunk, HitLook.REGULAR_DAMAGE, delay);
            receivedHits.add(bleedHit);

            delay += tickInterval;
        }
    }


    private transient final int totalHitsProcess = 8;//this should in reality be 255

    public void processReceivedHits() {
        if (this instanceof Player player) {
            if (player.isTeleporting()) {
                resetReceivedHits();
                return;
            }
            if (player.getEmotesManager().getNextEmoteEnd() >= Utils.currentTimeMillis())
                return;
            if (player.isLocked())
                return;
        }
        int processedCount = 0;
        Iterator<Hit> iterator = receivedHits.iterator();
        while (iterator.hasNext() && processedCount < totalHitsProcess) {
            Hit hit = iterator.next();
            if (this instanceof Player player) {
                if (player.getEmotesManager().getNextEmoteEnd() >= Utils.currentTimeMillis())
                    return;
                if (player.isLocked())
                    return;
            }
            if (hit instanceof BleedHit bleed) {
                if (!bleed.tick()) {
                    continue;
                }
            }
            iterator.remove();
            processHit(hit);
            processedCount++;
        }
        processOverflowHits();
    }

    public void processHit(Hit hit) {
        Entity source = hit.getSource();
        if (source instanceof Player && source.dead && hit.isCombatLook()) {
            resetReceivedHits();
            System.out.println("reset2 damage because source was dead & hit is " + hit.getLook().name());
            return;
        }
        if (this instanceof Player) {
            if (hit.getDamage() < 0)
                return;
        }
        if (source instanceof Player p2) {
            if (hit.getLook() == HitLook.REGULAR_DAMAGE) {
                System.out.println("processHit: removeHitpoints " + p2.getUsername());
            }
        }
        removeHitpoints(hit);
        if (nextHits.size() < totalHitsProcess) {
            nextHits.add(hit);
        } else {
            hitOverflow.add(hit);
        }
    }

    public void processOverflowHits() {
        while (!hitOverflow.isEmpty() && nextHits.size() < totalHitsProcess) {
            nextHits.add(hitOverflow.poll());
        }
    }


    public void applyHeal(Hit hit) {
        hit.setHealHit();
        processHit(hit);
    }

    public void removeHitpoints(Hit hit) {
        if (isDead() || hit.getLook() == HitLook.ABSORB_DAMAGE)
            return;
        Entity source = hit.getSource();
        if (source instanceof Player && source.dead && hit.isCombatLook()) {
            System.out.println("reset3 damage because source was dead & hit is " + hit.getLook().name());
            return;
        }
        if (hit.getLook() == HitLook.HEALED_DAMAGE) {
            heal(hit.getDamage());
            return;
        }
        handleHit(hit);
        addReceivedDamage(hit.getSource(), hit.getDamage());
        if (this instanceof Player) {
            Player p = (Player) this;
            if (hitpoints - hit.getDamage() <= p.getMaxHitpoints() * 0.2 && hitpoints - hit.getDamage() > 0) {
                if (p.getPrayer().isActive(NormalPrayer.RETRIBUTION)) {
                    p.gfx(new Graphics(436));
                    p.heal((int) Math.round((p.getSkills().getLevelForXp(Skills.PRAYER) * 0.25)));
                    p.getPrayer().drainPrayer(p.getPrayer().getPrayerPoints());
                } else if (p.getEquipment().getAmuletId() == 11090) {
                    p.heal((int) (p.getMaxHitpoints() * 0.3));
                    for (Hit hits : p.getNextHits())
                        hits.setDamage(-2);
                    p.getEquipment().deleteItem(11090, 1);
                    p.getAppearence().generateAppearenceData();
                    p.getPackets().sendGameMessage("Your pheonix necklace heals you, but is destroyed in the process.");
                } else if (p.getEquipment().getRingId() == 2570) {
                    ModernMagicks.sendNormalTeleportSpell(p, -1, Settings.RESPAWN_PLAYER_LOCATION);
                    p.getEquipment().deleteItem(2570, 1);
                    p.getAppearence().generateAppearenceData();
                    p.getPackets().sendGameMessage("Your ring of life saves you, but is destroyed in the process.");
                }
            }
        }
        if (this instanceof NPC) {
            if (hitpoints < hit.getDamage()) {
                getPoison().reset();
                hit.setDamage(hitpoints);
            }
        }

        setHitpoints(hitpoints < hit.getDamage() ? 0 : hitpoints - hit.getDamage());
        if (hit.getSource() instanceof Player) {
            Player p = (Player) hit.getSource();
            p.refreshHitPoints();
        }
        Entity attacker = hit.getSource();
        if (attacker instanceof Player player && attacker != this) {
            if (player.toggles("HEALTH_OVERLAY", false)) {
                if (player.getTemporaryTarget() == this) {
                    player.healthOverlay.updateHealthOverlay(player, this, true);
                }
            }
        }
        if (hitpoints <= 0) {
            sendDeath(hit.getSource());
        }
    }

    public void resetReceivedDamage() {
        receivedDamage.clear();
    }

    public void resetAllDamage() {
        receivedDamage.clear();
    }

    public void removeDamage(Entity entity) {
        receivedDamage.remove(entity);
    }

    public Player getMostDamageReceivedSourcePlayer() {
        Player player = null;
        int damage = -1;
        for (Entity source : receivedDamage.keySet()) {
            if (!(source instanceof Player))
                continue;
            Integer d = receivedDamage.get(source);
            if (source.hasFinished()) {
                receivedDamage.remove(source);
                continue;
            }
            if (d > damage) {
                player = (Player) source;
                damage = d;
            }
        }
        return player;
    }

    public int getDamageReceived(Player source) {
        Integer d = receivedDamage.get(source);
        if (d == null || source.hasFinished()) {
            receivedDamage.remove(source);
            return -1;
        }
        return d;
    }

    public void processReceivedDamage() {
        for (Entity source : receivedDamage.keySet()) {
            Integer damage = receivedDamage.get(source);
            if (damage == null || source.hasFinished()) {
                receivedDamage.remove(source);
                continue;
            }
            damage--;
            if (damage == 0) {
                receivedDamage.remove(source);
                continue;
            }
            receivedDamage.put(source, damage);
        }
    }

    public void addReceivedDamage(Entity source, int amount) {
        if (source == null)
            return;
        Integer damage = receivedDamage.get(source);
        damage = damage == null ? amount : damage + amount;
        if (damage < 0)
            receivedDamage.remove(source);
        else
            receivedDamage.put(source, damage);
    }

    public void heal(int ammount) {
        heal(ammount, 0);
    }

    public void heal(int ammount, int extra) {
        setHitpoints((hitpoints + ammount) >= (getMaxHitpoints() + extra) ? (getMaxHitpoints() + extra) : (hitpoints + ammount));
    }

    public void heal(int amount, boolean message, boolean hitmark) {
        if (hitmark)
            applyHeal(new Hit(this, amount, HitLook.HEALED_DAMAGE));
        else
            setHitpoints((hitpoints + amount) >= (getMaxHitpoints()) ? (getMaxHitpoints()) : (hitpoints + amount));
    }

    public boolean hasWalkSteps() {
        return !walkSteps.isEmpty();
    }

    public abstract void sendDeath(Entity source);

    public void processMovement() {
        WorldTile preMovementPosition = new WorldTile(this);
        predictedWorldTile = null;
        lastWorldTile = new WorldTile(this);
        if (lastFaceEntity >= 0) {
            Entity target = lastFaceEntity >= 32768
                    ? World.getPlayers().get(lastFaceEntity - 32768)
                    : World.getNPCs().get(lastFaceEntity);
            if (target != null) {
                int size = target.getSize();
                updateAngle(target, size, size);
            }
        }

        nextWalkDirection = nextRunDirection = -1;

        if (nextWorldTile != null) {
            setLocation(nextWorldTile);
            nextWorldTile = null;
            teleported = true;

            if (this instanceof Player p && p.getTemporaryMoveType() == -1)
                p.setTemporaryMoveType(Player.TELE_MOVE_TYPE);

            World.updateEntityRegion(this);
            if (needMapUpdate())
                loadMapRegions();

            resetWalkSteps();
            predictedWorldTile = null;
            return;
        }

        teleported = false;

        if (walkSteps == null || walkSteps.isEmpty()) {
            predictedWorldTile = null;
            return;
        }

        predictedWorldTile = calculatePostMovementPosition();

        if (this instanceof Player p) {
            if (p.getEmotesManager().getNextEmoteEnd() >= Utils.currentTimeMillis()) {
                predictedWorldTile = null;
                return;
            }
        }

        if (this instanceof TorturedSoul ts) {
            if (ts.switchWalkStep()) {
                predictedWorldTile = null;
                return;
            }
        }

        if (this instanceof Player p && p.getRunEnergy() <= 0)
            setRun(false);

        for (int stepCount = 0; stepCount < (run ? 2 : 1); stepCount++) {
            Object[] nextStep = getNextWalkStep();
            if (nextStep == null)
                break;

            int dir = (int) nextStep[0];

            boolean checkClip = (boolean) nextStep[3];
            if ((checkClip && !World.checkWalkStep(getPlane(), getX(), getY(), dir, getSize()))
                    || (this instanceof NPC && !canWalkNPC(getX() + Utils.DIRECTION_DELTA_X[dir],
                    getY() + Utils.DIRECTION_DELTA_Y[dir]))) {
                resetWalkSteps();
                break;
            }

            previousTile = new WorldTile(getX(), getY(), getPlane());

            if (stepCount == 0)
                nextWalkDirection = dir;
            else
                nextRunDirection = dir;

            moveLocation(Utils.DIRECTION_DELTA_X[dir], Utils.DIRECTION_DELTA_Y[dir], 0);

            if (run && stepCount == 0) {
                Object[] previewStep = previewNextWalkStep();
                if (previewStep == null)
                    break;

                int previewDir = (int) previewStep[0];
                if (Utils.getPlayerRunningDirection(
                        Utils.DIRECTION_DELTA_X[dir] + Utils.DIRECTION_DELTA_X[previewDir],
                        Utils.DIRECTION_DELTA_Y[dir] + Utils.DIRECTION_DELTA_Y[previewDir]) == -1)
                    break;
            }
        }

        if (this instanceof Player player && run && nextWalkDirection != -1 && nextRunDirection == -1) {
            if (walkSteps.size() <= 1 || player.getRunEnergy() <= 0) {
                player.setTemporaryMoveType(Player.WALK_MOVE_TYPE);
            }
        }

        World.updateEntityRegion(this);
        if (needMapUpdate())
            loadMapRegions();
    }


    private WorldTile calculatePostMovementPosition() {
        if (walkSteps == null || walkSteps.isEmpty())
            return new WorldTile(this);

        int simulatedX = getX();
        int simulatedY = getY();
        int simulatedPlane = getPlane();

        int maxSteps = run ? 2 : 1;

        for (int stepCount = 0; stepCount < maxSteps; stepCount++) {
            Object[] step = previewWalkStep(stepCount);
            if (step == null)
                break;

            int dir = (int) step[0];
            boolean checkClip = (boolean) step[3];

            if (checkClip && !World.checkWalkStep(simulatedPlane, simulatedX, simulatedY, dir, getSize()))
                break;

            if (this instanceof NPC) {
                int nextX = simulatedX + Utils.DIRECTION_DELTA_X[dir];
                int nextY = simulatedY + Utils.DIRECTION_DELTA_Y[dir];
                if (!canWalkNPC(nextX, nextY))
                    break;
            }

            simulatedX += Utils.DIRECTION_DELTA_X[dir];
            simulatedY += Utils.DIRECTION_DELTA_Y[dir];

            if (run && stepCount == 0) {
                Object[] preview = previewWalkStep(stepCount + 1);
                if (preview == null)
                    break;

                int previewDir = (int) preview[0];
                if (Utils.getPlayerRunningDirection(
                        Utils.DIRECTION_DELTA_X[dir] + Utils.DIRECTION_DELTA_X[previewDir],
                        Utils.DIRECTION_DELTA_Y[dir] + Utils.DIRECTION_DELTA_Y[previewDir]) == -1) {
                    break;
                }
            }
        }

        return new WorldTile(simulatedX, simulatedY, simulatedPlane);
    }


    public WorldTile getFaceWorldTile() {
        return new WorldTile(getCoordFaceX(getSize()), getCoordFaceY(getSize()), getPlane());
    }


    private Object[] previewWalkStep(int index) {
        if (walkSteps == null || walkSteps.isEmpty())
            return null;

        int i = 0;
        for (Object[] step : walkSteps) {
            if (i++ == index)
                return step;
        }
        return null;
    }



    public void updateAngle(WorldTile base, int sizeX, int sizeY) {
        WorldTile from = nextWorldTile != null ? nextWorldTile : this;
        int srcX = (from.getX() * 512) + (getSize() * 256);
        int srcY = (from.getY() * 512) + (getSize() * 256);
        int dstX = (base.getX() * 512) + (sizeX * 256);
        int dstY = (base.getY() * 512) + (sizeY * 256);
        int deltaX = srcX - dstX;
        int deltaY = srcY - dstY;
        direction = deltaX != 0 || deltaY != 0 ? (int) (Math.atan2((double) deltaX, (double) deltaY) * 2607.5945876176133) & 0x3FFF : 0;
    }

    private Object[] previewNextWalkStep() {
        Object[] step = walkSteps.peek();
        if (step == null)
            return null;
        return step;
    }

    @Override
    public void moveLocation(int xOffset, int yOffset, int planeOffset) {
        super.moveLocation(xOffset, yOffset, planeOffset);
        direction = Utils.getFaceDirection(xOffset, yOffset);
    }

    private boolean needMapUpdate() {
        return needMapUpdate(lastLoadedMapRegionTile);
    }

    public boolean needMapUpdate(WorldTile tile) {
        int lastMapRegionX = tile.getChunkX();
        int lastMapRegionY = tile.getChunkY();
        int regionX = getChunkX();
        int regionY = getChunkY();
        int size = ((Settings.MAP_SIZES[mapSize] >> 3) / 2) - 1;
        return Math.abs(lastMapRegionX - regionX) >= size || Math.abs(lastMapRegionY - regionY) >= size;
    }

    // normal walk steps method
    public boolean addWalkSteps(int destX, int destY) {
        return addWalkSteps(destX, destY, -1);
    }

    public boolean clipedProjectile(WorldTile tile, boolean checkClose) {
        if (tile instanceof Entity) {
            Entity e = (Entity) tile;
            WorldTile me = this;
            if (tile instanceof NPC) {
                NPC n = (NPC) tile;
                tile = n.getMiddleWorldTile();
            } else if (this instanceof NPC) {
                NPC n = (NPC) this;
                me = n.getMiddleWorldTile();
            }
            return clipedProjectile(tile, checkClose, 1) || e.clipedProjectile(me, checkClose, 1);
        }
        return clipedProjectile(tile, checkClose, 1);
    }

    public boolean clipedProjectile(WorldTile tile, boolean checkClose, int size) {
        int myX = getX();
        int myY = getY();
        if (this instanceof NPC) {
            NPC n = (NPC) this;
            WorldTile thist = n.getMiddleWorldTile();
            myX = thist.getX();
            myY = thist.getY();
        }
        int destX = tile.getX();
        int destY = tile.getY();
        if (myX == destX && destY == myY)
            return true;
        int lastTileX = myX;
        int lastTileY = myY;
        while (true) {
            if (myX < destX)
                myX++;
            else if (myX > destX)
                myX--;
            if (myY < destY)
                myY++;
            else if (myY > destY)
                myY--;
            int dir = Utils.getMoveDirection(myX - lastTileX, myY - lastTileY);
            if (dir == -1)
                return false;
            if (checkClose) {
                if (!World.checkWalkStep(getPlane(), lastTileX, lastTileY, dir, size))
                    return false;
            } else if (!World.checkProjectileStep(getPlane(), lastTileX, lastTileY, dir, size))
                return false;
            lastTileX = myX;
            lastTileY = myY;
            if (lastTileX == destX && lastTileY == destY)
                return true;
        }
    }

    public boolean calcFollow(WorldTile target, boolean inteligent) {
        return calcFollow(target, -1, true, inteligent);
    }

    public boolean addWalkStepsInteract(int destX, int destY, int maxStepsCount, int size, boolean calculate) {
        return addWalkStepsInteract(destX, destY, maxStepsCount, size, size, calculate);
    }

    /*
     * return added all steps
     */
    public boolean addWalkStepsInteract(final int destX, final int destY, int maxStepsCount, int sizeX, int sizeY, boolean calculate) {
        if (isFrozen())
            return false;
        int[] lastTile = getLastWalkTile();
        int myX = lastTile[0];
        int myY = lastTile[1];
        int stepCount = 0;
        while (true) {
            stepCount++;
            int myRealX = myX;
            int myRealY = myY;
            if (myX < destX)
                myX++;
            else if (myX > destX)
                myX--;
            if (myY < destY)
                myY++;
            else if (myY > destY)
                myY--;
            if ((this instanceof NPC && !canWalkNPC(myX, myY)) || !addWalkStep(myX, myY, lastTile[0], lastTile[1], true)) {
                if (!calculate)
                    return false;
                myX = myRealX;
                myY = myRealY;
                int[] myT = calculatedStep(myRealX, myRealY, destX, destY, lastTile[0], lastTile[1], sizeX, sizeY);
                if (myT == null)
                    return false;
                myX = myT[0];
                myY = myT[1];
            }
            int distanceX = myX - destX;
            int distanceY = myY - destY;
            if (!(distanceX > sizeX || distanceX < -1 || distanceY > sizeY || distanceY < -1))
                return true;
            if (stepCount == maxStepsCount)
                return true;
            lastTile[0] = myX;
            lastTile[1] = myY;
            if (lastTile[0] == destX && lastTile[1] == destY)
                return true;
        }
    }

    private int getPreviewNextWalkStep() {
        Object[] step = walkSteps.poll();
        if (step == null)
            return -1;
        return (int) step[0];
    }

    // checks collisions
    public boolean canWalkNPC(int toX, int toY) {
        // stucking nomad is part of strategy
        if (this instanceof Familiar || this instanceof Nomad || ((NPC) this).isIntelligentRouteFinder() || ((NPC) this).isForceWalking())
            return true;
        int size = getSize();
        for (int regionId : getMapRegionsIds()) {
            List<Integer> npcIndexes = World.getRegion(regionId).getNPCsIndexes();
            if (npcIndexes != null/* && npcIndexes.size() < 100 */) {
                for (int npcIndex : npcIndexes) {
                    NPC target = World.getNPCs().get(npcIndex);
                    if (target == null || target == this || target.isDead() || target.hasFinished() || target.getPlane() != getPlane() || target instanceof Familiar || target instanceof Pet)
                        continue;
                    int targetSize = target.getSize();
                    // npc is under this target so skip checking it
                    if (Utils.collides(this, target))
                        continue;
                    WorldTile tile = new WorldTile(target);
                    // has to be checked aswell, cuz other one assumes npc will manage to move no
                    // matter what
                    if (Utils.colides(toX, toY, size, tile.getX(), tile.getY(), targetSize))
                        return false;
                    if (target.getNextWalkDirection() != -1) {
                        tile.moveLocation(Utils.DIRECTION_DELTA_X[target.getNextWalkDirection()], Utils.DIRECTION_DELTA_Y[target.getNextWalkDirection()], 0);
                        if (target.getNextRunDirection() != -1)
                            tile.moveLocation(Utils.DIRECTION_DELTA_X[target.getNextRunDirection()], Utils.DIRECTION_DELTA_Y[target.getNextRunDirection()], 0);
                        // target is at x,y
                        if (Utils.colides(toX, toY, size, tile.getX(), tile.getY(), targetSize))
                            return false;
                    }
                }
            }
        }
        return true;
    }

    // checks collisions
    public boolean canWalkNPC(int toX, int toY, boolean checkUnder) {
        if (!isAtMultiArea() /*
         * || (!checkUnder && !canWalkNPC(getX(), getY(), true))
         */)
            return true;
        int size = getSize();
        for (int regionId : getMapRegionsIds()) {
            List<Integer> npcIndexes = World.getRegion(regionId).getNPCsIndexes();
            if (npcIndexes != null && npcIndexes.size() < 50) {
                for (int npcIndex : npcIndexes) {
                    NPC target = World.getNPCs().get(npcIndex);
                    if (target == null || target == this || target.isDead() || target.hasFinished() || target.getPlane() != getPlane() || !target.isAtMultiArea() || (!(this instanceof Familiar) && target instanceof Familiar))
                        continue;
                    int targetSize = target.getSize();
                    if (!checkUnder && target.getNextWalkDirection() == -1) { // means
                        // the
                        // walk
                        // hasnt
                        // been
                        // processed
                        // yet
                        int previewDir = getPreviewNextWalkStep();
                        if (previewDir != -1) {
                            WorldTile tile = target.transform(Utils.DIRECTION_DELTA_X[previewDir], Utils.DIRECTION_DELTA_Y[previewDir], 0);
                            if (Utils.colides(tile.getX(), tile.getY(), targetSize, getX(), getY(), size))
                                continue;

                            if (Utils.colides(tile.getX(), tile.getY(), targetSize, toX, toY, size))
                                return false;
                        }
                    }
                    if (Utils.colides(target.getX(), target.getY(), targetSize, getX(), getY(), size))
                        continue;
                    if (Utils.colides(target.getX(), target.getY(), targetSize, toX, toY, size))
                        return false;
                }
            }
        }
        return true;
    }

    public WorldTile getMiddleWorldTile() {
        int size = getSize();
        return size == 1 ? this : new WorldTile(getCoordFaceX(size), getCoordFaceY(size), getPlane());
    }

    public static boolean findBasicRoute(Entity src, WorldTile dest, int maxStepsCount, boolean calculate) {
        int[] srcPos = src.getLastWalkTile();
        int[] destPos = {dest.getX(), dest.getY()};
        int srcSize = src.getSize();
        // set destSize to 0 to walk under it else follows
        int destSize = dest instanceof Entity ? ((Entity) dest).getSize() : 1;
        int[] destScenePos = {destPos[0] + destSize - 1, destPos[1] + destSize - 1};// Arrays.copyOf(destPos,
        // 2);//destSize
        // ==
        // 1
        // ?
        // Arrays.copyOf(destPos,
        // 2)
        // :
        // new
        // int[]
        // {WorldTile.getCoordFaceX(destPos[0],
        // destSize,
        // destSize,
        // -1),
        // WorldTile.getCoordFaceY(destPos[1],
        // destSize,
        // destSize,
        // -1)};
        while (maxStepsCount-- != 0) {
            int[] srcScenePos = {srcPos[0] + srcSize - 1, srcPos[1] + srcSize - 1};// srcSize
            // ==
            // 1
            // ?
            // Arrays.copyOf(srcPos,
            // 2)
            // :
            // new
            // int[]
            // {
            // WorldTile.getCoordFaceX(srcPos[0],
            // srcSize,
            // srcSize,
            // -1),
            // WorldTile.getCoordFaceY(srcPos[1],
            // srcSize,
            // srcSize,
            // -1)};
            if (!Utils.isOnRange(srcPos[0], srcPos[1], srcSize, destPos[0], destPos[1], destSize, 0)) {
                if (srcScenePos[0] < destScenePos[0] && srcScenePos[1] < destScenePos[1] && (!(src instanceof NPC) || src.canWalkNPC(srcPos[0] + 1, srcPos[1] + 1)) && src.addWalkStep(srcPos[0] + 1, srcPos[1] + 1, srcPos[0], srcPos[1], true)) {
                    srcPos[0]++;
                    srcPos[1]++;
                    continue;
                }
                if (srcScenePos[0] > destScenePos[0] && srcScenePos[1] > destScenePos[1] && (!(src instanceof NPC) || src.canWalkNPC(srcPos[0] - 1, srcPos[1] - 1)) && src.addWalkStep(srcPos[0] - 1, srcPos[1] - 1, srcPos[0], srcPos[1], true)) {
                    srcPos[0]--;
                    srcPos[1]--;
                    continue;
                }
                if (srcScenePos[0] < destScenePos[0] && srcScenePos[1] > destScenePos[1] && (!(src instanceof NPC) || src.canWalkNPC(srcPos[0] + 1, srcPos[1] - 1)) && src.addWalkStep(srcPos[0] + 1, srcPos[1] - 1, srcPos[0], srcPos[1], true)) {
                    srcPos[0]++;
                    srcPos[1]--;
                    continue;
                }
                if (srcScenePos[0] > destScenePos[0] && srcScenePos[1] < destScenePos[1] && (!(src instanceof NPC) || src.canWalkNPC(srcPos[0] - 1, srcPos[1] + 1)) && src.addWalkStep(srcPos[0] - 1, srcPos[1] + 1, srcPos[0], srcPos[1], true)) {
                    srcPos[0]--;
                    srcPos[1]++;
                    continue;
                }
                if (srcScenePos[0] < destScenePos[0] && (!(src instanceof NPC) || src.canWalkNPC(srcPos[0] + 1, srcPos[1])) && src.addWalkStep(srcPos[0] + 1, srcPos[1], srcPos[0], srcPos[1], true)) {
                    srcPos[0]++;
                    continue;
                }
                if (srcScenePos[0] > destScenePos[0] && (!(src instanceof NPC) || src.canWalkNPC(srcPos[0] - 1, srcPos[1])) && src.addWalkStep(srcPos[0] - 1, srcPos[1], srcPos[0], srcPos[1], true)) {
                    srcPos[0]--;
                    continue;
                }
                if (srcScenePos[1] < destScenePos[1] && (!(src instanceof NPC) || src.canWalkNPC(srcPos[0], srcPos[1] + 1)) && src.addWalkStep(srcPos[0], srcPos[1] + 1, srcPos[0], srcPos[1], true)) {
                    srcPos[1]++;
                    continue;
                }
                if (srcScenePos[1] > destScenePos[1] && (!(src instanceof NPC) || src.canWalkNPC(srcPos[0], srcPos[1] - 1)) && src.addWalkStep(srcPos[0], srcPos[1] - 1, srcPos[0], srcPos[1], true)) {
                    srcPos[1]--;
                    continue;
                }
                return false;
            }
            break; // for now nothing between break and return
        }
        return true;
    }

    public void setAttackedBy(Entity e) {
        temporaryAttribute().put("attackedBy", e);
    }

    public Entity getAttackedBy() {
        Entity attackedBy = (Entity) temporaryAttribute().get("attackedBy");
        if (attackedBy == null)
            return null;
        return attackedBy;
    }

    public void setAttackedByDelay(long attackedByDelay) {
        temporaryAttribute().put("attackedByDelay", attackedByDelay + Utils.currentTimeMillis());
    }

    public long getAttackedByDelay() {

        Long attackedByDelay = (Long) temporaryAttribute().get("attackedByDelay");
        if (attackedByDelay == null)
            return 0;
        return attackedByDelay;
    }

    public void setAttackDelay(long attackDelay) {
        temporaryAttribute().put("attackDelay", attackDelay + Utils.currentTimeMillis());
    }

    public boolean isInCombat() {
        return getTickManager().isActive(TickManager.TickKeys.LAST_ATTACKED_TICK);
    }

    public void setFlinch(long flinchDelay) {
        temporaryAttribute().put("flinchDelay", flinchDelay + Utils.currentTimeMillis());
    }

    public long getFlinchDelay() {
        Long flinchDelay = (Long) temporaryAttribute().get("flinchDelay");
        if (flinchDelay == null)
            return 0;
        return flinchDelay;
    }

    public void setFreezeDelay(int ticks) {
        setTimer(Keys.IntKey.FREEZE_TICKS, ticks);
    }

    public void setFreezeImmune(int ticks) {
        setTimer(Keys.IntKey.FREEZE_IMMUNE_TICKS, ticks);
    }

    // used for normal npc follow int maxStepsCount, boolean calculate used to
    // save mem on normal path
    public boolean calcFollow(WorldTile target, int maxStepsCount, boolean calculate, boolean inteligent) {
        if (isFrozen())
            return false;
        if (inteligent) {
            int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, getX(), getY(), getPlane(), getSize(), target instanceof WorldObject ? new ObjectStrategy((WorldObject) target) : new EntityStrategy((Entity) target), true);
            if (steps == -1)
                return false;
            if (steps == 0)
                return true;
            int[] bufferX = RouteFinder.getLastPathBufferX();
            int[] bufferY = RouteFinder.getLastPathBufferY();
            int stepsAdded = 0;
            for (int step = steps - 1; step >= 0; step--) {
                if (addWalkSteps(bufferX[step], bufferY[step], 25, true)) {
                    stepsAdded++;
                    if (stepsAdded >= 25) break;
                }
            }
            return true;
        }
        return findBasicRoute(this, target, maxStepsCount, true);
        /*
         * else if (true == true) { //just keeping old code System.out.println("test");
         * return findBasicRoute(this, (Entity) target, maxStepsCount, true); }
         *
         * int[] lastTile = getLastWalkTile(); int myX = lastTile[0]; int myY =
         * lastTile[1]; int stepCount = 0; int size = getSize(); int destX =
         * target.getX(); int destY = target.getY(); int sizeX = target instanceof
         * WorldObject ? ((WorldObject)target).getDefinitions().getSizeX() :
         * ((Entity)target).getSize(); int sizeY = target instanceof WorldObject ?
         * ((WorldObject)target).getDefinitions().getSizeY() : sizeX; while (true) {
         * stepCount++; int myRealX = myX; int myRealY = myY; if (Utils.isOnRange(myX,
         * myY, size, destX, destY, sizeX, 0)) return true; if (myX < destX) myX++; else
         * if (myX > destX) myX--; if (myY < destY) myY++; else if (myY > destY) myY--;
         * if ((this instanceof NPC && !canWalkNPC(myX, myY)) || !addWalkStep(myX, myY,
         * lastTile[0], lastTile[1], true)) { if (!calculate) return false; myX =
         * myRealX; myY = myRealY; int[] myT = calculatedStep(myRealX, myRealY, destX,
         * destY, lastTile[0], lastTile[1], sizeX, sizeY); if (myT == null) return
         * false; myX = myT[0]; myY = myT[1]; } if (stepCount == maxStepsCount) return
         * true; lastTile[0] = myX; lastTile[1] = myY; if (lastTile[0] == destX &&
         * lastTile[1] == destY) return true; }
         */
    }

    // used for normal npc follow
    private int[] calculatedStep(int myX, int myY, int destX, int destY, int lastX, int lastY, int sizeX, int sizeY) {
        if (myX < destX) {
            myX++;
            if ((this instanceof NPC && !canWalkNPC(myX, myY)) || !addWalkStep(myX, myY, lastX, lastY, true))
                myX--;
            else if (!(myX - destX > sizeX || myX - destX < -1 || myY - destY > sizeY || myY - destY < -1)) {
                if (myX == lastX || myY == lastY)
                    return null;
                return new int[]{myX, myY};
            }
        } else if (myX > destX) {
            myX--;
            if ((this instanceof NPC && !canWalkNPC(myX, myY)) || !addWalkStep(myX, myY, lastX, lastY, true))
                myX++;
            else if (!(myX - destX > sizeX || myX - destX < -1 || myY - destY > sizeY || myY - destY < -1)) {
                if (myX == lastX || myY == lastY)
                    return null;
                return new int[]{myX, myY};
            }
        }
        if (myY < destY) {
            myY++;
            if ((this instanceof NPC && !canWalkNPC(myX, myY)) || !addWalkStep(myX, myY, lastX, lastY, true))
                myY--;
            else if (!(myX - destX > sizeX || myX - destX < -1 || myY - destY > sizeY || myY - destY < -1)) {
                if (myX == lastX || myY == lastY)
                    return null;
                return new int[]{myX, myY};
            }
        } else if (myY > destY) {
            myY--;
            if ((this instanceof NPC && !canWalkNPC(myX, myY)) || !addWalkStep(myX, myY, lastX, lastY, true)) {
                myY++;
            } else if (!(myX - destX > sizeX || myX - destX < -1 || myY - destY > sizeY || myY - destY < -1)) {
                if (myX == lastX || myY == lastY)
                    return null;
                return new int[]{myX, myY};
            }
        }
        if (myX == lastX || myY == lastY)
            return null;
        return new int[]{myX, myY};
    }

    /*
     * return added all steps
     */
    public boolean addWalkSteps(final int destX, final int destY, int maxStepsCount) {
        return addWalkSteps(destX, destY, -1, true);
    }

    /*
     * return added all steps
     */
    public boolean addWalkSteps(final int destX, final int destY, int maxStepsCount, boolean check) {
        int[] lastTile = getLastWalkTile();
        int myX = lastTile[0];
        int myY = lastTile[1];
        int stepCount = 0;
        while (true) {
            stepCount++;
            if (myX < destX)
                myX++;
            else if (myX > destX)
                myX--;
            if (myY < destY)
                myY++;
            else if (myY > destY)
                myY--;
            if (!addWalkStep(myX, myY, lastTile[0], lastTile[1], check))
                return false;
            if (stepCount == maxStepsCount)
                return true;
            lastTile[0] = myX;
            lastTile[1] = myY;
            if (lastTile[0] == destX && lastTile[1] == destY) {
                return true;
            }
        }
    }

    public int[] getLastWalkTile() {
        Object[] objects = walkSteps.toArray();
        if (objects.length == 0)
            return new int[]{getX(), getY()};
        Object step[] = (Object[]) objects[objects.length - 1];
        return new int[]{(int) step[1], (int) step[2]};
    }

    public boolean addWalkStep(int nextX, int nextY, int lastX, int lastY, boolean check) {
        int dir = Utils.getMoveDirection(nextX - lastX, nextY - lastY);
        if (dir == -1)
            return false;
        if (check && !World.checkWalkStep(getPlane(), lastX, lastY, dir, getSize()))
            return false;
        if (this instanceof Player) {
            if (!((Player) this).getControlerManager().addWalkStep(lastX, lastY, nextX, nextY))
                return false;
        }
        walkSteps.add(new Object[]{dir, nextX, nextY, check});
        return true;
    }

    public ConcurrentLinkedQueue<Object[]> getWalkSteps() {
        return walkSteps;
    }

    public void resetWalkSteps() {
        walkSteps.clear();
    }

    private Object[] getNextWalkStep() {
        Object[] step = walkSteps.poll();
        if (step == null)
            return null;
        return step;
    }

    public boolean restoreHitPoints() {
        int maxHp = getMaxHitpoints();
        boolean regen = false;
        if (this instanceof Player) {
            Player player = (Player) this;
            regen = player.getEquipment().getGlovesId() == 11133;
        }
        if (hitpoints < maxHp) {
            setHitpoints(hitpoints + (regen ? 2 : 1));
            return true;
        }
        return false;
    }

    public boolean drainHitPoints() {
        int maxHp = getMaxHitpoints();
        if (hitpoints > maxHp) {
            setHitpoints(hitpoints - 1);
            return true;
        }
        return false;
    }

    public boolean needMasksUpdate() {
        return nextFaceEntity != -2 || nextAnimation != null || nextGraphics1 != null || nextGraphics2 != null || nextGraphics3 != null || nextGraphics4 != null || (nextWalkDirection == -1 && nextFaceWorldTile != null) || !nextHits.isEmpty() || nextForceMovement != null || updateMask != null || nextForceTalk != null;
    }

    public boolean dead = false;

    public boolean isDead() {
        return dead || hitpoints == 0;
    }

    public void resetMasks() {
        nextAnimation = null;
        nextGraphics1 = null;
        nextGraphics2 = null;
        nextGraphics3 = null;
        nextGraphics4 = null;
        updateMask = null;
        if (nextWalkDirection == -1)
            nextFaceWorldTile = null;
        nextForceMovement = null;
        nextForceTalk = null;
        nextFaceEntity = -2;
        nextHits.clear();
    }

    public long getLockDelay() {
        return tickManager.getTicksLeft(TickManager.TickKeys.ENTITY_LOCK_TICK);
    }

    public void unlock() {
        tickManager.remove(TickManager.TickKeys.ENTITY_LOCK_TICK);
    }

    public void lock() {
        lock(5000);
    }

    public void setLocked(boolean locked) {
        if (locked) {
            unlock();
        } else {
            lock(5000);
        }
    }

    public void lock(int time) {
        tickManager.addTicks(TickManager.TickKeys.ENTITY_LOCK_TICK, time);
    }

    public boolean isLocked() {
        return tickManager.isActive(TickManager.TickKeys.ENTITY_LOCK_TICK);
    }


    public abstract void finish();

    public abstract int getMaxHitpoints();

    public transient int gameTick;

    public int getGameTicks() {
        return gameTick;
    }

    public void processEntity() {
        processMovement();
        poison.processPoison();
        newPoison.processPoison();
        tickManager.tick();
        gameTick++;
        processReceivedHits();
        processReceivedDamage();
        Iterator<Map.Entry<Keys.IntKey, Integer>> it = tickTimers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Keys.IntKey, Integer> entry = it.next();
            int ticksLeft = entry.getValue() - 1;
            if (ticksLeft <= 0) {
                it.remove();
                onTimerExpired(entry.getKey());
            } else {
                entry.setValue(ticksLeft);
            }
        }
    }

    public int getFreezeDelay() {
        return tickManager.getTicksLeft(TickManager.TickKeys.FREEZE_TICKS);
    }

    public boolean isFrozen() {
        return tickManager.isActive(TickManager.TickKeys.FREEZE_TICKS);
    }

    public boolean isTeleportBlocked() {
        return tickManager.isActive(TickManager.TickKeys.TELEPORT_BLOCK);
    }

    public boolean isTeleportBlockImmune() {
        return tickManager.isActive(TickManager.TickKeys.TELEPORT_BLOCK_IMMUNITY);
    }

    public void teleportBlock(int seconds) {
        tickManager.addSeconds(TickManager.TickKeys.TELEPORT_BLOCK, seconds);
        tickManager.addSeconds(TickManager.TickKeys.TELEPORT_BLOCK_IMMUNITY, seconds + 5);
        if (this instanceof Player)
            ((Player) this).message("You have been teleport blocked.");
    }

    public boolean isFreezeImmune() {
        return tickManager.isActive(TickManager.TickKeys.FREEZE_IMMUNE_TICKS);
    }

    public void freeze(int value) {
        if (this instanceof Player player) {
            player.getTickManager().addTicks(TickManager.TickKeys.FREEZE_TICKS, value, () ->
                    player.message("You are no longer frozen.")
            );
            player.getTickManager().addTicks(TickManager.TickKeys.FREEZE_IMMUNE_TICKS, value + 5);
        } else {
            tickManager.addTicks(TickManager.TickKeys.FREEZE_TICKS, value);
            tickManager.addTicks(TickManager.TickKeys.FREEZE_IMMUNE_TICKS, value + 5);
        }
    }

    public void unfreeze() {
        tickManager.remove(TickManager.TickKeys.FREEZE_TICKS);
    }

    public void addFreezeDelay(int ticks, boolean entangle) {
        if (!isFrozen() && !isFreezeImmune()) {
            freeze(ticks);
            resetWalkSteps();
            if (this instanceof Player player) {
                player.resetWalkSteps();
                if (!entangle)
                    player.message("You have been frozen.");
            }
        } else {
            if (entangle) {
                if (this instanceof Player player) {
                    player.getPackets().sendGameMessage("This player is already effected by this spell.", true);
                }
            }
        }
    }

    public void drainStat(int bonusIndex, int amount) {
        drainStat(-1, bonusIndex, amount, null);
    }

    public void drainStat(int skillId, int amount, String message) {
        drainStat(skillId, -1, amount, message);
    }

    public void drainStat(int skillId, int bonusIndex, int amount, String message) {
        if (this instanceof Player player) {
            player.getSkills().drainLevel(skillId, amount);
            if (message != null) {
                player.message(message);
            }
        } else if (this instanceof NPC npc) {
            int[] bonuses = npc.getBonuses();
            if (bonuses != null && bonusIndex >= 0 && bonusIndex < bonuses.length) {
                bonuses[bonusIndex] = Math.max(0, bonuses[bonusIndex] - amount);
            }
        }
    }

    public void drainDefence(int amount) {
        if (this instanceof Player player) {
            player.getSkills().drainLevel(Skills.DEFENCE, amount);
            player.message("Your defence has been drained!");
        }
    }

    public void drainAttack(int amount) {
        if (this instanceof Player player) {
            player.getSkills().drainLevel(Skills.ATTACK, amount);
            player.message("Your attack has been drained!");
        }
    }

    protected void onTimerExpired(Keys.IntKey timerName) {
        /*if (timerName.equals(Keys.IntKey.FREEZE_TICKS)) {
            tickTimers.remove(Keys.IntKey.FREEZE_TICKS);
        }*/
    }

    private Map<Keys.LongKey, Long> longMap = new HashMap<>();
    private Map<Keys.IntKey, Integer> intMap = new HashMap<>();
    private Map<Keys.BooleanKey, Boolean> booleanMap = new HashMap<>();
    private Map<Keys.StringKey, String> stringKey = new HashMap<>();

    public void set(Keys.LongKey key, long i) {
        longMap.put(key, i);
    }

    public long get(Keys.LongKey key) {
        Long map = longMap.getOrDefault(key, key.getDefaultValue());
        if (map == null) {
            //System.out.println("LongMap: " + map + " is null");
            return -1;
        }
        return map.longValue();
    }

    public void set(Keys.IntKey key, int i) {
        intMap.put(key, i);
    }

    public void add(Keys.IntKey key, int i) {
        intMap.merge(key, i, Integer::sum);
    }

    public void remove(Keys.IntKey key, int i) {
        intMap.compute(key, (k, v) -> Math.max(v - i, 0));
    }

    public void remove(Keys.LongKey key, int i) {
        longMap.compute(key, (k, v) -> Math.max(v - i, 0));
    }

    public void clear(Keys.LongKey key) {
        longMap.remove(key);
    }

    public void clear(Keys.IntKey key) {
        intMap.remove(key);
    }

    public int get(Keys.IntKey key) {
        Integer map = intMap.getOrDefault(key, key.getDefaultValue());
        if (map == null)
            return 0;
        return map.intValue();
    }

    public void set(Keys.BooleanKey key, boolean i) {
        booleanMap.put(key, i);
    }

    public boolean get(Keys.BooleanKey key) {
        Boolean map = booleanMap.getOrDefault(key, key.getDefaultValue());
        if (map == null)
            return false;
        return map.booleanValue();
    }

    public Map<Keys.IntKey, Integer> tickTimers = new HashMap<>();

    protected TickManager tickManager = new TickManager(this);

    public TickManager getTickManager() {
        return tickManager;
    }

    public void setTimer(Keys.IntKey key, int ticks) {
        tickTimers.put(key, ticks);
    }

    public boolean hasTimer(Keys.IntKey key) {
        return tickTimers.containsKey(key) && tickTimers.get(key) > 0;
    }

    public int getTimer(Keys.IntKey key) {
        return tickTimers.getOrDefault(key, 0);
    }

    public void removeTimer(Keys.IntKey key) {
        tickTimers.remove(key);
    }

    public void loadMapRegions() {
        mapRegionsIds.clear();
        isAtDynamicRegion = false;
        int chunkX = getChunkX();
        int chunkY = getChunkY();
        int mapHash = Settings.MAP_SIZES[mapSize] >> 4;
        int minRegionX = (chunkX - mapHash) / 8;
        int minRegionY = (chunkY - mapHash) / 8;
        for (int xCalc = minRegionX < 0 ? 0 : minRegionX; xCalc <= ((chunkX + mapHash) / 8); xCalc++)
            for (int yCalc = minRegionY < 0 ? 0 : minRegionY; yCalc <= ((chunkY + mapHash) / 8); yCalc++) {
                int regionId = yCalc + (xCalc << 8);
                if (World.getRegion(regionId, this instanceof Player) instanceof DynamicRegion)
                    isAtDynamicRegion = true;
                mapRegionsIds.add(regionId);
            }
        lastLoadedMapRegionTile = new WorldTile(this); // creates a immutable
        // copy of this
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }

    public void setIncreasedMaxHitpoints(int increasedMaxHitpoints) {
        this.increasedMaxHitpoints = increasedMaxHitpoints;
    }

    public int getIncreasedMaxHitpoints() {
        return increasedMaxHitpoints;
    }

    public long getPID() {
        return pid;
    }

    public void setLastRegionId(int lastRegionId) {
        this.lastRegionId = lastRegionId;
    }

    public int getLastRegionId() {
        return lastRegionId;
    }

    public int getMapSize() {
        return mapSize;
    }

    public void setMapSize(int size) {
        this.mapSize = size;
        loadMapRegions();
    }

    public CopyOnWriteArrayList<Integer> getMapRegionsIds() {
        return mapRegionsIds;
    }

    public void animate(Animation animation) {
        if (animation == null) {
            this.nextAnimation = new Animation(-1);
        }
        if (this.nextAnimation != null && animation != null) {
            AnimationDefinitions newAnimation = AnimationDefinitions.getAnimationDefinitions(animation.getIds()[0]);
            AnimationDefinitions nextAnim = AnimationDefinitions.getAnimationDefinitions(this.nextAnimation.getIds()[0]);
            if (newAnimation.getPriority() >= nextAnim.getPriority()) {
                this.nextAnimation = animation;
            }
        } else {
            this.nextAnimation = animation;
        }
        if (nextAnimation != null && nextAnimation.getIds()[0] >= 0)
            lastAnimationEnd = Utils.currentTimeMillis() + AnimationDefinitions.getAnimationDefinitions(nextAnimation.getIds()[0]).getEmoteTime();
    }

    public void animateNoCheck(Animation nextAnimation) {
        if (this.nextAnimation != null) {
            this.forceAnimation = true;
        }
        this.nextAnimation = nextAnimation;
    }

    public void setNextAnimation(int id) {
        animate(new Animation(id));
    }

    public void animate(int animationId) {
        animate(new Animation(animationId));
    }

    public void animate(String animation) {
        animate(new Animation(animation));
    }

    public void delayedAnimation(int animationId, int milliseconds, boolean reset) {
    }

    public void setNextAnimationNoPriority(Animation nextAnimation, Entity target) {
        animate(nextAnimation);
    }

    public Animation getNextAnimation() {
        return nextAnimation;
    }

    public void gfx(String graphic) {
        gfx(new Graphics(graphic));
    }

    public void gfx(String graphic, int height) {
        gfx(new Graphics(graphic, height));
    }

    public void gfx(int gfxId) {
        gfx(new Graphics(gfxId));
    }

    public void gfx(int gfxId, int height) {
        gfx(new Graphics(gfxId, 0, height));
    }

    public void gfx(int gfxId, int height, int rotation) {
        gfx(new Graphics(gfxId, rotation, height));
    }

    public void gfxAnchoredSouthWest(int gfxId, int height) {
        var tile = new WorldTile(this.getX() - 1, this.getY() - 1, this.getPlane());
        World.sendGraphics(this, new Graphics(gfxId), tile);
    }

    public void delayGfx(Graphics nextGraphics, int delay) {
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                gfx(nextGraphics);
            }
        }, delay - 1);
    }

    public void gfx(Graphics nextGraphics) {
        if (nextGraphics == null) {
            if (nextGraphics4 != null)
                nextGraphics4 = null;
            else if (nextGraphics3 != null)
                nextGraphics3 = null;
            else if (nextGraphics2 != null)
                nextGraphics2 = null;
            else
                nextGraphics1 = null;
        } else {
            if (nextGraphics.equals(nextGraphics1) || nextGraphics.equals(nextGraphics2) || nextGraphics.equals(nextGraphics3) || nextGraphics.equals(nextGraphics4))
                return;
            if (nextGraphics1 == null)
                nextGraphics1 = nextGraphics;
            else if (nextGraphics2 == null)
                nextGraphics2 = nextGraphics;
            else if (nextGraphics3 == null)
                nextGraphics3 = nextGraphics;
            else
                nextGraphics4 = nextGraphics;
        }
    }

    public void setNextGraphics(int id, int speed, int height, int rotation) {
        gfx(new Graphics(id, speed, height, rotation));
    }

    public Graphics getNextGraphics1() {
        return nextGraphics1;
    }

    public Graphics getNextGraphics2() {
        return nextGraphics2;
    }

    public Graphics getNextGraphics3() {
        return nextGraphics3;
    }

    public Graphics getNextGraphics4() {
        return nextGraphics4;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean hasFinished() {
        return finished;
    }

    public void setNextWorldTile(WorldTile nextWorldTile) {
        this.nextWorldTile = nextWorldTile;
    }
    public void setPredictedWorldTile(WorldTile predictedWorldTile) {
        this.predictedWorldTile = predictedWorldTile;
    }

    public WorldTile getNextWorldTile() {
        return nextWorldTile;
    }

    public WorldTile getPredictedWorldTile() {
        return predictedWorldTile;
    }

    public boolean hasTeleported() {
        return teleported;
    }

    public WorldTile getLastLoadedMapRegionTile() {
        return lastLoadedMapRegionTile;
    }

    public int getNextWalkDirection() {
        return nextWalkDirection;
    }

    public int getNextRunDirection() {
        return nextRunDirection;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public boolean getRun() {
        return run;
    }

    public WorldTile getNextFaceWorldTile() {
        return nextFaceWorldTile;
    }

    public void setNextFaceWorldTile(WorldTile nextFaceWorldTile) {
        if (nextFaceWorldTile.getX() == getX() && nextFaceWorldTile.getY() == getY())
            return;
        this.nextFaceWorldTile = nextFaceWorldTile;
        if (nextWorldTile != null)
            direction = Utils.getFaceDirection(nextFaceWorldTile.getX() - nextWorldTile.getX(), nextFaceWorldTile.getY() - nextWorldTile.getY());
        else
            direction = Utils.getFaceDirection(nextFaceWorldTile.getX() - getX(), nextFaceWorldTile.getY() - getY());
    }

    public abstract int getSize();

    public void cancelFaceEntityNoCheck() {
        nextFaceEntity = -2;
        lastFaceEntity = -1;
    }

    public void setNextFaceEntity(Entity entity) {
        if (entity == null) {
            nextFaceEntity = -1;
            lastFaceEntity = -1;
        } else {
            nextFaceEntity = entity.getClientIndex();
            lastFaceEntity = nextFaceEntity;
        }
    }

    public int getNextFaceEntity() {
        return nextFaceEntity;
    }

    public int getLastFaceEntity() {
        return lastFaceEntity;
    }

    public long getCastedSpellDelay() {
        return castedSpellDelay;
    }

    public void setCastedSpellDelay(long castedSpellDelay) {
        this.castedSpellDelay = castedSpellDelay;
    }

    public void checkMultiArea() {
        multiArea = forceMultiArea || World.isMultiArea(this);
    }

    public boolean isAtMultiArea() {
        return multiArea;
    }

    public void setAtMultiArea(boolean multiArea) {
        this.multiArea = multiArea;
    }

    public boolean isAtDynamicRegion() {
        return isAtDynamicRegion;
    }

    public ForceMovement getNextForceMovement() {
        return nextForceMovement;
    }

    public void setNextForceMovement(ForceMovement nextForceMovement) {
        this.nextForceMovement = nextForceMovement;
    }

    public Poison getPoison() {
        return poison;
    }

    public NewPoison getNewPoison() {
        return newPoison;
    }


    public ForceTalk getNextForceTalk() {
        return nextForceTalk;
    }

    public void setNextForceTalk(ForceTalk nextForceTalk) {
        this.nextForceTalk = nextForceTalk;
    }

    public void faceEntity(Entity target) {
        setNextFaceWorldTile(new WorldTile(target.getCoordFaceX(target.getSize()), target.getCoordFaceY(target.getSize()), target.getPlane()));
    }

    public void faceWorldTile(int x, int y, int h) {
        setNextFaceWorldTile(new WorldTile(x, y, h));
    }

    public void faceWorldTile(NPC npc, String direction) {
        switch (direction) {
            case "south":
                setNextFaceWorldTile(new WorldTile(npc.getX(), npc.getY() - 1, npc.getPlane()));
                break;
            case "west":
                setNextFaceWorldTile(new WorldTile(npc.getX() - 1, npc.getY(), npc.getPlane()));
                break;
            case "north":
                setNextFaceWorldTile(new WorldTile(npc.getX(), npc.getY() + 1, npc.getPlane()));
                break;
            case "east":
                setNextFaceWorldTile(new WorldTile(npc.getX() + 1, npc.getY(), npc.getPlane()));
                break;
        }
    }

    public void faceObject(WorldObject object) {
        ObjectDefinitions objectDef = object.getDefinitions();
        setNextFaceWorldTile(new WorldTile(object.getCoordFaceX(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation()), object.getCoordFaceY(objectDef.getSizeX(), objectDef.getSizeY(), object.getRotation()), object.getPlane()));
    }

    public long getLastAnimationEnd() {
        return lastAnimationEnd;
    }

    public Map<Object, Object> temporaryAttribute() {
        return temporaryAttributes;
    }

    public Map<Object, Object> getTemporaryAttributtes() {
        return temporaryAttributes;
    }

    public Map<Object, Object> temporaryAttributes() {
        return temporaryAttributes;
    }
    public boolean isForceMultiArea() {
        return forceMultiArea;
    }

    public void setForceMultiArea(boolean forceMultiArea) {
        this.forceMultiArea = forceMultiArea;
        checkMultiArea();
    }

    public WorldTile getLastWorldTile() {
        return lastWorldTile;
    }
    public WorldTile getPreviousTile() {
        return previousTile;
    }

    public ArrayList<Hit> getNextHits() {
        return nextHits;
    }

    public void playLocalSound(int soundId, int type) {
        if (this instanceof Player player) {
            player.getPackets().sendSound(soundId, 0, type);
        }
    }

    public void playSound(int soundId, int delay, int type) {
        for (int regionId : getMapRegionsIds()) {
            List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
            if (playerIndexes != null) {
                for (int playerIndex : playerIndexes) {
                    Player player = World.getPlayers().get(playerIndex);
                    if (player == null || !player.isActive() || !withinDistance(player))
                        continue;

                    int distance = Utils.getDistance(player, this);
                    int maxDistance = 16;
                    double factor = 1.0 - (Math.min(distance, maxDistance) / (double) maxDistance);
                    int volume = (int)(255 * factor * factor);

                    player.getPackets().sendSoundWithVolume(soundId, delay, type, volume);
                }
            }
        }
    }


    public void playSound(int soundId, int type) {
        playSound(soundId, 0, type);
    }


    public void playSound(String sound, int type) {
        int soundId = Rscm.lookup(sound);
        playSound(soundId, 0, type);
    }

    public void playSound(String sound, int delay, int type) {
        int soundId = Rscm.lookup(sound);
        playSound(soundId, delay, type);
    }

    public UpdateMask getUpdatedMask() {
        return updateMask;
    }

    public void glow(UpdateMask newMask) {
        if (updateMask != null)
            updateMask = null;
        this.updateMask = newMask;
        if (this instanceof Player)
            ((Player) this).getAppearence().generateAppearenceData();
    }

    public void glow(int duration, int[] colors) {
        if (updateMask != null)
            updateMask = null;
        this.updateMask = new UpdateMask(duration, colors);
        if (this instanceof Player)
            ((Player) this).getAppearence().generateAppearenceData();
    }

    public long getFindTargetDelay() {
        return findTargetDelay;
    }

    public void setFindTargetDelay(long findTargetDelay) {
        this.findTargetDelay = findTargetDelay;
    }

    public abstract double getProtectionPrayerEffectiveness();
}
