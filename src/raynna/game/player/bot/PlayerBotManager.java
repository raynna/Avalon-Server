package raynna.game.player.bot;

import raynna.core.cache.defintions.ItemDefinitions;
import raynna.core.networking.Session;
import raynna.game.Animation;
import raynna.game.ForceTalk;
import raynna.game.World;
import raynna.game.WorldTile;
import raynna.game.item.Item;
import raynna.game.player.Player;
import raynna.game.player.Skills;
import raynna.game.player.Equipment;
import raynna.game.player.actions.PlayerFollow;
import raynna.game.player.controllers.WildernessController;
import raynna.game.player.prayer.AncientPrayer;
import raynna.game.player.prayer.NormalPrayer;
import raynna.game.player.prayer.Prayer;
import raynna.game.player.prayer.PrayerBook;
import raynna.game.route.RouteFinder;
import raynna.game.route.strategy.FixedTileStrategy;
import raynna.util.IsaacKeyPair;
import raynna.util.MachineInformation;
import raynna.util.Utils;
import raynna.game.player.bot.BotPresetCatalog;
import raynna.game.player.combat.CombatAction;
import raynna.game.player.combat.magic.RuneRequirement;
import raynna.game.player.combat.magic.Spellbook;
import raynna.game.player.combat.magic.lunar.spells.VengeanceService;
import raynna.game.player.combat.range.RangeData;
import raynna.game.world.area.Area;
import raynna.game.world.area.AreaManager;
import raynna.game.world.pvp.PvpManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class PlayerBotManager {

    private static final MachineInformation BOT_MACHINE_INFO =
            new MachineInformation(1, true, 0, 0, 17, 0, 0, false, 512, 2, 4096, 2400, 0, 0, 0);

    private static final Map<String, ManagedBot> BOTS = new LinkedHashMap<>();
    private static final int ANCIENT_STAFF_ID = Item.getId("item.ancient_staff");
    private static final int DRAGON_SCIMITAR_ID = Item.getId("item.dragon_scimitar");
    private static final int BERSERKER_HELM_ID = Item.getId("item.berserker_helm");
    private static final int RUNE_BOOTS_ID = Item.getId("item.rune_boots");
    private static final int ADAMANT_GLOVES_ID = Item.getId("item.adamant_gloves");
    private static final int RUNE_GLOVES_ID = Item.getId("item.rune_gloves");
    private static final int BARROWS_GLOVES_ID = Item.getId("item.barrows_gloves");
    private static final int AVA_ACCUMULATOR_ID = Item.getId("item.ava_s_accumulator");
    private static final int DRAGON_CLAWS_ID = Item.getId("item.dragon_claws");
    private static final int DRAGON_DAGGER_ID = 5698;
    private static final int DRAGON_MACE_ID = 1434;
    private static final int GRANITE_MAUL_ID = 4153;
    private static final int ARMADYL_GODSWORD_ID = 11694;
    private static final int BANDOS_GODSWORD_ID = 11696;
    private static final int CHAOTIC_MAUL_ID = 18353;
    private static final int KORASI_ID = 19784;
    private static final int MAX_TARGET_DISTANCE = 18;
    private static final int MAX_ROUTE_STEPS = 48;
    private static final int BOT_HEAVY_AI_BUCKETS = 8;
    private static final String[] NAME_PREFIXES = {
            "Risky", "Silent", "Lucky", "Swift", "Vast", "Noble", "Feral", "Icy", "Final", "Prime",
            "Shadow", "Fatal", "Clever", "Wild", "Brave", "Ghost", "Rapid", "Night", "Solar", "Rune"
    };
    private static final String[] NAME_ROOTS = {
            "Arrow", "Vex", "Nova", "Ash", "Flint", "Vale", "Storm", "Hex", "Drift", "Steel",
            "Rogue", "Fang", "Bloom", "Skies", "Myth", "Blitz", "Talon", "Blaze", "Frost", "Crest"
    };
    private static final String[] NAME_SUFFIXES = {
            "x", "z", "jr", "qt", "pk", "xo", "tv", "age", "rs", "gg",
            "x1", "v2", "ftw", "ish", "live", "ishh", "up", "fy", "eh", "zz"
    };

    private static boolean initialized;
    private static int nextBotId = 1;
    private static int tickCounter = 0;
    private static volatile String lastProfileSummary = "";

    private PlayerBotManager() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
    }

    public static synchronized List<Player> spawnBattleBots(int count, WorldTile center, int radius) {
        return spawnBattleBots(count, center, radius, null);
    }

    public static synchronized List<Player> spawnBattleBots(int count, WorldTile center, int radius, String selector) {
        init();
        List<Player> spawned = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            BotArchetype archetype = BotArchetype.random(selector);
            String name = randomBotName();
            Player bot = createBot(name, center, radius, archetype);
            spawned.add(bot);
        }
        return spawned;
    }

    public static String listArchetypes() {
        return BotArchetype.listSelectors();
    }

    private static String randomBotName() {
        for (int attempt = 0; attempt < 40; attempt++) {
            StringBuilder builder = new StringBuilder();
            if (Utils.random(3) != 0) {
                builder.append(NAME_PREFIXES[Utils.random(NAME_PREFIXES.length)]);
            }
            builder.append(NAME_ROOTS[Utils.random(NAME_ROOTS.length)]);
            if (Utils.random(2) == 0) {
                builder.append(NAME_SUFFIXES[Utils.random(NAME_SUFFIXES.length)]);
            } else if (Utils.random(3) == 0) {
                builder.append(Utils.random(10, 99));
            }
            String candidate = builder.toString();
            if (!BOTS.containsKey(candidate.toLowerCase())) {
                return candidate;
            }
        }
        return "Player" + nextBotId++;
    }

    public static synchronized int clearBots() {
        int count = BOTS.size();
        for (ManagedBot bot : new ArrayList<>(BOTS.values())) {
            bot.player.forceLogout();
            bot.player.finish();
        }
        BOTS.clear();
        return count;
    }

    public static synchronized Collection<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (ManagedBot bot : BOTS.values()) {
            players.add(bot.player);
        }
        return players;
    }

    public static synchronized boolean isManagedBot(Player player) {
        return getBot(player) != null;
    }

    public static String consumeLastProfileSummary() {
        String summary = lastProfileSummary;
        lastProfileSummary = "";
        return summary;
    }

    public static synchronized String status() {
        if (BOTS.isEmpty()) {
            return "No active player bots.";
        }
        long fighting = BOTS.values().stream()
                .filter(bot -> bot.player.getActionManager().getAction() instanceof CombatAction)
                .count();
        long inSafe = BOTS.values().stream()
                .filter(bot -> isSafe(bot.player))
                .count();
        return "Player bots: " + BOTS.size() + ", fighting: " + fighting + ", still in safezone: " + inSafe + ".";
    }

    public static synchronized boolean handleChatInteraction(Player requester, String rawMessage) {
        if (requester == null || rawMessage == null || rawMessage.isBlank()) {
            return false;
        }
        String lower = rawMessage.toLowerCase();
        ManagedBot conversationalBot = getBotConversationTarget(requester);
        if (conversationalBot != null) {
            if (!looksLikeFightRequest(lower)) {
                respond(conversationalBot.player, randomChatReply(conversationalBot));
                return true;
            }
            return handleFightRequest(requester, conversationalBot);
        }
        for (ManagedBot bot : BOTS.values()) {
            String botName = bot.player.getDisplayName().toLowerCase();
            if (!lower.contains(botName)) {
                continue;
            }
            if (!looksLikeFightRequest(lower)) {
                respond(bot.player, randomChatReply(bot));
                return true;
            }
            return handleFightRequest(requester, bot);
        }
        return false;
    }

    public static synchronized void setConversationTarget(Player requester, Player target) {
        if (requester == null) {
            return;
        }
        ManagedBot bot = getBot(target);
        if (bot == null) {
            requester.temporaryAttribute().remove("bot_conversation_target");
            return;
        }
        requester.temporaryAttribute().put("bot_conversation_target", bot.key());
    }

    private static boolean handleFightRequest(Player requester, ManagedBot bot) {
        if (requester == null || requester.isDead() || requester.hasFinished()) {
            return true;
        }
        if (bot.player.isDead() || bot.player.hasFinished()) {
            respond(bot.player, "Not now.");
            return true;
        }
        if (bot.player.getActionManager().getAction() instanceof CombatAction || getEngagedTarget(bot) != null) {
            respond(bot.player, "I'm currently busy.");
            return true;
        }
        if (!canArrangeFight(bot, requester)) {
            respond(bot.player, "We can't fight, we're different levels.");
            return true;
        }
        bot.challengeTargetName = requester.getUsername();
        bot.challengeTicks = 180;
        bot.targetFocusTicks = 0;
        respond(bot.player, Utils.random(2) == 0 ? "Sure!" : "Yeah, let's fight.");
        return true;
    }

    private static boolean looksLikeFightRequest(String message) {
        return message.contains("fight") || message.contains("1v1") || message.contains("pk")
                || message.contains("wanna") || message.contains("want to");
    }

    private static String randomChatReply(ManagedBot bot) {
        return switch (bot.archetype) {
            case PURE_NH, GHOSTLY_NH, HYBRID_PURE -> "Ask me for a fight.";
            case DHAROK_MAIN, DHAROK_MED -> "Meet me in danger.";
            default -> "Busy roaming. Ask if you want a fight.";
        };
    }

    private static Player createBot(String username, WorldTile center, int radius, BotArchetype archetype) {
        Player bot = new Player("bot");
        Session session = new Session(null);
        session.setEncoder(2, bot);
        session.setDecoder(3, bot);
        bot.init(session, username, 2, 765, 503, BOT_MACHINE_INFO, new IsaacKeyPair(new int[]{1, 2, 3, 4}));
        bot.recievedStarter = true;
        ManagedBot managed = new ManagedBot(bot, new WorldTile(center), new WorldTile(center), Math.max(4, radius), archetype);
        managed.stageCenter = findDangerousStage(randomTile(center, Math.max(2, radius)), managed.stageDirectionBias);
        BOTS.put(username.toLowerCase(), managed);
        bot.start();
        bot.setRun(true);
        bot.setRunHidden(false);
        resetBot(managed, true);
        return bot;
    }

    public static synchronized void processTick() {
        if (BOTS.isEmpty()) {
            lastProfileSummary = "";
            return;
        }
        tickCounter++;
        List<Player> worldPlayers = new ArrayList<>(World.getPlayers());
        BotTickProfile profile = new BotTickProfile(tickCounter, BOTS.size());

        for (ManagedBot bot : new ArrayList<>(BOTS.values())) {
            if (bot.player == null || bot.player.hasFinished()) {
                BOTS.remove(bot.key());
                continue;
            }
            processBot(bot, worldPlayers, tickCounter, profile);
        }
        lastProfileSummary = profile.render();
    }

    private static void processBot(ManagedBot bot, List<Player> worldPlayers, int tick, BotTickProfile profile) {
        Player player = bot.player;
        long botStart = System.nanoTime();
        boolean heavyTick = shouldRunHeavyAi(bot, tick);
        profile.processedBots++;
        if (heavyTick) {
            profile.heavyBots++;
        }

        if (player.isDead()) {
            bot.state = BotState.RESPAWNING;
            profile.respawningBots++;
            profile.recordBot(bot, System.nanoTime() - botStart, "dead");
            return;
        }

        if (bot.state == BotState.RESPAWNING) {
            long start = System.nanoTime();
            resetBot(bot, false);
            profile.resetMs += System.nanoTime() - start;
            profile.respawningBots++;
            profile.recordBot(bot, System.nanoTime() - botStart, "reset");
            return;
        }

        long upkeepStart = System.nanoTime();
        long cooldownStart = System.nanoTime();
        if (bot.foodCooldownTicks > 0) {
            bot.foodCooldownTicks--;
        }
        if (bot.specCooldownTicks > 0) {
            bot.specCooldownTicks--;
        }
        if (bot.buffCooldownTicks > 0) {
            bot.buffCooldownTicks--;
        }
        if (bot.prayerSwitchTicks > 0) {
            bot.prayerSwitchTicks--;
        }
        if (bot.offensivePrayerTicks > 0) {
            bot.offensivePrayerTicks--;
        }
        if (bot.potionCooldownTicks > 0) {
            bot.potionCooldownTicks--;
        }
        if (bot.restockTicks > 0) {
            bot.restockTicks--;
        }
        if (bot.targetSearchCooldownTicks > 0) {
            bot.targetSearchCooldownTicks--;
        }
        if (bot.routeCooldownTicks > 0) {
            bot.routeCooldownTicks--;
        }
        if (!player.getRun()) {
            player.setRun(true);
        }
        if (player.getRunEnergy() < 100) {
            player.setRunEnergy(100);
        }
        profile.cooldownMs += System.nanoTime() - cooldownStart;
        long movementStart = System.nanoTime();
        trackMovement(bot);
        profile.movementMs += System.nanoTime() - movementStart;
        profile.upkeepMs += System.nanoTime() - upkeepStart;

        if (shouldRestock(bot)) {
            long start = System.nanoTime();
            handleRestock(bot);
            profile.restockMs += System.nanoTime() - start;
            profile.restockingBots++;
            profile.recordBot(bot, System.nanoTime() - botStart, "restock");
            return;
        }

        long supportStart = System.nanoTime();
        ensurePvpController(player);
        maybeConsumeSupplies(bot);
        trackPlayerAggressor(bot);
        profile.supportMs += System.nanoTime() - supportStart;

        if (bot.challengeTicks > 0) {
            bot.challengeTicks--;
        }

        long challengeLookupStart = System.nanoTime();
        Player challengeTarget = getChallengeTarget(bot, worldPlayers);
        profile.challengeLookupMs += System.nanoTime() - challengeLookupStart;
        if (challengeTarget != null) {
            long start = System.nanoTime();
            if (processChallenge(bot, challengeTarget)) {
                profile.challengeMs += System.nanoTime() - start;
                profile.challengeBots++;
                profile.recordBot(bot, System.nanoTime() - botStart, "challenge");
                return;
            }
            profile.challengeMs += System.nanoTime() - start;
        }

        long directAggressorStart = System.nanoTime();
        Player directAggressor = getDirectAggressor(bot);
        profile.aggressorLookupMs += System.nanoTime() - directAggressorStart;
        if (directAggressor != null) {
            bot.state = BotState.FIGHTING;
            long start = System.nanoTime();
            updateTargetFocus(bot, directAggressor);
            maybeSwitchCombatMode(bot, directAggressor);
            maybeSwitchProtectionPrayer(bot, directAggressor);
            maybeMaintainSoulSplit(bot, directAggressor);
            maybeSwitchOffensivePrayer(bot);
            maybeCastVengeance(bot, directAggressor);
            maybeUseSpecial(bot, directAggressor);
            player.stopAll(true, false, true);
            bot.lastPathTarget = null;
            bot.roamTarget = null;
            if (!(player.getActionManager().getAction() instanceof CombatAction) || player.getTemporaryTarget() != directAggressor) {
                player.getActionManager().setAction(new CombatAction(directAggressor));
            }
            profile.combatMs += System.nanoTime() - start;
            profile.directAggressorBots++;
            profile.recordBot(bot, System.nanoTime() - botStart, "retaliate");
            return;
        }

        long engagedStart = System.nanoTime();
        Player engagedTarget = getEngagedTarget(bot);
        profile.engagedLookupMs += System.nanoTime() - engagedStart;
        if (engagedTarget != null) {
            bot.state = BotState.FIGHTING;
            long start = System.nanoTime();
            updateTargetFocus(bot, engagedTarget);
            maybeSwitchCombatMode(bot, engagedTarget);
            maybeSwitchProtectionPrayer(bot, engagedTarget);
            maybeMaintainSoulSplit(bot, engagedTarget);
            maybeSwitchOffensivePrayer(bot);
            maybeCastVengeance(bot, engagedTarget);
            maybeUseSpecial(bot, engagedTarget);
            player.stopAll(true, false, true);
            bot.lastPathTarget = null;
            bot.roamTarget = null;
            if (!(player.getActionManager().getAction() instanceof CombatAction) || player.getTemporaryTarget() != engagedTarget) {
                player.getActionManager().setAction(new CombatAction(engagedTarget));
            }
            profile.combatMs += System.nanoTime() - start;
            profile.engagedBots++;
            profile.recordBot(bot, System.nanoTime() - botStart, "engaged");
            return;
        }

        if (isSafe(player) || !player.isCanPvp() || !isDangerous(player)) {
            bot.state = BotState.LEAVING_SAFEZONE;
            long start = System.nanoTime();
            walkToDanger(bot);
            profile.safeExitMs += System.nanoTime() - start;
            profile.safeExitBots++;
            profile.recordBot(bot, System.nanoTime() - botStart, "safe-exit");
            return;
        }

        if (!isNearCenter(player, bot.stageCenter, bot.radius + 18)) {
            bot.state = BotState.RETURNING;
            long start = System.nanoTime();
            walkTowards(bot, bot.stageCenter);
            profile.returnMs += System.nanoTime() - start;
            profile.returningBots++;
            profile.recordBot(bot, System.nanoTime() - botStart, "return");
            return;
        }

        if (player.getHitpoints() < player.getMaxHitpoints() * 0.60 && bot.foodCooldownTicks == 0) {
            player.heal(250);
            bot.foodCooldownTicks = 8;
        }

        maybeCastVengeance(bot, null);

        long targetSearchStart = System.nanoTime();
        Player target = findTarget(bot, worldPlayers, heavyTick);
        profile.targetSearchMs += System.nanoTime() - targetSearchStart;
        if (target == null) {
            if (player.getActionManager().getAction() instanceof CombatAction) {
                player.getActionManager().forceStop();
                player.resetWalkSteps();
            }
            bot.state = BotState.ROAMING;
            long start = System.nanoTime();
            roam(bot);
            profile.roamMs += System.nanoTime() - start;
            profile.roamingBots++;
            profile.recordBot(bot, System.nanoTime() - botStart, "roam");
            return;
        }

        bot.state = BotState.FIGHTING;
        long start = System.nanoTime();
        updateTargetFocus(bot, target);

        maybeSwitchCombatMode(bot, target);
        maybeSwitchProtectionPrayer(bot, target);
        maybeMaintainSoulSplit(bot, target);
        maybeSwitchOffensivePrayer(bot);
        maybeCastVengeance(bot, target);
        maybeUseSpecial(bot, target);

        player.stopAll(true, false, true);
        bot.lastPathTarget = null;
        bot.roamTarget = null;
        if (!(player.getActionManager().getAction() instanceof CombatAction) || player.getTemporaryTarget() != target) {
            player.getActionManager().setAction(new CombatAction(target));
        }
        profile.combatMs += System.nanoTime() - start;
        profile.targetedBots++;
        profile.recordBot(bot, System.nanoTime() - botStart, "target");
    }

    private static void ensurePvpController(Player player) {
        if (player.isAtWild() && !(player.getControlerManager().getControler() instanceof WildernessController)) {
            player.getControlerManager().startControler("WildernessControler");
        }
    }

    private static void resetBot(ManagedBot bot, boolean freshSpawn) {
        Player player = bot.player;
        player.dead = false;
        player.reset();
        player.unlock();
        player.setRun(true);
        player.setRunHidden(false);
        player.getPrayer().reset();
        player.getCombatDefinitions().resetSpells(true);
        player.getCombatDefinitions().resetSpecialAttack();
        player.resetWalkSteps();
        player.animate(new Animation(-1));
        player.getActionManager().forceStop();
        ensurePvpController(player);

        BotArchetype archetype = freshSpawn ? bot.archetype : BotArchetype.random();
        bot.archetype = archetype;
        bot.currentMode = archetype.defaultMode();
        bot.kit = archetype.randomKit();
        WorldTile safeAnchor = !freshSpawn && isSafe(player) ? new WorldTile(player) : bot.spawnCenter;
        bot.stageDirectionBias = Utils.random(0, 3);
        bot.stageCenter = findDangerousStage(randomTile(safeAnchor, Math.max(2, bot.radius + 4)), bot.stageDirectionBias);

        BotPresetCatalog.BotPresetSelection presetSelection = BotPresetCatalog.pickSelection(archetype.shortName);
        bot.dynamicOverrides = items(presetSelection.getEquipped());
        bot.dynamicInventoryOverrides = items(presetSelection.getInventory());
        bot.selectedSpecWeaponId = chooseSpecWeaponId(archetype);
        refillSupplies(bot);
        applyArchetype(bot, true);
        WorldTile respawnTile;
        if (freshSpawn) {
            respawnTile = findInitialSpawnTile(bot);
            if (respawnTile != null && !respawnTile.matches(player)) {
                player.setNextWorldTile(respawnTile);
            }
        } else if (isSafe(player)) {
            respawnTile = new WorldTile(player);
        } else {
            respawnTile = findRelocationTile(bot, true);
            player.setNextWorldTile(respawnTile);
        }
        World.updateEntityRegion(player);
        player.loadMapRegions();
        player.getControlerManager().moved();
        AreaManager.INSTANCE.onMoved(player);
        PvpManager.onMoved(player);
        player.checkMultiArea();
        player.setNextFaceEntity(null);
        player.setTemporaryTarget(null);
        player.setAttackedBy(null);
        player.setAttackedByDelay(0);
        player.setHitpoints(player.getMaxHitpoints());
        player.refreshHitPoints();
        player.getPackets().sendPlayerOption("Follow", 2, false);
        player.getPackets().sendPlayerOption("Trade", 4, false);

        bot.state = isSafe(respawnTile) ? BotState.LEAVING_SAFEZONE : BotState.ROAMING;
        bot.safeWanderTicks = 0;
        bot.safePauseTicks = 0;
        bot.roamTicks = Utils.random(1, 3);
        bot.roamPauseTicks = Utils.random(4, 8);
        bot.roamTarget = null;
        bot.wideWander = Utils.random(7) == 0;
        bot.wanderRadius = bot.wideWander ? Utils.random(36, 96) : Utils.random(8, Math.max(10, bot.radius + 6));
        bot.switchTicks = Utils.random(4, 9);
        bot.foodCooldownTicks = 0;
        bot.specCooldownTicks = 0;
        bot.buffCooldownTicks = 2;
        bot.prayerSwitchTicks = Utils.random(1, 4);
        bot.offensivePrayerTicks = Utils.random(1, 4);
        bot.potionCooldownTicks = 0;
        bot.restockTicks = 0;
        bot.respawnTicks = 0;
        bot.lastTargetName = null;
        bot.targetFocusTicks = 0;
    }

    private static void applyArchetype(ManagedBot bot, boolean rebuildInventory) {
        Player player = bot.player;
        BotArchetype archetype = bot.archetype;
        BotCombatMode mode = bot.currentMode;
        LoadoutItem[] kitOverrides = bot.kit != null ? bot.kit.overrides : new LoadoutItem[0];

        if (rebuildInventory) {
            player.getInventory().reset();
            for (int skill = 0; skill < 25; skill++) {
                int targetLevel = archetype.levelFor(skill);
                player.getSkills().setXp(skill, Skills.getXPForLevel(targetLevel));
            }
            player.getSkills().restoreSkills();
            for (LoadoutItem item : collectInventory(bot)) {
                if (item.valid()) {
                    player.getInventory().addItem(item.id, item.amount);
                }
            }
        }
        player.getEquipment().reset();

        for (LoadoutItem item : mode.equipment) {
            equipIfWearable(player, item);
        }

        for (LoadoutItem item : kitOverrides) {
            equipIfWearable(player, item);
        }

        if (bot.dynamicOverrides != null) {
            for (LoadoutItem item : bot.dynamicOverrides) {
                equipIfWearable(player, item);
            }
        }

        applySelectedWeaponOverrides(bot);

        ensureCoreEquipment(bot);

        player.getEquipment().refresh();
        player.getInventory().refresh();
        player.getPrayer().setPrayerBook(mode.curses);
        player.getPrayer().restorePrayer(player.getSkills().getRealLevel(Skills.PRAYER) * 10);
        if (!bot.archetype.isNhBuild()) {
            player.getPrayer().closeProtectionPrayers();
        }
        player.getCombatDefinitions().setSpellBook(mode.spellbook);
        int attackStyle = chooseAttackStyleIndex(player, mode);
        player.getCombatDefinitions().setAttackStyle(attackStyle);
        if (mode.autoCastSpell > 0) {
            player.getCombatDefinitions().setAutoCastSpell(mode.autoCastSpell);
        } else {
            player.getCombatDefinitions().resetSpells(true);
        }
        player.getAppearance().generateAppearenceData();
    }

    private static int chooseAttackStyleIndex(Player player, BotCombatMode mode) {
        if (mode == null || mode.isMagicStyle()) {
            return 0;
        }
        return 1;
    }

    private static void equipIfWearable(Player player, LoadoutItem item) {
        if (!item.valid()) {
            return;
        }
        ItemDefinitions definitions = ItemDefinitions.getItemDefinitions(item.id);
        if (definitions == null || !meetsWearRequirements(player, definitions)) {
            return;
        }
        int slot = definitions.getEquipSlot();
        if (slot >= 0) {
            if (slot == Equipment.SLOT_WEAPON && definitions.getEquipType() == 5) {
                player.getEquipment().getItems().set(Equipment.SLOT_SHIELD, null);
            } else if (slot == Equipment.SLOT_SHIELD) {
                Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
                if (weapon != null && Equipment.isTwoHandedWeapon(weapon)) {
                    return;
                }
            }
            player.getEquipment().getItems().set(slot, new Item(item.id, item.amount));
        }
    }

    private static boolean meetsWearRequirements(Player player, ItemDefinitions definitions) {
        Map<Integer, Integer> requirements = definitions.getWearingSkillRequiriments();
        if (requirements == null || requirements.isEmpty()) {
            return true;
        }
        for (Map.Entry<Integer, Integer> entry : requirements.entrySet()) {
            Integer skill = entry.getKey();
            Integer level = entry.getValue();
            if (skill == null || level == null) {
                continue;
            }
            if (skill < 0 || skill >= 25) {
                continue;
            }
            if (player.getSkills().getRealLevel(skill) < level) {
                return false;
            }
        }
        return true;
    }

    private static void ensureCoreEquipment(ManagedBot bot) {
        Player player = bot.player;
        BotCombatMode mode = bot.currentMode;
        if (mode == null) {
            return;
        }

        if (player.getEquipment().getItem(Equipment.SLOT_WEAPON) == null) {
            int fallbackWeapon = fallbackWeaponId(player, mode);
            if (fallbackWeapon > 0) {
                equipIfWearable(player, new LoadoutItem(fallbackWeapon, 1));
            }
        }

        Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
        if (weapon != null
                && !Equipment.isTwoHandedWeapon(weapon)
                && player.getEquipment().getItem(Equipment.SLOT_SHIELD) == null) {
            int fallbackShield = fallbackShieldId(player, mode);
            if (fallbackShield > 0) {
                equipIfWearable(player, new LoadoutItem(fallbackShield, 1));
            }
        }

        if (player.getEquipment().getItem(Equipment.SLOT_HEAD) == null) {
            int fallbackHead = fallbackHeadId(player, mode);
            if (fallbackHead > 0) {
                equipIfWearable(player, new LoadoutItem(fallbackHead, 1));
            }
        }

        if (player.getEquipment().getItem(Equipment.SLOT_FEET) == null) {
            int fallbackFeet = fallbackFeetId(player, mode);
            if (fallbackFeet > 0) {
                equipIfWearable(player, new LoadoutItem(fallbackFeet, 1));
            }
        }

        if (player.getEquipment().getItem(Equipment.SLOT_HANDS) == null) {
            int fallbackHands = fallbackHandsId(player);
            if (fallbackHands > 0) {
                equipIfWearable(player, new LoadoutItem(fallbackHands, 1));
            }
        }

        ensureModeWeaponry(player, mode);
    }

    private static void ensureModeWeaponry(Player player, BotCombatMode mode) {
        Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
        if (mode.isMagicStyle()) {
            if (weapon == null || !isMagicWeapon(weapon.getId())) {
                int fallbackWeapon = fallbackWeaponId(player, mode);
                if (fallbackWeapon > 0) {
                    equipIfWearable(player, new LoadoutItem(fallbackWeapon, 1));
                }
            }
            return;
        }

        if (mode.isRangeStyle()) {
            if (weapon == null || RangeData.Companion.getWeaponByItemId(weapon.getId()) == null) {
                int fallbackWeapon = fallbackWeaponId(player, mode);
                if (fallbackWeapon > 0) {
                    equipIfWearable(player, new LoadoutItem(fallbackWeapon, 1));
                }
            }
            int fallbackAmmo = fallbackAmmoId(player);
            if (fallbackAmmo > 0) {
                Item ammo = player.getEquipment().getItem(Equipment.SLOT_ARROWS);
                if (ammo == null || ammo.getId() != fallbackAmmo) {
                    player.getEquipment().getItems().set(Equipment.SLOT_ARROWS, new Item(fallbackAmmo, 250));
                }
            }
            return;
        }

        if (weapon == null || RangeData.Companion.getWeaponByItemId(weapon.getId()) != null || isMagicWeapon(weapon.getId())) {
            int fallbackWeapon = fallbackWeaponId(player, mode);
            if (fallbackWeapon > 0) {
                equipIfWearable(player, new LoadoutItem(fallbackWeapon, 1));
            }
        }
    }

    private static int fallbackAmmoId(Player player) {
        int weaponId = player.getEquipment().getWeaponId();
        if (weaponId == 9185) {
            return 9244;
        }
        if (weaponId == 861) {
            return 892;
        }
        if (weaponId == 11235) {
            return 11212;
        }
        return -1;
    }

    private static boolean isMagicWeapon(int itemId) {
        if (itemId <= 0) {
            return false;
        }
        String name = ItemDefinitions.getItemDefinitions(itemId).getName().toLowerCase();
        return name.contains("staff") || name.contains("wand") || name.contains("battlestaff");
    }

    private static int fallbackWeaponId(Player player, BotCombatMode mode) {
        if (mode.isMagicStyle()) {
            if (player.getSkills().getRealLevel(Skills.ATTACK) >= 50 && player.getSkills().getRealLevel(Skills.DEFENCE) <= 1) {
                return ANCIENT_STAFF_ID;
            }
            return player.getSkills().getRealLevel(Skills.MAGIC) >= 75 ? 15486 : 1393;
        }
        if (mode.isRangeStyle()) {
            if (player.getSkills().getRealLevel(Skills.RANGE) >= 65) {
                return 9185;
            }
            return 861;
        }
        int attack = player.getSkills().getRealLevel(Skills.ATTACK);
        int defence = player.getSkills().getRealLevel(Skills.DEFENCE);
        if (attack >= 80 && defence >= 80) {
            return 18349;
        }
        if (attack >= 70) {
            return 4151;
        }
        if (attack >= 60) {
            return DRAGON_SCIMITAR_ID;
        }
        return attack >= 40 ? 1434 : -1;
    }

    private static int fallbackShieldId(Player player, BotCombatMode mode) {
        int defence = player.getSkills().getRealLevel(Skills.DEFENCE);
        if (mode.isMeleeStyle() && defence >= 40) {
            return 8850;
        }
        if (defence >= 40) {
            return 1201;
        }
        return 1540;
    }

    private static int fallbackHeadId(Player player, BotCombatMode mode) {
        int defence = player.getSkills().getRealLevel(Skills.DEFENCE);
        if (mode.isMagicStyle()) {
            if (defence <= 1) {
                return 2413;
            }
            return defence >= 45 ? 10828 : 2413;
        }
        if (mode.isRangeStyle()) {
            if (defence <= 1) {
                return 1169;
            }
            return defence >= 45 ? 10828 : 1169;
        }
        if (defence == 45) {
            return BERSERKER_HELM_ID;
        }
        if (defence >= 55) {
            return 10828;
        }
        if (defence >= 40) {
            return 1159;
        }
        return 1159;
    }

    private static int fallbackFeetId(Player player, BotCombatMode mode) {
        int defence = player.getSkills().getRealLevel(Skills.DEFENCE);
        if (mode.isMagicStyle()) {
            if (defence <= 1) {
                return 6106;
            }
            return defence >= 40 ? 6920 : 6106;
        }
        if (mode.isRangeStyle()) {
            if (defence <= 1) {
                return 2579;
            }
            return defence >= 40 ? RUNE_BOOTS_ID : 2579;
        }
        if (defence == 45) {
            return RUNE_BOOTS_ID;
        }
        if (defence >= 40) {
            return 11732;
        }
        return 4129;
    }

    private static int fallbackHandsId(Player player) {
        int defence = player.getSkills().getRealLevel(Skills.DEFENCE);
        if (defence <= 1) {
            return ADAMANT_GLOVES_ID;
        }
        if (defence >= 70) {
            return BARROWS_GLOVES_ID;
        }
        return RUNE_GLOVES_ID;
    }

    private static void applySelectedWeaponOverrides(ManagedBot bot) {
        if (bot.currentMode == null || bot.selectedSpecWeaponId <= 0 || !bot.currentMode.useSpecial) {
            return;
        }
        equipIfWearable(bot.player, new LoadoutItem(bot.selectedSpecWeaponId, 1));
    }

    private static int chooseSpecWeaponId(BotArchetype archetype) {
        return switch (archetype) {
            case PURE_NH, GHOSTLY_NH -> Utils.random(2) == 0 ? DRAGON_DAGGER_ID : DRAGON_CLAWS_ID;
            case HYBRID_PURE, ZERKER_DDS, DHAROK_MAIN, DHAROK_MED -> DRAGON_DAGGER_ID;
            case ZERKER_GMAUL -> GRANITE_MAUL_ID;
            case ZERKER_KORASI -> KORASI_ID;
            case RUNE_MAIN -> switch (Utils.random(3)) {
                case 0 -> ARMADYL_GODSWORD_ID;
                case 1 -> BANDOS_GODSWORD_ID;
                default -> CHAOTIC_MAUL_ID;
            };
            case MAIN_HYBRID -> switch (Utils.random(3)) {
                case 0 -> DRAGON_DAGGER_ID;
                case 1 -> DRAGON_MACE_ID;
                default -> ARMADYL_GODSWORD_ID;
            };
            case AGS_MAIN -> ARMADYL_GODSWORD_ID;
            case BGS_MAIN -> BANDOS_GODSWORD_ID;
            default -> -1;
        };
    }

    private static boolean isSpecialWeaponItem(int itemId) {
        return itemId == DRAGON_DAGGER_ID
                || itemId == DRAGON_CLAWS_ID
                || itemId == DRAGON_MACE_ID
                || itemId == GRANITE_MAUL_ID
                || itemId == ARMADYL_GODSWORD_ID
                || itemId == BANDOS_GODSWORD_ID
                || itemId == CHAOTIC_MAUL_ID
                || itemId == KORASI_ID;
    }

    private static void maybeSwitchCombatMode(ManagedBot bot, Player target) {
        if (bot.currentMode == null) {
            bot.currentMode = bot.archetype.defaultMode();
        }

        if (--bot.switchTicks > 0 && bot.lastTargetName != null && bot.lastTargetName.equalsIgnoreCase(target.getUsername())) {
            return;
        }

        BotCombatMode desired = bot.archetype.chooseMode(bot.player, target, bot.currentMode);
        bot.switchTicks = Utils.random(4, 9);

        if (desired == bot.currentMode) {
            return;
        }

        bot.player.getActionManager().forceStop();
        bot.currentMode = desired;
        applyArchetype(bot, false);
    }

    private static void maybeCastVengeance(ManagedBot bot, Player target) {
        if (bot.currentMode == null || !bot.currentMode.useVengeance) {
            return;
        }
        if (bot.buffCooldownTicks > 0 || bot.player.hasVengeance()) {
            return;
        }
        if (target == null && Utils.random(3) != 0) {
            return;
        }
        if (target != null && Utils.getDistance(bot.player, target) > 10) {
            return;
        }
        if (VengeanceService.INSTANCE.cast(bot.player)) {
            bot.buffCooldownTicks = 10;
        }
    }

    private static void maybeUseSpecial(ManagedBot bot, Player target) {
        if (bot.currentMode == null || !bot.currentMode.useSpecial) {
            return;
        }
        if (bot.specCooldownTicks > 0) {
            return;
        }
        if (bot.player.getCombatDefinitions().isUsingSpecialAttack()) {
            return;
        }
        if (bot.player.getCombatDefinitions().getSpecialAttackPercentage() < bot.currentMode.specialThreshold) {
            return;
        }
        if (Utils.getDistance(bot.player, target) > bot.currentMode.specialMaxDistance) {
            return;
        }
        boolean koWindow = isKoOpportunity(bot, target);
        if (!koWindow) {
            if (bot.targetFocusTicks < Utils.random(3, 7)) {
                return;
            }
            if (target.getHitpoints() > target.getMaxHitpoints() * 0.55 || Utils.random(3) != 0) {
                return;
            }
        }
        if (target.getHitpoints() > target.getMaxHitpoints() * 0.75 && Utils.random(2) == 0) {
            return;
        }
        bot.player.getCombatDefinitions().switchUsingSpecialAttack();
        bot.specCooldownTicks = koWindow ? Utils.random(10, 16) : Utils.random(14, 22);
        bot.targetFocusTicks = 0;
    }

    private static void maybeSwitchProtectionPrayer(ManagedBot bot, Player target) {
        if (bot.currentMode == null || bot.prayerSwitchTicks > 0) {
            return;
        }
        if (!bot.archetype.isNhBuild()) {
            return;
        }

        Prayer desired = detectProtectionPrayer(bot, target);
        if (desired == null) {
            if (hasActiveProtectionPrayer(bot.player)) {
                bot.prayerSwitchTicks = Utils.random(1, 3);
                return;
            }
            desired = fallbackProtectionPrayer(bot, target);
            if (desired == null) {
                return;
            }
        }

        PrayerBook prayer = bot.player.getPrayer();
        if (prayer.isActive(desired)) {
            bot.prayerSwitchTicks = Utils.random(1, 4);
            return;
        }

        boolean urgentSwap = target != null &&
                (bot.recentAggressorName != null && bot.recentAggressorName.equalsIgnoreCase(target.getUsername())
                        || bot.player.getAttackedBy() == target);

        if (!urgentSwap && hasActiveProtectionPrayer(bot.player) && Utils.random(5) == 0) {
            bot.prayerSwitchTicks = Utils.random(2, 6);
            return;
        }

        prayer.closeProtectionPrayers();
        prayer.switchPrayer(desired);
        bot.prayerSwitchTicks = urgentSwap ? 1 : Utils.random(2, 5);
    }

    private static Prayer fallbackProtectionPrayer(ManagedBot bot, Player target) {
        if (target == null) {
            return null;
        }
        ProtectionStyle style = Utils.getDistance(bot.player, target) <= 1 ? ProtectionStyle.MELEE : Utils.random(2) == 0
                ? ProtectionStyle.MAGIC : ProtectionStyle.RANGE;
        return switch (style) {
            case MELEE -> bot.player.getPrayer().isAncientCurses() ? AncientPrayer.DEFLECT_MELEE : NormalPrayer.PROTECT_FROM_MELEE;
            case RANGE -> bot.player.getPrayer().isAncientCurses() ? AncientPrayer.DEFLECT_MISSILES : NormalPrayer.PROTECT_FROM_MISSILES;
            case MAGIC -> bot.player.getPrayer().isAncientCurses() ? AncientPrayer.DEFLECT_MAGIC : NormalPrayer.PROTECT_FROM_MAGIC;
        };
    }

    private static boolean hasActiveProtectionPrayer(Player player) {
        return player.getPrayer().isMageProtecting() ||
                player.getPrayer().isRangeProtecting() ||
                player.getPrayer().isMeleeProtecting();
    }

    private static void maybeSwitchOffensivePrayer(ManagedBot bot) {
        if (bot.currentMode == null || bot.offensivePrayerTicks > 0) {
            return;
        }

        Prayer desired = chooseOffensivePrayer(bot);
        if (desired == null) {
            return;
        }

        PrayerBook prayer = bot.player.getPrayer();
        if (prayer.isActive(desired)) {
            bot.offensivePrayerTicks = Utils.random(3, 6);
            return;
        }

        if (Utils.random(6) == 0) {
            bot.offensivePrayerTicks = Utils.random(3, 7);
            return;
        }

        prayer.switchPrayer(desired);
        bot.offensivePrayerTicks = Utils.random(3, 6);
    }

    private static void maybeMaintainSoulSplit(ManagedBot bot, Player target) {
        if (bot.currentMode == null || target == null || bot.offensivePrayerTicks > 0) {
            return;
        }
        if (!bot.archetype.usesSoulSplit() || !bot.player.getPrayer().isAncientCurses() || bot.archetype.isNhBuild()) {
            return;
        }
        if (bot.player.getSkills().getRealLevel(Skills.PRAYER) < 92) {
            return;
        }
        PrayerBook prayer = bot.player.getPrayer();
        if (prayer.isActive(AncientPrayer.SOUL_SPLIT)) {
            return;
        }
        prayer.switchPrayer(AncientPrayer.SOUL_SPLIT);
        bot.offensivePrayerTicks = Utils.random(3, 6);
    }

    private static void updateTargetFocus(ManagedBot bot, Player target) {
        String username = target.getUsername();
        if (bot.lastTargetName != null && bot.lastTargetName.equalsIgnoreCase(username)) {
            bot.targetFocusTicks++;
        } else {
            bot.targetFocusTicks = 1;
        }
        bot.lastTargetName = username;
    }

    private static void maybeConsumeSupplies(ManagedBot bot) {
        maybeEat(bot);
        maybeDrinkRestore(bot);
        maybeDrinkCombatPotion(bot);
    }

    private static void maybeEat(ManagedBot bot) {
        Player player = bot.player;
        if (bot.foodLeft <= 0 || bot.foodCooldownTicks > 0 || player.isFoodLocked()) {
            return;
        }

        double healthRatio = player.getHitpoints() / (double) player.getMaxHitpoints();
        if (healthRatio > 0.68 && !(bot.state == BotState.FIGHTING && healthRatio < 0.78 && Utils.random(4) == 0)) {
            return;
        }

        int healAmount = player.getActionManager().getAction() instanceof CombatAction ? 180 : 220;
        player.animate(new Animation(829));
        player.addFoodLock(3);
        player.getActionManager().setActionDelay(player.getActionManager().getActionDelay() + 3);
        player.heal(healAmount);
        bot.foodLeft--;
        bot.foodCooldownTicks = healthRatio < 0.35 ? 2 : 5;
        refreshSupplies(bot);
    }

    private static void maybeDrinkRestore(ManagedBot bot) {
        Player player = bot.player;
        if (bot.restoreLeft <= 0 || bot.potionCooldownTicks > 0) {
            return;
        }

        int maxPrayer = player.getSkills().getRealLevel(Skills.PRAYER) * 10;
        boolean lowPrayer = player.getPrayer().getPrayerPoints() < Math.max(120, maxPrayer / 4);
        boolean drainedCombat = player.getSkills().getLevel(Skills.ATTACK) < player.getSkills().getRealLevel(Skills.ATTACK) ||
                player.getSkills().getLevel(Skills.STRENGTH) < player.getSkills().getRealLevel(Skills.STRENGTH) ||
                player.getSkills().getLevel(Skills.RANGE) < player.getSkills().getRealLevel(Skills.RANGE) ||
                player.getSkills().getLevel(Skills.MAGIC) < player.getSkills().getRealLevel(Skills.MAGIC);
        if (!lowPrayer && !drainedCombat) {
            return;
        }

        player.getPrayer().restorePrayer(Math.max(200, maxPrayer / 2));
        player.getSkills().restoreSkills();
        bot.restoreLeft--;
        bot.potionCooldownTicks = 6;
        refreshSupplies(bot);
    }

    private static void maybeDrinkCombatPotion(ManagedBot bot) {
        if (bot.boostPotionLeft <= 0 || bot.potionCooldownTicks > 0 || bot.currentMode == null) {
            return;
        }

        int boostedSkill = bot.currentMode.primaryBoostSkill;
        if (boostedSkill < 0) {
            return;
        }
        int current = bot.player.getSkills().getLevel(boostedSkill);
        int real = bot.player.getSkills().getRealLevel(boostedSkill);
        if (current > real + Math.max(2, real / 10)) {
            return;
        }

        bot.player.getSkills().boost(boostedSkill, Math.max(3, real / 5));
        if (bot.currentMode.secondaryBoostSkill >= 0) {
            int secondaryReal = bot.player.getSkills().getRealLevel(bot.currentMode.secondaryBoostSkill);
            bot.player.getSkills().boost(bot.currentMode.secondaryBoostSkill, Math.max(3, secondaryReal / 5));
        }
        bot.boostPotionLeft--;
        bot.potionCooldownTicks = 8;
        refreshSupplies(bot);
    }

    private static boolean shouldRestock(ManagedBot bot) {
        if (bot.restockTicks > 0 || bot.state == BotState.RESTOCKING) {
            return true;
        }
        Player player = bot.player;
        if (bot.foodLeft > 0) {
            return false;
        }
        if (player.getHitpoints() < player.getMaxHitpoints() * 0.70) {
            return true;
        }
        return bot.restoreLeft <= 0 && bot.boostPotionLeft <= 0;
    }

    private static boolean isCombatGraceActive(Player player) {
        long now = Utils.currentTimeMillis();
        return player.getAttackedByDelay() + 10_000L > now
                || player.isInCombat()
                || player.getActionManager().getAction() instanceof CombatAction;
    }

    private static boolean isKoOpportunity(ManagedBot bot, Player target) {
        return isKoOpportunity(bot.player, target, bot.currentMode);
    }

    private static boolean isKoOpportunity(Player player, Player target, BotCombatMode mode) {
        if (mode == null) {
            return false;
        }
        double hpRatio = target.getHitpoints() / (double) target.getMaxHitpoints();
        if (mode.isMeleeStyle()) {
            if (hpRatio <= 0.38) {
                return true;
            }
            return hpRatio <= 0.48 && !isProtectingAgainst(target, ProtectionStyle.MELEE);
        }
        if (mode.isRangeStyle()) {
            return hpRatio <= 0.34 && !isProtectingAgainst(target, ProtectionStyle.RANGE);
        }
        return hpRatio <= 0.30 && !isProtectingAgainst(target, ProtectionStyle.MAGIC);
    }

    private static Prayer detectProtectionPrayer(ManagedBot bot, Player target) {
        ProtectionStyle style = detectAttackStyle(target);
        if (style == null) {
            if (target.getTemporaryTarget() == bot.player && Utils.random(2) == 0) {
                style = ProtectionStyle.MELEE;
            } else {
                return null;
            }
        }
        return switch (style) {
            case MELEE -> bot.player.getPrayer().isAncientCurses() ? AncientPrayer.DEFLECT_MELEE : NormalPrayer.PROTECT_FROM_MELEE;
            case RANGE -> bot.player.getPrayer().isAncientCurses() ? AncientPrayer.DEFLECT_MISSILES : NormalPrayer.PROTECT_FROM_MISSILES;
            case MAGIC -> bot.player.getPrayer().isAncientCurses() ? AncientPrayer.DEFLECT_MAGIC : NormalPrayer.PROTECT_FROM_MAGIC;
        };
    }

    private static Prayer chooseOffensivePrayer(ManagedBot bot) {
        PrayerBook prayer = bot.player.getPrayer();
        int prayerLevel = bot.player.getSkills().getRealLevel(Skills.PRAYER);

        if (prayer.isAncientCurses()) {
            if (bot.archetype.usesSoulSplit()) {
                return bot.currentMode.isMeleeStyle() && prayerLevel >= 95 ? AncientPrayer.TURMOIL : null;
            }
            if (bot.currentMode.isMeleeStyle()) {
                return prayerLevel >= 95 ? AncientPrayer.TURMOIL : AncientPrayer.SAP_WARRIOR;
            }
            if (bot.currentMode.isRangeStyle()) {
                return prayerLevel >= 74 ? AncientPrayer.LEECH_RANGED : AncientPrayer.SAP_RANGER;
            }
            if (bot.currentMode.isMagicStyle()) {
                return prayerLevel >= 74 ? AncientPrayer.LEECH_MAGIC : AncientPrayer.SAP_MAGE;
            }
            return null;
        }

        if (bot.currentMode.isMeleeStyle()) {
            if (prayerLevel >= 70) {
                return NormalPrayer.PIETY;
            }
            if (prayerLevel >= 60) {
                return NormalPrayer.CHIVALRY;
            }
            if (prayerLevel >= 34) {
                return NormalPrayer.INCREDIBLE_REFLEXES;
            }
            return prayerLevel >= 31 ? NormalPrayer.ULTIMATE_STRENGTH : null;
        }
        if (bot.currentMode.isRangeStyle()) {
            if (prayerLevel >= 74) {
                return NormalPrayer.RIGOUR;
            }
            return prayerLevel >= 44 ? NormalPrayer.EAGLE_EYE : null;
        }
        if (bot.currentMode.isMagicStyle()) {
            if (prayerLevel >= 77) {
                return NormalPrayer.AUGURY;
            }
            return prayerLevel >= 45 ? NormalPrayer.MYSTIC_MIGHT : null;
        }
        return null;
    }

    private static boolean isProtectingAgainst(Player target, ProtectionStyle style) {
        return switch (style) {
            case MELEE -> target.getPrayer().isMeleeProtecting();
            case RANGE -> target.getPrayer().isRangeProtecting();
            case MAGIC -> target.getPrayer().isMageProtecting();
        };
    }

    private static ProtectionStyle detectAttackStyle(Player target) {
        if (target.getCombatDefinitions().getSpellId() > 0) {
            return ProtectionStyle.MAGIC;
        }
        if (RangeData.Companion.getWeaponByItemId(target.getEquipment().getWeaponId()) != null) {
            return ProtectionStyle.RANGE;
        }
        Item weapon = target.getEquipment().getItem(Equipment.SLOT_WEAPON);
        if (weapon == null) {
            return ProtectionStyle.MELEE;
        }
        String name = weapon.getDefinitions().getName().toLowerCase();
        if (name.contains("staff") || name.contains("wand") || name.contains("battlestaff")) {
            return ProtectionStyle.MAGIC;
        }
        if (name.contains("bow") || name.contains("crossbow") || name.contains("knife") || name.contains("dart")
                || name.contains("javelin") || name.contains("thrownaxe") || name.contains("chinchompa")) {
            return ProtectionStyle.RANGE;
        }
        return ProtectionStyle.MELEE;
    }

    private static void handleRestock(ManagedBot bot) {
        Player player = bot.player;
        bot.state = BotState.RESTOCKING;
        bot.lastTargetName = null;
        bot.targetFocusTicks = 0;
        bot.challengeTargetName = null;
        bot.challengeTicks = 0;
        player.setRun(true);
        player.setRunHidden(false);
        if (player.getActionManager().getAction() instanceof CombatAction) {
            player.getActionManager().forceStop();
        }
        player.resetWalkSteps();
        if (!isSafe(player)) {
            walkTowards(bot, randomSafeTile(bot.spawnCenter, Math.max(4, bot.radius + 6)));
            return;
        }
        if (isCombatGraceActive(player)) {
            bot.restockTicks = 0;
            return;
        }
        if (bot.restockTicks == 0) {
            bot.restockTicks = Utils.random(8, 16);
            bot.selectedSpecWeaponId = chooseSpecWeaponId(bot.archetype);
            if (bot.kit == null) {
                bot.kit = bot.archetype != null ? bot.archetype.randomKit() : null;
            }
            refillSupplies(bot);
            applyArchetype(bot, true);
            player.setHitpoints(player.getMaxHitpoints());
            player.refreshHitPoints();
            player.getPrayer().restorePrayer(player.getSkills().getRealLevel(Skills.PRAYER) * 10);
            player.getCombatDefinitions().resetSpecialAttack();
            bot.safeWanderTicks = 0;
            bot.safePauseTicks = 0;
            return;
        }
        if (bot.restockTicks <= 1) {
            bot.state = BotState.LEAVING_SAFEZONE;
        }
    }

    private static void roam(ManagedBot bot) {
        if (bot.player.hasWalkSteps()) {
            return;
        }
        if (bot.roamPauseTicks > 0) {
            bot.roamPauseTicks--;
            return;
        }
        if (bot.roamTarget == null || Utils.getDistance(bot.player, bot.roamTarget) <= 1) {
            bot.roamTarget = randomRoamTile(bot);
            bot.roamTicks = Utils.random(1, 3);
        }
        if (bot.roamTicks > 0) {
            bot.roamTicks--;
            walkTowards(bot, bot.roamTarget);
            return;
        }
        bot.roamPauseTicks = Utils.random(4, 9);
        bot.roamTarget = null;
    }

    private static void walkToDanger(ManagedBot bot) {
        if (bot.player.hasWalkSteps()) {
            return;
        }
        if (bot.routeCooldownTicks > 0) {
            return;
        }
        WorldTile target = bot.stageCenter;
        if (target == null || !isDangerous(target) || isSafe(target)) {
            rerollStage(bot);
            target = bot.stageCenter;
        }
        walkTowards(bot, target);
        if (bot.routeCooldownTicks <= 0) {
            bot.routeCooldownTicks = Utils.random(4, 8);
        }
    }

    private static void walkTowards(ManagedBot bot, WorldTile tile) {
        Player player = bot.player;
        if (tile == null) {
            return;
        }
        if (Utils.getDistance(player, tile) <= 1) {
            bot.lastPathTarget = tile;
            return;
        }
        if (bot.lastPathTarget != null &&
                bot.lastPathTarget.matches(tile) &&
                player.hasWalkSteps()) {
            return;
        }
        if (bot.routeCooldownTicks > 0 &&
                bot.lastPathTarget != null &&
                bot.lastPathTarget.matches(tile)) {
            return;
        }
        bot.lastPathTarget = tile;
        player.resetWalkSteps();
        boolean routed = routeTo(player, tile);
        if (routed) {
            bot.failedPathTicks = 0;
        } else {
            bot.failedPathTicks++;
            bot.lastPathTarget = null;
            if (bot.failedPathTicks >= 2) {
                bot.failedPathTicks = 0;
                if (bot.state == BotState.LEAVING_SAFEZONE) {
                    rerollStage(bot);
                } else if (isSafe(player) || !isDangerous(player)) {
                    player.resetWalkSteps();
                } else {
                    WorldTile repath = bot.stageCenter;
                    if (repath != null && !repath.matches(player)) {
                        walkTowards(bot, repath);
                    } else {
                        rerollStage(bot);
                        player.resetWalkSteps();
                    }
                }
                return;
            }
        }
        bot.routeCooldownTicks = Utils.random(2, 4);
    }

    private static Player findTarget(ManagedBot source, List<Player> worldPlayers, boolean heavyTick) {
        Player challenge = getChallengeTarget(source, worldPlayers);
        if (challenge != null && isValidCombatTarget(source, challenge, true)) {
            return challenge;
        }
        Player engaged = getEngagedTarget(source);
        if (engaged != null) {
            return engaged;
        }
        Player cached = getCachedTarget(source, worldPlayers);
        if (cached != null && isValidCombatTarget(source, cached, true)) {
            return cached;
        }
        if (!heavyTick) {
            return null;
        }
        if (source.targetSearchCooldownTicks > 0) {
            return null;
        }

        List<Player> candidates = new ArrayList<>();
        for (Player player : worldPlayers) {
            if (player == null || player == source.player || player.hasFinished() || player.isDead()) {
                continue;
            }
            if (player.getPlane() != source.player.getPlane()) {
                continue;
            }
            if (!isValidCombatTarget(source, player, true)) {
                continue;
            }
            if (!isNearCenter(player, source.stageCenter, source.radius + 16)) {
                continue;
            }
            if (!isReasonablyReachable(source.player, player)) {
                continue;
            }
            candidates.add(player);
            if (candidates.size() >= 12) {
                break;
            }
        }

        Player target = candidates.stream()
                .min(Comparator.comparingInt(player -> Utils.getDistance(source.player, player)))
                .orElse(null);
        source.cachedTargetName = target != null ? target.getUsername() : null;
        source.targetSearchCooldownTicks = target != null ? Utils.random(4, 8) : Utils.random(6, 10);
        return target;
    }

    private static Player getEngagedTarget(ManagedBot bot) {
        Player player = bot.player;
        if (player.getAttackedBy() instanceof Player attacker &&
                player.getAttackedByDelay() > Utils.currentTimeMillis() &&
                isValidCombatTarget(bot, attacker, false)) {
            return attacker;
        }
        if (player.getTemporaryTarget() instanceof Player target &&
                isValidCombatTarget(bot, target, false) &&
                isReasonablyReachable(player, target)) {
            return target;
        }
        return null;
    }

    private static Player getDirectAggressor(ManagedBot bot) {
        Player player = bot.player;
        if (player.getAttackedBy() instanceof Player attacker &&
                player.getAttackedByDelay() > Utils.currentTimeMillis() &&
                isValidAggressorTarget(bot, attacker)) {
            return attacker;
        }
        if (bot.recentAggressorTicks > 0 && bot.recentAggressorName != null) {
            Player recent = World.getPlayer(bot.recentAggressorName);
            if (isValidAggressorTarget(bot, recent)) {
                return recent;
            }
        }
        return null;
    }

    private static boolean isValidAggressorTarget(ManagedBot source, Player target) {
        Player self = source.player;
        if (target == null || target == self || target.hasFinished() || target.isDead()) {
            return false;
        }
        if (target.getPlane() != self.getPlane()) {
            return false;
        }
        if (!target.isCanPvp() || !isDangerous(target)) {
            return false;
        }
        if (!self.isCanPvp() || !isDangerous(self)) {
            return false;
        }
        if (!PvpManager.canPlayerAttack(self, target) || !PvpManager.canPlayerAttack(target, self)) {
            return false;
        }
        if (!self.getControlerManager().canAttack(target) || !target.getControlerManager().canAttack(self)) {
            return false;
        }
        return self.getControlerManager().canHit(target) && target.getControlerManager().canHit(self);
    }

    private static boolean isValidCombatTarget(ManagedBot source, Player target) {
        return isValidCombatTarget(source, target, false);
    }

    private static boolean isValidCombatTarget(ManagedBot source, Player target, boolean requireProjectileSight) {
        Player self = source.player;
        if (target == null || target == self || target.hasFinished() || target.isDead()) {
            return false;
        }
        ManagedBot targetBot = getBot(target);
        if (targetBot == null && !isApprovedRealPlayerTarget(source, target)) {
            return false;
        }
        if (targetBot != null && !source.archetype.canFight(targetBot.archetype)) {
            return false;
        }
        if (target.getPlane() != self.getPlane()) {
            return false;
        }
        if (isSafe(target) || !target.isCanPvp()) {
            return false;
        }
        if (!self.isCanPvp() || isSafe(self)) {
            return false;
        }
        if (isBusyWithOtherCombat(self, target)) {
            return false;
        }
        if (!PvpManager.canPlayerAttack(self, target) || !PvpManager.canPlayerAttack(target, self)) {
            return false;
        }
        if (!self.getControlerManager().canAttack(target)) {
            return false;
        }
        if (!target.getControlerManager().canAttack(self)) {
            return false;
        }
        if (!self.getControlerManager().canHit(target) || !target.getControlerManager().canHit(self)) {
            return false;
        }
        return !requireProjectileSight || hasInitialAttackSight(self, target);
    }

    private static boolean hasInitialAttackSight(Player self, Player target) {
        if (Utils.getDistance(self, target) <= 1) {
            return true;
        }
        return self.clipedProjectile(target, false) && target.clipedProjectile(self, false);
    }

    private static boolean isApprovedRealPlayerTarget(ManagedBot source, Player target) {
        if (source.challengeTargetName != null && source.challengeTargetName.equalsIgnoreCase(target.getUsername())) {
            return true;
        }
        Player self = source.player;
        return self.getTemporaryTarget() == target ||
                (self.getAttackedBy() == target && self.getAttackedByDelay() > Utils.currentTimeMillis());
    }

    private static void trackPlayerAggressor(ManagedBot bot) {
        Player player = bot.player;
        if (!(player.getAttackedBy() instanceof Player attacker)) {
            if (bot.recentAggressorTicks > 0) {
                bot.recentAggressorTicks--;
                if (bot.recentAggressorTicks <= 0) {
                    bot.recentAggressorName = null;
                }
            }
            return;
        }
        if (player.getAttackedByDelay() <= Utils.currentTimeMillis()) {
            if (bot.recentAggressorTicks > 0) {
                bot.recentAggressorTicks--;
                if (bot.recentAggressorTicks <= 0) {
                    bot.recentAggressorName = null;
                }
            }
            return;
        }
        if (attacker.hasFinished() || attacker.isDead() || attacker.getPlane() != player.getPlane()) {
            return;
        }
        bot.recentAggressorName = attacker.getUsername();
        bot.recentAggressorTicks = 12;
        bot.challengeTargetName = attacker.getUsername();
        bot.challengeTicks = Math.max(bot.challengeTicks, 80);
        if (player.getActionManager().getAction() instanceof CombatAction && player.getTemporaryTarget() instanceof Player target &&
                target != attacker && !isReasonablyReachable(player, target)) {
            player.getActionManager().forceStop();
            player.resetWalkSteps();
        }
        if (!(player.getActionManager().getAction() instanceof CombatAction) || player.getTemporaryTarget() != attacker) {
            player.stopAll(true, false, true);
            bot.lastPathTarget = null;
            bot.roamTarget = null;
        }
    }

    private static boolean canArrangeFight(ManagedBot source, Player target) {
        Player self = source.player;
        if (target == null || target == self || target.hasFinished() || target.isDead()) {
            return false;
        }
        if (target.getPlane() != self.getPlane()) {
            return false;
        }
        return Math.abs(baseCombatLevel(self) - baseCombatLevel(target)) <= 15;
    }

    private static int baseCombatLevel(Player player) {
        return player.getSkills().getCombatLevel();
    }

    private static ManagedBot getBot(Player player) {
        if (player == null) {
            return null;
        }
        return BOTS.get(player.getUsername().toLowerCase());
    }

    private static ManagedBot getBotConversationTarget(Player requester) {
        Object raw = requester.temporaryAttribute().get("bot_conversation_target");
        if (!(raw instanceof String key)) {
            return null;
        }
        ManagedBot bot = BOTS.get(key);
        if (bot == null || bot.player.hasFinished() || bot.player.isDead()) {
            requester.temporaryAttribute().remove("bot_conversation_target");
            return null;
        }
        if (bot.player.getPlane() != requester.getPlane() || Utils.getDistance(bot.player, requester) > 16) {
            return null;
        }
        return bot;
    }

    private static Player getChallengeTarget(ManagedBot bot, List<Player> worldPlayers) {
        if (bot.challengeTargetName == null || bot.challengeTicks <= 0) {
            bot.challengeTargetName = null;
            return null;
        }
        for (Player player : worldPlayers) {
            if (player != null && bot.challengeTargetName.equalsIgnoreCase(player.getUsername()) &&
                    !player.hasFinished() && !player.isDead()) {
                return player;
            }
        }
        bot.challengeTargetName = null;
        return null;
    }

    private static boolean processChallenge(ManagedBot bot, Player challenger) {
        Player player = bot.player;
        if (!canArrangeFight(bot, challenger)) {
            if (bot.challengeTicks == 119 || bot.challengeTicks % 20 == 0) {
                respond(player, "We can't fight, we're different levels.");
            }
            bot.challengeTargetName = null;
            bot.challengeTicks = 0;
            return false;
        }

        if (!isDangerous(player) || isSafe(player) || !isDangerous(challenger) || isSafe(challenger)) {
            bot.state = BotState.RETURNING;
            player.setRun(true);
            player.setRunHidden(false);
            player.stopAll(false);
            player.getActionManager().setAction(new PlayerFollow(challenger));
            return true;
        }

        if (!isValidCombatTarget(bot, challenger)) {
            respond(player, "I'm currently busy.");
            bot.challengeTargetName = null;
            bot.challengeTicks = 0;
            return false;
        }

        updateTargetFocus(bot, challenger);
        bot.state = BotState.FIGHTING;
        maybeSwitchCombatMode(bot, challenger);
        maybeSwitchProtectionPrayer(bot, challenger);
        maybeSwitchOffensivePrayer(bot);
        maybeCastVengeance(bot, challenger);
        maybeUseSpecial(bot, challenger);
        player.stopAll(true, false, true);
        if (!(player.getActionManager().getAction() instanceof CombatAction)) {
            player.getActionManager().setAction(new CombatAction(challenger));
        }
        return true;
    }

    private static void respond(Player bot, String text) {
        bot.setNextForceTalk(new ForceTalk(text));
    }

    private static boolean isBusyWithOtherCombat(Player self, Player target) {
        if (isMultiCombat(self, target)) {
            return false;
        }
        if (target.getTemporaryTarget() instanceof Player temporaryTarget && temporaryTarget != self) {
            return true;
        }
        if (target.getAttackedBy() instanceof Player attacker &&
                attacker != self &&
                target.getAttackedByDelay() > Utils.currentTimeMillis()) {
            return true;
        }
        return target.isInCombat() &&
                target.getAttackedBy() != null &&
                target.getAttackedBy() != self;
    }

    private static boolean isMultiCombat(Player self, Player target) {
        return World.isMultiArea(self) && World.isMultiArea(target);
    }

    private static boolean isDangerous(Player player) {
        return player.inPkingArea() || PvpManager.isInDangerous(player);
    }

    private static boolean isDangerous(WorldTile tile) {
        return !AreaManager.isInEnvironment(tile, Area.Environment.SAFEZONE)
                && !AreaManager.isInEnvironment(tile, Area.Environment.WILDERNESS_SAFE)
                && (World.isPvpArea(tile) || AreaManager.isInEnvironment(tile, Area.Environment.WILDERNESS));
    }

    private static boolean isSafe(Player player) {
        return WildernessController.isAtWildSafe(player) ||
                AreaManager.isInEnvironment(player, Area.Environment.SAFEZONE);
    }

    private static boolean isSafe(WorldTile tile) {
        return WildernessController.isAtWildSafe(tile) ||
                AreaManager.isInEnvironment(tile, Area.Environment.SAFEZONE);
    }

    private static boolean isNearCenter(Player player, WorldTile center, int radius) {
        return Math.abs(player.getX() - center.getX()) <= radius &&
                Math.abs(player.getY() - center.getY()) <= radius;
    }

    private static boolean isNearCenter(WorldTile tile, WorldTile center, int radius) {
        return Math.abs(tile.getX() - center.getX()) <= radius &&
                Math.abs(tile.getY() - center.getY()) <= radius;
    }

    private static WorldTile findDangerousStage(WorldTile center, int preferredDirection) {
        if (isDangerous(center) && !isSafe(center)) {
            return new WorldTile(center);
        }
        List<WorldTile> candidates = new ArrayList<>();
        int sampleRadius = 32;
        for (int i = 0; i < 40; i++) {
            WorldTile tile = randomTile(center, sampleRadius);
            if (isDangerous(tile) && !isSafe(tile)) {
                candidates.add(tile);
            }
        }
        for (int radius = 4; radius <= sampleRadius; radius += 8) {
            for (int step = 0; step < 8; step++) {
                double angle = (Math.PI * 2D * step) / 16D;
                int dx = (int) Math.round(Math.cos(angle) * radius);
                int dy = (int) Math.round(Math.sin(angle) * radius);
                WorldTile tile = offsetTile(center, dx, dy);
                if (isDangerous(tile) && !isSafe(tile)) {
                    candidates.add(tile);
                }
            }
        }
        if (candidates.isEmpty()) {
            for (int radius = 1; radius <= 32; radius += 2) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dy = -radius; dy <= radius; dy++) {
                        if (Math.abs(dx) != radius && Math.abs(dy) != radius) {
                            continue;
                        }
                        WorldTile tile = offsetTile(center, dx, dy);
                        if (isDangerous(tile) && !isSafe(tile)) {
                            candidates.add(tile);
                        }
                    }
                }
                if (candidates.size() >= 24 && radius >= 8) {
                    break;
                }
            }
        }
        if (candidates.isEmpty()) {
            return center;
        }

        List<WorldTile> distinctTiles = candidates.stream()
                .distinct()
                .toList();
        List<WorldTile> sectorTiles = filterPreferredDirection(center, distinctTiles, preferredDirection);
        List<WorldTile> pool = sectorTiles.isEmpty() ? distinctTiles : sectorTiles;
        List<WorldTile> bestTiles = pool.stream()
                .distinct()
                .sorted(Comparator
                        .comparingInt((WorldTile tile) -> scoreDangerousTile(center, tile))
                        .thenComparingInt(WorldTile::getX)
                        .thenComparingInt(WorldTile::getY))
                .limit(8)
                .toList();
        return bestTiles.get(Utils.random(bestTiles.size()));
    }

    private static List<WorldTile> filterPreferredDirection(WorldTile center, List<WorldTile> tiles, int preferredDirection) {
        List<WorldTile> filtered = new ArrayList<>();
        for (WorldTile tile : tiles) {
            int dx = tile.getX() - center.getX();
            int dy = tile.getY() - center.getY();
            int sector = Math.abs(dx) >= Math.abs(dy) ? (dx >= 0 ? 0 : 1) : (dy >= 0 ? 2 : 3);
            if (sector == preferredDirection) {
                filtered.add(tile);
            }
        }
        if (filtered.size() >= 6) {
            return filtered;
        }
        return List.of();
    }

    private static WorldTile randomDangerousTile(WorldTile center, int radius) {
        for (int i = 0; i < 24; i++) {
            WorldTile tile = randomTile(center, radius);
            if (isDangerous(tile) && !isSafe(tile)) {
                return tile;
            }
        }
        return center;
    }

    private static WorldTile randomRoamTile(ManagedBot bot) {
        WorldTile base = bot.wideWander ? bot.player : (Utils.random(2) == 0 ? bot.player : bot.stageCenter);
        int minRadius = bot.wideWander ? Math.max(10, bot.wanderRadius / 3) : 5;
        int maxRadius = bot.wideWander ? bot.wanderRadius : Math.max(8, bot.radius + 6);
        for (int i = 0; i < 36; i++) {
            int radius = Utils.random(minRadius, maxRadius);
            WorldTile tile = randomTile(base, radius);
            if (!isDangerous(tile) || isSafe(tile)) {
                continue;
            }
            if (!bot.wideWander && !isNearCenter(tile, bot.stageCenter, bot.radius + 16)) {
                continue;
            }
            if (bot.wideWander || isNearCenter(tile, bot.stageCenter, bot.radius + 16)) {
                return tile;
            }
        }
        return bot.wideWander
                ? randomDangerousTile(bot.player, Math.max(12, Math.min(bot.wanderRadius, 64)))
                : randomDangerousTile(bot.stageCenter, Math.max(8, bot.radius));
    }

    private static WorldTile randomSafeTile(WorldTile center, int radius) {
        for (int i = 0; i < 24; i++) {
            WorldTile tile = randomTile(center, radius);
            if (isSafe(tile)) {
                return tile;
            }
        }
        return center;
    }
    private static WorldTile randomTile(WorldTile center, int radius) {
        return offsetTile(center, Utils.random(-radius, radius), Utils.random(-radius, radius));
    }

    private static int scoreDangerousTile(WorldTile from, WorldTile tile) {
        return Math.abs(tile.getX() - from.getX()) + Math.abs(tile.getY() - from.getY());
    }

    private static boolean routeTo(Player player, WorldTile tile) {
        int steps = routeSteps(player.getX(), player.getY(), player.getPlane(), player.getSize(), tile);
        if (steps < 0) {
            return false;
        }
        int[] bufferX = RouteFinder.getLastPathBufferX();
        int[] bufferY = RouteFinder.getLastPathBufferY();
        for (int step = steps - 1; step >= 0; step--) {
            if (!player.addWalkSteps(bufferX[step], bufferY[step], 25, true)) {
                break;
            }
        }
        return true;
    }

    private static int routeSteps(int fromX, int fromY, int plane, int size, WorldTile tile) {
        if (tile == null || plane != tile.getPlane()) {
            return -1;
        }
        if (Math.abs(tile.getX() - fromX) > 63 || Math.abs(tile.getY() - fromY) > 63) {
            return -1;
        }
        try {
            return RouteFinder.findRoute(
                    RouteFinder.WALK_ROUTEFINDER,
                    fromX,
                    fromY,
                    plane,
                    size,
                    new FixedTileStrategy(tile.getX(), tile.getY()),
                    true
            );
        } catch (RuntimeException e) {
            return -1;
        }
    }

    private static boolean isReasonablyReachable(Player source, Player target) {
        if (target == null || source.getPlane() != target.getPlane()) {
            return false;
        }
        int distance = Utils.getDistance(source, target);
        if (distance <= 1) {
            return true;
        }
        if (distance > MAX_TARGET_DISTANCE) {
            return false;
        }
        return source.getMapRegionsIds().contains(target.getRegionId());
    }

    private static void trackMovement(ManagedBot bot) {
        Player player = bot.player;
        if (shouldForceRescue(bot) && needsRelocation(bot)) {
            relocateBot(bot, shouldRelocateToSafe(bot));
            return;
        }
        if (player.getX() == bot.lastX && player.getY() == bot.lastY) {
            if (player.hasWalkSteps()) {
                bot.stuckTicks++;
            } else if (bot.lastPathTarget != null) {
                bot.failedPathTicks++;
            } else {
                bot.stuckTicks = 0;
            }
        } else {
            bot.stuckTicks = 0;
            bot.failedPathTicks = 0;
            bot.lastX = player.getX();
            bot.lastY = player.getY();
        }

        if (hasMeaningfulMovement(bot)) {
            bot.rescueAnchorX = player.getX();
            bot.rescueAnchorY = player.getY();
            bot.rescueTicks = 0;
        } else {
            bot.rescueTicks++;
        }

        if (bot.stuckTicks < 4 && bot.failedPathTicks < 3) {
            return;
        }

        if (hasVeryLimitedMobility(bot)) {
            if (shouldForceRescue(bot)) {
                relocateBot(bot, shouldRelocateToSafe(bot));
                return;
            }
            rerollStage(bot);
            player.resetWalkSteps();
            bot.stuckTicks = 0;
            bot.failedPathTicks = 0;
            return;
        }

        bot.stuckTicks = 0;
        bot.failedPathTicks = 0;
        player.resetWalkSteps();
        WorldTile repath = bot.lastPathTarget != null ? bot.lastPathTarget : bot.stageCenter;
        if (isSafe(player) || !isDangerous(player)) {
            if (bot.state == BotState.LEAVING_SAFEZONE) {
                rerollStage(bot);
                return;
            }
            walkTowards(bot, bot.stageCenter);
        } else {
            walkTowards(bot, repath);
        }
    }

    private static void rerollStage(ManagedBot bot) {
        if (bot.routeCooldownTicks > 0 && bot.stageCenter != null) {
            return;
        }
        WorldTile anchor = isSafe(bot.player) ? new WorldTile(bot.player) : bot.spawnCenter;
        bot.stageDirectionBias = (bot.stageDirectionBias + Utils.random(1, 3)) % 4;
        bot.stageCenter = findDangerousStage(randomTile(anchor, Math.max(4, bot.radius + 6)), bot.stageDirectionBias);
        bot.routeCooldownTicks = Utils.random(6, 10);
    }

    private static boolean needsRelocation(ManagedBot bot) {
        Player player = bot.player;
        return !World.isTileFree(player.getPlane(), player.getX(), player.getY(), player.getSize());
    }

    private static boolean hasMeaningfulMovement(ManagedBot bot) {
        Player player = bot.player;
        return Math.abs(player.getX() - bot.rescueAnchorX) > 2 || Math.abs(player.getY() - bot.rescueAnchorY) > 2;
    }

    private static boolean shouldForceRescue(ManagedBot bot) {
        return bot.rescueTicks >= 20;
    }

    private static boolean hasVeryLimitedMobility(ManagedBot bot) {
        Player player = bot.player;
        int freeTiles = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                if (World.isTileFree(player.getPlane(), player.getX() + dx, player.getY() + dy, player.getSize())) {
                    freeTiles++;
                }
            }
        }
        return freeTiles <= 2;
    }

    private static void relocateBot(ManagedBot bot, boolean preferSafe) {
        Player player = bot.player;
        preferSafe = preferSafe || shouldRelocateToSafe(bot);
        player.resetWalkSteps();
        player.getActionManager().forceStop();
        WorldTile relocation = findRelocationTile(bot, preferSafe);
        if (relocation == null || relocation.matches(player) || Utils.getDistance(player, relocation) <= 2) {
            bot.lastPathTarget = null;
            bot.roamTarget = null;
            return;
        }
        player.setNextWorldTile(relocation);
        bot.lastX = relocation.getX();
        bot.lastY = relocation.getY();
        bot.rescueAnchorX = relocation.getX();
        bot.rescueAnchorY = relocation.getY();
        bot.rescueTicks = 0;
        bot.stuckTicks = 0;
        bot.failedPathTicks = 0;
        bot.lastPathTarget = null;
        bot.roamTarget = null;
    }

    private static boolean shouldRelocateToSafe(ManagedBot bot) {
        Player player = bot.player;
        return bot.state == BotState.LEAVING_SAFEZONE
                || bot.state == BotState.RESTOCKING
                || isSafe(player)
                || !isDangerous(player)
                || isNearCenter(player, bot.spawnCenter, bot.radius + 12);
    }

    private static WorldTile findInitialSpawnTile(ManagedBot bot) {
        WorldTile anchor = bot.spawnCenter;
        boolean wantSafe = isSafe(anchor);
        boolean wantDanger = isDangerous(anchor) && !wantSafe;
        for (int i = 0; i < 48; i++) {
            WorldTile tile = World.getRandomFreeTileAround(anchor, 0, Math.max(3, bot.radius), bot.player.getSize(), 8);
            if (!World.isTileFree(tile.getPlane(), tile.getX(), tile.getY(), bot.player.getSize())) {
                continue;
            }
            if (wantSafe && !isSafe(tile)) {
                continue;
            }
            if (wantDanger && (!isDangerous(tile) || isSafe(tile))) {
                continue;
            }
            return tile;
        }
        if (World.isTileFree(anchor.getPlane(), anchor.getX(), anchor.getY(), bot.player.getSize())) {
            return new WorldTile(anchor);
        }
        return World.getRandomFreeTileAround(anchor, 0, Math.max(4, bot.radius + 2), bot.player.getSize(), 16);
    }

    private static WorldTile findRelocationTile(ManagedBot bot, boolean preferSafe) {
        WorldTile anchor = preferSafe ? bot.spawnCenter : bot.stageCenter;
        int minRadius = preferSafe ? 1 : 2;
        int maxRadius = preferSafe ? Math.max(4, bot.radius + 4) : Math.max(6, bot.radius + 8);
        for (int i = 0; i < 48; i++) {
            WorldTile tile = World.getRandomFreeTileAround(anchor, minRadius, maxRadius, bot.player.getSize(), 8);
            if (!World.isTileFree(tile.getPlane(), tile.getX(), tile.getY(), bot.player.getSize())) {
                continue;
            }
            if (preferSafe) {
                if (isSafe(tile)) {
                    return tile;
                }
            } else if (isDangerous(tile) && !isSafe(tile)) {
                return tile;
            }
        }
        if (preferSafe) {
            if (isSafe(bot.spawnCenter) && World.isTileFree(bot.spawnCenter.getPlane(), bot.spawnCenter.getX(), bot.spawnCenter.getY(), bot.player.getSize())) {
                return new WorldTile(bot.spawnCenter);
            }
            return randomSafeTile(bot.spawnCenter, Math.max(8, bot.radius + 10));
        }
        for (int i = 0; i < 24; i++) {
            WorldTile tile = randomDangerousTile(bot.stageCenter, Math.max(6, bot.radius + 8));
            if (tile != null && isDangerous(tile) && !isSafe(tile)
                    && World.isTileFree(tile.getPlane(), tile.getX(), tile.getY(), bot.player.getSize())) {
                return tile;
            }
        }
        if (isDangerous(bot.stageCenter) && !isSafe(bot.stageCenter)
                && World.isTileFree(bot.stageCenter.getPlane(), bot.stageCenter.getX(), bot.stageCenter.getY(), bot.player.getSize())) {
            return new WorldTile(bot.stageCenter);
        }
        return null;
    }

    private static boolean shouldRunHeavyAi(ManagedBot bot, int tick) {
        if (bot.player.getActionManager().getAction() instanceof CombatAction) {
            return true;
        }
        if (bot.challengeTargetName != null && bot.challengeTicks > 0) {
            return true;
        }
        return (tick + bot.aiOffset) % BOT_HEAVY_AI_BUCKETS == 0;
    }

    private static Player getCachedTarget(ManagedBot bot, List<Player> worldPlayers) {
        if (bot.cachedTargetName == null) {
            return null;
        }
        for (Player player : worldPlayers) {
            if (player != null && bot.cachedTargetName.equalsIgnoreCase(player.getUsername())) {
                return player;
            }
        }
        bot.cachedTargetName = null;
        return null;
    }

    private static WorldTile offsetTile(WorldTile center, int dx, int dy) {
        return new WorldTile(center.getX() + dx, center.getY() + dy, center.getPlane());
    }

    private static int scoreCombatMode(Player self, Player target, BotCombatMode mode, BotCombatMode current) {
        int distance = Utils.getDistance(self, target);
        int score = current == mode ? 1 : 0;
        boolean targetFrozen = target.isFrozen();
        boolean koWindow = isKoOpportunity(self, target, mode);
        boolean specialReady = self.getCombatDefinitions().getSpecialAttackPercentage() >= mode.specialThreshold;
        boolean targetProtectsMelee = isProtectingAgainst(target, ProtectionStyle.MELEE);
        boolean targetProtectsRange = isProtectingAgainst(target, ProtectionStyle.RANGE);
        boolean targetProtectsMagic = isProtectingAgainst(target, ProtectionStyle.MAGIC);

        if (mode.useSpecial) {
            if (specialReady && koWindow) {
                score += 6;
            } else if (!specialReady || target.getHitpoints() > target.getMaxHitpoints() * 0.55) {
                score -= 8;
            } else {
                score -= 3;
            }
        }

        if (mode.isMeleeStyle()) {
            if (self.isFrozen()) {
                return -10_000;
            }
            score += Math.max(0, 6 - distance);
            score += target.getSkills().getLevel(Skills.DEFENCE) <= 75 ? 3 : 0;
            score += target.getHitpoints() < target.getMaxHitpoints() * 0.45 ? 5 : 0;
            if (distance > 1 && !targetFrozen) {
                score -= 6;
            }
            if (targetFrozen) {
                score += 2;
            }
            if (targetProtectsMelee) {
                score -= 8;
            }
            return score;
        }

        if (mode.isMagicStyle()) {
            int magicDefence = target.getSkills().getLevel(Skills.MAGIC) + (target.getSkills().getLevel(Skills.DEFENCE) / 2);
            score += distance >= 4 ? 4 : 1;
            score += magicDefence <= 120 ? 5 : magicDefence <= 150 ? 2 : -1;
            score += targetFrozen ? 3 : 8;
            if (targetProtectsMagic) {
                score -= 9;
            } else {
                score += 5;
            }
            return score;
        }

        if (mode.isRangeStyle()) {
            int rangedDefence = target.getSkills().getLevel(Skills.DEFENCE) + (target.getSkills().getLevel(Skills.RANGE) / 3);
            score += distance >= 3 ? 4 : 1;
            score += rangedDefence <= 125 ? 4 : rangedDefence <= 155 ? 1 : -1;
            score += target.getHitpoints() < target.getMaxHitpoints() * 0.55 ? 2 : 0;
            score += targetFrozen ? 7 : 1;
            if (targetProtectsRange) {
                score -= 9;
            } else {
                score += 3;
            }
            return score;
        }

        return score;
    }

    private enum BotState {
        LEAVING_SAFEZONE,
        ROAMING,
        FIGHTING,
        RETURNING,
        RESTOCKING,
        RESPAWNING
    }

    private enum ProtectionStyle {
        MELEE,
        RANGE,
        MAGIC
    }

    private static final class ManagedBot {
        private final Player player;
        private final WorldTile spawnCenter;
        private WorldTile stageCenter;
        private final int radius;
        private BotArchetype archetype;
        private BotKit kit;
        private BotCombatMode currentMode;
        private BotState state;
        private int roamTicks;
        private int roamPauseTicks;
        private int switchTicks;
        private int respawnTicks;
        private int foodCooldownTicks;
        private int specCooldownTicks;
        private int buffCooldownTicks;
        private int safeWanderTicks;
        private int safePauseTicks;
        private int prayerSwitchTicks;
        private int offensivePrayerTicks;
        private int potionCooldownTicks;
        private int restockTicks;
        private int targetFocusTicks;
        private boolean wideWander;
        private int wanderRadius;
        private int foodLeft;
        private int restoreLeft;
        private int boostPotionLeft;
        private int foodItemId;
        private int restoreItemId;
        private int boostPotionItemId;
        private int challengeTicks;
        private String lastTargetName;
        private String challengeTargetName;
        private WorldTile lastPathTarget;
        private WorldTile roamTarget;
        private int lastX;
        private int lastY;
        private int stuckTicks;
        private int failedPathTicks;
        private int rescueAnchorX;
        private int rescueAnchorY;
        private int rescueTicks;
        private int aiOffset;
        private int stageDirectionBias;
        private int targetSearchCooldownTicks;
        private int routeCooldownTicks;
        private String cachedTargetName;
        private String recentAggressorName;
        private int recentAggressorTicks;
        private int selectedSpecWeaponId;
        private LoadoutItem[] dynamicOverrides;
        private LoadoutItem[] dynamicInventoryOverrides;

        private ManagedBot(Player player, WorldTile spawnCenter, WorldTile stageCenter, int radius, BotArchetype archetype) {
            this.player = player;
            this.spawnCenter = spawnCenter;
            this.stageCenter = stageCenter;
            this.radius = radius;
            this.archetype = archetype;
            this.lastX = player.getX();
            this.lastY = player.getY();
            this.rescueAnchorX = player.getX();
            this.rescueAnchorY = player.getY();
            this.aiOffset = Utils.random(0, 3);
            this.stageDirectionBias = Utils.random(0, 3);
        }

        private String key() {
            return player.getUsername().toLowerCase();
        }
    }

    private enum BotArchetype {
        RANGE_PURE(
                "rngpure",
                levelMap(1, 1, 1, 99, 99, 52, 94, 1),
                mode(
                        false, 0, 0, false, 100, 1, Skills.RANGE, -1,
                        new int[][]{
                                {10499, 1}, {6585, 1}, {6733, 1}, {1131, 1}, {2495, 1}, {2581, 1}, {861, 1}, {892, 250}
                        },
                        new int[][]{
                                {9185, 1}, {9244, 80}
                        }
                ),
                mode(
                        false, 0, 0, false, 100, 7, Skills.RANGE, -1,
                        new int[][]{
                                {10499, 1}, {6585, 1}, {6733, 1}, {1131, 1}, {2495, 1}, {2581, 1}, {9185, 1}, {9244, 250}
                        },
                        new int[][]{
                                {861, 1}, {892, 80}
                        }
                )
        ),
        DARKBOW_PURE(
                "dbpure",
                levelMap(1, 1, 1, 99, 99, 52, 94, 1),
                mode(
                        false, 0, 0, false, 100, 8, Skills.RANGE, -1,
                        new int[][]{
                                {10499, 1}, {6585, 1}, {6733, 1}, {2497, 1}, {2577, 1}, {2581, 1}, {11235, 1}, {11212, 250}
                        },
                        new int[][]{}
                )
        ),
        HYBRID_PURE(
                "hybpure",
                levelMap(60, 1, 70, 70, 70, 43, 70, 1),
                mode(
                        false, 1, 23, false, 100, 8, Skills.MAGIC, -1,
                        new int[][]{
                                {2413, 1}, {6109, 1}, {6585, 1}, {6107, 1}, {6108, 1}, {6110, 1}, {4675, 1}
                        },
                        new int[][]{
                                {9185, 1}, {9244, 160}, {560, 240}, {565, 120}, {555, 360}
                        }
                ),
                mode(
                        false, 0, 0, true, 25, 7, Skills.RANGE, -1,
                        new int[][]{
                                {10499, 1}, {6585, 1}, {6107, 1}, {6108, 1}, {6110, 1}, {9185, 1}, {9244, 220}
                        },
                        new int[][]{
                                {861, 1}, {892, 120}, {5698, 1}
                        }
                )
        ),
        PURE_NH(
                "nhpure",
                levelMap(60, 1, 99, 99, 99, 52, 94, 1),
                mode(
                        false, 1, 23, false, 100, 8, Skills.MAGIC, -1,
                        new int[][]{
                                {6109, 1}, {2412, 1}, {6585, 1}, {6731, 1}, {6107, 1},
                                {6108, 1}, {6110, 1}, {4675, 1}
                        },
                        new int[][]{
                                {9185, 1}, {9244, 250}, {560, 400}, {565, 200}, {555, 600}
                        }
                ),
                mode(
                        false, 0, 0, false, 100, 7, Skills.RANGE, -1,
                        new int[][]{
                                {AVA_ACCUMULATOR_ID, 1}, {6585, 1}, {6733, 1}, {2497, 1}, {2577, 1},
                                {ADAMANT_GLOVES_ID, 1}, {9185, 1}, {9244, 250}
                        },
                        new int[][]{
                                {560, 180}, {565, 90}, {555, 270}
                        }
                ),
                mode(
                        false, 0, 0, true, 25, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {6585, 1}, {6737, 1}, {4587, 1}, {1131, 1}, {2497, 1}, {2581, 1}, {ADAMANT_GLOVES_ID, 1}
                        },
                        new int[][]{
                                {9185, 1}, {9244, 100}, {560, 200}, {565, 100}, {555, 300}
                        }
                )
        ),
        GHOSTLY_NH(
                "ghostnh",
                levelMap(50, 1, 99, 99, 99, 52, 94, 1),
                mode(
                        false, 1, 23, false, 100, 8, Skills.MAGIC, -1,
                        new int[][]{
                                {6109, 1}, {2412, 1}, {6585, 1}, {6107, 1}, {6108, 1}, {6110, 1}, {4675, 1}
                        },
                        new int[][]{
                                {9185, 1}, {9244, 180}, {560, 400}, {565, 200}, {555, 600}
                        }
                ),
                mode(
                        false, 0, 0, false, 100, 7, Skills.RANGE, -1,
                        new int[][]{
                                {AVA_ACCUMULATOR_ID, 1}, {2412, 1}, {6585, 1}, {6107, 1}, {2497, 1}, {2577, 1},
                                {ADAMANT_GLOVES_ID, 1}, {9185, 1}, {9244, 250}
                        },
                        new int[][]{
                                {560, 180}, {565, 90}, {555, 270}
                        }
                ),
                mode(
                        false, 0, 0, true, 25, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {6109, 1}, {2412, 1}, {6585, 1}, {6107, 1}, {2497, 1}, {2581, 1}, {ADAMANT_GLOVES_ID, 1}
                        },
                        new int[][]{
                                {9185, 1}, {9244, 100}, {560, 180}, {565, 90}, {555, 270}
                        }
                )
        ),
        MAIN_HYBRID(
                "mainnh",
                levelMap(99, 99, 99, 99, 99, 99, 99, 99),
                mode(
                        true, 1, 23, false, 100, 8, Skills.MAGIC, -1,
                        new int[][]{
                                {2412, 1}, {4708, 1}, {18335, 1}, {6731, 1}, {6920, 1},
                                {7462, 1}, {4712, 1}, {4714, 1}, {6889, 1}, {15486, 1}
                        },
                        new int[][]{
                                {11694, 1}, {9185, 1}, {9244, 250}, {560, 400}, {565, 200}, {555, 600}, {7158, 1}
                        }
                ),
                mode(
                        false, 0, 0, true, 50, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {10828, 1}, {19748, 1}, {6585, 1}, {6737, 1}, {11732, 1}, {7462, 1},
                                {4736, 1}, {4720, 1}, {4722, 1}, {12681, 1}, {4151, 1}, {8850, 1}
                        },
                        new int[][]{
                                {560, 200}, {565, 100}, {555, 300}, {9185, 1}, {9244, 150}, {1434, 1}
                        }
                ),
                mode(
                        false, 0, 0, false, 100, 7, Skills.RANGE, -1,
                        new int[][]{
                                {10828, 1}, {10499, 1}, {6585, 1}, {6733, 1}, {11732, 1}, {7462, 1}, {2497, 1}, {2577, 1}, {2581, 1}, {9185, 1}, {9244, 250}
                        },
                        new int[][]{
                                {11694, 1}, {560, 200}, {565, 100}, {555, 300}, {861, 1}, {892, 150}
                        }
                ),
                mode(
                        false, 0, 0, false, 100, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {10828, 1}, {19748, 1}, {6585, 1}, {6737, 1}, {11732, 1}, {7462, 1},
                                {4736, 1}, {4720, 1}, {4722, 1}, {12681, 1}, {4151, 1}, {8850, 1}
                        },
                        new int[][]{
                                {1434, 1}, {11694, 1}
                        }
                ),
                mode(
                        false, 0, 0, true, 50, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {10828, 1}, {19748, 1}, {6585, 1}, {6737, 1}, {11732, 1}, {7462, 1},
                                {4736, 1}, {4720, 1}, {4722, 1}, {12681, 1}, {4151, 1}, {8850, 1}
                        },
                        new int[][]{
                                {11694, 1}, {560, 120}, {565, 60}, {555, 180}, {5698, 1}
                        }
                )
        ),
        SURGE_MAIN(
                "surgem",
                levelMap(99, 99, 99, 99, 99, 77, 99, 1),
                mode(
                        false, 0, 91, false, 100, 8, Skills.MAGIC, -1,
                        new int[][]{
                                {10828, 1}, {2413, 1}, {6585, 1}, {6731, 1}, {7462, 1},
                                {4101, 1}, {4712, 1}, {4714, 1}, {6889, 1}, {15486, 1}
                        },
                        new int[][]{
                                {565, 300}, {560, 600}, {554, 1000}, {556, 1000}
                        }
                )
        ),
        ZERKER_DDS(
                "zerkdds",
                levelMap(60, 45, 99, 99, 99, 95, 95, 1),
                mode(
                        true, 0, 0, false, 100, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {3751, 1}, {6585, 1}, {6737, 1}, {4131, 1}, {7462, 1},
                                {1127, 1}, {1079, 1}, {1201, 1}, {4587, 1}
                        },
                        new int[][]{
                                {5698, 1}
                        }
                ),
                mode(
                        true, 0, 0, true, 25, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {3751, 1}, {6585, 1}, {6737, 1}, {4131, 1}, {7462, 1},
                                {1127, 1}, {1079, 1}, {1201, 1}, {4587, 1}
                        },
                        new int[][]{
                                {5698, 1}
                        }
                )
        ),
        ZERKER_GMAUL(
                "zerkgm",
                levelMap(60, 45, 99, 99, 99, 95, 95, 1),
                mode(
                        true, 0, 0, false, 100, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {3751, 1}, {1725, 1}, {6737, 1}, {4131, 1}, {7462, 1},
                                {1127, 1}, {1079, 1}, {1201, 1}, {4587, 1}
                        },
                        new int[][]{
                                {4153, 1}
                        }
                ),
                mode(
                        true, 0, 0, true, 50, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {3751, 1}, {1725, 1}, {6737, 1}, {4131, 1}, {7462, 1},
                                {1127, 1}, {1079, 1}, {1201, 1}, {4587, 1}
                        },
                        new int[][]{
                                {4153, 1}
                        }
                )
        ),
        ZERKER_KORASI(
                "zerkkor",
                levelMap(78, 45, 99, 99, 99, 95, 95, 1),
                mode(
                        true, 0, 0, false, 100, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {3751, 1}, {1704, 1}, {6737, 1}, {4131, 1}, {7462, 1},
                                {1127, 1}, {1079, 1}, {1201, 1}, {4587, 1}
                        },
                        new int[][]{
                                {19784, 1}
                        }
                ),
                mode(
                        true, 0, 0, true, 60, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {3751, 1}, {1704, 1}, {6737, 1}, {4131, 1}, {7462, 1},
                                {1127, 1}, {1079, 1}, {1201, 1}, {19784, 1}
                        },
                        new int[][]{
                                {4587, 1}
                        }
                )
        ),
        RUNE_MAIN(
                "runemain",
                levelMap(80, 80, 99, 99, 99, 74, 95, 1),
                mode(
                        false, 0, 0, false, 100, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {10828, 1}, {1704, 1}, {6737, 1}, {11732, 1}, {7462, 1},
                                {1127, 1}, {1079, 1}, {8850, 1}, {18349, 1}
                        },
                        new int[][]{
                                {11694, 1}, {11696, 1}, {18353, 1}
                        }
                ),
                mode(
                        false, 0, 0, false, 100, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {10828, 1}, {6585, 1}, {6737, 1}, {11732, 1}, {7462, 1},
                                {1127, 1}, {1079, 1}, {6524, 1}, {4151, 1}
                        },
                        new int[][]{
                                {18349, 1}, {18353, 1}
                        }
                ),
                mode(
                        false, 0, 0, false, 100, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {1163, 1}, {1725, 1}, {6737, 1}, {11732, 1}, {7462, 1},
                                {1127, 1}, {1079, 1}, {8850, 1}, {18353, 1}
                        },
                        new int[][]{
                                {18349, 1}, {4151, 1}
                        }
                )
        ),
        AGS_MAIN(
                "agsmain",
                levelMap(99, 99, 99, 99, 99, 74, 95, 1),
                mode(
                        false, 0, 0, false, 100, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {10828, 1}, {6585, 1}, {6737, 1}, {11732, 1}, {7462, 1},
                                {1127, 1}, {1079, 1}, {8850, 1}, {4151, 1}
                        },
                        new int[][]{
                                {11694, 1}
                        }
                ),
                mode(
                        false, 0, 0, true, 50, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {10828, 1}, {6585, 1}, {6737, 1}, {11732, 1}, {7462, 1},
                                {1127, 1}, {1079, 1}, {11694, 1}
                        },
                        new int[][]{
                                {4151, 1}
                        }
                )
        ),
        BGS_MAIN(
                "bgsmain",
                levelMap(99, 99, 99, 99, 99, 74, 95, 1),
                mode(
                        false, 0, 0, false, 100, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {4753, 1}, {1704, 1}, {6737, 1}, {11732, 1}, {7462, 1},
                                {1127, 1}, {1079, 1}, {6524, 1}, {4151, 1}
                        },
                        new int[][]{
                                {11696, 1}
                        }
                ),
                mode(
                        false, 0, 0, true, 50, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {4753, 1}, {1704, 1}, {6737, 1}, {11732, 1}, {7462, 1},
                                {1127, 1}, {1079, 1}, {11696, 1}
                        },
                        new int[][]{
                                {4151, 1}
                        }
                )
        ),
        DHAROK_MAIN(
                "dhmain",
                levelMap(99, 99, 99, 99, 99, 95, 94, 1),
                mode(
                        false, 0, 0, false, 100, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {4716, 1}, {6585, 1}, {6737, 1}, {11732, 1}, {7462, 1},
                                {4720, 1}, {8850, 1}, {4722, 1}, {4718, 1}, {4151, 1}
                        },
                        new int[][]{
                                {560, 60}, {9075, 120}, {557, 300}, {5698, 1}
                        }
                ),
                mode(
                        false, 2, 0, false, 100, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {4716, 1}, {6585, 1}, {6737, 1}, {11732, 1}, {7462, 1},
                                {4720, 1}, {4722, 1}, {4718, 1}
                        },
                        new int[][]{
                                {4151, 1}, {560, 60}, {9075, 120}, {557, 300}, {5698, 1}
                        }
                )
        ),
        DHAROK_MED(
                "dhmed",
                levelMap(70, 70, 99, 99, 99, 74, 94, 1),
                mode(
                        false, 0, 0, false, 100, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {4716, 1}, {6585, 1}, {6737, 1}, {11732, 1}, {7462, 1},
                                {4720, 1}, {8850, 1}, {4722, 1}, {4718, 1}, {4151, 1}
                        },
                        new int[][]{
                                {560, 60}, {9075, 120}, {557, 300}, {5698, 1}
                        }
                ),
                mode(
                        false, 2, 0, false, 100, 1, Skills.STRENGTH, Skills.ATTACK,
                        new int[][]{
                                {4716, 1}, {6585, 1}, {6737, 1}, {11732, 1}, {7462, 1},
                                {4720, 1}, {4722, 1}, {4718, 1}
                        },
                        new int[][]{
                                {4151, 1}, {560, 60}, {9075, 120}, {557, 300}, {5698, 1}
                        }
                )
        );

        private final String shortName;
        private final int[] levels;
        private final int foodItemId;
        private final int restoreItemId;
        private final int boostPotionItemId;
        private final int foodAmount;
        private final int restoreAmount;
        private final int boostPotionAmount;
        private final BotCombatMode[] modes;

        BotArchetype(String shortName, int[] levels, BotCombatMode... modes) {
            this.shortName = shortName;
            this.levels = levels;
            this.foodItemId = 385;
            this.restoreItemId = 3024;
            this.boostPotionItemId = shortName.contains("pure") || shortName.contains("ghost") ? 2444 : 2436;
            this.foodAmount = shortName.contains("dh") ? 16 : shortName.contains("nh") ? 16 : 13;
            this.restoreAmount = shortName.contains("dh") ? 4 : 4;
            this.boostPotionAmount = shortName.contains("nh") ? 4 : 3;
            this.modes = modes;
        }

        private int levelFor(int skill) {
            return levels[skill];
        }

        private BotCombatMode defaultMode() {
            return modes[0];
        }

        private BotKit randomKit() {
            return switch (this) {
                case RANGE_PURE -> Utils.random(2) == 0 ? BotKit.CLASSIC_PURE : BotKit.BLACK_DHIDE_PURE;
                case DARKBOW_PURE -> BotKit.DARK_BOW_PURE;
                case HYBRID_PURE -> Utils.random(2) == 0 ? BotKit.CLASSIC_PURE : BotKit.GHOSTLY_RED;
                case PURE_NH -> switch (Utils.random(3)) {
                    case 0 -> BotKit.CLASSIC_PURE;
                    case 1 -> BotKit.GHOSTLY_BLUE;
                    default -> BotKit.BLACK_DHIDE_PURE;
                };
                case GHOSTLY_NH -> Utils.random(2) == 0 ? BotKit.GHOSTLY_BLUE : BotKit.GHOSTLY_RED;
                case MAIN_HYBRID -> switch (Utils.random(4)) {
                    case 0 -> BotKit.HYBRID_GREEN;
                    case 1 -> BotKit.HYBRID_BLACK;
                    case 2 -> BotKit.MELEE_NEIT;
                    default -> BotKit.MELEE_FIRE;
                };
                case SURGE_MAIN -> Utils.random(2) == 0 ? BotKit.MAGE_NEIT : BotKit.MAGE_AHRIMS;
                case ZERKER_DDS, ZERKER_GMAUL, ZERKER_KORASI -> switch (Utils.random(4)) {
                    case 0 -> BotKit.ZERK_FIRE;
                    case 1 -> BotKit.ZERK_TEAM;
                    case 2 -> BotKit.ZERK_GLORY;
                    default -> BotKit.ZERK_STRENGTH;
                };
                case RUNE_MAIN -> switch (Utils.random(5)) {
                    case 0 -> BotKit.RUNE_FIRE;
                    case 1 -> BotKit.RUNE_TEAM;
                    case 2 -> BotKit.RUNE_ARDY;
                    case 3 -> BotKit.RUNE_GLORY;
                    default -> BotKit.RUNE_STRENGTH;
                };
                case AGS_MAIN, BGS_MAIN -> switch (Utils.random(4)) {
                    case 0 -> BotKit.MELEE_FIRE;
                    case 1 -> BotKit.MELEE_ARDY;
                    case 2 -> BotKit.MELEE_TEAM;
                    default -> BotKit.MELEE_GLORY;
                };
                case DHAROK_MAIN, DHAROK_MED -> Utils.random(2) == 0 ? BotKit.DHAROK_STANDARD : BotKit.DHAROK_FIRECAPE;
            };
        }

        private boolean canFight(BotArchetype other) {
            return combatProfile().canFight(other.combatProfile());
        }

        private boolean isNhBuild() {
            return switch (this) {
                case PURE_NH, GHOSTLY_NH, MAIN_HYBRID -> true;
                default -> false;
            };
        }

        private boolean usesSoulSplit() {
            return switch (this) {
                case ZERKER_DDS, ZERKER_GMAUL, ZERKER_KORASI -> true;
                default -> false;
            };
        }

        private BotCombatProfile combatProfile() {
            return switch (this) {
                case PURE_NH, GHOSTLY_NH, MAIN_HYBRID -> BotCombatProfile.FREEZER;
                case RANGE_PURE, DARKBOW_PURE, HYBRID_PURE, SURGE_MAIN, ZERKER_DDS, ZERKER_GMAUL,
                        ZERKER_KORASI, RUNE_MAIN, AGS_MAIN, BGS_MAIN, DHAROK_MAIN, DHAROK_MED -> BotCombatProfile.STANDARD;
            };
        }

        private BotCombatMode chooseMode(Player player, Player target, BotCombatMode current) {
            if (modes.length == 1) {
                return modes[0];
            }

            if (this == DHAROK_MAIN || this == DHAROK_MED) {
                double selfHpRatio = player.getHitpoints() / (double) player.getMaxHitpoints();
                double targetHpRatio = target.getHitpoints() / (double) target.getMaxHitpoints();
                if (selfHpRatio <= 0.72 && targetHpRatio <= 0.58 && !player.isFrozen()) {
                    return modes[1];
                }
                return modes[0];
            }

            BotCombatMode best = current != null ? current : modes[0];
            int bestScore = Integer.MIN_VALUE;
            for (BotCombatMode mode : modes) {
                int score = scoreCombatMode(player, target, mode, current);
                if (isNhBuild()) {
                    if (mode.isMagicStyle() && !target.isFrozen()) {
                        score += 3;
                    }
                    if (mode.isMeleeStyle() && target.getHitpoints() < target.getMaxHitpoints() * 0.45) {
                        score += 4;
                    }
                }
                if (this == RANGE_PURE && mode.isRangeStyle()) {
                    score += mode.specialMaxDistance >= 7 ? 2 : 0;
                }
                if (mode.useSpecial) {
                    score += isKoOpportunity(player, target, mode) ? 9 : target.getHitpoints() < target.getMaxHitpoints() * 0.55 ? 4 : 0;
                }
                if (score > bestScore) {
                    bestScore = score;
                    best = mode;
                }
            }
            return best;
        }

        private static BotArchetype random() {
            return random(null);
        }

        private static BotArchetype random(String selector) {
            List<BotArchetype> matches = matching(selector);
            if (matches.isEmpty()) {
                BotArchetype[] values = values();
                return values[Utils.random(values.length)];
            }
            return matches.get(Utils.random(matches.size()));
        }

        private static List<BotArchetype> matching(String selector) {
            if (selector == null || selector.isBlank()) {
                return new ArrayList<>(List.of(values()));
            }
            String key = selector.toLowerCase().replace("_", "").replace("-", "").replace(" ", "");
            List<BotArchetype> matches = new ArrayList<>();
            for (BotArchetype archetype : values()) {
                if (archetype.matchesSelector(key)) {
                    matches.add(archetype);
                }
            }
            return matches;
        }

        private boolean matchesSelector(String key) {
            if (shortName.replace("_", "").equalsIgnoreCase(key) || name().replace("_", "").equalsIgnoreCase(key)) {
                return true;
            }
            return switch (key) {
                case "pure", "pures" -> this == RANGE_PURE || this == DARKBOW_PURE || this == HYBRID_PURE || this == PURE_NH || this == GHOSTLY_NH;
                case "nh", "nhpure", "nhpures" -> this == PURE_NH || this == GHOSTLY_NH;
                case "zerker", "zerkers" -> this == ZERKER_DDS || this == ZERKER_GMAUL || this == ZERKER_KORASI;
                case "dh", "dharok", "dharoker", "dharokers" -> this == DHAROK_MAIN || this == DHAROK_MED;
                case "main", "mains" -> this == MAIN_HYBRID || this == RUNE_MAIN || this == AGS_MAIN || this == BGS_MAIN || this == SURGE_MAIN || this == DHAROK_MAIN;
                case "surge", "surgemage" -> this == SURGE_MAIN;
                default -> false;
            };
        }

        private static String listSelectors() {
            return "Archetypes: rngpure, dbpure, hybpure, nhpure, ghostnh, mainnh, surgem, zerkdds, zerkgm, zerkkor, runemain, agsmain, bgsmain, dhmain, dhmed. Groups: pure, nh, zerker, dh, main.";
        }
    }

    private static final class BotCombatMode {
        private final boolean curses;
        private final int spellbook;
        private final int autoCastSpell;
        private final boolean useSpecial;
        private final int specialThreshold;
        private final int specialMaxDistance;
        private final boolean useVengeance;
        private final int primaryBoostSkill;
        private final int secondaryBoostSkill;
        private final LoadoutItem[] equipment;
        private final LoadoutItem[] inventory;

        private BotCombatMode(boolean curses, int spellbook, int autoCastSpell, boolean useSpecial, int specialThreshold,
                              int specialMaxDistance, boolean useVengeance, int primaryBoostSkill, int secondaryBoostSkill,
                              LoadoutItem[] equipment, LoadoutItem[] inventory) {
            this.curses = curses;
            this.spellbook = spellbook;
            this.autoCastSpell = autoCastSpell;
            this.useSpecial = useSpecial;
            this.specialThreshold = specialThreshold;
            this.specialMaxDistance = specialMaxDistance;
            this.useVengeance = useVengeance;
            this.primaryBoostSkill = primaryBoostSkill;
            this.secondaryBoostSkill = secondaryBoostSkill;
            this.equipment = equipment;
            this.inventory = inventory;
        }

        private boolean isMagicStyle() {
            return autoCastSpell > 0;
        }

        private boolean isRangeStyle() {
            for (LoadoutItem item : equipment) {
                if (item.id == 9185 || item.id == 861 || item.id == 11235) {
                    return true;
                }
            }
            return false;
        }

        private boolean isMeleeStyle() {
            return !isMagicStyle() && !isRangeStyle();
        }
    }

    private enum BotKit {
        CLASSIC_PURE(new int[][]{}),
        BLACK_DHIDE_PURE(new int[][]{{2495, 1}}),
        DARK_BOW_PURE(new int[][]{{10499, 1}, {6585, 1}, {6733, 1}, {2495, 1}}),
        GHOSTLY_BLUE(new int[][]{{6109, 1}, {6107, 1}, {6108, 1}, {6110, 1}, {2413, 1}}),
        GHOSTLY_RED(new int[][]{{6109, 1}, {6107, 1}, {6108, 1}, {6110, 1}, {2414, 1}}),
        HYBRID_GREEN(new int[][]{{19748, 1}}),
        HYBRID_BLACK(new int[][]{{6585, 1}}),
        MELEE_NEIT(new int[][]{{10828, 1}}),
        MELEE_FIRE(new int[][]{{6570, 1}, {6585, 1}}),
        MELEE_ARDY(new int[][]{{20211, 1}, {1704, 1}}),
        MELEE_TEAM(new int[][]{{20211, 1}, {1725, 1}}),
        MELEE_GLORY(new int[][]{{1704, 1}}),
        MAGE_NEIT(new int[][]{{10828, 1}}),
        MAGE_AHRIMS(new int[][]{{4708, 1}}),
        ZERK_FIRE(new int[][]{{6570, 1}, {6585, 1}}),
        ZERK_TEAM(new int[][]{{20211, 1}, {1725, 1}}),
        ZERK_GLORY(new int[][]{{20211, 1}, {1704, 1}}),
        ZERK_STRENGTH(new int[][]{{1725, 1}}),
        RUNE_FIRE(new int[][]{{6570, 1}, {6585, 1}}),
        RUNE_TEAM(new int[][]{{20211, 1}, {1725, 1}}),
        RUNE_ARDY(new int[][]{{20211, 1}, {1704, 1}}),
        RUNE_GLORY(new int[][]{{1704, 1}}),
        RUNE_STRENGTH(new int[][]{{1725, 1}}),
        DHAROK_STANDARD(new int[][]{}),
        DHAROK_FIRECAPE(new int[][]{{6570, 1}});

        private final LoadoutItem[] overrides;

        BotKit(int[][] overrides) {
            this.overrides = items(overrides);
        }
    }

    private static final class LoadoutItem {
        private final int id;
        private final int amount;

        private LoadoutItem(int id, int amount) {
            this.id = id;
            this.amount = amount;
        }

        private boolean valid() {
            return id > 0 && amount > 0;
        }
    }

    private enum BotCombatProfile {
        FREEZER,
        STANDARD;

        private boolean canFight(BotCombatProfile other) {
            if (this == FREEZER || other == FREEZER) {
                return this == other;
            }
            return true;
        }
    }

    private static LoadoutItem[] collectInventory(ManagedBot bot) {
        Map<Integer, Integer> merged = new HashMap<>();
        for (BotCombatMode mode : bot.archetype.modes) {
            for (LoadoutItem item : mode.inventory) {
                if (item.valid() && (!isSpecialWeaponItem(item.id) || item.id == bot.selectedSpecWeaponId)) {
                    merged.merge(item.id, item.amount, Math::max);
                }
            }
            addSpellRunes(merged, mode);
        }
        addModeEquipmentSwitches(merged, bot);
        if (bot.selectedSpecWeaponId > 0) {
            merged.merge(bot.selectedSpecWeaponId, 1, Math::max);
        }
        if (bot.dynamicInventoryOverrides != null) {
            for (LoadoutItem item : bot.dynamicInventoryOverrides) {
                if (item.valid()) {
                    merged.merge(item.id, item.amount, Math::max);
                }
            }
        }
        addSupplyItem(merged, bot.foodItemId, bot.foodLeft);
        addSupplyItem(merged, bot.restoreItemId, bot.restoreLeft);
        addSupplyItem(merged, bot.boostPotionItemId, bot.boostPotionLeft);
        LoadoutItem[] items = new LoadoutItem[merged.size()];
        int index = 0;
        for (Map.Entry<Integer, Integer> entry : merged.entrySet()) {
            items[index++] = new LoadoutItem(entry.getKey(), entry.getValue());
        }
        return items;
    }

    private static void addModeEquipmentSwitches(Map<Integer, Integer> merged, ManagedBot bot) {
        if (bot == null || bot.archetype == null || !bot.archetype.isNhBuild()) {
            return;
        }
        BotCombatMode current = bot.currentMode;
        for (BotCombatMode mode : bot.archetype.modes) {
            if (mode == current) {
                continue;
            }
            for (LoadoutItem item : mode.equipment) {
                if (!item.valid()) {
                    continue;
                }
                if (isSpecialWeaponItem(item.id) && item.id != bot.selectedSpecWeaponId) {
                    continue;
                }
                merged.merge(item.id, item.amount, Math::max);
            }
        }
    }

    private static void addSupplyItem(Map<Integer, Integer> merged, int itemId, int amount) {
        if (itemId > 0 && amount > 0) {
            merged.put(itemId, amount);
        }
    }

    private static void addSpellRunes(Map<Integer, Integer> merged, BotCombatMode mode) {
        if (mode == null || mode.autoCastSpell <= 0) {
            return;
        }
        int spellbookId = switch (mode.spellbook) {
            case 0 -> 192;
            case 1 -> 193;
            case 2 -> 430;
            case 3 -> 950;
            default -> -1;
        };
        if (spellbookId == -1) {
            return;
        }
        Spellbook spellbook = Spellbook.get(spellbookId);
        if (spellbook == null) {
            return;
        }
        for (RuneRequirement requirement : spellbook.getRunesFor(mode.autoCastSpell)) {
            if (requirement == null || requirement.getId() <= 0 || requirement.getAmount() <= 0) {
                continue;
            }
            int desiredAmount = Math.max(requirement.getAmount() * 120,
                    requirement.getId() == 560 || requirement.getId() == 565 || requirement.getId() == 566 ? 240 : 480);
            merged.merge(requirement.getId(), desiredAmount, Math::max);
        }
    }

    private static void refillSupplies(ManagedBot bot) {
        bot.foodItemId = bot.archetype.foodItemId;
        bot.restoreItemId = bot.archetype.restoreItemId;
        bot.boostPotionItemId = bot.archetype.boostPotionItemId;
        bot.foodLeft = bot.archetype.foodAmount + Utils.random(0, 4);
        bot.restoreLeft = bot.archetype.restoreAmount;
        bot.boostPotionLeft = bot.archetype.boostPotionAmount;
    }

    private static void refreshSupplies(ManagedBot bot) {
        bot.player.getInventory().reset();
        for (LoadoutItem item : collectInventory(bot)) {
            if (item.valid()) {
                bot.player.getInventory().addItem(item.id, item.amount);
            }
        }
        bot.player.getInventory().refresh();
    }

    private static BotCombatMode mode(boolean curses, int spellbook, int autoCastSpell, boolean useSpecial,
                                      int specialThreshold, int specialMaxDistance, int primaryBoostSkill,
                                      int secondaryBoostSkill, int[][] equipment, int[][] inventory) {
        return new BotCombatMode(
                curses,
                spellbook,
                autoCastSpell,
                useSpecial,
                specialThreshold,
                specialMaxDistance,
                spellbook == 2,
                primaryBoostSkill,
                secondaryBoostSkill,
                items(equipment),
                items(inventory)
        );
    }

    private static LoadoutItem[] items(int[][] raw) {
        LoadoutItem[] items = new LoadoutItem[raw.length];
        for (int i = 0; i < raw.length; i++) {
            items[i] = new LoadoutItem(raw[i][0], raw[i][1]);
        }
        return items;
    }

    private static int[] levelMap(int attack, int defence, int strength, int hitpoints, int range, int prayer, int magic, int summoning) {
        int[] levels = new int[25];
        for (int i = 0; i < levels.length; i++) {
            levels[i] = 1;
        }
        levels[Skills.ATTACK] = attack;
        levels[Skills.DEFENCE] = defence;
        levels[Skills.STRENGTH] = strength;
        levels[Skills.HITPOINTS] = hitpoints;
        levels[Skills.RANGE] = range;
        levels[Skills.PRAYER] = prayer;
        levels[Skills.MAGIC] = magic;
        levels[Skills.SUMMONING] = summoning;
        levels[Skills.HERBLORE] = 99;
        levels[Skills.AGILITY] = 99;
        levels[Skills.SLAYER] = 99;
        levels[Skills.RUNECRAFTING] = 99;
        return levels;
    }

    private static final class BotTickProfile {
        private static final int TOP_BOTS = 3;
        private final int tick;
        private final int totalBots;
        private int processedBots;
        private int heavyBots;
        private int respawningBots;
        private int restockingBots;
        private int challengeBots;
        private int safeExitBots;
        private int directAggressorBots;
        private int engagedBots;
        private int targetedBots;
        private int roamingBots;
        private int returningBots;
        private long upkeepMs;
        private long cooldownMs;
        private long movementMs;
        private long supportMs;
        private long resetMs;
        private long restockMs;
        private long challengeLookupMs;
        private long challengeMs;
        private long safeExitMs;
        private long aggressorLookupMs;
        private long engagedLookupMs;
        private long targetSearchMs;
        private long combatMs;
        private long roamMs;
        private long returnMs;
        private final List<BotHotEntry> topBots = new ArrayList<>();

        private BotTickProfile(int tick, int totalBots) {
            this.tick = tick;
            this.totalBots = totalBots;
        }

        private void recordBot(ManagedBot bot, long nanos, String reason) {
            topBots.add(new BotHotEntry(bot.player.getDisplayName(), bot.state.name(), reason, nanos));
            topBots.sort((a, b) -> Long.compare(b.nanos, a.nanos));
            if (topBots.size() > TOP_BOTS) {
                topBots.remove(topBots.size() - 1);
            }
        }

        private String render() {
            StringBuilder builder = new StringBuilder();
            builder.append("tick=").append(tick)
                    .append(" bots=").append(totalBots)
                    .append(" processed=").append(processedBots)
                    .append(" heavy=").append(heavyBots)
                    .append(" respawn=").append(respawningBots)
                    .append(" restock=").append(restockingBots)
                    .append(" challenge=").append(challengeBots)
                    .append(" safeExit=").append(safeExitBots)
                    .append(" retaliate=").append(directAggressorBots)
                    .append(" engaged=").append(engagedBots)
                    .append(" target=").append(targetedBots)
                    .append(" roam=").append(roamingBots)
                    .append(" return=").append(returningBots)
                    .append(" timings[upkeep=").append(toMillis(upkeepMs))
                    .append("ms cooldowns=").append(toMillis(cooldownMs))
                    .append("ms movement=").append(toMillis(movementMs))
                    .append("ms support=").append(toMillis(supportMs))
                    .append("ms reset=").append(toMillis(resetMs))
                    .append("ms restock=").append(toMillis(restockMs))
                    .append("ms challengeLookup=").append(toMillis(challengeLookupMs))
                    .append("ms challenge=").append(toMillis(challengeMs))
                    .append("ms safeExit=").append(toMillis(safeExitMs))
                    .append("ms aggressorLookup=").append(toMillis(aggressorLookupMs))
                    .append("ms engagedLookup=").append(toMillis(engagedLookupMs))
                    .append("ms targetSearch=").append(toMillis(targetSearchMs))
                    .append("ms combat=").append(toMillis(combatMs))
                    .append("ms roam=").append(toMillis(roamMs))
                    .append("ms return=").append(toMillis(returnMs))
                    .append("ms]");
            if (!topBots.isEmpty()) {
                builder.append(" topBots=");
                for (int i = 0; i < topBots.size(); i++) {
                    if (i > 0) {
                        builder.append(", ");
                    }
                    BotHotEntry entry = topBots.get(i);
                    builder.append(entry.name)
                            .append('(').append(entry.state).append('/')
                            .append(entry.reason).append(':')
                            .append(entry.nanos / 1_000_000L).append("ms)");
                }
            }
            return builder.toString();
        }

        private long toMillis(long nanos) {
            return nanos / 1_000_000L;
        }
    }

    private static final class BotHotEntry {
        private final String name;
        private final String state;
        private final String reason;
        private final long nanos;

        private BotHotEntry(String name, String state, String reason, long nanos) {
            this.name = name;
            this.state = state;
            this.reason = reason;
            this.nanos = nanos;
        }
    }
}
