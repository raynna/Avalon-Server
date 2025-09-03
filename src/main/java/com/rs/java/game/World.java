package com.rs.java.game;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.rs.Launcher;
import com.rs.Settings;
import com.rs.core.thread.CoresManager;
import com.rs.java.game.area.Area;
import com.rs.java.game.area.AreaManager;
import com.rs.java.game.item.FloorItem;
import com.rs.java.game.item.Item;
import com.rs.java.game.map.MapUtils;
import com.rs.java.game.map.MapUtils.Structure;
import com.rs.java.game.minigames.clanwars.FfaZone;
import com.rs.java.game.minigames.clanwars.RequestController;
import com.rs.java.game.minigames.godwars.armadyl.GodwarsArmadylFaction;
import com.rs.java.game.minigames.godwars.armadyl.KreeArra;
import com.rs.java.game.minigames.godwars.bandos.GeneralGraardor;
import com.rs.java.game.minigames.godwars.bandos.GodwarsBandosFaction;
import com.rs.java.game.minigames.godwars.saradomin.CommanderZilyana;
import com.rs.java.game.minigames.godwars.saradomin.GodwarsSaradominFaction;
import com.rs.java.game.minigames.godwars.zamorak.GodwarsZammorakFaction;
import com.rs.java.game.minigames.godwars.zamorak.KrilTstsaroth;
import com.rs.java.game.minigames.godwars.zaros.Nex;
import com.rs.java.game.minigames.godwars.zaros.NexMinion;
import com.rs.java.game.minigames.godwars.zaros.ZarosGodwars;
import com.rs.java.game.minigames.warriorguild.WarriorsGuild;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.corporeal.CorporealBeast;
import com.rs.java.game.npc.dagannoth.DagannothKings;
import com.rs.java.game.npc.dagannoth.Spyinolyp;
import com.rs.java.game.npc.dragons.KingBlackDragon;
import com.rs.java.game.npc.glacior.Glacor;
import com.rs.java.game.npc.kalphite.KalphiteQueen;
import com.rs.java.game.npc.nomad.FlameVortex;
import com.rs.java.game.npc.nomad.Nomad;
import com.rs.java.game.npc.others.*;
import com.rs.java.game.player.OwnedObjectManager;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.BoxAction.HunterNPC;
import com.rs.java.game.player.actions.skills.mining.LivingRockCavern;
import com.rs.java.game.player.content.ItemConstants;
import com.rs.java.game.player.content.Update;
import com.rs.java.game.player.content.grandexchange.GrandExchange;
import com.rs.java.game.player.content.shootingstar.ShootingStar;
import com.rs.java.game.player.controlers.EdgevillePvPControler;
import com.rs.java.game.player.controlers.WildernessControler;
import com.rs.java.game.route.Flags;
import com.rs.java.game.timer.TimerRepository;
import com.rs.java.utils.AntiFlood;
import com.rs.java.utils.IPBanL;
import com.rs.java.utils.Logger;
import com.rs.java.utils.ShopsHandler;
import com.rs.java.utils.Utils;
import com.rs.kotlin.Rscm;
import com.rs.kotlin.game.world.pvp.PvpManager;

/**
 * @Improved Andreas - AvalonPK
 */

public final class World {

    public static int exiting_delay;
    public static long exiting_start;


    private static TimerRepository timers = new TimerRepository();
    public static final EntityList<Player> players = new EntityList<>(Settings.PLAYERS_LIMIT);

    private static final EntityList<NPC> npcs = new EntityList<>(Settings.NPCS_LIMIT);
    private static final Map<Integer, Region> regions = Collections.synchronizedMap(new HashMap<>());

    public static boolean isInUpdate;

    public static List<Player> getLocalPlayers(int regionId) {
        List<Player> localPlayers = new ArrayList<>();
        List<Integer> indexes = getRegion(regionId).getPlayerIndexes();
        if (indexes == null)
            return localPlayers;

        for (Integer index : indexes) {
            Player player = players.get(index);
            if (player != null && player.hasStarted() && !player.hasFinished()) {
                localPlayers.add(player);
            }
        }
        return localPlayers;
    }

    public static int getPlayersInWilderness() {
        int result = 0;
        for (Player players : World.getPlayers())
            if ((players.getControlerManager().getControler() instanceof WildernessControler)) {
                result++;
            }
        return result;
    }

    public static int getPlayersInFFA() {
        int result = 0;
        for (Player players : World.getPlayers()) {
            if (FfaZone.inPvpArea(players))
                result++;
        }
        return result;
    }

    public static int getPlayersInPVP() {
        int result = 0;
        for (Player players : World.getPlayers())
            if ((players.getControlerManager().getControler() instanceof EdgevillePvPControler)) {
                result++;
            }
        return result;
    }

    public static void init() {
        addRandomMessagesTask();
        addRestoreShopItemsTask();
        addDegradeShopItemsTask();
        addOwnedObjectsTask();
        Update.ProcessUpdates();
        LivingRockCavern.init();
        WarriorsGuild.init();
        //TODO DISABLED FOR NOW executeShootingStar();
        artisanWorkShopBonusExp();
    }

    public static TimerRepository getTimers() {
        return timers;
    }

    public static void setTimers(TimerRepository timers) {
        World.timers = timers;
    }

    public enum ARTISAN_TYPES {
        HELM, CHEST, GLOVES, BOOTS
    }

    ;

    public static ARTISAN_TYPES artisanBonusExp;

    public static void artisanWorkShopBonusExp() {
        spawnNPC(6654, new WorldTile(3060, 3339, 0), -1, true);
        NPC suak = getNPC(6654);
        int time = 36000;
        CoresManager.getFastExecutor().schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    ARTISAN_TYPES[] values2 = ARTISAN_TYPES.values();
                    artisanBonusExp = values2[Utils.random(0, values2.length - 1)];
                    assert suak != null;
                    suak.setNextForceTalk(new ForceTalk("Smith " + artisanBonusExp));
                } catch (Throwable e) {
                    World.sendWorldMessage("" + e, true);
                    Logger.handle(e);
                }
            }
        }, 0, time);
        World.sendWorldMessage("" + artisanBonusExp, true);
    }
    /*
     * private static void addLogicPacketsTask() {
     * CoresManager.fastExecutor.scheduleAtFixedRate(new TimerTask() {
     *
     * @Override public void run() { for (Player player : World.getPlayers()) { if
     * (!player.hasStarted() || player.hasFinished()) continue;
     * player.processLogicPackets(); } } }, 300, 300); }
     */

    /*
     * private static void addNpcAgressionTask() {
     * CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
     *
     * @Override public void run() { try { for (NPC npc : getNPCs()) { for (Player
     * player : getPlayers()) { if (player.withinDistance(npc, 5) &&
     * player.inWilderness() && !npc.isDead() && npc.canWalkNPC(player.getX(),
     * player.getY(), true)) { npc.setTarget(player); player.sm("NPC " + npc.getId()
     * + " is wanting the D."); } } } } catch (Throwable e) { Logger.handle(e); }
     *
     * }
     *
     * }, 0, 2, TimeUnit.SECONDS); }
     */

    public static void executeAfterLoadRegion(final int regionId, final Runnable event) {
        executeAfterLoadRegion(regionId, 0, event);
    }

    public static void executeAfterLoadRegion(final int regionId, long startTime, final Runnable event) {
        executeAfterLoadRegion(regionId, startTime, 10000, event);
    }

    public static void executeAfterLoadRegion(final int regionId, long startTime, final long expireTime,
                                              final Runnable event) {
        final long start = Utils.currentTimeMillis();
        World.getRegion(regionId, true);
        CoresManager.getFastExecutor().schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    if (!World.isRegionLoaded(regionId) && Utils.currentTimeMillis() - start < expireTime)
                        return;
                    event.run();
                    cancel();
                } catch (Throwable e) {
                    System.out.println(e);
                }
            }

        }, startTime, 600);
    }

    public static void executeAfterLoadRegion(final int fromRegionX, final int fromRegionY, final int toRegionX,
                                              final int toRegionY, long startTime, final long expireTime, final Runnable event) {
        final long start = Utils.currentTimeMillis();
        for (int x = fromRegionX; x <= toRegionX; x++) {
            for (int y = fromRegionY; y <= toRegionY; y++) {
                int regionId = MapUtils.encode(Structure.REGION, x, y);
                World.getRegion(regionId, true); // forces check load if not loaded
            }
        }
        CoresManager.getFastExecutor().schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    for (int x = fromRegionX; x <= toRegionX; x++) {
                        for (int y = fromRegionY; y <= toRegionY; y++) {
                            int regionId = MapUtils.encode(Structure.REGION, x, y);
                            if (!World.isRegionLoaded(regionId) && Utils.currentTimeMillis() - start < expireTime)
                                return;
                        }
                    }
                    event.run();
                    cancel();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

        }, startTime, 600);
    }

    /**
     * shooting star handler
     */
    public static ShootingStar shootingStar;

    public static void executeShootingStar() {
        CoresManager.getSlowExecutor().scheduleWithFixedDelay(() -> shootingStar = new ShootingStar(), 0, 30, TimeUnit.MINUTES);
    }

    private static void addOwnedObjectsTask() {
        CoresManager.getSlowExecutor().scheduleWithFixedDelay(() -> {
            try {
                OwnedObjectManager.processAll();
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private static void addRestoreShopItemsTask() {
        CoresManager.getSlowExecutor().scheduleWithFixedDelay(() -> {
            try {
                ShopsHandler.restoreShops();
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private static void addDegradeShopItemsTask() {
        CoresManager.getSlowExecutor().scheduleWithFixedDelay(() -> {
            try {
                ShopsHandler.degradeShops();
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, 0, 90, TimeUnit.SECONDS);
    }

    private static final int lastMessage = -1;

    private static void addRandomMessagesTask() {
        CoresManager.getSlowExecutor().scheduleWithFixedDelay(() -> {
            try {
                for (Player player : getPlayers()) {
                    if (player == null || !player.isActive())
                        continue;
                    int random = Utils.getRandom(5);
                    while (random != lastMessage) {
                        random = Utils.getRandom(5);
                    }
                    String colorIcon = "<img=7><col=9966ff> ";
                    switch (random) {
                        case 0:
                            player.getPackets().sendFilteredGameMessage(true,
                                    colorIcon + "Tip: It's always smart to report something suspicious!");
                            break;
                        case 1:
                            player.getPackets().sendFilteredGameMessage(true,
                                    colorIcon + "Like the server? Don't forget to give us your feedback!");
                            break;
                        case 2:
                            player.getPackets().sendFilteredGameMessage(true,
                                    colorIcon + "Tip: Don't forget to send in a ticket, if you need assistance.");
                            break;
                        case 3:
                            player.getPackets().sendFilteredGameMessage(true,
                                    colorIcon + "Tip: You can hide these kind of messages by filtering your chat.");
                            break;
                        case 4:
                            player.getPackets().sendFilteredGameMessage(true,
                                    colorIcon + "Did you know? " + Settings.SERVER_NAME + " has achievement diaries?");
                            break;
                        case 5:
                            player.getPackets().sendFilteredGameMessage(true,
                                    colorIcon + Utils.getRegisteredAccounts());
                            break;
                    }
                }
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, 0, 120, TimeUnit.SECONDS);
    }

    public static Map<Integer, Region> getRegions() {
        return regions;
    }

    public static Region getRegion(int id) {
        return getRegion(id, false);
    }

    public static Region getRegion(int id, boolean load) {
        Region region = regions.get(id);
        if (region == null) {
            region = new Region(id);
            regions.put(id, region);
        }
        if (load)
            region.checkLoadMap();
        return region;
    }

    public static void addNPC(NPC npc) {
        npcs.add(npc);
    }

    public static void removeNPC(NPC npc) {
        npcs.remove(npc);
    }

    public static NPC getNPC(int npcId) {
        for (NPC npc : getNPCs()) {
            if (npc.getId() == npcId) {
                return npc;
            }
        }
        return null;
    }

    public static NPC getNPCByIndex(int index) {
        if (index < 0 || index >= npcs.size()) return null;
        return npcs.get(index);
    }

    public static NPC spawnNPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
                               boolean spawned) {
        NPC n = null;
        HunterNPC hunterNPCs = HunterNPC.forId(id);
        if (hunterNPCs != null) {
            if (id == hunterNPCs.getNpcId())
                n = new ItemHunterNPC(hunterNPCs, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        } else if (id >= 5533 && id <= 5558)
            n = new Elemental(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 6078 || id == 6079 || id == 4292 || id == 4291 || id == 6080 || id == 6081)
            n = new Cyclopse(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
        else if (id == 7134)
            n = new Bork(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 9463 || id == 9465 || id == 9467)
            n = new Strykewyrms(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == Rscm.lookup("npc.dagannoth_supreme_lv303") || id == Rscm.lookup("npc.dagannoth_prime_lv303") || id == Rscm.lookup("npc.dagannoth_rex_lv303"))
            n = new DagannothKings(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 2892 || id == 2893 || id == 2894)
            n = new Spyinolyp(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 9441)
            n = new FlameVortex(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id >= 8832 && id <= 8834)
            n = new LivingRock(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id >= 13465 && id <= 13481)
            n = new Revenant(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 1158 || id == 1160)
            n = new KalphiteQueen(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id >= 8528 && id <= 8532)
            n = new Nomad(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
            /*
             * else if (id == 6281 || id == 6282 || id == 6275 || id == 6279 || id == 9184
             * || id == 6268 || id == 6270 || id == 6274 || id == 6277 || id == 6276 || id
             * == 6278) n = new GodwarsBandosFaction(id, tile, mapAreaNameHash,
             * canBeAttackFromOutOfArea, spawned);
             */
        else if (id == 6260)
            n = new GeneralGraardor(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 3200)
            n = new ChaosElemental(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 6222)
            n = new KreeArra(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 6203)
            n = new KrilTstsaroth(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 50 || id == 2642)
            n = new KingBlackDragon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id >= 9462 && id <= 9467)
            n = new Strykewyrm(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
        else if (id == 6247)
            n = new CommanderZilyana(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id >= 6210 && id <= 6221)
            n = new GodwarsZammorakFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id >= 6254 && id <= 6259)
            n = new GodwarsSaradominFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id >= 6268 && id <= 6283)
            n = new GodwarsBandosFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id >= 6228 && id <= 6246)
            n = new GodwarsArmadylFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 8133)
            n = new CorporealBeast(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 19000 || id == 19001 || id == 19002 || id == 6367 || id == 3229 || id == 1919)
            n = new Hybrid(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 4474 || id == 7891)
            n = new Dummy(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 13447)
            n = ZarosGodwars.nex = new Nex(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 13451)
            n = ZarosGodwars.fumus = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 13452)
            n = ZarosGodwars.umbra = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 13453)
            n = ZarosGodwars.cruor = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 14301)
            n = new Glacor(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
        else if (id == 13454)
            n = ZarosGodwars.glacies = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 8349 || id == 8450 || id == 8451)
            n = new TormentedDemon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 15149)
            n = new MasterOfFear(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 1266 || id == 1268)
            n = new Rocks(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 1265 || id == 1267)
            n = new RockCrabs(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        else if (id == 3373)
            n = new Max(id, tile, canBeAttackFromOutOfArea);
        else
            n = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
        if (n != null)
            n.setBonuses();
        return n;
    }

    public static NPC spawnNPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
                               boolean spawned, Player owner) {
        NPC n = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned, owner);
        if (n.getId() == 9151) {
            n.animate(new Animation(11907));
        }
        return n;
    }

    public static NPC spawnNPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
        return spawnNPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
    }

    /*
     * check if the entity region changed because moved or teled then we update it
     */
    public static void updateEntityRegion(Entity entity) {
        if (entity.hasFinished()) {
            if (entity instanceof Player)
                getRegion(entity.getLastRegionId()).removePlayerIndex(entity.getIndex());
            else
                getRegion(entity.getLastRegionId()).removeNPCIndex(entity.getIndex());
            return;
        }
        int regionId = entity.getRegionId();
        if (entity.getLastRegionId() != regionId) { // map region entity at
            // changed
            if (entity instanceof Player player) {
                if (entity.getLastRegionId() > 0)
                    getRegion(entity.getLastRegionId()).removePlayerIndex(entity.getIndex());
                Region region = getRegion(regionId);
                region.addPlayerIndex(entity.getIndex());
                int musicId = region.getRandomMusicId();
                if (musicId != -1)
                    player.getMusicsManager().checkMusic(musicId);
                if (player.getControlerManager() != null)
                    player.getControlerManager().moved();
                if (player.hasStarted()) {
                    checkControlersAtMove(player);
                    PvpManager.onMoved(player);
                }
            } else {
                if (entity.getLastRegionId() > 0)
                    getRegion(entity.getLastRegionId()).removeNPCIndex(entity.getIndex());
                getRegion(regionId).addNPCIndex(entity.getIndex());
            }
            entity.checkMultiArea();
            entity.setLastRegionId(regionId);
        } else {
            if (entity instanceof Player player) {
                player.getControlerManager().moved();
                PvpManager.onMoved(player);
                if (player.hasStarted()) {
                    checkControlersAtMove(player);
                    PvpManager.onMoved(player);
                }
            }
            entity.checkMultiArea();
        }
    }

    private static void checkControlersAtMove(Player player) {
        if (!(player.getControlerManager().getControler() instanceof RequestController)
                && RequestController.inWarRequest(player))
            player.getControlerManager().startControler("clan_wars_request");
        else if (player.getRegionId() == 13363)
            player.getControlerManager().startControler("DuelControler");
        else if (FfaZone.inArea(player))
            player.getControlerManager().startControler("clan_wars_ffa");
    }

    /*
     * checks clip
     */
    public static boolean canMoveNPC(int plane, int x, int y, int size) {
        for (int tileX = x; tileX < x + size; tileX++)
            for (int tileY = y; tileY < y + size; tileY++)
                if (getMask(plane, tileX, tileY) != 0)
                    return false;
        return true;
    }

    /*
     * checks clip
     */
    public static boolean isNotCliped(int plane, int x, int y, int size) {
        for (int tileX = x; tileX < x + size; tileX++)
            for (int tileY = y; tileY < y + size; tileY++)
                if ((getMask(plane, tileX, tileY) & 2097152) != 0)
                    return false;
        return true;
    }

    public static boolean isClipped(int plane, int x, int y) {
        return (getMask(plane, x, y) & 2097152) != 0;
    }

    public static void setMask(int plane, int x, int y, int mask) {
        WorldTile tile = new WorldTile(x, y, plane);
        int regionId = tile.getRegionId();
        Region region = getRegion(regionId);
        int baseLocalX = x - ((regionId >> 8) * 64);
        int baseLocalY = y - ((regionId & 0xff) * 64);
        region.setMask(tile.getPlane(), baseLocalX, baseLocalY, mask);
    }

    public static int getRotation(int plane, int x, int y) {
        WorldTile tile = new WorldTile(x, y, plane);
        int regionId = tile.getRegionId();
        Region region = getRegion(regionId);
        int baseLocalX = x - ((regionId >> 8) * 64);
        int baseLocalY = y - ((regionId & 0xff) * 64);
        return region.getRotation(tile.getPlane(), baseLocalX, baseLocalY);
    }

    /*
     * checks clip
     */
    public static boolean isRegionLoaded(int regionId) {
        Region region = getRegion(regionId);
        return region.getLoadMapStage() == 2;
    }

    public static boolean isTileFree(int plane, int x, int y, int size) {
        for (int tileX = x; tileX < x + size; tileX++)
            for (int tileY = y; tileY < y + size; tileY++)
                if (!isFloorFree(plane, tileX, tileY) || !isWallsFree(plane, tileX, tileY))
                    return false;
        return true;
    }

    public static boolean isFloorFree(int plane, int x, int y) {
        return (getMask(plane, x, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ)) == 0;
    }

    public static boolean isWallsFree(int plane, int x, int y) {
        return (getMask(plane, x, y) & (Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_NORTHWEST
                | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST | Flags.WALLOBJ_EAST | Flags.WALLOBJ_NORTH
                | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST)) == 0;
    }

    public static int getMask(int plane, int x, int y) {
        WorldTile tile = new WorldTile(x, y, plane);
        Region region = getRegion(tile.getRegionId());
        return region.getMask(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion());
    }

    private static int getClipedOnlyMask(int plane, int x, int y) {
        WorldTile tile = new WorldTile(x, y, plane);
        Region region = getRegion(tile.getRegionId());
        return region.getMaskClipedOnly(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion());
    }

    public static boolean checkProjectileStep(int plane, int x, int y, int dir, int size) {
        int xOffset = Utils.DIRECTION_DELTA_X[dir];
        int yOffset = Utils.DIRECTION_DELTA_Y[dir];
        /*
         * int rotation = getRotation(plane,x+xOffset,y+yOffset); if(rotation != 0) {
         * dir += rotation; if(dir >= Utils.DIRECTION_DELTA_X.length) dir = dir -
         * (Utils.DIRECTION_DELTA_X.length-1); xOffset = Utils.DIRECTION_DELTA_X[dir];
         * yOffset = Utils.DIRECTION_DELTA_Y[dir]; }
         */
        if (size == 1) {
            int mask = getClipedOnlyMask(plane, x + Utils.DIRECTION_DELTA_X[dir], y + Utils.DIRECTION_DELTA_Y[dir]);
            if (xOffset == -1 && yOffset == 0)
                return (mask & 0x42240000) == 0;
            if (xOffset == 1 && yOffset == 0)
                return (mask & 0x60240000) == 0;
            if (xOffset == 0 && yOffset == -1)
                return (mask & 0x40a40000) == 0;
            if (xOffset == 0 && yOffset == 1)
                return (mask & 0x48240000) == 0;
            if (xOffset == -1 && yOffset == -1) {
                return (mask & 0x43a40000) == 0 && (getClipedOnlyMask(plane, x - 1, y) & 0x42240000) == 0
                        && (getClipedOnlyMask(plane, x, y - 1) & 0x40a40000) == 0;
            }
            if (xOffset == 1 && yOffset == -1) {
                return (mask & 0x60e40000) == 0 && (getClipedOnlyMask(plane, x + 1, y) & 0x60240000) == 0
                        && (getClipedOnlyMask(plane, x, y - 1) & 0x40a40000) == 0;
            }
            if (xOffset == -1 && yOffset == 1) {
                return (mask & 0x4e240000) == 0 && (getClipedOnlyMask(plane, x - 1, y) & 0x42240000) == 0
                        && (getClipedOnlyMask(plane, x, y + 1) & 0x48240000) == 0;
            }
            if (xOffset == 1 && yOffset == 1) {
                return (mask & 0x78240000) == 0 && (getClipedOnlyMask(plane, x + 1, y) & 0x60240000) == 0
                        && (getClipedOnlyMask(plane, x, y + 1) & 0x48240000) == 0;
            }
        } else if (size == 2) {
            if (xOffset == -1 && yOffset == 0)
                return (getClipedOnlyMask(plane, x - 1, y) & 0x43a40000) == 0
                        && (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4e240000) == 0;
            if (xOffset == 1 && yOffset == 0)
                return (getClipedOnlyMask(plane, x + 2, y) & 0x60e40000) == 0
                        && (getClipedOnlyMask(plane, x + 2, y + 1) & 0x78240000) == 0;
            if (xOffset == 0 && yOffset == -1)
                return (getClipedOnlyMask(plane, x, y - 1) & 0x43a40000) == 0
                        && (getClipedOnlyMask(plane, x + 1, y - 1) & 0x60e40000) == 0;
            if (xOffset == 0 && yOffset == 1)
                return (getClipedOnlyMask(plane, x, y + 2) & 0x4e240000) == 0
                        && (getClipedOnlyMask(plane, x + 1, y + 2) & 0x78240000) == 0;
            if (xOffset == -1 && yOffset == -1)
                return (getClipedOnlyMask(plane, x - 1, y) & 0x4fa40000) == 0
                        && (getClipedOnlyMask(plane, x - 1, y - 1) & 0x43a40000) == 0
                        && (getClipedOnlyMask(plane, x, y - 1) & 0x63e40000) == 0;
            if (xOffset == 1 && yOffset == -1)
                return (getClipedOnlyMask(plane, x + 1, y - 1) & 0x63e40000) == 0
                        && (getClipedOnlyMask(plane, x + 2, y - 1) & 0x60e40000) == 0
                        && (getClipedOnlyMask(plane, x + 2, y) & 0x78e40000) == 0;
            if (xOffset == -1 && yOffset == 1)
                return (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4fa40000) == 0
                        && (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4e240000) == 0
                        && (getClipedOnlyMask(plane, x, y + 2) & 0x7e240000) == 0;
            if (xOffset == 1 && yOffset == 1)
                return (getClipedOnlyMask(plane, x + 1, y + 2) & 0x7e240000) == 0
                        && (getClipedOnlyMask(plane, x + 2, y + 2) & 0x78240000) == 0
                        && (getClipedOnlyMask(plane, x + 1, y + 1) & 0x78e40000) == 0;
        } else {
            if (xOffset == -1 && yOffset == 0) {
                if ((getClipedOnlyMask(plane, x - 1, y) & 0x43a40000) != 0
                        || (getClipedOnlyMask(plane, x - 1, -1 + (y + size)) & 0x4e240000) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
                    if ((getClipedOnlyMask(plane, x - 1, y + sizeOffset) & 0x4fa40000) != 0)
                        return false;
            } else if (xOffset == 1 && yOffset == 0) {
                if ((getClipedOnlyMask(plane, x + size, y) & 0x60e40000) != 0
                        || (getClipedOnlyMask(plane, x + size, y - (-size + 1)) & 0x78240000) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
                    if ((getClipedOnlyMask(plane, x + size, y + sizeOffset) & 0x78e40000) != 0)
                        return false;
            } else if (xOffset == 0 && yOffset == -1) {
                if ((getClipedOnlyMask(plane, x, y - 1) & 0x43a40000) != 0
                        || (getClipedOnlyMask(plane, x + size - 1, y - 1) & 0x60e40000) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
                    if ((getClipedOnlyMask(plane, x + sizeOffset, y - 1) & 0x63e40000) != 0)
                        return false;
            } else if (xOffset == 0 && yOffset == 1) {
                if ((getClipedOnlyMask(plane, x, y + size) & 0x4e240000) != 0
                        || (getClipedOnlyMask(plane, x + (size - 1), y + size) & 0x78240000) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
                    if ((getClipedOnlyMask(plane, x + sizeOffset, y + size) & 0x7e240000) != 0)
                        return false;
            } else if (xOffset == -1 && yOffset == -1) {
                if ((getClipedOnlyMask(plane, x - 1, y - 1) & 0x43a40000) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
                    if ((getClipedOnlyMask(plane, x - 1, y + (-1 + sizeOffset)) & 0x4fa40000) != 0
                            || (getClipedOnlyMask(plane, sizeOffset - 1 + x, y - 1) & 0x63e40000) != 0)
                        return false;
            } else if (xOffset == 1 && yOffset == -1) {
                if ((getClipedOnlyMask(plane, x + size, y - 1) & 0x60e40000) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
                    if ((getClipedOnlyMask(plane, x + size, sizeOffset + (-1 + y)) & 0x78e40000) != 0
                            || (getClipedOnlyMask(plane, x + sizeOffset, y - 1) & 0x63e40000) != 0)
                        return false;
            } else if (xOffset == -1 && yOffset == 1) {
                if ((getClipedOnlyMask(plane, x - 1, y + size) & 0x4e240000) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
                    if ((getClipedOnlyMask(plane, x - 1, y + sizeOffset) & 0x4fa40000) != 0
                            || (getClipedOnlyMask(plane, -1 + (x + sizeOffset), y + size) & 0x7e240000) != 0)
                        return false;
            } else if (xOffset == 1 && yOffset == 1) {
                if ((getClipedOnlyMask(plane, x + size, y + size) & 0x78240000) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
                    if ((getClipedOnlyMask(plane, x + sizeOffset, y + size) & 0x7e240000) != 0
                            || (getClipedOnlyMask(plane, x + size, y + sizeOffset) & 0x78e40000) != 0)
                        return false;
            }
        }
        return true;
    }

    public static boolean checkWalkStep(int plane, int x, int y, int dir, int size) {
        return checkWalkStep(plane, x, y, Utils.DIRECTION_DELTA_X[dir], Utils.DIRECTION_DELTA_Y[dir], size);
    }

    public static Player[] getNearPlayers(Player player, int distance, int maxTargets) {
        List<Entity> possibleTargets = new ArrayList<Entity>();
        stop:
        for (int regionId : player.getMapRegionsIds()) {
            Region region = World.getRegion(regionId);
            List<Integer> playerIndexes = region.getPlayerIndexes();
            if (playerIndexes == null)
                continue;
            for (int playerIndex : playerIndexes) {
                Player p2 = World.getPlayers().get(playerIndex);
                if (p2 == null || p2 == player || p2.isDead() || !p2.hasStarted() || p2.hasFinished()
                        || !p2.withinDistance(player, distance))
                    continue;
                possibleTargets.add(p2);
                if (possibleTargets.size() == maxTargets)
                    break stop;
            }
        }
        return possibleTargets.toArray(new Player[possibleTargets.size()]);
    }

    public static boolean checkWalkStep(int plane, int x, int y, int xOffset, int yOffset, int size) {
        if (size == 1) {
            int mask = getMask(plane, x + xOffset, y + yOffset);
            if (xOffset == -1 && yOffset == 0)
                return (mask
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST)) == 0;
            if (xOffset == 1 && yOffset == 0)
                return (mask
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_WEST)) == 0;
            if (xOffset == 0 && yOffset == -1)
                return (mask
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH)) == 0;
            if (xOffset == 0 && yOffset == 1)
                return (mask
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH)) == 0;
            if (xOffset == -1 && yOffset == -1)
                return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
                        | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
                        && (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_EAST)) == 0
                        && (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_NORTH)) == 0;
            if (xOffset == 1 && yOffset == -1)
                return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
                        | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0
                        && (getMask(plane, x + 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_WEST)) == 0
                        && (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_NORTH)) == 0;
            if (xOffset == -1 && yOffset == 1)
                return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST
                        | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0
                        && (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_EAST)) == 0
                        && (getMask(plane, x, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_SOUTH)) == 0;
            if (xOffset == 1 && yOffset == 1)
                return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
                        | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0
                        && (getMask(plane, x + 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_WEST)) == 0
                        && (getMask(plane, x, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_SOUTH)) == 0;
        } else if (size == 2) {
            if (xOffset == -1 && yOffset == 0)
                return (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
                        && (getMask(plane, x - 1, y + 1)
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST
                        | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0;
            if (xOffset == 1 && yOffset == 0)
                return (getMask(plane, x + 2, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0
                        && (getMask(plane, x + 2, y + 1)
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
                        | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
            if (xOffset == 0 && yOffset == -1)
                return (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
                        && (getMask(plane, x + 1, y - 1)
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
                        | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0;
            if (xOffset == 0 && yOffset == 1)
                return (getMask(plane, x, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0
                        && (getMask(plane, x + 1, y + 2)
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
                        | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
            if (xOffset == -1 && yOffset == -1)
                return (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST
                        | Flags.CORNEROBJ_SOUTHEAST)) == 0
                        && (getMask(plane, x - 1, y - 1)
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
                        | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
                        && (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST
                        | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) == 0;
            if (xOffset == 1 && yOffset == -1)
                return (getMask(plane, x + 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST
                        | Flags.CORNEROBJ_NORTHEAST)) == 0
                        && (getMask(plane, x + 2, y - 1)
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
                        | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0
                        && (getMask(plane, x + 2, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
                        | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
            if (xOffset == -1 && yOffset == 1)
                return (getMask(plane, x - 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST
                        | Flags.CORNEROBJ_SOUTHEAST)) == 0
                        && (getMask(plane, x - 1, y + 1)
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST
                        | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0
                        && (getMask(plane, x, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
                        | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
            if (xOffset == 1 && yOffset == 1)
                return (getMask(plane, x + 1, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST
                        | Flags.CORNEROBJ_SOUTHWEST)) == 0
                        && (getMask(plane, x + 2, y + 2)
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
                        | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0
                        && (getMask(plane, x + 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
                        | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
                        | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
        } else {
            if (xOffset == -1 && yOffset == 0) {
                if ((getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0
                        || (getMask(plane, x - 1, -1 + (y + size))
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST
                        | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
                    if ((getMask(plane, x - 1, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
                            | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH
                            | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0)
                        return false;
            } else if (xOffset == 1 && yOffset == 0) {
                if ((getMask(plane, x + size, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0
                        || (getMask(plane, x + size, y - (-size + 1))
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
                        | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
                    if ((getMask(plane, x + size, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
                            | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
                            | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
                        return false;
            } else if (xOffset == 0 && yOffset == -1) {
                if ((getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0
                        || (getMask(plane, x + size - 1, y - 1)
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
                        | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
                    if ((getMask(plane, x + sizeOffset, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
                            | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST
                            | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
                        return false;
            } else if (xOffset == 0 && yOffset == 1) {
                if ((getMask(plane, x, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0
                        || (getMask(plane, x + (size - 1), y + size)
                        & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
                        | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
                    if ((getMask(plane, x + sizeOffset, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
                            | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
                            | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
                        return false;
            } else if (xOffset == -1 && yOffset == -1) {
                if ((getMask(plane, x - 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
                    if ((getMask(plane, x - 1, y + (-1 + sizeOffset)) & (Flags.FLOOR_BLOCKSWALK
                            | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST
                            | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0
                            || (getMask(plane, sizeOffset - 1 + x, y - 1) & (Flags.FLOOR_BLOCKSWALK
                            | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST
                            | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
                        return false;
            } else if (xOffset == 1 && yOffset == -1) {
                if ((getMask(plane, x + size, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
                    if ((getMask(plane, x + size, sizeOffset + (-1 + y)) & (Flags.FLOOR_BLOCKSWALK
                            | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH
                            | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0
                            || (getMask(plane, x + sizeOffset, y - 1) & (Flags.FLOOR_BLOCKSWALK
                            | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST
                            | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
                        return false;
            } else if (xOffset == -1 && yOffset == 1) {
                if ((getMask(plane, x - 1, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
                        | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
                    if ((getMask(plane, x - 1, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
                            | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH
                            | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0
                            || (getMask(plane, -1 + (x + sizeOffset), y + size) & (Flags.FLOOR_BLOCKSWALK
                            | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH
                            | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
                        return false;
            } else if (xOffset == 1 && yOffset == 1) {
                if ((getMask(plane, x + size, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
                        | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
                    return false;
                for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
                    if ((getMask(plane, x + sizeOffset, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
                            | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
                            | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0
                            || (getMask(plane, x + size, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK
                            | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH
                            | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
                        return false;
            }
        }
        return true;
    }

    public static boolean containsPlayer(String username) {
        for (Player p2 : players) {
            if (p2 == null)
                continue;
            if (p2.getUsername().equals(username))
                return true;
        }
        return false;
    }

    public static Player getPlayer(String username) {
        for (Player player : getPlayers()) {
            if (player == null)
                continue;
            if (player.getUsername().equals(username))
                return player;
        }
        return null;
    }

    public static Player getPlayerByDisplayName(String username) {
        String formatedUsername = Utils.formatPlayerNameForDisplay(username);
        for (Player player : getPlayers()) {
            if (player == null)
                continue;
            if (player.getUsername().equalsIgnoreCase(formatedUsername)
                    || player.getDisplayName().equalsIgnoreCase(formatedUsername))
                return player;
        }
        return null;
    }

    public static EntityList<Player> getPlayers() {
        return players;
    }

    public static EntityList<NPC> getNPCs() {
        return npcs;
    }

    private World() {

    }

    public static void setUpdateTime(int newTime) {
        if (exiting_start == 0) {
            // System.out.println("There was an error setting the new update time.");
            return;
        }
        exiting_start = Utils.currentTimeMillis();
        exiting_delay = newTime;
        for (Player player : World.getPlayers()) {
            if (player == null || !player.hasStarted() || player.hasFinished())
                continue;
            player.getPackets().sendSystemUpdate(newTime);
        }
        CoresManager.getSlowExecutor().schedule(() -> {
            try {
                for (Player player : World.getPlayers()) {
                    if (player == null || !player.hasStarted())
                        continue;
                    player.realFinish();
                }
                IPBanL.save();
                GrandExchange.save();
                // PlayerOwnedShops.save();
                Launcher.shutdown();
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, newTime, TimeUnit.SECONDS);
    }

    public static void safeShutdown(int delay) {
        if (exiting_start != 0)
            return;
        exiting_start = Utils.currentTimeMillis();
        exiting_delay = delay;
        for (Player player : World.getPlayers()) {
            if (player == null || !player.hasStarted() || player.hasFinished())
                continue;
            player.getPackets().sendSystemUpdate(delay);
        }
        CoresManager.getSlowExecutor().schedule(() -> {
            try {
                for (Player player : World.getPlayers()) {
                    if (player == null || !player.hasStarted())
                        continue;
                    player.realFinish();
                }
                IPBanL.save();
                GrandExchange.save();
                // PlayerOwnedShops.save();
                Launcher.shutdown();
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, delay, TimeUnit.SECONDS);
    }

    public static void safeRestart(int delay) {
        if (exiting_start != 0)
            return;
        exiting_start = Utils.currentTimeMillis();
        exiting_delay = delay;
        for (Player player : World.getPlayers()) {
            if (player == null || !player.hasStarted() || player.hasFinished())
                continue;
            player.getPackets().sendSystemUpdate(delay);
            isInUpdate = true;
            if (delay == 5) {
                player.getSession().getChannel().disconnect();
            }
        }
        CoresManager.getSlowExecutor().schedule(() -> {
            try {
                for (Player player : World.getPlayers()) {
                    if (player == null || !player.hasStarted())
                        continue;
                    player.realFinish();
                }
                isInUpdate = false;
                GrandExchange.save();
                // PlayerOwnedShops.save();
                Launcher.restart();
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, delay, TimeUnit.SECONDS);
    }

    public static boolean isSpawnedObject(WorldObject object) {
        return getRegion(object.getRegionId()).getSpawnedObjects().contains(object);
    }

    public static void spawnObject(WorldObject object) {
        getRegion(object.getRegionId()).spawnObject(object, object.getPlane(), object.getXInRegion(),
                object.getYInRegion(), false);
    }

    public static void unclipTile(WorldTile tile) {
        getRegion(tile.getRegionId()).unclip(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion());
    }

    public static void removeObject(WorldObject object) {
        getRegion(object.getRegionId()).removeObject(object, object.getPlane(), object.getXInRegion(),
                object.getYInRegion());
    }

    public static void spawnObjectTemporary(final WorldObject object, long time) {
        spawnObjectTemporary(object, time, false, false);
    }

    public static void spawnObjectTemporary(final WorldObject object, long time,
                                            final boolean checkObjectInstance, boolean checkObjectBefore) {
        final WorldObject before = checkObjectBefore ? World.getObjectWithType(object, object.getType()) : null;
        spawnObject(object);
        CoresManager.getSlowExecutor().schedule(() -> {
            try {
                if (checkObjectInstance && World.getObjectWithId(object, object.getId()) != object)
                    return;
                if (before != null)
                    spawnObject(before);
                else
                    removeObject(object);
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, time, TimeUnit.MILLISECONDS);
    }

    public static boolean removeObjectTemporary(final WorldObject object, long time) {
        removeObject(object);
        CoresManager.getSlowExecutor().schedule(() -> {
            try {
                spawnObject(object);
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, time, TimeUnit.MILLISECONDS);
        return true;
    }

    public static void spawnTempGroundObject(final WorldObject object, final int replaceId, long time) {
        spawnObject(object);
        CoresManager.getSlowExecutor().schedule(() -> {
            try {
                removeObject(object);
                addGroundItem(new Item(replaceId), object, null, false, 180);
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, time, TimeUnit.MILLISECONDS);
    }

    public static WorldObject getStandardFloorObject(WorldTile tile) {
        return getRegion(tile.getRegionId()).getStandardFloorObject(tile.getPlane(), tile.getXInRegion(),
                tile.getYInRegion());
    }

    public static WorldObject getStandardFloorDecoration(WorldTile tile) {
        return getRegion(tile.getRegionId()).getStandardFloorDecoration(tile.getPlane(), tile.getXInRegion(),
                tile.getYInRegion());
    }

    public static WorldObject getStandardWallDecoration(WorldTile tile) {
        return getRegion(tile.getRegionId()).getStandardWallDecoration(tile.getPlane(), tile.getXInRegion(),
                tile.getYInRegion());
    }

    public static void spawnObject(WorldObject object, boolean clip) {
        getRegion(object.getRegionId()).spawnObject(object, object.getPlane(), object.getXInRegion(), object.getYInRegion(), false);
    }

    public static WorldObject getStandardWallObject(WorldTile tile) {
        return getRegion(tile.getRegionId()).getStandardWallObject(tile.getPlane(), tile.getXInRegion(),
                tile.getYInRegion());
    }

    public static WorldObject getObjectWithType(WorldTile tile, int type) {
        return getRegion(tile.getRegionId()).getObjectWithType(tile.getPlane(), tile.getXInRegion(),
                tile.getYInRegion(), type);
    }

    public static WorldObject getObjectWithSlot(WorldTile tile, int slot) {
        return getRegion(tile.getRegionId()).getObjectWithSlot(tile.getPlane(), tile.getXInRegion(),
                tile.getYInRegion(), slot);
    }

    public static WorldObject getRealObject(WorldTile tile, int slot) {
        return getRegion(tile.getRegionId()).getRealObject(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(),
                slot);
    }

    public static boolean containsObjectWithId(WorldTile tile, int id) {
        return getRegion(tile.getRegionId()).containsObjectWithId(tile.getPlane(), tile.getXInRegion(),
                tile.getYInRegion(), id);
    }

    public static WorldObject getObjectWithId(WorldTile tile, int id) {
        return getRegion(tile.getRegionId()).getObjectWithId(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(),
                id);
    }

    public static boolean isSpawnedObject(Player player, WorldObject object) {
        return getRegion(player.getRegionId()).getSpawnedObjects().contains(object);
    }

    public static void removeObject(WorldObject object, boolean removeClip) {
        getRegion(object.getRegionId()).removeObject(object, object.getPlane(), object.getXInRegion(),
                object.getYInRegion(), removeClip);
    }

    public static void spawnObjectTemporaryNewItem(final WorldObject object, long time, int newId) {
        WorldObject newObject = new WorldObject(newId, object.getType(), object.getRotation(), object.getX(),
                object.getY(), object.getPlane());
        spawnObject(object);
        CoresManager.getSlowExecutor().schedule(() -> {
            try {
                if (!World.isSpawnedObject(object))
                    return;
                spawnObject(newObject);
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, time, TimeUnit.MILLISECONDS);
    }

    public static boolean removeObjectTemporary(final WorldObject object, long time, boolean removeClip) {
        removeObject(object, removeClip);
        CoresManager.getSlowExecutor().schedule(() -> {
            try {
                // if (!World.containsObjectWithId(new WorldTile(object.getTileHash()),
                // object.getId()))
                spawnObject(object);
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, time, TimeUnit.MILLISECONDS);
        return true;
    }

    public static void spawnTempGroundObject(final WorldObject object, final int replaceId, long time,
                                             final boolean removeClip) {
        spawnObject(object);
        CoresManager.getSlowExecutor().schedule(() -> {
            try {
                removeObject(object, removeClip);
                addGroundItem(new Item(replaceId), object, null, false, 180);
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, time, TimeUnit.MILLISECONDS);
    }

    public static void addGlobalGroundItem(final Item item, final WorldTile tile, final int tick,
                                           final boolean spawned) {
        FloorItem floorItem = World.getRegion(tile.getRegionId()).getGroundItem(item.getId(), tile, null);
        if (floorItem == null) {
            floorItem = new FloorItem(item, tile, null, false, tick, spawned);
            final Region region = getRegion(tile.getRegionId());
            if (floorItem.isGlobalPicked())
                return;
            region.getGroundItemsSafe().add(floorItem);
            int regionId = tile.getRegionId();
            for (Player player : players) {
                if (player == null || !player.hasStarted() || player.hasFinished()
                        || player.getPlane() != tile.getPlane() || !player.getMapRegionsIds().contains(regionId)
                        || player.getRegionId() != region.getRegionId())
                    continue;
                player.getPackets().sendGroundItem(floorItem);
            }
        }
    }

    public static void addGroundItem(final Item item, final WorldTile tile) {
        addGroundItem(item, tile, null, false, -1, 2, -1);
    }

    public static void addGroundItem(final Item item, final WorldTile tile, int publicTime) {
        addGroundItem(item, tile, null, false, -1, 2, publicTime);
    }

    public static void addGroundItem(final Item item, final WorldTile tile, final Player owner, boolean invisible,
                                     long hiddenTime) {
        addGroundItem(item, tile, owner, invisible, hiddenTime, 2, 60);
    }

    public static void addGlobalGroundItem(final Item item, final WorldTile tile) {
        addGroundItem(item, tile, null, false, -1, 2, -1, null);
    }

    public static FloorItem addGroundItem(final Item item, final WorldTile tile, final Player owner,
                                          boolean invisible, long hiddenTime, int type) {
        return addGroundItem(item, tile, owner, invisible, hiddenTime, type, 60);
    }

    public static FloorItem addGroundItem(final Item item, final WorldTile tile, final Player owner,
                                          boolean invisible, long hiddenTime, int type, String ironmanName) {
        return addGroundItem(item, tile, owner, invisible, hiddenTime, type, 60, ironmanName);
    }

    public static void turnPublic(FloorItem item, int publicTime) {
        if (!item.isInvisible())
            return;

        Region region = getRegion(item.getTile().getRegionId());
        if (!region.getGroundItemsSafe().contains(item))
            return;

        Player owner = item.hasOwner() ? World.getPlayer(item.getOwner()) : null;

        // Handle attached items
        int attachedId = ItemConstants.removeAttachedId(item);
        if (attachedId != -1) {
            int attachedId2 = ItemConstants.removeAttachedId2(item);
            if (attachedId2 != -1) {
                World.updateGroundItem(new Item(attachedId2, 1), item.getTile(), owner, 0, 2);
            }
            removeGroundItem(item, 0);
            removeGroundItem(item, 0); // Remove the untradeable one
            World.updateGroundItem(new Item(attachedId, 1), item.getTile(), null, 0, 2); // Spawn tradeable (no owner)
            return;
        }

        item.setInvisible(false);

        // Remove player beam if applicable
        if (owner != null && owner.getBeam() != null && owner.getBeamItem() != null) {
            if (item.getTile().matches(owner.getBeam()) && owner.getBeamItem().getId() == item.getId()) {
                owner.setBeam(null);
                owner.setBeamItem(null);
            }
        }

        // Send ground item to other players
        int regionId = item.getTile().getRegionId();
        for (Player player : players) {
            if (player == null || player == owner || !player.hasStarted() || player.hasFinished())
                continue;
            if (player.getPlane() != item.getTile().getPlane())
                continue;
            if (!player.getMapRegionsIds().contains(regionId))
                continue;
            if (!ItemConstants.isTradeable(item))
                continue;

            player.getPackets().sendGroundItem(item);
        }

        if (publicTime != -1)
            removeGroundItem(item, publicTime);
    }

    private static void broadcastGroundItem(FloorItem item, boolean checkTradeable) {
        int regionId = item.getTile().getRegionId();
        for (Player player : players) {
            if (player == null || !player.hasStarted() || player.hasFinished())
                continue;
            if (player.getPlane() != item.getTile().getPlane())
                continue;
            if (!player.getMapRegionsIds().contains(regionId))
                continue;
            if (checkTradeable && !ItemConstants.isTradeable(item))
                continue;

            player.getPackets().sendGroundItem(item);
        }
    }


    public static void addGroundItemForever(Item item, WorldTile tile) {
        FloorItem floorItem = new FloorItem(item, tile, true);
        Region region = getRegion(tile.getRegionId());
        region.getGroundItemsSafe().add(floorItem);

        broadcastGroundItem(floorItem, false);
    }

    public static FloorItem addGroundItem(Item item, WorldTile tile, Player owner,
                                          boolean invisible, long hiddenTime, int type, int publicTime) {
        return addGroundItemInternal(item, tile, owner, invisible, hiddenTime, type, publicTime, null);
    }

    public static FloorItem addGroundItem(Item item, WorldTile tile, Player owner,
                                          boolean invisible, long hiddenTime, int type, int publicTime, String ironmanName) {
        return addGroundItemInternal(item, tile, owner, invisible, hiddenTime, type, publicTime, ironmanName);
    }

    private static FloorItem addGroundItemInternal(Item item, WorldTile tile, Player owner,
                                                   boolean invisible, long hiddenTime, int type, int publicTime, String ironmanName) {
        FloorItem floorItem = new FloorItem(new Item(item), tile, owner, false, invisible, ironmanName);

        Region region = getRegion(tile.getRegionId());

        boolean shouldBroadcast = !invisible;
        boolean shouldTrack = type != 2 || ItemConstants.isTradeable(item) || ItemConstants.turnCoins(item);

        if (shouldTrack) {
            region.getGroundItemsSafe().add(floorItem);
        }

        // Send to owner if invisible
        if (invisible && owner != null) {
            if (type != 2 || ItemConstants.isTradeable(item) || ItemConstants.turnCoins(item))
                owner.getPackets().sendGroundItem(floorItem);
        }

        // Public broadcast
        if (shouldBroadcast) {
            boolean checkTradeable = (type != 2);
            broadcastGroundItem(floorItem, checkTradeable);
            if (publicTime != -1) {
                removeGroundItem(floorItem, publicTime);
            }
        } else if (hiddenTime != -1) {
            CoresManager.getSlowExecutor().schedule(() -> {
                try {
                    turnPublic(floorItem, publicTime);
                } catch (Throwable e) {
                    Logger.handle(e);
                }
            }, hiddenTime, TimeUnit.SECONDS);
        }

        return floorItem;
    }


    public static void updateGroundItem(Item item, WorldTile tile, Player owner) {
        updateGroundItem(item, tile, owner, 60, 0, null);
    }

    public static void updateGroundItem(Item item, WorldTile tile, Player owner, int hiddenTime, int type) {
        updateGroundItem(item, tile, owner, hiddenTime, type, null);
    }

    public static void updateGroundItem(Item item, WorldTile tile, Player owner, int hiddenTime, int type, String ironmanName) {
        FloorItem floorItem = World.getRegion(tile.getRegionId()).getGroundItem(item.getId(), tile, owner);

        if (floorItem == null) {
            spawnAsNewGroundItem(item, tile, owner, hiddenTime, type, ironmanName);
            return;
        }

        boolean stackable = floorItem.getDefinitions().isStackable() || floorItem.getDefinitions().isNoted();

        if (stackable) {
            int total = floorItem.getAmount() + item.getAmount();

            if (total < 0) {
                int amountCanAdd = Integer.MAX_VALUE - floorItem.getAmount();
                floorItem.setAmount(Integer.MAX_VALUE);
                item.setAmount(item.getAmount() - amountCanAdd);

                if (ironmanName != null)
                    addGroundItem(item, tile, owner, true, hiddenTime, type, ironmanName);
                else
                    addGroundItem(item, tile, owner, true, hiddenTime, type);

                owner.getPackets().sendRemoveGroundItem(floorItem);
                owner.getPackets().sendGroundItem(floorItem);
            } else {
                floorItem.setAmount(floorItem.getAmount() + item.getAmount());
                owner.getPackets().sendRemoveGroundItem(floorItem);
                owner.getPackets().sendGroundItem(floorItem);
            }

        } else {
            spawnAsNewGroundItem(item, tile, owner, hiddenTime, type, ironmanName);
        }
    }

    private static void spawnAsNewGroundItem(Item item, WorldTile tile, Player owner, int hiddenTime, int type, String ironmanName) {
        boolean stackable = item.getDefinitions().isStackable() || item.getDefinitions().isNoted();

        if (!stackable && item.getAmount() > 1) {
            Item copy = item.clone();
            for (int i = 0; i < copy.getAmount(); i++) {
                item.setAmount(1);
                addGroundItem(item, tile, owner, false, hiddenTime, type, ironmanName);
            }
        } else {
            addGroundItem(item, tile, owner, owner != null, hiddenTime, type, ironmanName);
        }
    }

    private static boolean shouldNotifyPlayer(Player player, FloorItem item, int regionId) {
        return player != null
                && player.hasStarted()
                && !player.hasFinished()
                && player.getPlane() == item.getTile().getPlane()
                && player.getMapRegionsIds().contains(regionId);
    }


    private static void removeGroundItem(final FloorItem item, long delaySeconds) {
        CoresManager.getSlowExecutor().schedule(() -> {
            try {
                final int regionId = item.getTile().getRegionId();
                final Region region = getRegion(regionId);

                if (!region.getGroundItemsSafe().remove(item)) {
                    return; // Item already removed
                }

                for (Player player : World.getPlayers()) {
                    if (shouldNotifyPlayer(player, item, regionId)) {
                        player.getPackets().sendRemoveGroundItem(item);
                    }
                }
            } catch (Exception e) {
                Logger.handle(e);
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }


    public static void removeGroundItem(Player player, FloorItem floorItem) {
        removeGroundItem(player, floorItem, true);
    }

    public static boolean removeGroundItem(Player player, final FloorItem floorItem, boolean addToInventory) {
        final int regionId = floorItem.getTile().getRegionId();
        final Region region = getRegion(regionId);

        if (player == null) {
            region.getGroundItemsSafe().remove(floorItem);
            broadcastRemoveGroundItem(floorItem, regionId);

            if (floorItem.isForever()) {
                scheduleGroundItemRespawn(floorItem);
            }
            return false;
        }

        if (!region.getGroundItemsSafe().contains(floorItem)) return false;

        if (!canPickupItem(player, floorItem)) return false;

        if (floorItem.getId() == 995 && !player.inPkingArea() && !FfaZone.inRiskArea(player)) {
            return handleCoinPickup(player, floorItem, region, regionId);
        }

        if (!hasInventorySpace(player, floorItem)) {
            player.getPackets().sendGameMessage("Not enough space in your inventory.");
            return false;
        }

        if (player.isFrozen() && !floorItem.getTile().matches(player.getTile())) {
            player.animate(new Animation("animation.pickup_floor"));
        }

        if (addToInventory) {
            if (floorItem.getId() == 7957)
                floorItem.setId(1005);
            player.getInventory().addItem(floorItem);
        }

        region.getGroundItemsSafe().remove(floorItem);
        handleBeamPickup(player, floorItem);

        if (floorItem.isInvisible()) {
            player.getPackets().sendRemoveGroundItem(floorItem);
        } else {
            broadcastRemoveGroundItem(floorItem, regionId);
            if (floorItem.isForever()) {
                scheduleGroundItemRespawn(floorItem);
            }
        }

        return true;
    }

    private static boolean canPickupItem(Player player, FloorItem item) {
        if ((item.cantPickupBy(player.getDisplayName()) || item.getOwn() != player)
                && player.getPlayerRank().isIronman()) {
            return sendIronmanRestriction(player);
        }
        if (isDeveloperOnlyItem(item, player)) {
            player.getPackets().sendGameMessage("This item has been dropped by a developer, therefore you can't pick it up.");
            return false;
        }
        return canPickupClueScroll(player, item);
    }

    private static boolean sendIronmanRestriction(Player player) {
        player.getPackets().sendGameMessage("You can't pickup other players items as an Iron " +
                (player.getAppearence().isMale() ? "Man" : "Woman") + ".");
        return false;
    }

    private static boolean isDeveloperOnlyItem(FloorItem item, Player player) {
        return item.getOwn() != null && item.getOwn().isDeveloper() && !player.isDeveloper();
    }

    private static boolean canPickupClueScroll(Player player, FloorItem item) {
        int id = item.getId();
        switch (id) {
            case 2677: // easy
            case 2801: // medium
            case 2722: // hard
            case 19043: // elite
                if (player.getInventory().containsOneItem(id) || player.getBank().getItem(id) != null) {
                    player.getPackets().sendGameMessage("You can only have one " + getClueTypeName(id) + " clue scroll at a time.");
                    return false;
                }
                break;
        }
        return true;
    }

    private static String getClueTypeName(int id) {
        switch (id) {
            case 2677:
                return "easy";
            case 2801:
                return "medium";
            case 2722:
                return "hard";
            case 19043:
                return "elite";
            default:
                return "";
        }
    }

    private static boolean hasInventorySpace(Player player, FloorItem item) {
        int id = item.getId();
        int amount = item.getAmount();
        boolean stackable = item.getDefinitions().isStackable() || item.getDefinitions().isNoted();

        if (player.getInventory().getAmountOf(id) == Integer.MAX_VALUE) {
            return false;
        }
        if (!player.getInventory().hasFreeSlots()) {
            if (stackable && !player.getInventory().containsItem(id, 1)) {
                return false;
            } else if (!stackable) {
                return false;
            }
        }
        return true;
    }

    private static void broadcastRemoveGroundItem(FloorItem item, int regionId) {
        for (Player player : World.getPlayers()) {
            if (player == null || !player.hasStarted() || player.hasFinished()) continue;
            if (player.getPlane() != item.getTile().getPlane()) continue;
            if (!player.getMapRegionsIds().contains(regionId)) continue;
            player.getPackets().sendRemoveGroundItem(item);
        }
    }

    private static void scheduleGroundItemRespawn(FloorItem item) {
        CoresManager.getSlowExecutor().schedule(() -> {
            try {
                addGroundItemForever(item, item.getTile());
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, 60, TimeUnit.SECONDS);
    }

    private static void handleBeamPickup(Player player, FloorItem item) {
        if (player.getBeam() != null && player.getBeamItem() != null) {
            if (item.getTile().matches(player.getBeam()) && player.getBeamItem().getId() == item.getId()) {
                World.sendGraphics(player, new Graphics(-1, 0, 0), new WorldTile(item.getTile()));
                player.setBeam(null);
                player.setBeamItem(null);
            }
        }
    }

    private static boolean handleCoinPickup(Player player, FloorItem item, Region region, int regionId) {
        int amount = item.getAmount();
        int pouchTotal = player.getMoneyPouch().getTotal();
        int coinsInventory = player.getInventory().getNumberOf(995);

        if (pouchTotal == Integer.MAX_VALUE && coinsInventory == Integer.MAX_VALUE) {
            player.getPackets().sendGameMessage("You don't have enough space to hold more coins.");
            return false;
        }

        int canAdd = Integer.MAX_VALUE - pouchTotal;
        int toPouch = Math.min(amount, canAdd);
        int leftover = amount - toPouch;

        if (toPouch > 0) {
            player.getMoneyPouch().setTotal(pouchTotal + toPouch);
            player.getPackets().sendRunScript(5561, 1, toPouch);
            player.getMoneyPouch().refresh();
            player.getPackets().sendGameMessage(
                    toPouch == 1 ? "One coin has been added to your money pouch." :
                            Utils.getFormattedNumber(toPouch, ',') + " coins have been added to your money pouch.");
        }

        if (leftover > 0) {
            if (!player.getInventory().hasFreeSlots() && !player.getInventory().containsOneItem(995)) {
                player.getPackets().sendGameMessage("You don't have enough inventory space.");
                return false;
            }
            if (player.getInventory().getNumberOf(995) + leftover < 0) {
                int invCanAdd = Integer.MAX_VALUE - player.getInventory().getNumberOf(995);
                player.getInventory().addItem(995, invCanAdd);
                item.setAmount(leftover - invCanAdd);
                player.getPackets().sendRemoveGroundItem(item);
                player.getPackets().sendGroundItem(item);
                return false;
            }
            player.getInventory().addItem(995, leftover);
        }

        region.getGroundItemsSafe().remove(item);
        if (item.isInvisible()) {
            player.getPackets().sendRemoveGroundItem(item);
        } else {
            broadcastRemoveGroundItem(item, regionId);
            if (item.isForever()) scheduleGroundItemRespawn(item);
        }
        return true;
    }


    public static void sendObjectAnimation(WorldObject object, Animation animation) {
        sendObjectAnimation(null, object, animation);
    }

    public static void sendObjectAnimation(Entity creator, WorldObject object, Animation animation) {
        if (creator == null) {
            for (Player player : World.getPlayers()) {
                if (player == null || !player.hasStarted() || player.hasFinished() || !player.withinDistance(object))
                    continue;
                player.getPackets().sendObjectAnimation(object, animation);
            }
        } else {
            for (int regionId : creator.getMapRegionsIds()) {
                List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
                if (playersIndexes == null)
                    continue;
                for (Integer playerIndex : playersIndexes) {
                    Player player = players.get(playerIndex);
                    if (player == null || !player.hasStarted() || player.hasFinished()
                            || !player.withinDistance(object))
                        continue;
                    player.getPackets().sendObjectAnimation(object, animation);
                }
            }
        }
    }

    public static void sendGraphics(Entity creator, Graphics graphics, WorldTile tile) {
        if (creator == null) {
            for (Player player : World.getPlayers()) {
                if (player == null || !player.hasStarted() || player.hasFinished() || !player.withinDistance(tile))
                    continue;
                player.getPackets().sendGraphics(graphics, tile);
            }
        } else {
            for (int regionId : creator.getMapRegionsIds()) {
                List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
                if (playersIndexes == null)
                    continue;
                for (Integer playerIndex : playersIndexes) {
                    Player player = players.get(playerIndex);
                    if (player == null || !player.hasStarted() || player.hasFinished() || !player.withinDistance(tile))
                        continue;
                    player.getPackets().sendGraphics(graphics, tile);
                }
            }
        }
    }

    public static void sendPrivateGraphics(Entity creator, Graphics graphics, WorldTile tile) {
        if (creator == null) {
            for (Player player : World.getPlayers()) {
                if (player != creator || player == null || !player.hasStarted() || player.hasFinished()
                        || !player.withinDistance(tile))
                    continue;
                player.getPackets().sendGraphics(graphics, tile);
            }
        } else {
            for (int regionId : creator.getMapRegionsIds()) {
                List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
                if (playersIndexes == null)
                    continue;
                for (Integer playerIndex : playersIndexes) {
                    Player player = players.get(playerIndex);
                    if (player != creator || player == null || !player.hasStarted() || player.hasFinished()
                            || !player.withinDistance(tile))
                        continue;
                    player.getPackets().sendGraphics(graphics, tile);
                }
            }
        }
    }

    private static int getSpeed(Entity shooter, Entity target) {
        int distance = Utils.getDistance(shooter, target);
        return distance < 2 ? 41 : (distance == 2 ? 46 : 51);
    }

    private static int getSpeed(Entity shooter, WorldTile targetTile) {
        int distance = Utils.getDistance(shooter, targetTile);
        return distance < 2 ? 41 : (distance == 2 ? 46 : 51);
    }

    public static void sendProjectileToTile(Entity shooter, WorldTile targetTile, int gfxId,
                                            int startHeight, int endHeight, int speed, int delay, int curve,
                                            int startOffsetIfClose, int closeDistanceThreshold, boolean checkDistanceToTarget) {
        for (int regionId : shooter.getMapRegionsIds()) {
            List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
            if (playersIndexes == null)
                continue;

            for (Integer playerIndex : playersIndexes) {
                Player player = players.get(playerIndex);
                if (player == null || !player.hasStarted() || player.hasFinished())
                    continue;

                boolean nearShooter = player.withinDistance(shooter);
                boolean nearTarget = false;

                if (checkDistanceToTarget && targetTile != null) {
                    nearTarget = player.withinDistance(targetTile);
                }

                if (!nearShooter && !nearTarget)
                    continue;

                int size = shooter.getSize();
                int distance = Utils.getDistance(shooter, targetTile);
                int startOffsetDistance = (distance <= closeDistanceThreshold) ? startOffsetIfClose : 0;

                WorldTile startTile = new WorldTile(
                        shooter.getCoordFaceX(size),
                        shooter.getCoordFaceY(size),
                        shooter.getPlane()
                );

                player.getPackets().sendProjectile(null, startTile, targetTile, gfxId,
                        startHeight, endHeight, speed, delay, curve, startOffsetDistance, size);
            }
        }
    }


    public static void sendProjectileToPlayers(Entity shooter, WorldTile targetTile, Entity targetEntity, int gfxId,
                                               int startHeight, int endHeight, int speed, int delay, int curve,
                                               int startOffsetIfClose, int closeDistanceThreshold, boolean checkDistanceToTarget,
                                               boolean includeTargetEntity, boolean useReceiverAsTarget) {
        for (int regionId : shooter.getMapRegionsIds()) {
            List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
            if (playersIndexes == null)
                continue;
            for (Integer playerIndex : playersIndexes) {
                Player player = players.get(playerIndex);
                if (player == null || !player.hasStarted() || player.hasFinished())
                    continue;

                boolean nearShooter = player.withinDistance(shooter);
                boolean nearTarget = false;
                if (checkDistanceToTarget) {
                    if (useReceiverAsTarget && targetEntity != null)
                        nearTarget = player.withinDistance(targetEntity);
                    else if (targetTile != null)
                        nearTarget = player.withinDistance(targetTile);
                }

                if (!nearShooter && !nearTarget)
                    continue;

                int size = shooter.getSize();
                int distance = targetEntity != null ? Utils.getDistance(shooter, targetEntity)
                        : Utils.getDistance(shooter, targetTile);

                int startOffsetDistance = (distance <= closeDistanceThreshold) ? startOffsetIfClose : 0;

                WorldTile startTile = new WorldTile(shooter.getCoordFaceX(size), shooter.getCoordFaceY(size), shooter.getPlane());

                if (includeTargetEntity && targetEntity != null) {
                    player.getPackets().sendProjectile(targetEntity, startTile, targetEntity, gfxId, startHeight, endHeight, speed, delay, curve, startOffsetDistance, size);
                } else {
                    player.getPackets().sendProjectile(null, startTile, targetTile, gfxId, startHeight, endHeight, speed, delay, curve, startOffsetDistance, size);
                }
            }
        }
    }

    public static void sendProjectileToTile(Entity shooter, WorldTile tile, int gfxId) {
        sendProjectileToTile(shooter, tile, gfxId,
                43, 34, (Utils.getDistance(shooter, tile) < 2 ? 51 : Utils.getDistance(shooter, tile) == 2 ? 56 : 61),
                51, 6, 11, 2, true);
    }

    public static void sendProjectile(WorldObject object, WorldTile startTile, WorldTile endTile, int gfxId,
                                      int startHeight, int endHeight, int speed, int delay, int curve, int startOffset) {
        for (Player pl : players) {
            if (pl == null || !pl.withinDistance(object, 20))
                continue;
            pl.getPackets().sendProjectile(null, startTile, endTile, gfxId, startHeight, endHeight, speed, delay, curve,
                    startOffset, 1);
        }
    }

    public static void sendDragonfireProjectile(Entity shooter, Entity receiver, int gfxId) {
        sendProjectileToPlayers(shooter, null, receiver, gfxId,
                53, 34, receiver != null ? (Utils.getDistance(shooter, receiver) < 4 ? 36 : 41) : 41,
                41, 0, 11, 2, true, true, false);
    }

    public static void sendNPCProjectile(Entity shooter, WorldTile tile, int gfxId) {
        sendProjectileToPlayers(shooter, tile, null, gfxId,
                53, 34, (Utils.getDistance(shooter, tile) < 4 ? 36 : 41), 41,
                0, 0, 2, true, false, false);
    }

    public static void sendGroundProjectile(Entity shooter, WorldTile tile, int gfxId) {
        sendProjectileToPlayers(shooter, tile, null, gfxId,
                53, 0, (Utils.getDistance(shooter, tile) < 4 ? 36 : 41), 41,
                0, 0, 2, true, false, false);
    }

    public static void sendNPCSlowProjectile(Entity shooter, WorldTile tile, int gfxId) {
        sendProjectileToPlayers(shooter, tile, null, gfxId,
                53, 34, (Utils.getDistance(shooter, tile) < 4 ? 26 : 31), 21,
                0, 0, 2, true, false, false);
    }

    public static void sendJadProjectile(Entity shooter, Entity receiver, int gfxId) {
        sendProjectileToPlayers(shooter, null, receiver, gfxId,
                53, 34, (Utils.getDistance(shooter, receiver) < 4 ? 32 : 48), 0,
                0, 11, 2, true, true, false);
    }

    public static void sendProjectile(Entity shooter, Entity receiver, int gfxId, int startHeight, int endHeight,
                                      int speed, int delay, int curve) {
        sendProjectileToPlayers(shooter, null, receiver, gfxId,
                startHeight, endHeight, speed, delay, curve,
                0, 2, true, true, false);
    }

    public static void sendElementalProjectile(Entity shooter, Entity receiver, int gfxId) {
        sendProjectileToPlayers(shooter, null, receiver, gfxId,
                43, 34, receiver != null ? (Utils.getDistance(shooter, receiver) < 2 ? 51 : Utils.getDistance(shooter, receiver) == 2 ? 56 : 61) : 61,
                51, 6, 11, 2, true, true, true);
    }

    public static void sendFastBowProjectile(Entity shooter, Entity receiver, int gfxId) {
        sendProjectileToPlayers(shooter, null, receiver, gfxId,
                43, 34, receiver != null ? (Utils.getDistance(shooter, receiver) < 2 ? 51 : Utils.getDistance(shooter, receiver) == 2 ? 56 : 61) : 61,
                41, 6, 11, 2, true, true, true);
    }

    public static int sendObjectProjectile(Entity shooter, WorldTile tile, int gfxId) {
        sendProjectileToPlayers(shooter, tile, null, gfxId,
                54, 4, 61, 41,
                6, 11, 2, true, false, false);
        return gfxId;
    }

    public static void sendMSBProjectile(Entity shooter, Entity receiver, int gfxId) {
        sendProjectileToPlayers(shooter, null, receiver, gfxId,
                43, 34, receiver != null ? (Utils.getDistance(shooter, receiver) < 2 ? 51 : Utils.getDistance(shooter, receiver) == 2 ? 56 : 61) : 61,
                31, 6, 11, 2, true, true, false);
    }

    public static void sendMSBProjectile2(Entity shooter, Entity receiver, int gfxId) {
        sendProjectileToPlayers(shooter, null, receiver, gfxId,
                43, 34, receiver != null ? (Utils.getDistance(shooter, receiver) < 2 ? 51 : Utils.getDistance(shooter, receiver) == 2 ? 56 : 61) : 61,
                56, 6, 11, 2, true, true, false);
    }

    public static void sendFastBowSwiftProjectile(Entity shooter, Entity receiver, int gfxId) {
        sendProjectileToPlayers(shooter, null, receiver, gfxId,
                38, 29, receiver != null ? (Utils.getDistance(shooter, receiver) < 2 ? 51 : Utils.getDistance(shooter, receiver) == 2 ? 56 : 61) : 61,
                41, 6, 11, 2, true, true, false);
    }

    public static void sendSlowBowProjectile(Entity shooter, Entity receiver, int gfxId) {
        sendProjectileToPlayers(shooter, null, receiver, gfxId,
                43, 34, getSpeed(shooter, receiver), 41, 16,
                11, 2, true, true, true);
    }

    public static void sendSlowBowProjectile(Entity shooter, WorldTile tile, int gfxId) {
        sendProjectileToPlayers(shooter, tile, null, gfxId,
                43, 34, getSpeed(shooter, tile), 41, 16,
                11, 2, true, false, false);
    }

    public static void sendSlowBow2Projectile(Entity shooter, Entity receiver, int gfxId) {
        sendProjectileToPlayers(shooter, null, receiver, gfxId,
                43, 34, getSpeed(shooter, receiver), 61, 16,
                11, 2, true, true, true);
    }

    public static void sendSlowBowSwiftProjectile(Entity shooter, Entity receiver, int gfxId) {
        sendProjectileToPlayers(shooter, null, receiver, gfxId,
                38, 29, getSpeed(shooter, receiver), 21, 6,
                11, 2, true, true, true);
    }

    public static void sendCBOWProjectile(Entity shooter, Entity receiver, int gfxId) {
        sendProjectileToPlayers(shooter, null, receiver, gfxId,
                43, 34, getSpeed(shooter, receiver), 41, 6,
                11, 2, true, true, true);
    }

    public static void sendCBOWSwiftProjectile(Entity shooter, Entity receiver, int gfxId) {
        sendProjectileToPlayers(shooter, null, receiver, gfxId,
                38, 29, getSpeed(shooter, receiver), 41, 6,
                11, 2, true, true, true);
    }

    public static void sendThrowSwiftProjectile(Entity shooter, Entity receiver, int gfxId) {
        sendProjectileToPlayers(shooter, null, receiver, gfxId,
                38, 29, 42, 32, 6,
                11, 2, true, true, true);
    }

    public static void sendThrowProjectile(Entity shooter, Entity receiver, int gfxId) {
        sendProjectileToPlayers(shooter, null, receiver, gfxId,
                43, 34, 42, 31, 6,
                11, 2, true, true, true);
    }

    public static void sendDartProjectile(Entity shooter, Entity receiver, int gfxId) {
        sendProjectileToPlayers(
                shooter, null, receiver, gfxId,
                43, 34, 42, 16, 6,
                11, 2, true, true, false
        );
    }

    public static void sendCannonProjectile(Entity shooter, Entity receiver, int gfxId) {
        int distance = Utils.getDistance(shooter, receiver);
        int speed = distance < 2 ? 51 : distance == 2 ? 56 : 61;

        sendProjectileToPlayers(
                shooter, null, receiver, gfxId,
                18, 18, speed, 41, 0,
                11, 2, true, true, false
        );
    }

    public static void sendSOAProjectile(Entity shooter, Entity receiver, int gfxId) {
        int distance = Utils.getDistance(shooter, receiver);
        int speed = distance < 4 ? 51 : 61;

        sendProjectileToPlayers(
                shooter, null, receiver, gfxId,
                0, 0, speed, 51, 6,
                11, 2, true, true, false
        );
    }

    public static void sendSoulsplitProjectile(Entity shooter, Entity receiver, int gfxId) {
        int distance = Utils.getDistance(shooter, receiver);
        int speed = distance < 4 ? 26 : 31;

        sendProjectileToPlayers(
                shooter, null, receiver, gfxId,
                0, 0, speed, 41, 6,
                11, 2, true, true, false
        );
    }

    public static void sendLeechProjectile(Entity shooter, Entity receiver, int gfxId) {
        int distance = Utils.getDistance(shooter, receiver);
        int speed = distance < 2 ? 6 : 11;
        int startHeight = 36;
        int endHeight = 36;
        int delay = 31;
        int duration = 36;
        int arc = 0;
        int displacement = 100;

        sendProjectileToPlayers(shooter, null, receiver, gfxId, startHeight, endHeight, speed, delay, arc,
                11, 2, true, true, false
        );
    }

    public static boolean isMultiArea(WorldTile tile) {
        Area area = AreaManager.get(tile);
        return (area != null && area.environment() == Area.Environment.MULTI);
    }

    public static boolean atMultiArea(Player player) {
        Area area = AreaManager.get(player);
        return (area != null && area.environment() == Area.Environment.MULTI);
    }


    public static boolean inWilderness(WorldTile tile) {
        return WildernessControler.isAtWild(tile);
    }

    public static boolean isPvpArea(WorldTile tile) {
        return WildernessControler.isAtWild(tile) || EdgevillePvPControler.isAtPvP(tile);
    }

    private static final EntityList<Player> lobbyPlayers = new EntityList<>(Settings.PLAYERS_LIMIT);

    public static EntityList<Player> getLobbyPlayers() {
        return lobbyPlayers;
    }

    public static Player getLobbyPlayerByDisplayName(String username) {
        String formatted = Utils.formatPlayerNameForDisplay(username);
        return lobbyPlayers.stream()
                .filter(p -> p != null && (p.getUsername().equalsIgnoreCase(formatted) || p.getDisplayName().equalsIgnoreCase(formatted)))
                .findFirst().orElse(null);
    }

    public static boolean containsLobbyPlayer(String username) {
        return lobbyPlayers.stream()
                .anyMatch(p -> p != null && p.getUsername().equalsIgnoreCase(username));
    }

    public static void addLobbyPlayer(Player player) {
        lobbyPlayers.add(player);
        AntiFlood.add(player.getSession().getIP());
    }

    public static void removeLobbyPlayer(Player player) {
        removeFromList(player, lobbyPlayers);
    }

    public static void addPlayer(Player player) {
        players.add(player);

        if (containsLobbyPlayer(player.getUsername())) {
            removeLobbyPlayer(player);
        }

        AntiFlood.add(player.getSession().getIP());
    }

    public static void removePlayer(Player player) {
        removeFromList(player, players);
    }

    private static void removeFromList(Player player, EntityList<Player> list) {
        list.stream()
                .filter(p -> p != null && p.getUsername().equalsIgnoreCase(player.getUsername()))
                .findFirst()
                .ifPresent(p -> {
                    if (p.getCurrentFriendChat() != null) {
                        p.getCurrentFriendChat().leaveChat(p, true);
                    }
                    list.remove(p);
                });

        AntiFlood.remove(player.getSession().getIP());
    }

    public enum MessageType {

        EVENT("<img=7><col=ff0000>Event: "),
        NEWS("<img=7><col=ff0000>News: "),
        SERVER("<img=7><col=ff0000>Server: "),
        RARE_DROP("<img=7><col=36648b>Drop: ");//TODO ALL TYPES

        private String message;

        MessageType(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static void sendWorldMessage(MessageType type, String message) {
        StringBuilder builder = new StringBuilder();
        builder.append(type.getMessage()).append(message);
        for (Player p : World.getPlayers()) {
            if (p == null || !p.isActive())
                continue;
            p.getPackets().sendGameMessage(builder.toString());
        }
    }

    public static void sendWorldMessage(String message, boolean forStaff) {
        for (Player p : World.getPlayers()) {
            if (p == null || !p.isActive() || p.isYellOff() || (forStaff && !p.isStaff()))
                continue;
            p.getPackets().sendGameMessage(message);
        }
    }

    public static void sendNewsMessage(String message, boolean TwoHundredM) {
        World.sendWorldMessage("<img=7>" + (TwoHundredM ? "<col=ff0000>" : "<col=ff8c38>") + "News: " + message, false);
    }
}
