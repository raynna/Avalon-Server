package raynna.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import raynna.core.cache.Cache;
import raynna.core.cache.defintions.ItemDefinitions;
import raynna.core.cache.defintions.ItemsEquipIds;
import raynna.core.cache.defintions.NPCDefinitions;
import raynna.core.cache.defintions.ObjectDefinitions;
import raynna.core.thread.CoresManager;
import raynna.integrations.discord.DiscordAnnouncer;
import raynna.integrations.discord.DiscordRoutes;
import raynna.integrations.discord.DiscordWebhook;
import raynna.game.Region;
import raynna.game.World;
import raynna.game.city.CityEventHandler;
import raynna.game.item.ItemPluginLoader;
import raynna.game.item.ground.AutomaticGroundItem;
import raynna.game.map.MapBuilder;
import raynna.game.npc.NpcPluginLoader;
import raynna.game.npc.combat.CombatScriptsHandler;
import raynna.game.player.content.collectionlog.CollectionLog;
import raynna.data.json.JsonNpcCombatDefinitions;
import raynna.integrations.api.LogApiServer;
import raynna.logging.Logs;
import raynna.game.player.grandexchange.GrandExchange;
import raynna.game.player.grandexchange.LimitedGEReader;
import raynna.game.player.grandexchange.UnlimitedGEReader;
import raynna.game.player.shop.ShopPriceManager;
import raynna.data.rscm.Rscm;
import raynna.game.data.npc.JsonNpcSpawns;
import raynna.game.npc.worldboss.RandomWorldBossHandler;
import raynna.game.npc.drops.DropTablesSetup;
import raynna.game.objects.GlobalObjectAddition;
import raynna.game.objects.GlobalObjectDeletion;
import raynna.game.objects.ObjectPluginLoader;
import raynna.game.player.AccountCreation;
import raynna.game.player.Player;
import raynna.game.player.content.EdgevillePvPInstance;
import raynna.game.player.content.KillScoreBoard;
import raynna.game.player.content.clans.ClansManager;
import raynna.game.player.content.friendschat.FriendChatsManager;
import raynna.game.player.controllers.ControlerHandler;
import raynna.game.player.cutscenes.CutscenesHandler;
import raynna.game.player.dialogues.DialogueHandler;
import raynna.game.worldlist.WorldList;
import raynna.core.networking.ServerChannelHandler;
import raynna.util.Credentials;
import raynna.util.DTRank;
import raynna.util.DisplayNames;
import raynna.util.IPBanL;
import raynna.util.ItemBonuses;
import raynna.util.ItemExamines;
import raynna.util.Logger;
import raynna.util.MapArchiveKeys;
import raynna.util.MapAreas;
import raynna.util.MusicHints;
import raynna.util.NPCExamines;
import raynna.util.ObjectSpawns;
import raynna.util.ShopsHandler;
import raynna.util.WeaponTypesLoader;
import raynna.util.Weights;
import raynna.util.huffman.Huffman;
import raynna.game.player.command.CommandRegistry;
import raynna.game.player.shop.ShopInitializer;
import raynna.game.world.activity.pvpgame.tournament.TournamentScheduler;
import raynna.game.world.area.AreaManager;
import com.alex.store.Index;

public final class Launcher {

	public static void main(String[] args) throws Exception {
		loadEnvFile("secret.env");
		System.out.println("Current working directory: " + System.getProperty("user.dir"));
		if (args.length < 2) {
			Settings.DEBUG = true;
			Settings.PORT_ID = 43594;
		} else {
			Settings.DEBUG = Boolean.parseBoolean(args[0]);
			Settings.PORT_ID = Integer.parseInt(args[1]);
		}
		Cache.init();
		Rscm.INSTANCE.loadAll();
		//RscmGenerator.INSTANCE.generateGroupedNpcRscm();
		DropTablesSetup.INSTANCE.setup();
		AreaManager.INSTANCE.init();
		ItemsEquipIds.init();
		Huffman.init();
		DisplayNames.init();
		MapArchiveKeys.init();
		MapAreas.init();
		IPBanL.init();
		ObjectSpawns.init();
		ItemExamines.init();
		ItemBonuses.init();
		MusicHints.init();
		ShopsHandler.init();
		DialogueHandler.init();
		ControlerHandler.init();
		CutscenesHandler.init();
		FriendChatsManager.init();
		ClansManager.init();
		CoresManager.init();
		World.init();
		MapBuilder.init();
		Weights.init();
		GrandExchange.INSTANCE.init();
		ShopPriceManager.INSTANCE.init();
		LimitedGEReader.INSTANCE.init();
		UnlimitedGEReader.INSTANCE.init();
		GlobalObjectDeletion.init();
		GlobalObjectAddition.init();
		//StarterProtection.loadIPS();
		NPCExamines.loadPackedExamines();
		Credentials.init();
		WeaponTypesLoader.loadDefinitions();
		AutomaticGroundItem.initialize();
		//MobRewardRDT.getInstance().structureNode();
		WorldList.init();
		KillScoreBoard.init();
		EdgevillePvPInstance.buildInstance();
		CityEventHandler.registerCitys();
		DTRank.init();
		CombatScriptsHandler.init();
		ObjectPluginLoader.init();
		NpcPluginLoader.init();
		ItemPluginLoader.init();
		CommandRegistry.INSTANCE.registerCommands();
		ShopInitializer.INSTANCE.initializeShops();
		JsonNpcCombatDefinitions.INSTANCE.init();
		JsonNpcSpawns.INSTANCE.init();
		AccountCreation.Companion.init();
		try {
			ServerChannelHandler.init();
		} catch (Throwable e) {
			Logger.handle(e);
			Logger.log("Launcher", "Failed initing Server Channel Handler. Shutting down...");
			System.exit(1);
			return;
		}
		Logger.log("Economy Mode",
				Settings.ECONOMY_MODE == 2 ? "Full Spawn" : Settings.ECONOMY_MODE == 1 ? "Half Economy" : "Full Economy");
		Logger.log("Debug", Settings.DEBUG ? "Debug is activated." : "Debug is inactived.");
		Logger.log("Status", Settings.SERVER_NAME + " is now online.");
		addAccountsSavingTask();
		addCleanMemoryTask();
        DiscordRoutes.INSTANCE.init();
        DiscordWebhook.INSTANCE.setWebhookUrl(Settings.eventsWebhook);

        DiscordAnnouncer.INSTANCE.announceGlobalEvent(
                "Server Online",
                "World is up and running!",
                null
        );
		CollectionLog.init();
		RandomWorldBossHandler.INSTANCE.start();
		TournamentScheduler.INSTANCE.start();
		FriendChatsManager.init();
		String logApiKey = System.getProperty("LOG_API_KEY", "dev-key");
		LogApiServer.INSTANCE.start(8765, logApiKey);
		System.out.println("[LogApiServer] API KEY = " + logApiKey);
		//RscmGenerator.INSTANCE.generateGroupedItemRscm();
    }

	private static void loadEnvFile(String fileName) {
		try {
			java.nio.file.Path path = java.nio.file.Path.of(fileName).toAbsolutePath();
			System.out.println("[ENV] Looking for file at: " + path);

			if (!java.nio.file.Files.exists(path)) {
				System.out.println("[ENV] File not found: " + path);
				return;
			}

			java.nio.file.Files.lines(path)
					.map(String::trim)
					.filter(line -> !line.isEmpty() && !line.startsWith("#"))
					.forEach(line -> {
						String[] parts = line.split("=", 2);
						if (parts.length == 2) {
							String key = parts[0].trim();
							String value = parts[1].trim();
							System.setProperty(key, value);
							System.out.println("[ENV] Loaded key: " + key + "=" + value);
						}
					});

			System.out.println("[ENV] Loaded " + path);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void addCleanMemoryTask() {
		CoresManager.getSlowExecutor().scheduleWithFixedDelay(() -> {
            try {
                cleanMemory(Runtime.getRuntime().freeMemory() < Settings.MIN_FREE_MEM_ALLOWED);
            } catch (Throwable e) {
                Logger.handle(e);
            }
        }, 0, 10, TimeUnit.SECONDS);
	}

	private static String envOrProp(String env, String prop, String defVal) {
		String v = System.getenv(env);
		if (v != null && !v.isBlank()) return v;
		v = System.getProperty(prop);
		if (v != null && !v.isBlank()) return v;
		return defVal;
	}

	private static int i = 0;

	private static void addAccountsSavingTask() {
		CoresManager.getSlowExecutor().scheduleWithFixedDelay(() -> {
            try {
                i++;
                if (i % 30 == 0 && !Settings.DEBUG) {
                    //discord.getChannelByName("public-chat").sendMessage("30 Minutes Message\n"
                    //		+ World.getPlayers().size() + " players are currently playing Avalon.");
                    i = 0;
                }
                saveFiles();
            } catch (Throwable e) {
                Logger.handle(e);
            }

        }, 0, 1, TimeUnit.MINUTES);
	}

	public static String Time(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	public static String Time;

	private static long lastDiscordAnnounce = 0;
	private static List<String> lastAnnouncedPlayers = List.of();
	private final static int MINUTES_TO_ANNOUNCE = 1;

	public static void saveFiles() {
		for (Player player : World.getPlayers()) {
			if (player == null || !player.hasStarted() || player.hasFinished())
				continue;
			AccountCreation.Companion.savePlayer(player);
		}

		if (!World.getPlayers().isEmpty()) {
			Logger.log("Launcher", "There is currently " + World.getPlayers().size() + " players online.");
		}

		List<String> playerNames = World.getPlayers().stream()
				.filter(p -> p != null && p.hasStarted() && !p.hasFinished())
				.map(Player::getUsername)
				.sorted(String.CASE_INSENSITIVE_ORDER)
				.toList();

		long now = System.currentTimeMillis();
		if (!playerNames.equals(lastAnnouncedPlayers) && (now - lastDiscordAnnounce >= (long) MINUTES_TO_ANNOUNCE * 60 * 1000)) {
			String namesDisplay = playerNames.isEmpty()
					? "-"
					: String.join(", ", playerNames); // nice comma-separated list
			DiscordAnnouncer.INSTANCE.announce(
					"Players Status",
					"Players online: " + namesDisplay,
					"Total: " + playerNames.size(), 0
			);
			lastAnnouncedPlayers = playerNames;
			lastDiscordAnnounce = now;
		}
		if (!playerNames.isEmpty()) {
			Logger.log("Launcher", "Players: " + playerNames);
		}
		IPBanL.save();
		GrandExchange.INSTANCE.save();
		Time = Time("dd MMMMM yyyy 'at' hh:mm:ss z");
	}


	public static void cleanMemory(boolean force) {
		if (force) {
			ItemDefinitions.clearItemsDefinitions();
			NPCDefinitions.clearNPCDefinitions();
			ObjectDefinitions.clearObjectDefinitions();
			skip: for (Region region : World.getRegions().values()) {
				for (int regionId : MapBuilder.FORCE_LOAD_REGIONS)
					if (regionId == region.getRegionId())
						continue skip;
				region.unloadMap();
			}
		}
		for (Index index : Cache.STORE.getIndexes())
			index.resetCachedFiles();
		CoresManager.getFastExecutor().purge();
		System.gc();
	}

	public static void shutdown() {
		try {
			closeServices();
		} catch (Throwable t) {
			Logger.log("Launcher", t);
		}
		System.exit(0);
	}

	public static void closeServices() {
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			Logger.log("Launcher", e);
		}
		for (int cycle = 0;; cycle++) {
			Logger.log("Launcher", "Logging out players... Cycle #" + cycle);
			if (World.getPlayers().isEmpty() && World.getLobbyPlayers().isEmpty())
				break;
			for (Player player : World.getPlayers())
				player.realFinish();
			for (Player player : World.getLobbyPlayers())
				player.realFinish();
			Logger.log("Launcher",
					"Logging out players: " + (World.getPlayers().size() + World.getLobbyPlayers().size()) + ".");
			try {
				Thread.sleep(2000);
			} catch (Throwable t) {
				Logger.log("Launcher", t);
			}
		}
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Logs.INSTANCE.shutdown();
			LogApiServer.INSTANCE.stop();
		}));
		ServerChannelHandler.shutdown();
		CoresManager.shutdown();
	}

	public static void restart() {
		closeServices();
		System.gc();

		try {
			System.exit(0);
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

}
