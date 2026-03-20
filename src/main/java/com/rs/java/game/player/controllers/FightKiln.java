package com.rs.java.game.player.controllers;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.rs.Settings;
import com.rs.core.cache.defintions.NPCDefinitions;
import com.rs.core.thread.CoresManager;
import com.rs.java.game.Animation;
import com.rs.java.game.World;
import com.rs.java.game.WorldObject;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.ground.GroundItems;
import com.rs.java.game.map.MapBuilder;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.fightkiln.FightKilnNPC;
import com.rs.java.game.npc.fightkiln.HarAken;
import com.rs.java.game.npc.fightkiln.TokHaarKetDill;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.content.FadingScreen;
import com.rs.java.game.player.cutscenes.Cutscene;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Logger;
import com.rs.java.utils.Utils;

public class FightKiln extends Controller {

    public static final WorldTile OUTSIDE = new WorldTile(4744, 5172, 0);

    private static final int TOKHAAR_HOK = 15195;
    private static final int TOKHAAR_HOK_SCENE = 15200;

    private static final int[] MUSICS = {1088, 1082, 1086};

    private int[] boundChuncks;
    private Stages stage;

    private boolean logoutAtEnd;
    private boolean login;

    public int selectedMusic;

    private NPC tokHaarHok;
    private HarAken harAken;

    private int aliveNPCSCount;

    private enum Stages {
        LOADING, RUNNING, DESTROYING
    }

    public void playMusic() {
        player.getMusicsManager().playMusic(selectedMusic);
    }

    public static void enterFightKiln(Player player, boolean quickEnter) {
        int startWave = determineStartingWave(player);
        player.getControlerManager()
                .startControler("FightKilnControler", startWave, quickEnter ? 1 : 0);
    }

    private static int determineStartingWave(Player player) {
        return player.isCompletedFightKiln() ? 31 : 21;
    }

    private boolean isQuickEnter() {
        return getArguments() != null
                && getArguments().length > 1
                && (int) getArguments()[1] == 1;
    }

    private void debug(String msg) {
        System.out.println("[FightKiln][" + player.getUsername() + "] " + msg);
    }

    private final int[][] WAVES = {{15202, 15202, 15205, 15201, 15201}, // 1
            {15202, 15202, 15205, 15205, 15201}, // 2
            {15202, 15205, 15205, 15205, 15201}, // 3
            {15205, 15205, 15205, 15203, 15203}, // 4
            {15202, 15205, 15205, 15205, 15213}, // 5
            {15202, 15205, 15203, 15203, 15205, 15205}, // 6
            {15203, 15205, 15205, 15205, 15202, 15205}, // 7
            {15207, 15205, 15205}, // 8
            {15205, 15205, 15205, 15205, 15205, 15205}, // 9
            {15205, 15208}, // 10
            {15203, 15203, 15203, 15203}, // 11
            {15203, 15205, 15205, 15203}, // 12
            {15203, 15207, 15203}, // 13
            {15207, 15207, 15203, 15203}, // 14
            {15207, 15207, 15205}, // 15
            {15207, 15207, 15205, 15203, 15203}, // 16
            {15207, 15207, 15205, 15203, 15206}, // 17
            {15207, 15207, 15205, 15206, 15205, 15205}, // 18
            {15203, 15203, 15203, 15203, 15203, 15203, 15203, 15203, 15203}, // 19
            {15207, 15208}, // 20
            {15201, 15201, 15201, 15201, 15201, 15201, 15201, 15201, 15201, 15201, 15201, 15201}, // 21
            {15206, 15201, 15204, 15204, 15201}, // 22
            {15206, 15206, 15204, 15206, 15201}, // 23
            {15206, 15206, 15206, 15205, 15206}, // 24
            {15206, 15206, 15205, 15205, 15207}, // 25
            {15206, 15206, 15205, 15207, 15207}, // 26
            {15204, 15206, 15205, 15204, 15206}, // 27
            {15213, 15213, 15207, 15213, 15213, 15213, 15213}, // 28
            {15206, 15206, 15206, 15206, 15206, 15206}, // 29
            {15206, 15208, 15206, 15206}, // 30
            {15205, 15205, 15205, 15205}, // 31
            {15206, 15206, 15206, 15206}, // 32
            {15207, 15207, 15207, 15207}, // 33
            {15205, 15208, 15206}, // 34
            {15207, 15206, 15206, 15208}, // 35
            {15208, 15208} // 36
    };

    @Override
    public void start() {
        debug("Controller start()");
        loadCave(false);
    }

    @Override
    public boolean processButtonClick(int interfaceId, int componentId, int slotId, int packetId) {
        if (stage != Stages.RUNNING)
            return false;
        if (interfaceId == 182 && (componentId == 6 || componentId == 13)) {
            if (!logoutAtEnd) {
                logoutAtEnd = true;
                player.getPackets()
                        .sendGameMessage("<col=ff0000>You will be logged out automatically at the end of this wave.");
                player.getPackets()
                        .sendGameMessage("<col=ff0000>If you log out sooner, you will have to repeat this wave.");
            } else
                player.logout(false);
            return false;
        }
        return true;
    }

    /**
     * return process normaly
     */
    @Override
    public boolean processObjectClick1(WorldObject object) {
        if (object.getId() == 68111) {
            if (stage != Stages.RUNNING)
                return false;
            exitCave(1);
            return false;
        }
        return true;
    }

    /*
     * return false so wont remove script
     */
    @Override
    public boolean login() {
        debug("Controller login() stage=" + stage);
        if (stage == Stages.LOADING) {
            debug("Player was loading -> unlocking");
            player.unlock();
        }
        loadCave(true);
        return false;
    }

    public int[] getMap() {
        int wave = getCurrentWave();
        if (wave < 11)
            return new int[]{504, 632};
        if (wave < 21)
            return new int[]{512, 632};
        if (wave < 31)
            return new int[]{520, 632};
        if (wave < 34)
            return new int[]{528, 632};
        return new int[]{536, 632};
    }

    public void buildMap() {
        int[] map = getMap();
        MapBuilder.copyAllPlanesMap(map[0], map[1], boundChuncks[0], boundChuncks[1], 8);
        debug("Map built.");
    }

    private void preparePlayer() {
        selectedMusic = MUSICS[Utils.random(MUSICS.length)];
        playMusic();
        player.setForceMultiArea(true);
        player.setLargeSceneView(true);
        player.stopAll();
        if (player.getFamiliar() != null)
            player.getFamiliar().call(false);
    }

    public void loadCave(boolean login) {

        final FightKiln kiln = this;

        stage = Stages.LOADING;
        player.lock();

        Runnable event = () -> CoresManager.getSlowExecutor().execute(() -> {

            try {

                int currentWave = getCurrentWave();

                if (boundChuncks == null || login) {

                    boundChuncks = MapBuilder.findEmptyChunkBound(8, 8);
                    buildMap();

                    WorldTasksManager.schedule(new WorldTask() {

                        @Override
                        public void run() {

                            player.setForceNextMapLoadRefresh(true);
                            player.loadMapRegions();

                            if (login) {

                                teleportPlayerToMiddle();
                                preparePlayer();

                                stage = Stages.RUNNING;
                                player.unlock();

                                setWaveEvent();
                                stop();
                                return;
                            }

                            continueLoad(kiln, currentWave);
                            stop();
                        }

                    });

                    return;
                }

                if (currentWave == 11 || currentWave == 21
                        || currentWave == 31 || currentWave == 34)
                    buildMap();

                WorldTasksManager.schedule(new WorldTask() {

                    @Override
                    public void run() {

                        player.setForceNextMapLoadRefresh(true);
                        player.loadMapRegions();

                        continueLoad(kiln, currentWave);
                        stop();
                    }

                });

            } catch (Throwable e) {

                Logger.handle(e);

                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        player.unlock();
                        stage = Stages.RUNNING;
                        stop();
                    }
                });
            }
        });

        if (!login)
            FadingScreen.fade(player, event);
        else
            event.run();
    }

    private void continueLoad(FightKiln kiln, int currentWave) {

        preparePlayer();

        if (!isQuickEnter()) {
            playIntroScene(kiln);
            return;
        }

        if (currentWave > 1) {

            teleportPlayerToMiddle();

            stage = Stages.RUNNING;
            player.unlock();

            setWaveEvent();
        }
    }

    private void playIntroScene(FightKiln kiln) {

        removeCrystals();

        player.setNextWorldTile(getWorldTile(31, 51));

        tokHaarHok = new NPC(TOKHAAR_HOK, getWorldTile(30, 36), -1, true, true);
        tokHaarHok.setDirection(Utils.getFaceDirection(0, 1));

        WorldTasksManager.schedule(new WorldTask() {

            int count;
            boolean run;

            @Override
            public void run() {

                if (count == 0) {

                    WorldTile look = getWorldTile(29, 39);
                    player.getPackets().sendCameraLook(
                            Cutscene.getX(player, look.getX()),
                            Cutscene.getY(player, look.getY()),
                            3500
                    );

                    WorldTile pos = getWorldTile(27, 30);
                    player.getPackets().sendCameraPos(
                            Cutscene.getX(player, pos.getX()),
                            Cutscene.getY(player, pos.getY()),
                            3500
                    );

                    run = player.getRun();
                    player.setRun(false);

                    WorldTile walk = getWorldTile(31, 39);
                    player.addWalkSteps(walk.getX(), walk.getY(), -1, false);

                } else if (count == 1) {

                    player.getPackets().sendResetCamera();

                } else if (count == 2) {

                    player.getDialogueManager()
                            .startDialogue("TokHaarHok", 0, TOKHAAR_HOK, kiln);

                    player.setRun(run);

                    stage = Stages.RUNNING;
                    player.unlock();

                    stop();
                }

                count++;
            }

        }, 1, 6);
    }

    public WorldTile getMaxTile() {
        if (getCurrentWave() < 11)
            return getWorldTile(49, 49);
        if (getCurrentWave() < 21)
            return getWorldTile(47, 47);
        if (getCurrentWave() < 31)
            return getWorldTile(45, 45);
        if (getCurrentWave() < 34)
            return getWorldTile(43, 43);
        return getWorldTile(41, 41);
    }

    public WorldTile getMinTile() {
        if (getCurrentWave() < 11)
            return getWorldTile(14, 14);
        if (getCurrentWave() < 21)
            return getWorldTile(16, 16);
        if (getCurrentWave() < 31)
            return getWorldTile(18, 18);
        if (getCurrentWave() < 34)
            return getWorldTile(20, 20);
        return getWorldTile(22, 22);
    }

    /*
     * 20, 20 min X, min Y 42 42 maxX, maxY
     */
    /*
     * 0 - north 1 - south 2 - east 3 - west
     */
    public WorldTile getTentacleTile() {
        int corner = Utils.random(4);
        int position = Utils.random(5);
        while (corner != 0 && position == 2)
            position = Utils.random(5);
        switch (corner) {
            case 0: // north
                return getWorldTile(21 + (position * 5), 42);
            case 1: // south
                return getWorldTile(21 + (position * 5), 20);
            case 2: // east
                return getWorldTile(42, 21 + (position * 5));
            case 3: // west
            default:
                return getWorldTile(20, 21 + (position * 5));
        }
    }

    public WorldTile getSpawnTile(int count, int size) {
        int position = count % 4;
        switch (position) {
            case 0: // east south
                WorldTile maxTile = getMaxTile();
                WorldTile minTile = getMinTile();
                return new WorldTile(maxTile.getX() - 1 - size, minTile.getY() + 2, 1);
            case 1: // west south
                return getMinTile().transform(2, 2, 0);
            case 2: // west north
                maxTile = getMaxTile();
                minTile = getMinTile();
                return new WorldTile(minTile.getX() + 2, maxTile.getY() - 1 - size, 1);
            case 3: // east north
            default:
                return getMaxTile().transform(-1 - size, -1 - size, 0);
        }
    }

    @Override
    public void moved() {
        if (stage != Stages.RUNNING || !login)
            return;
        login = false;
        setWaveEvent();
    }

    public void startWave() {
        if (stage != Stages.RUNNING)
            return;
        if (player.getFamiliar() != null)
            player.getFamiliar().call(false);
        int currentWave = getCurrentWave();
        player.getInterfaceManager().sendOverlay(316, false);
        player.getVarsManager().sendVar(639, currentWave);
        if (currentWave > WAVES.length) {
            if (currentWave == 37)
                aliveNPCSCount = 1;
            return;
        } else if (currentWave == 0) {
            exitCave(1);
            return;
        }
        aliveNPCSCount = WAVES[currentWave - 1].length;
        for (int i = 0; i < WAVES[currentWave - 1].length; i += 4) {
            final int next = i;
            CoresManager.getFastExecutor().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (stage != Stages.RUNNING)
                            return;
                        spawn(next);
                    } catch (Throwable e) {
                        Logger.handle(e);
                    }
                }
            }, (next / 4) * 4000, TimeUnit.MILLISECONDS);
        }
    }

    public void spawn(int index) {
        int currentWave = getCurrentWave();
        for (int i = index; i < (index + 4 > WAVES[currentWave - 1].length ? WAVES[currentWave - 1].length
                : index + 4); i++) {
            int npcId = WAVES[currentWave - 1][i];
            if (npcId == 15213)
                new TokHaarKetDill(WAVES[currentWave - 1][i],
                        getSpawnTile(i, NPCDefinitions.getNPCDefinitions(npcId).size), this);
            else
                new FightKilnNPC(WAVES[currentWave - 1][i],
                        getSpawnTile(i, NPCDefinitions.getNPCDefinitions(npcId).size), this);

        }
    }

    private int[] getLavaCrystal() {
        switch (getCurrentWave()) {
            case 1:
            case 13:
            case 25:
                return new int[]{23653};
            case 3:
            case 15:
            case 27:
                return new int[]{23654};
            case 5:
            case 18:
            case 29:
                return new int[]{23655};
            case 7:
            case 19:
            case 31:
                return new int[]{23656};
            case 9:
            case 21:
                return new int[]{23657};
            case 11:
            case 23:
                return new int[]{23658};
            case 35:
                return new int[]{23657, 23658};
            default:
                return null;
        }
    }

    public void checkCrystal() {
        if (stage != Stages.RUNNING)
            return;
        if (aliveNPCSCount == 1) {
            int[] crystals = getLavaCrystal();
            if (crystals != null) {
                for (int crystal : crystals) {
                    GroundItems.updateGroundItem(new Item(crystal), getWorldTile(32, 32), player);
                }
            }
        }
    }

    public void removeNPC() {
        if (stage != Stages.RUNNING)
            return;
        aliveNPCSCount--;
        if (aliveNPCSCount == 0)
            nextWave();
    }

    public void win() {
        if (stage != Stages.RUNNING)
            return;
        exitCave(4);
    }

    public void unlockPlayer() {
        stage = Stages.RUNNING;
        player.unlock(); // unlocks player
    }

    public void removeScene() {
        FadingScreen.fade(player, new Runnable() {
            @Override
            public void run() {
                if (stage != Stages.RUNNING)
                    unlockPlayer();
                removeTokHaarTok();
                if (player.getFamiliar() != null)
                    player.getFamiliar().call(false);
                player.getPackets().sendResetCamera();
                player.getPackets().sendBlackOut(0);
                player.getVarsManager().sendVar(1241, 0);
                if (getCurrentWave() == 38) {
                    Integer reward = (Integer) player.temporaryAttribute().get("FightKilnReward");
                    if (reward != null)
                        win();
                } else {
                    teleportPlayerToMiddle();
                    setWaveEvent();
                }
            }

        });
    }

    public void teleportPlayerToMiddle() {
        player.setNextWorldTile(getWorldTile(31, 32));
    }

    public void removeTokHaarTok() {
        if (tokHaarHok != null)
            tokHaarHok.finish();
    }

    public void nextWave() {
        if (stage != Stages.RUNNING)
            return;
        if (player.getFamiliar() != null)
            player.getFamiliar().call(false);
        playMusic();
        player.getVarBitList().put(10838, 1);
        int nextWave = getCurrentWave() + 1;
        if (getCurrentWave() < 20) {
            setCurrentWave(21);
        } else
            setCurrentWave(nextWave);
        if (logoutAtEnd) {
            player.forceLogout();
            return;
        } else if (nextWave == 1 || nextWave == 11 || nextWave == 21 || nextWave == 31 || nextWave == 34
                || nextWave == 37 || nextWave == 38) {
            harAken = null;
            player.stopAll();
            loadCave(false);
            return;
        }
        setWaveEvent();
    }

    public void setWaveEvent() {
        CoresManager.getFastExecutor().schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    if (stage != Stages.RUNNING)
                        return;
                    startWave();
                } catch (Throwable e) {
                    Logger.handle(e);
                }
            }
        }, 6000, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean sendDeath() {
        player.lock(7);
        player.stopAll();
        WorldTasksManager.schedule(new WorldTask() {
            int loop;

            @Override
            public void run() {
                if (loop == 0) {
                    player.animate(new Animation(836));
                } else if (loop == 1) {
                    player.getPackets().sendGameMessage("You have been defeated!");
                } else if (loop == 3) {
                    player.reset();
                    exitCave(1);
                    player.animate(new Animation(-1));
                } else if (loop == 4) {
                    player.getPackets().sendMusicEffect(90);
                    stop();
                }
                loop++;
            }
        }, 0, 1);
        return false;
    }

    @Override
    public void magicTeleported(int type) {
        exitCave(2);
    }

    public void exitCave(int type) {

        stage = Stages.DESTROYING;

        WorldTile outside = new WorldTile(OUTSIDE, 2);

        if (type == 0) {
            player.setLocation(outside);
            if (getCurrentWave() == 0)
                removeControler();
        } else {

            player.setForceMultiArea(false);
            player.setLargeSceneView(false);
            player.getInterfaceManager().closeOverlay(false);

            if (type == 1 || type == 4) {
                player.useStairs(-1, outside, 0, 1);

                if (type == 4)
                    handleVictoryReward();
            }

            removeCrystals();
            removeCrystalEffects();

            removeControler();
        }

        destroyInstance();
    }

    private void removeCrystalEffects() {
        player.setInvulnerable(false);
        player.getSkills().restoreSkills();
        player.setHpBoostMultiplier(0);
        player.getEquipment().refreshConfigs(false);
        player.temporaryAttribute().remove("FightKilnCrystal");
    }

    public WorldTile getWorldTile(int mapX, int mapY) {
        if (boundChuncks == null)
            return new WorldTile(mapX, mapY, 0);

        return new WorldTile(
                boundChuncks[0] * 8 + mapX,
                boundChuncks[1] * 8 + mapY,
                1
        );
    }

    @Override
    public boolean logout() {
        if (stage != Stages.RUNNING)
            return false;
        exitCave(0);
        return false;

    }

    public int getCurrentWave() {
        try {
            if (getArguments() == null || getArguments().length == 0) {
                debug("getCurrentWave(): no arguments, defaulting to 0");
                return 0;
            }

            Object arg = getArguments()[0];
            debug("getCurrentWave(): args length=" + getArguments().length + ", arg0=" + arg);

            if (arg instanceof Number)
                return ((Number) arg).intValue();

            return 0;

        } catch (Throwable e) {
            debug("getCurrentWave() FAILED: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public void setCurrentWave(int wave) {
        if (getArguments() == null || getArguments().length == 0)
            this.setArguments(new Object[1]);

        getArguments()[0] = Integer.valueOf(wave);
    }

    @Override
    public void forceClose() {
        if (stage != Stages.RUNNING)
            return;
        exitCave(2);
    }

    public void showHarAken() {
        if (harAken == null) {
            harAken = new HarAken(15211, getWorldTile(45, 26), this);
            harAken.setDirection(Utils.getFaceDirection(-1, -1));
        } else {
            if (stage != Stages.RUNNING)
                return;
            switch (Utils.random(3)) {
                case 0:
                    harAken.setLocation(getWorldTile(29, 17));
                    harAken.setDirection(Utils.getFaceDirection(0, 1));
                    break;
                case 1:
                    harAken.setLocation(getWorldTile(17, 30));
                    harAken.setDirection(Utils.getFaceDirection(1, 0));
                    break;
                case 2:
                    harAken.setLocation(getWorldTile(42, 30));
                    harAken.setDirection(Utils.getFaceDirection(-1, 0));
                    break;
            }
            harAken.spawn();
            // TODO set worldtile
        }
        harAken.setCantInteract(false);
        harAken.animate(new Animation(16232));
    }

    public static void useCrystal(final Player player, int id) {
        if (!(player.getControlerManager().getControler() instanceof FightKiln)
                || player.temporaryAttribute().get("FightKilnCrystal") != null)
            return;
        player.getInventory().deleteItem(new Item(id, 1));
        switch (id) {
            case 23653: // invulnerability
                player.getPackets().sendGameMessage("<col=7E2217>>The power of this crystal makes you invulnerable.");
                player.temporaryAttribute().put("FightKilnCrystal", Boolean.TRUE);
                player.setInvulnerable(true);
                WorldTasksManager.schedule(new WorldTask() {

                    @Override
                    public void run() {
                        player.temporaryAttribute().remove("FightKilnCrystal");
                        player.getPackets().sendGameMessage(
                                "<col=7E2217>The power of the crystal dwindles and you're vulnerable once more.");
                        player.setInvulnerable(false);
                    }

                }, 25);
                break;
            case 23654: // RESTORATION
                player.heal(player.getMaxHitpoints());
                player.getPrayer().restorePrayer(player.getSkills().getRealLevel(Skills.PRAYER) * 10);
                player.getPackets().sendGameMessage("<col=7E2217>The power of this crystal heals you fully.");
                break;
            case 23655: // MAGIC
                boostCrystal(player, Skills.MAGIC);
                break;
            case 23656: // RANGED
                boostCrystal(player, Skills.RANGE);
                break;
            case 23657: // STRENGTH
                boostCrystal(player, Skills.STRENGTH);
                break;
            case 23658: // CONSTITUTION
                player.temporaryAttribute().put("FightKilnCrystal", Boolean.TRUE);
                player.setHpBoostMultiplier(0.5);
                player.getEquipment().refreshConfigs(false);
                player.heal(player.getSkills().getRealLevel(Skills.HITPOINTS) * 5);
                player.getPackets().sendGameMessage("<col=7E2217>The power of this crystal improves your Constitution.");
                WorldTasksManager.schedule(new WorldTask() {

                    @Override
                    public void run() {
                        player.temporaryAttribute().remove("FightKilnCrystal");
                        player.getPackets().sendGameMessage(
                                "<col=7E2217>The power of the crystal dwindles and your constitution prowess returns to normal.");
                        player.setHpBoostMultiplier(0);
                        player.getEquipment().refreshConfigs(false);
                    }
                }, 350);
                break;
        }
    }

    private static void boostCrystal(final Player player, final int skill) {
        player.temporaryAttribute().put("FightKilnCrystal", Boolean.TRUE);
        if (skill == Skills.RANGE)
            player.getPackets().sendGameMessage(
                    "<col=7E2217>The power of the crystal improves your Ranged prowess, at the expense of your Defence, Strength and Magical ability.");
        else if (skill == Skills.MAGIC)
            player.getPackets().sendGameMessage(
                    "<col=7E2217>The power of the crystal improves your Magic prowess, at the expense of your Defence, Strength and Ranged ability.");
        else if (skill == Skills.STRENGTH)
            player.getPackets().sendGameMessage(
                    "<col=7E2217>The power of the crystal improves your Strength prowess, at the expense of your Defence, Ranged and Magical ability.");
        CoresManager.getFastExecutor().schedule(new TimerTask() {

            private int count;

            @Override
            public void run() {
                try {
                    if (count++ == 7 || !(player.getControlerManager().getControler() instanceof FightKiln)) {
                        player.temporaryAttribute().remove("FightKilnCrystal");
                        player.getPackets().sendGameMessage("<col=7E2217>The power of the crystal dwindles and your "
                                + Skills.SKILL_NAME[skill] + " prowess returns to normal.");
                        player.getSkills().set(Skills.DEFENCE, player.getSkills().getRealLevel(Skills.DEFENCE));
                        player.getSkills().set(Skills.STRENGTH, player.getSkills().getRealLevel(Skills.STRENGTH));
                        player.getSkills().set(Skills.RANGE, player.getSkills().getRealLevel(Skills.RANGE));
                        player.getSkills().set(Skills.MAGIC, player.getSkills().getRealLevel(Skills.MAGIC));
                        cancel();
                    } else {
                        for (int i = 1; i < 7; i++) {
                            if (i == skill || i == 3 || i == 5)
                                continue;
                            player.getSkills().set(i, player.getSkills().getRealLevel(i) / 2);
                        }
                        player.getSkills().set(skill, (int) (player.getSkills().getRealLevel(skill) * 1.5));
                    }
                } catch (Throwable e) {
                    Logger.handle(e);
                }
            }
        }, 3000, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean processNPCClick1(NPC npc) {
        if (npc.getId() == 15195 && getCurrentWave() == 0) {
            player.getDialogueManager().startDialogue("TokHaarHok", 0, TOKHAAR_HOK, this);
            return false;
        }
        return true;

    }

    @Override
    public void process() {
        if (harAken != null)
            harAken.process();
    }

    public void hideHarAken() {
        if (stage != Stages.RUNNING)
            return;
        harAken.resetTimer();
        harAken.setCantInteract(true);
        harAken.animate(new Animation(16234));
        harAken.removeTentacles();
        CoresManager.getFastExecutor().schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    if (stage != Stages.RUNNING)
                        return;
                    harAken.finish();
                } catch (Throwable e) {
                    Logger.handle(e);
                }
            }
        }, 3000, TimeUnit.MILLISECONDS);
    }

    private void handleVictoryReward() {

        if (!player.isCompletedFightKiln()) {

            player.getPackets().sendGameMessage(
                    "<col=ff0000>Congratulations, you have completed a completionist cape requirement;");
            player.getPackets().sendGameMessage(
                    "<col=ff0000>Complete the Fight kiln");

        }

        player.setCompletedFightKiln();

        player.getPackets().sendGameMessage("You were victorious!!");

        Integer reward = (Integer) player.temporaryAttribute().get("FightKilnReward");

        int itemId = reward != null && reward == 1 ? 6571 : 23659;

        if (player.getInventory().hasFreeSlots())
            player.getInventory().addItem(itemId, 1);
        else {
            player.getBank().addItem(itemId, 1, true);
            player.getPackets().sendGameMessage(
                    "Don't have enough inventory space for reward, reward sent to bank");
        }

        player.setAvalonPoints(player.getAvalonPoints() + 100000);

        player.getPackets().sendGameMessage(
                "You gain 100,000 " + Settings.SERVER_NAME + " points for completing fight kiln.");

        World.sendWorldMessage(
                "<img=7><col=36648b>News: " + player.getDisplayName() +
                        " has completed the fight kiln!", false);

        player.reset();
    }

    private void destroyInstance() {

        final int[] chunks = boundChuncks;

        boundChuncks = null;

        if (chunks == null)
            return;

        CoresManager.getSlowExecutor().schedule(() -> {
            try {
                MapBuilder.destroyMap(chunks[0], chunks[1], 8, 8);
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, 1200, TimeUnit.MILLISECONDS);
    }

    private void removeCrystals() {

        player.getInventory().removeItems(
                new Item(23653, Integer.MAX_VALUE),
                new Item(23654, Integer.MAX_VALUE),
                new Item(23655, Integer.MAX_VALUE),
                new Item(23656, Integer.MAX_VALUE),
                new Item(23657, Integer.MAX_VALUE),
                new Item(23658, Integer.MAX_VALUE)
        );
    }
}
