package com.rs.java.game.npc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.rs.Settings;
import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.core.cache.defintions.NPCDefinitions;
import com.rs.core.thread.CoresManager;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.SecondaryBar;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.ground.GroundItems;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NPCCombat;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.player.TickManager;
import com.rs.json.JsonNpcCombatDefinitions;
import com.rs.kotlin.Rscm;
import com.rs.kotlin.game.npc.combatdata.*;
import com.rs.kotlin.game.npc.drops.DropSource;
import com.rs.kotlin.game.npc.drops.DropTable;
import com.rs.kotlin.game.npc.drops.DropTableRegistry;
import com.rs.kotlin.game.npc.drops.Drop;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.RouteEvent;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.skills.herblore.HerbCleaning;
import com.rs.java.game.player.actions.skills.prayer.Burying;
import com.rs.java.game.player.actions.skills.slayer.SlayerManager;
import com.rs.java.game.player.content.friendschat.FriendChatsManager;
import com.rs.java.game.player.content.tasksystem.TaskManager.Tasks;
import com.rs.java.game.player.controllers.DungeonController;
import com.rs.java.game.player.controllers.WildernessController;
import com.rs.java.game.route.RouteFinder;
import com.rs.java.game.route.strategy.FixedTileStrategy;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.*;
import com.rs.kotlin.game.npc.worldboss.WorldBossNPC;
import com.rs.kotlin.tool.WikiApi;

import static java.lang.Integer.max;

/**
 * @Improved Andreas - AvalonPK
 */

public class NPC extends Entity implements Serializable {

    private static final long serialVersionUID = -4794678936277614443L;

    public static int NORMAL_WALK = 0x2, WATER_WALK = 0x4, FLY_WALK = 0x8;

    private int id;
    private NpcCombatDefinition definition;
    private WorldTile respawnTile;
    private int mapAreaNameHash;
    private boolean canBeAttackFromOutOfArea;
    private boolean randomwalk;
    private int[] bonuses;
    private transient Player owner;
    private boolean spawned;
    private transient NPCCombat combat;
    public WorldTile forceWalk;
    private int walkType;
    private transient RouteEvent routeEvent;

    private transient double dropRateFactor;

    private long lastAttackedByTarget;
    private boolean cantInteract;
    private int capDamage;
    private int lureDelay;
    private boolean cantFollowUnderCombat;
    private boolean forceAgressive;
    private int forceTargetDistance;
    private int forceAgressiveDistance;
    private boolean forceFollowClose;
    private boolean forceMultiAttacked;
    private boolean noDistanceCheck;
    private boolean intelligentRouteFinder;

    // npc masks
    private transient SecondaryBar nextSecondaryBar;
    private transient Transformation nextTransformation;
    // name changing masks
    private String name;
    private transient boolean changedName;
    private int combatLevel;
    private transient boolean changedCombatLevel;
    private transient boolean locked;

    private CombatData combatData;


    public CombatData getCombatData() {
        return combatData;
    }

    public static int[] getNpcs(String... names) {
        return Arrays.stream(names)
                .map(name -> name.startsWith("npc.") ? name : "npc." + name).mapToInt(Rscm::lookup).toArray();
    }

    public static int getNpc(String name) {
        String key = name.startsWith("npc.") ? name : "npc." + name;
        return Rscm.lookup(key);
    }

    public static boolean isNpc(int id, String... names) {
        for (String name : names) {
            if (id == getNpc(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNpc(int id, String name) {
        String key = name.startsWith("npc.") ? name : "npc." + name;
        return id == Rscm.lookup(key);
    }

    public boolean isNpc(String name) {
        String key = name.startsWith("npc.") ? name : "npc." + name;
        return id == Rscm.lookup(key);
    }

    public NPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
        this(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
    }

    public NPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
        super(tile);
        this.id = id;
        this.respawnTile = new WorldTile(tile);
        this.mapAreaNameHash = mapAreaNameHash;
        this.canBeAttackFromOutOfArea = canBeAttackFromOutOfArea;
        this.spawned = spawned;
        combatLevel = -1;
        setDirection(getRespawnDirection());
        setRandomWalk(getDefinitions().walkMask);
        setBonuses();
        setHitpoints(getMaxHitpoints());
        combat = new NPCCombat(this);
        capDamage = -1;
        lureDelay = 12000;
        initEntity();
        World.addNPC(this);
        World.updateEntityRegion(this);
        loadMapRegions();
        loadNPCSettings();
        checkMultiArea();
        if (!WikiApi.hasData(id) && combatLevel > 0) {
            WikiApi.dumpData(id, name, combatLevel);
        }
    }

    public NPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned,
               Player owner) {
        super(tile);
        this.id = id;
        this.respawnTile = new WorldTile(tile);
        this.mapAreaNameHash = mapAreaNameHash;
        this.canBeAttackFromOutOfArea = canBeAttackFromOutOfArea;
        this.spawned = spawned;
        this.setOwner(owner);
        combatLevel = -1;
        setDirection(getRespawnDirection());
        setRandomWalk(getDefinitions().walkMask);
        setBonuses();
        setHitpoints(getMaxHitpoints());
        combat = new NPCCombat(this);
        capDamage = -1;
        lureDelay = 12000;
        initEntity();
        World.addNPC(this);
        World.updateEntityRegion(this);
        loadMapRegions();
        loadNPCSettings();
        checkMultiArea();
        if (!WikiApi.hasData(id) && combatLevel > 0) {
            WikiApi.dumpData(id, name, combatLevel);
        }
    }

    public double increasedDropRate(Player player) {
        double dropRate = 1.0;
        return dropRate;
    }

    public void setBonuses() {
        if (combatLevel != 0) {
            combatData = CombatDataParser.getData(id);
            if (combatData != null) {
                return;
            }
        }
        // If no predefined data, generate standard CombatData
        int level = getCombatLevel();

        // Clamp level to reasonable max
        if (level > 750) level = 750;
        if (id == 7891 || id == 4474) {
            combatData = new CombatData(
                    level,
                    255,
                    255,
                    0,
                    255,
                    255,
                    10000,
                    255, 255, 255, 255, 255, 255,
                    new MeleeDefence(0, 0, 0),
                    new MagicDefence(0),
                    new RangedDefence(0, 0, 0),
                    new Immunities(false, false, false, false, false),
                    new MaxHit(0, 0, 0, 0), true, List.of("Crush"), 4, 0, level
            );
            return;
        }

        int defenceLevel = level / 3;
        int meleeAttack = level - (level / 3);
        int magicAttack = level / 3;
        int rangedAttack = level / 3;
        int constitution = level / 2 + 1;
        int maxHit = (int) Math.ceil(level * 0.05);

        combatData = new CombatData(
                level,
                meleeAttack,
                meleeAttack,
                defenceLevel,
                magicAttack,
                rangedAttack,
                constitution,
                0, 0, (int) Math.ceil(magicAttack * 0.75), 0, 0, 0,
                new MeleeDefence(defenceLevel * 2, defenceLevel * 2, defenceLevel * 2),
                new MagicDefence((int) (defenceLevel * 1.5)),
                new RangedDefence(defenceLevel * 2, defenceLevel * 2, defenceLevel * 2),
                new Immunities(false, false, false, false, false),
                new MaxHit(maxHit, maxHit, maxHit, maxHit),
                true,
                List.of("Crush"),
                4,
                25,
                level
        );
    }

    @Override
    public boolean needMasksUpdate() {
        return super.needMasksUpdate() || nextSecondaryBar != null || nextTransformation != null || changedCombatLevel
                || changedName;
    }

    public void transformIntoNPC(int id) {
        setNPC(id);
        nextTransformation = new Transformation(id);
    }

    public void setNPC(int id) {
        this.id = id;
    }

    @Override
    public void resetMasks() {
        super.resetMasks();
        nextTransformation = null;
        changedCombatLevel = false;
        changedName = false;
        nextSecondaryBar = null;
    }

    public int getMapAreaNameHash() {
        return mapAreaNameHash;
    }

    public void setCanBeAttackFromOutOfArea(boolean b) {
        canBeAttackFromOutOfArea = b;
    }

    public boolean canBeAttackFromOutOfArea() {
        return canBeAttackFromOutOfArea;
    }

    public NPCDefinitions getDefinitions() {
        return NPCDefinitions.getNPCDefinitions(id);
    }

    public NpcCombatDefinition getCombatDefinitions() {
        if (definition != null)
            return definition;
        NpcCombatDefinition def = JsonNpcCombatDefinitions.INSTANCE.getById(getId());

        if (def == null) {
            String n = getName();
            if (n == null) {
                try {
                    n = getDefinitions() != null ? getDefinitions().name : null;
                } catch (Throwable ignored) { /* keep null */ }
            }
            def = JsonNpcCombatDefinitions.INSTANCE.getByName(n);
        }

        if (def == null) {
            //Logger.log("CombatDef", "Missing combat def for id=" + getId() + " name=" + getName());
            def = new NpcCombatDefinition(
                    Collections.singletonList(getId()),
                    nListSafe(getName()),
                    -1, -1, -1, -1,  // attackAnim, attackSound, defenceAnim, defendSound
                    -1, 0, -1,       // deathAnim, deathDelay, deathSound
                    5,               // respawnDelay (ticks)
                    10,              // hitpoints
                    1,               // maxHit
                    AttackStyle.MELEE,
                    AttackMethod.MELEE,
                    java.util.Collections.emptyMap(),
                    -1, -1, -1,      // attackRange, attackGfx, attackProjectile
                    AggressivenessType.PASSIVE,
                    -1, -1, -1       // aggroDistance, deAggroDistance, maxDistFromSpawn
            );
        }
        if (definition == null)
            definition = def;
        return def;
    }

    private static List<String> nListSafe(String name) {
        return name == null ? java.util.Collections.emptyList() : java.util.Collections.singletonList(name);
    }


    @Override
    public int getMaxHitpoints() {
        if (getIncreasedMaxHitpoints() > 0)
            return getIncreasedMaxHitpoints();
        if (id == Rscm.lookup("npc.magic_dummy") || id == Rscm.lookup("npc.melee_dummy"))
            return 10000;
        if (definition != null && definition.getHitpoints() > 0) {
            return definition.getHitpoints();
        }
        int combatDefinitionHp = getCombatDefinitions().getHitpoints();
        if (combatDefinitionHp > 0)
            return combatDefinitionHp;
        if (combatData == null)
            setBonuses();
        return combatData.constitutionLevel * 10;
    }

    public int getId() {
        return id;
    }

    public void loadNPCSettings() {
        if (getId() == 2693 || getId() == 46)
            setRandomWalk(FLY_WALK);
        if (getId() == 231 || getId() == 705 || getId() == 1861 || getId() == 4707 || getId() == 5195
                || getId() == 4247) {
            setRandomWalk(0);
        }
        if (getId() == 1282) {
            setName("Sell Items");
        }
        if (getId() == 45) {
            setName("Bank");
            setRandomWalk(0);
            faceWorldTile(this, "west");
        }

        if (getId() == 5195) {
            setName("Teleport Wizard");
            setRandomWalk(0);
            faceWorldTile(this, "west");
        }
        if (getId() == 2593) {
            setName("Grand Exchange");
            setRandomWalk(0);
            faceWorldTile(this, "south");
        }
        if (getId() == 650) {
            setName("Combat Shops");
            setRandomWalk(0);
            faceWorldTile(this, "east");
        }
        if (getId() == 2676) {
            setName("Customise character");
            faceWorldTile(this, "west");
            setRandomWalk(0);
        }
        if (getId() == 1282) {
            setRandomWalk(0);
        }
        if (getId() == 231) {
            setName("Skilling Shops");
            setRandomWalk(0);
        }
        if (getId() == 960) {
            setRandomWalk(0);
        }
        if (getId() == 7891) {
            setName("Dummy");
            setRandomWalk(0);
        }
        if (getId() == 960) {
            setName("Healer");
            setRandomWalk(0);
            faceWorldTile(this, "south");
        }
        if (getId() == 4474) {
            setName("Max Hit Dummy");
            setRandomWalk(1);
        }
    }

    public void loadNPCFaceTile() {
        if (getId() == 2241)
            faceWorldTile(this, "west");
        if (getId() == 960)
            faceWorldTile(this, "south");
        if (getId() == 3785)
            faceWorldTile(this, "north");
    }

    public void setRouteEvent(RouteEvent routeEvent) {
        this.routeEvent = routeEvent;
    }

    public void processNPC() {
        if (isDead() || locked)
            return;
        if (routeEvent != null && routeEvent.processEvent(this))
            routeEvent = null;
        loadNPCSettings();
        if (!combat.process()) {
            if (!isForceWalking()) {
                if (!cantInteract) {
                    if (!checkAgressivity()) {
                        if (!isFrozen()) {
                            if (!hasWalkSteps() && (walkType & NORMAL_WALK) != 0) {
                                boolean can = false;
                                for (int i = 0; i < 2; i++) {
                                    if (Math.random() * 1000.0 < 100.0) {
                                        can = true;
                                        break;
                                    }
                                }
                                if (can) {
                                    int moveX = (int) Math.round(Math.random() * 10.0 - 5.0);
                                    int moveY = (int) Math.round(Math.random() * 10.0 - 5.0);
                                    resetWalkSteps();
                                    if (getMapAreaNameHash() != -1) {
                                        if (!MapAreas.isAtArea(getMapAreaNameHash(), this)) {
                                            forceWalkRespawnTile();
                                            return;
                                        }
                                        addWalkSteps(getX() + moveX, getY() + moveY,
                                                (NPCDefinitions.getNPCDefinitions(id).hasOption("Trade") ? 2 : 5),
                                                (walkType & FLY_WALK) == 0);
                                    } else
                                        addWalkSteps(respawnTile.getX() + moveX, respawnTile.getY() + moveY,
                                                getRandomWalkDistance() > 5 ? getRandomWalkDistance()
                                                        : (NPCDefinitions.getNPCDefinitions(id).hasOption("Trade") ? 2
                                                        : 5),
                                                (walkType & FLY_WALK) == 0);
                                }

                            }
                        }
                    }
                }
            }
        }
        if (isForceWalking()) {
            if (!isFrozen()) {
                if (getX() != forceWalk.getX() || getY() != forceWalk.getY()) {
                    if (!hasWalkSteps()) {
                        int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, getX(), getY(), getPlane(),
                                getSize(), new FixedTileStrategy(forceWalk.getX(), forceWalk.getY()), true);
                        int[] bufferX = RouteFinder.getLastPathBufferX();
                        int[] bufferY = RouteFinder.getLastPathBufferY();
                        for (int i = steps - 1; i >= 0; i--) {
                            if (!addWalkSteps(bufferX[i], bufferY[i], 25, true))
                                break;
                        }
                    }
                    if (!hasWalkSteps()) {
                        setNextWorldTile(new WorldTile(forceWalk));
                        forceWalk = null;
                    }
                } else
                    forceWalk = null;
            }
        }
    }

    @Override
    public void processEntity() {
        super.processEntity();
        // Decrease timers
        //super.cycle();
        if (getOwner() != null && getOwner().hasFinished()) {
            getOwner().setMarker(false);
            setOwner(null);
            finish();
        }
        if (gameTick % 96 == 0)
            combatData.regenerate(1);
        processNPC();
    }

    public int getRespawnDirection() {
        NPCDefinitions definitions = getDefinitions();
        if (definitions.anInt853 << 32 != 0 && definitions.respawnDirection > 0 && definitions.respawnDirection <= 8)
            return (4 + definitions.respawnDirection) << 11;
        return 0;
    }

    @Override
    public void handleHit(final Hit hit) {
        if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE
                && hit.getLook() != HitLook.MAGIC_DAMAGE)
            return;
        Entity source = hit.getSource();
        if (source == null)
            return;
    }

    @Override
    public int getHitDelay(Entity attacker, Entity defender) {
        int distance = Utils.getDistance(attacker, defender);
        return max(1, 1 + (1 + distance) / 3);
    }

    @Override
    public void handleIncommingHit(Hit hit) {
        if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE
                && hit.getLook() != HitLook.MAGIC_DAMAGE)
            return;
        Entity source = hit.getSource();
        setAttackedByDelay(4800);
        if (source == null)
            return;
        setAttackedBy(source);
    }

    @Override
    public void reset() {
        super.reset();
        setDirection(getRespawnDirection());
        combat.reset();
        setBonuses();
        setOwner(null);
        forceWalk = null;
    }

    @Override
    public void finish() {
        if (hasFinished())
            return;
        setFinished(true);
        setOwner(null);
        setNextFaceEntity(null);
        World.updateEntityRegion(this);
        World.removeNPC(this);
    }

    public void setRespawnTask() {
        if (!hasFinished()) {
            reset();
            setLocation(respawnTile);
            finish();
        }
        CoresManager.getSlowExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    spawn();
                } catch (Throwable e) {
                    Logger.handle(e);
                }
            }
        }, getCombatDefinitions().getRespawnDelay() * 600, TimeUnit.MILLISECONDS);
    }

    public void deserialize() {
        if (combat == null)
            combat = new NPCCombat(this);
        spawn();
    }

    public void spawn() {
        temporaryAttribute().clear();
        tickManager.reset();
        combat.reset();
        removeTarget();

        setFinished(false);
        World.addNPC(this);
        setLastRegionId(0);
        World.updateEntityRegion(this);
        loadMapRegions();
        checkMultiArea();
        loadNPCSettings();
    }

    public NPCCombat getCombat() {
        return combat;
    }


    @Override
    public void sendDeath(final Entity source) {
        final NpcCombatDefinition defs = getCombatDefinitions();
        resetWalkSteps();
        combat.removeTarget();
        source.getTickManager().remove(TickManager.TickKeys.PJ_TIMER);
        source.getTickManager().remove(TickManager.TickKeys.LAST_ATTACKED_TICK);
        animate(defs.getDeathAnim());
        playSound(defs.getDeathSound(), 1);
        WorldTasksManager.schedule(new WorldTask() {
            int loop;

            @Override
            public void run() {
                if (loop >= defs.getDeathDelay() - 4) {
                    drop();
                    reset();
                    setLocation(respawnTile);
                    finish();
                    if (!isSpawned())
                        setRespawnTask();
                    stop();
                }
                loop++;
            }
        }, 0, 1);
    }

    private void addAvalonPoints(Player killer, NPC npc, boolean wildy) {
        double points = wildy ? (getCombatLevel() / 2) * (WildernessController.getWildLevel(killer) / 2)
                : npc.getCombatLevel() * 4;
        double bonusPoints = wildy ? Math.round((points * killer.getBonusPoints()) - points)
                : (points * killer.getBonusPoints()) - points;
        double totalPoints = points + bonusPoints;
        killer.setAvalonPoints(killer.getAvalonPoints() + (int) (totalPoints + bonusPoints));
        killer.message("You gain " + (int) totalPoints
                + (bonusPoints > 1 ? " (" + (int) bonusPoints + " bonus points) " : " ") + Settings.SERVER_NAME
                + " points for killing " + getName() + (wildy ? " in the wilderness." : " boss."));
    }

    public boolean isCantSetTargetAutoRelatio() {
        return isCantSetTargetAutoRelatio();
    }

    public void setCantSetTargetAutoRelatio(boolean cantSetTargetAutoRelatio) {
        this.forceAgressive = cantSetTargetAutoRelatio;
    }

    public double getDropRateMultiplier() {
        double mult = 1.0;

        if (Settings.DROP_MULTIPLIER > 1.0)
            mult *= Settings.DROP_MULTIPLIER;

        return mult;
    }

    public List<Drop> rollDrops(Player player) {
        String key = DropTableRegistry.npcKeyFromId(this.id);
        //System.out.println("[rollDrops] NPC ID " + this.id + " key = " + key);

        DropTable table = DropTableRegistry.getDropTableForNpc(this.id);
        if (table != null) {
            if (Settings.DEBUG_DROP_MATH) {
                table.writeRatesToFile(getDropRateMultiplier());
            }
            return table.rollDrops(player, getDropRateMultiplier());
        }

        player.message("Missing droptable for npc: " + this.getName() + "(" + this.getId() + ")");
        System.out.println("[rollDrops] No drop table found for NPC " + this.getName() + "(" + this.getId() + ")");
        return Collections.emptyList();
    }

    public void drop() {
        Player killer = getMostDamageReceivedSourcePlayer();
        if (killer == null) {
            return;
        }
        SlayerManager manager = killer.getSlayerManager();
        if (manager.isValidTask(getName()))
            manager.checkCompletedTask(getDamageReceived(killer), 0);
        killer.getKillcount().increment(getId());
        if (getId() == 1615) {
            killer.getTaskManager().checkComplete(Tasks.KILL_ABYSSAL_DEMON);
        }
        if (killer.isAtWild() && killer.getControlerManager().getControler() instanceof WildernessController)
            addAvalonPoints(killer, this, true);
        List<Drop> drops = rollDrops(killer);
        if (drops.isEmpty()) {
            return;
        }

        for (Drop drop : drops) {
            sendDrop(killer, drop);
            if (drop.extraDrop != null) {
                sendDrop(killer, drop.extraDrop);
            }
        }
    }

    public void handleBonecrusher(Player player, Drop drop, Item item, boolean lootShare) {
        CopyOnWriteArrayList<Player> playersWithLs = new CopyOnWriteArrayList<Player>();
        String dropName = ItemDefinitions.getItemDefinitions(drop.itemId).getName().toLowerCase();
        if (lootShare) {
            player.message(String.format(("<col=216902>Your bonecrusher crushed: %s x %s. </col>"),
                    Utils.getFormattedNumber(item.getAmount(), ','), dropName));
            for (Player p : playersWithLs) {
                if (!p.equals(player)) {
                    p.message(String.format("%s bonecrusher crushed: %s x %s.", player.getDisplayName(),
                            Utils.getFormattedNumber(item.getAmount(), ','), dropName));
                }
            }
        }
        for (int i = 0; i < item.getAmount(); i++) {
            Burying.handleNecklaces(player, item.getId());
            player.getSkills().addXp(Skills.PRAYER, Burying.Bone.forId(item.getId()).getExperience());
        }
        item.setAmount(0);
    }

    public void handleHerbicide(Player player, Drop drop, Item item, boolean lootshare) {
        CopyOnWriteArrayList<Player> playersWithLs = new CopyOnWriteArrayList<Player>();
        String dropName = ItemDefinitions.getItemDefinitions(drop.itemId).getName();
        if (lootshare) {
            player.message(String.format(("<col=216902>Your herbicide burnt: %s x %s. </col>"),
                    Utils.getFormattedNumber(item.getAmount(), ','), dropName.toLowerCase()));
            for (Player p : playersWithLs) {
                if (!p.equals(player)) {
                    p.message(String.format("%s herbicide burnt: %s x %s.", player.getDisplayName(),
                            Utils.getFormattedNumber(item.getAmount(), ','), dropName.toLowerCase()));
                }
            }
        }
        for (int i = 0; i < item.getAmount(); i++) {
            player.getSkills().addXp(Skills.HERBLORE, HerbCleaning.getHerb(drop.itemId).getExperience() * 2);
        }
        item.setAmount(0);
    }

    private transient boolean sendDp = false;

    public void sendLootshare(Player player, Item item, Drop drop) {
        int size = getSize();
        String dropName = ItemDefinitions.getItemDefinitions(item.getId()).getName();
        /* LootShare/CoinShare */
        FriendChatsManager fc = player.getCurrentFriendChat();
        if (fc != null) {
            CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<Player>();
            for (String fcPlayers : fc.getPlayers()) {
                if (fcPlayers == null)
                    continue;
                players.add(World.getPlayerByDisplayName(fcPlayers));
            }
            CopyOnWriteArrayList<Player> playersWithLs = new CopyOnWriteArrayList<Player>();
            for (Player p : players) {
                if (p.isToogleLootShare() && p.getRegionId() == player.getRegionId())
                    playersWithLs.add(p);
            }
            Player luckyPlayer = playersWithLs.get((int) (Math.random() * playersWithLs.size()));
            if (item.getAmount() > 0) {
                GroundItems.updateGroundItem(item, new WorldTile(getCoordFaceX(size), getCoordFaceY(size), getPlane()),
                        luckyPlayer);
                luckyPlayer.message(String.format(
                        (luckyPlayer.getRareItem() == item ? "<col=ff0000>" : "<col=216902>") + "You received: %s x %s. ("
                                + getName() + ") </col>",
                        Utils.getFormattedNumber(item.getAmount(), ','), dropName));
                for (Player p : playersWithLs) {
                    if (!p.equals(luckyPlayer)) {
                        p.message(String.format("%s</col> received: %s x %s. (" + getName() + ") ",
                                HexColours.Colour.RED.getHex() + luckyPlayer.getDisplayName(),
                                Utils.getFormattedNumber(item.getAmount(), ','), dropName));
                    }
                }
                if (item.getDefinitions().getTipitPrice() > luckyPlayer.getDropLogs().getLowestValue()) {
                    if (!luckyPlayer.getDropLogs().toggledMessage()) {
                        luckyPlayer.message(item.getName() + " added to your droplog.");
                    }
                    if (!luckyPlayer.getDropLogs().getLowestValueMessage()) {
                        luckyPlayer.getDropLogs().setLowestValueMessage(true);
                        luckyPlayer.message("If you want to change value of droplogs enter ::droplogvalue price");
                        luckyPlayer.message("You can also hide droplog messages with ::toggledroplogmessage");
                    }
                    luckyPlayer.getDropLogs().addDrop(item);
                }
                if (luckyPlayer.getValueableDrop() < 1)
                    luckyPlayer.setValueableDrop(15000);
                if (!item.getDefinitions().isTradeable() && !item.getName().toLowerCase().contains(" charm")) {
                    luckyPlayer.message("<col=ff0000>Untradeable drop: " + item.getName() + ".");
                }
                if ((item.getDefinitions().getTipitPrice() * item.getAmount()) >= Integer
                        .parseInt(luckyPlayer.getToggleValue(luckyPlayer.toggles.get("DROPVALUE")))) {
                    player.message("<col=ff0000>Valuable drop: " + item.getName()
                            + (item.getAmount() > 1 ? " x " + item.getAmount() + " " : " ") + "("
                            + Utils.getFormattedNumber(item.getDefinitions().getTipitPrice() * item.getAmount(), ',')
                            + " coins.)");
                    sendLootBeam(item, luckyPlayer, this);
                } else if (drop.getSource() == DropSource.PREROLL || drop.getSource() == DropSource.TERTIARY) {
                    sendLootBeam(item, luckyPlayer, this);
                } else if (EconomyPrices.getPrice(item.getId()) >= 1_000_000) {
                    sendLootBeam(item, luckyPlayer, this);
                }
                if (EconomyPrices.getPrice(item.getId()) > 1_000_000) {
                    World.sendWorldMessage(
                            "<img=7><col=36648b>News: " + luckyPlayer.getDisplayName() + " has recieved "
                                    + (item.getAmount() > 1 ? item.getAmount() + " x " + Utils.formatString(dropName)
                                    : Utils.formatString(dropName))
                                    + " as a loot from " + getName() + "!",
                            false);
                }
            }
        }
        sendDp = false;
    }

    public void sendDrop(Player player, Drop drop) {
        int size = getSize();
        sendDp = true;
        String dropName = ItemDefinitions.getItemDefinitions(drop.itemId).getName().toLowerCase();
        Item item = ItemDefinitions.getItemDefinitions(drop.itemId).isStackable()
                ? new Item(drop.itemId,
                (drop.amount * Settings.DROP_RATE))
                : new Item(drop.itemId, drop.amount);
        if (Settings.DOUBLE_DROP && (drop.isAlways() || item.getDefinitions().isStackable()
                || item.getDefinitions().isNoted() || player.getRareItem() == item))
            item.setAmount(item.getAmount() * 2);
        ItemDefinitions defs = ItemDefinitions.getItemDefinitions(drop.itemId);
        /* LootShare/CoinShare */
        if (player.isToogleLootShare()) {
            sendLootshare(player, item, drop);
        } else if (!player.isToogleLootShare() || sendDp) {
            if (item.getDefinitions().getTipitPrice() > player.getDropLogs().getLowestValue()) {
                if (!player.getDropLogs().toggledMessage()) {
                    player.message(item.getName() + " added to your droplog.");
                }
                if (!player.getDropLogs().getLowestValueMessage()) {
                    player.getDropLogs().setLowestValueMessage(true);
                    player.message("If you want to change value of droplogs enter ::droplogvalue price");
                    player.message("You can also hide droplog messages with ::toggledroplogmessage");
                }
                player.getDropLogs().addDrop(item);
            }
            Item i = item;
            if (item.getDefinitions().isNoted())
                i = new Item(item.getDefinitions().getCertId(), item.getAmount());
            if (player.toggles("UNTRADEABLEMESSAGE", false) && !item.getDefinitions().isTradeable()
                    && !item.getName().toLowerCase().contains(" charm")) {
                player.message("<col=ff0000>Untradeable drop: " + item.getName()
                        + (item.getAmount() > 1 ? " x " + item.getAmount() + " " : " "));
            }
            if (player.getInventory().containsItem(19675, 1) && defs.getName().toLowerCase().contains("grimy")
                    && !defs.isNoted()) {
                if (player.getSkills().getLevel(Skills.HERBLORE) >= HerbCleaning.getHerb(drop.itemId).getLevel()) {
                    handleHerbicide(player, drop, item, false);
                    return;
                }
            }
            if ((defs.getName().toLowerCase().equalsIgnoreCase("bones")
                    || defs.getName().toLowerCase().contains(" bones")) && player.getInventory().containsItem(18337, 1)
                    && !defs.isNoted()) {
                handleBonecrusher(player, drop, item, false);
                return;
            }
            if (player.inPkingArea() && item.getName().toLowerCase().contains("dragon bones")) {
                item.setId(item.getDefinitions().getCertId());// bones into noted
            }
            if (item.getName().toLowerCase().contains("grimy")) {
                item.setAmount(item.getAmount() * 3);
                if (!item.getDefinitions().isNoted())
                    item.setId(item.getDefinitions().getCertId());
            }
            if (item.getAmount() > 0) {
                GroundItems.updateGroundItem(item, new WorldTile(getCoordFaceX(size), getCoordFaceY(size), getPlane()),
                        player);
                if ((i.getDefinitions().getTipitPrice() * i.getAmount()) >= Integer
                        .parseInt(player.getToggleValue(player.toggles.get("DROPVALUE")))) {
                    player.message("<col=ff0000>Valuable drop: " + i.getName()
                            + (i.getAmount() > 1 ? " x " + i.getAmount() + " " : " ") + "("
                            + Utils.getFormattedNumber(i.getDefinitions().getTipitPrice() * i.getAmount(), ',')
                            + " coins.)");
                    sendLootBeam(item, player, this);
                } else if (drop.getSource() == DropSource.PREROLL || drop.getSource() == DropSource.TERTIARY) {
                    sendLootBeam(item, player, this);
                } else if (EconomyPrices.getPrice(item.getId()) >= 1_000_000) {
                    sendLootBeam(item, player, this);
                }
                player.getCollectionLog().addItem(item);
                if (EconomyPrices.getPrice(item.getId()) >= 1000000) {
                    World.sendWorldMessage(
                            "<img=7><col=36648b>News: " + player.getDisplayName() + " has recieved "
                                    + (item.getAmount() > 1 ? item.getAmount() + " x " + Utils.formatString(dropName)
                                    : Utils.formatString(dropName))
                                    + " as a loot from " + getName() + "!",
                            false);
                }
            }
        }
    }

    public void sendLootBeam(Item item, Player player, NPC npc) {
        if (!player.toggles("LOOTBEAMS", false)) {
            return;
        }
        WorldTile tile = new WorldTile(getCoordFaceX(getSize()), getCoordFaceY(getSize()), getPlane());
        player.message("<col=b25200>A loot beam appears on your rare drop.");
        World.sendPrivateGraphics(player, new Graphics(7, 0, 0), tile);
        player.setBeam(tile);
        player.setBeamItem(item);
    }

    public void setNextNPCTransformation(int id) {
        setNPC(id);
        nextTransformation = new Transformation(id);
        if (getCustomCombatLevel() != -1)
            changedCombatLevel = true;
        if (getCustomName() != null)
            changedName = true;
        setBonuses();
    }

    @Override
    public int getSize() {
        switch (id) {
        }
        return getDefinitions().size;
    }

    public int[] getBonuses() {
        return bonuses;
    }

    public WorldTile getRespawnTile() {
        return respawnTile;
    }

    public void setRespawnTile(WorldTile tile) {
        this.respawnTile = tile;
    }

    public boolean isUnderCombat() {
        return combat.underCombat();
    }

    @Override
    public void setAttackedBy(Entity target) {
        super.setAttackedBy(target);
        if (target == combat.getTarget() && !(combat.getTarget() instanceof Familiar))
            lastAttackedByTarget = Utils.currentTimeMillis();
    }

    public boolean canBeAttackedByAutoRelatie() {
        return Utils.currentTimeMillis() - lastAttackedByTarget > lureDelay;
    }

    public boolean isForceWalking() {
        return forceWalk != null;
    }

    public void resetForcewalk() {
        this.forceWalk = null;
    }

    public void setTarget(Entity entity) {
        combat.setTarget(entity);
        lastAttackedByTarget = Utils.currentTimeMillis();
    }

    public void removeTarget() {
        if (combat.getTarget() == null)
            return;
        combat.removeTarget();
    }

    public void forceWalkRespawnTile() {
        setForceWalk(respawnTile);
    }

    public void setForceWalk(WorldTile tile) {
        resetWalkSteps();
        forceWalk = tile;
    }

    public boolean hasForceWalk() {
        return forceWalk != null;
    }

    public ArrayList<Entity> getPossibleTargets(boolean checkNPCs, boolean checkPlayers) {
        int size = getSize();
        ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
        AttackStyle attackStyle = getCombatDefinitions().getAttackStyle();
        for (int regionId : getMapRegionsIds()) {
            if (checkPlayers) {
                List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
                if (playerIndexes != null) {
                    for (int playerIndex : playerIndexes) {
                        Player player = World.getPlayers().get(playerIndex);
                        if (player == null || player.isDead() || player.hasFinished() || !player.isActive()
                                || player.getAppearence().isHidden()
                                || !Utils.isOnRange(getX(), getY(), size, player.getX(), player.getY(),
                                player.getSize(),
                                forceAgressiveDistance != 0 ? forceAgressiveDistance
                                        : isNoDistanceCheck() ? 64
                                        : this instanceof WorldBossNPC ? 16
                                        : player.getControlerManager()
                                        .getControler() instanceof DungeonController
                                        ? 12
                                        : getCombatDefinitions().getAggroDistance())
                                || (!forceMultiAttacked && (!isAtMultiArea() || !player.isAtMultiArea())
                                && (player.getAttackedBy() != this
                                && (player.getTickManager().isActive(TickManager.TickKeys.PJ_TIMER))))
                                || !clipedProjectile(player,
                                (attackStyle != AttackStyle.RANGE
                                        && attackStyle != AttackStyle.MAGIC))
                                || !getDefinitions().hasAttackOption()
                                || (!forceAgressive && !WildernessController.isAtWild(this)
                                && player.getSkills().getCombatLevelWithSummoning() >= getCombatLevel() * 2)) {
                            continue;
                        }
                        possibleTarget.add(player);
                    }
                }
            }
            if (checkNPCs) {
                List<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
                if (npcsIndexes != null) {
                    for (int npcIndex : npcsIndexes) {
                        NPC npc = World.getNPCs().get(npcIndex);
                        if (npc == null || npc == this || npc.isDead() || npc.hasFinished()
                                || !Utils.isOnRange(getX(), getY(), size, npc.getX(), npc.getY(), npc.getSize(),
                                forceAgressiveDistance > 0 ? forceAgressiveDistance : getSize())
                                || !npc.getDefinitions().hasAttackOption()
                                || ((!isAtMultiArea() || !npc.isAtMultiArea()) && npc.getAttackedBy() != this
                                && npc.getAttackedByDelay() > Utils.currentTimeMillis())
                                || !clipedProjectile(npc, false)) {
                            continue;
                        }
                        possibleTarget.add(npc);
                    }
                }
            }
        }
        return possibleTarget;
    }

    public ArrayList<Entity> getPossibleTargets() {
        return getPossibleTargets(false, true);
    }

    public ArrayList<Entity> getPossibleTargetsWithNpcs() {
        return getPossibleTargets(true, true);
    }

    public boolean checkAgressivity() {
        ArrayList<Entity> possibleTarget = getPossibleTargets();
        if (!possibleTarget.isEmpty()) {
            Entity target = possibleTarget.get(Utils.random(possibleTarget.size()));
            if (!forceAgressive) {
                NpcCombatDefinition defs = getCombatDefinitions();
                if (defs.getAggressivenessType() == AggressivenessType.PASSIVE
                        && !WildernessController.isAtWild(target))
                    return false;
            }
            resetWalkSteps();
            setTarget(target);
            return true;
        }
        return false;
    }

    public boolean isCantInteract() {
        return cantInteract;
    }

    public void setCantInteract(boolean cantInteract) {
        this.cantInteract = cantInteract;
        if (cantInteract)
            combat.reset();
    }

    public boolean isWithinMeleeRange(Entity target) {
        int distanceX = target.getX() - getX();
        int distanceY = target.getY() - getY();
        int size = getSize();
        return distanceX <= size && distanceX >= -1 && distanceY <= size && distanceY >= -1;
    }

    public int getCapDamage() {
        return capDamage;
    }

    public void setCapDamage(int capDamage) {
        this.capDamage = capDamage;
    }

    public int getLureDelay() {
        return lureDelay;
    }

    public void setLureDelay(int lureDelay) {
        this.lureDelay = lureDelay;
    }

    public boolean isCantFollowUnderCombat() {
        return cantFollowUnderCombat;
    }

    public void setCantFollowUnderCombat(boolean canFollowUnderCombat) {
        this.cantFollowUnderCombat = canFollowUnderCombat;
    }

    public Transformation getNextTransformation() {
        return nextTransformation;
    }

    @Override
    public String toString() {
        return getDefinitions().name + " - " + id + " - " + getX() + " " + getY() + " " + getPlane();
    }

    public boolean isForceAgressive() {
        return forceAgressive;
    }

    public void setForceAgressive(boolean forceAgressive) {
        this.forceAgressive = forceAgressive;
    }


    public int getForceTargetDistance() {
        return forceTargetDistance;
    }

    public void setForceTargetDistance(int forceTargetDistance) {
        this.forceTargetDistance = forceTargetDistance;
    }

    public int getForceAgressiveDistance() {
        return forceAgressiveDistance;
    }

    public void setForceAgressiveDistance(int forceAgressiveDistance) {
        this.forceAgressiveDistance = forceAgressiveDistance;
    }

    public boolean isForceFollowClose() {
        return forceFollowClose;
    }

    public void setForceFollowClose(boolean forceFollowClose) {
        this.forceFollowClose = forceFollowClose;
    }

    public boolean isForceMultiAttacked() {
        return forceMultiAttacked;
    }

    public void setForceMultiAttacked(boolean forceMultiAttacked) {
        this.forceMultiAttacked = forceMultiAttacked;
    }

    public boolean hasRandomWalk() {
        return randomwalk;
    }

    public void setRandomWalk(int forceRandomWalk) {
        this.walkType = forceRandomWalk;
    }

    private int walkDistance;

    public void setRandomWalkDistance(int distance) {
        this.walkDistance = distance;
    }

    public int getRandomWalkDistance() {
        return walkDistance;
    }

    public String getCustomName() {
        return name;
    }

    public boolean isIntelligentRouteFinder() {
        return intelligentRouteFinder;
    }

    public void setIntelligentRouteFinder(boolean intelligentRouteFinder) {
        this.intelligentRouteFinder = intelligentRouteFinder;
    }

    public void setName(String string) {
        this.name = getDefinitions().name.equals(string) ? null : string;
        changedName = true;
    }

    public int getCustomCombatLevel() {
        return combatLevel;
    }

    public int getCombatLevel() {
        return combatLevel >= 0 ? combatLevel : getDefinitions().combatLevel;
    }

    public String getName() {
        return name != null ? name : getDefinitions().name;
    }

    public void setCombatLevel(int level) {
        combatLevel = getDefinitions().combatLevel == level ? -1 : level;
        changedCombatLevel = true;
    }

    public boolean hasChangedName() {
        return changedName;
    }

    public boolean hasChangedCombatLevel() {
        return changedCombatLevel;
    }

    public WorldTile getMiddleWorldTile() {
        int size = getSize();
        return new WorldTile(getCoordFaceX(size), getCoordFaceY(size), getPlane());
    }

    public boolean isSpawned() {
        return spawned;
    }

    public void setSpawned(boolean spawned) {
        this.spawned = spawned;
    }

    public boolean isNoDistanceCheck() {
        return noDistanceCheck;
    }

    public void setNoDistanceCheck(boolean noDistanceCheck) {
        this.noDistanceCheck = noDistanceCheck;
    }

    public boolean withinDistance(Player tile, int distance) {
        return super.withinDistance(tile, distance);
    }


    public SecondaryBar getNextSecondaryBar() {
        return nextSecondaryBar;
    }

    public void setNextSecondaryBar(SecondaryBar secondaryBar) {
        this.nextSecondaryBar = secondaryBar;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getAttackSpeed() {
        return combatData.attackSpeedTicks;
    }

    public int getMaxHit() {
        if (definition != null && definition.getMaxHit() != 0) {
            return definition.getMaxHit();
        }
        if (combatData == null && getCombatDefinitions().getMaxHit() != 0) {
            return getCombatDefinitions().getMaxHit();
        }
        String style = getAttackStyle().name();
        int max = combatData.maxHit.forStyle(style);
        return max * 10;
    }

    public int getAttackAnimation() {
        return getCombatDefinitions().getAttackAnim();
    }

    public int getProjectileId() {
        return getCombatDefinitions().getAttackProjectile();
    }

    public int getAttackGfx() {
        return getCombatDefinitions().getAttackProjectile();
    }

    public Hit createAndRegisterHit(Entity target, int damage, HitLook look) {
        Hit hit = new Hit(this, damage, look);
        CombatScript.registerHit(this, target, hit);
        return hit;
    }

    public Hit regularHit(Entity target, int damage) {
        return createAndRegisterHit(target, damage, HitLook.REGULAR_DAMAGE);
    }

    public Hit meleeHit(Entity target, int maxHit) {
        return meleeHit(target, maxHit, HitLook.MELEE_DAMAGE);
    }

    public Hit meleeHit(Entity target, int maxHit, HitLook look) {
        int damage = NpcCombatCalculations.getRandomMaxHit(
                this, maxHit, NpcAttackStyle.CRUSH, target);
        return createAndRegisterHit(target, damage, look);
    }

    public Hit meleeHit(Entity target, int maxHit, NpcAttackStyle style) {
        int damage = NpcCombatCalculations.getRandomMaxHit(
                this, maxHit, style, target);
        return createAndRegisterHit(target, damage, HitLook.MELEE_DAMAGE);
    }

    public Hit magicalMelee(Entity target, int maxHit) {
        int damage = NpcCombatCalculations.getRandomMaxHit(
                this, maxHit, NpcAttackStyle.MAGICAL_MELEE, target);
        return createAndRegisterHit(target, damage, HitLook.MELEE_DAMAGE);
    }

    public Hit magicHit(Entity target, int maxHit) {
        HitLook look = HitLook.MAGIC_DAMAGE;
        if (getAttackStyle() == NpcAttackStyle.MAGICAL_MELEE)
            look = HitLook.MELEE_DAMAGE;
        return magicHit(target, maxHit, look);
    }

    public Hit magicHit(Entity target, int maxHit, HitLook look) {
        int damage = NpcCombatCalculations.getRandomMaxHit(
                this, maxHit, NpcAttackStyle.MAGIC, target);
        return createAndRegisterHit(target, damage, look);
    }

    public Hit rangedHit(Entity target, int maxHit) {
        return rangedHit(target, maxHit, HitLook.RANGE_DAMAGE);
    }

    public Hit rangedHit(Entity target, int maxHit, HitLook look) {
        int damage = NpcCombatCalculations.getRandomMaxHit(
                this, maxHit, NpcAttackStyle.RANGED, target);
        return createAndRegisterHit(target, damage, look);
    }

    public NpcAttackStyle getAttackStyle() {
        return NpcAttackStyle.fromList(combatData.attackStyles);
    }


    @Override
    public double getProtectionPrayerEffectiveness() {
        return 0.0;
    }
}
