package com.rs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alex.store.Index;
import com.rs.core.cache.Cache;
import com.rs.core.cache.defintions.*;
import com.rs.core.thread.CoresManager;
import com.rs.discord.DiscordAnnouncer;
import com.rs.discord.DiscordRoutes;
import com.rs.discord.DiscordWebhook;
import com.rs.java.game.Region;
import com.rs.java.game.World;
import com.rs.java.game.area.AreaManager;
import com.rs.java.game.cityhandler.CityEventHandler;
import com.rs.java.game.item.ItemPluginLoader;
import com.rs.java.game.item.ground.AutomaticGroundItem;
import com.rs.java.game.map.MapBuilder;
import com.rs.java.game.npc.NpcPluginLoader;
import com.rs.java.game.npc.combat.CombatScriptsHandler;
import com.rs.java.tools.GenericClientScriptMapDumper;
import com.rs.kotlin.Rscm;
import com.rs.kotlin.game.npc.drops.DropTablesSetup;
import com.rs.java.game.objects.GlobalObjectAddition;
import com.rs.java.game.objects.GlobalObjectDeletion;
import com.rs.java.game.objects.ObjectPluginLoader;
import com.rs.java.game.player.AccountCreation;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.actions.combat.weaponscript.WeaponScriptsManager;
import com.rs.java.game.player.actions.skills.fishing.FishingSpotsHandler;
import com.rs.java.game.player.content.EdgevillePvPInstance;
import com.rs.java.game.player.content.KillScoreBoard;
import com.rs.java.game.player.content.clans.ClansManager;
import com.rs.java.game.player.content.customshops.CustomStoreData;
import com.rs.java.game.player.content.friendschat.FriendChatsManager;
import com.rs.java.game.player.content.grandexchange.GrandExchange;
import com.rs.java.game.player.content.grandexchange.LimitedGEReader;
import com.rs.java.game.player.content.grandexchange.UnlimitedGEReader;
import com.rs.java.game.player.controlers.ControlerHandler;
import com.rs.java.game.player.cutscenes.CutscenesHandler;
import com.rs.java.game.player.dialogues.DialogueHandler;
import com.rs.java.game.worldlist.WorldList;
import com.rs.core.networking.ServerChannelHandler;
import com.rs.java.utils.Credentials;
import com.rs.java.utils.DTRank;
import com.rs.java.utils.DisplayNames;
import com.rs.java.utils.IPBanL;
import com.rs.java.utils.ItemBonuses;
import com.rs.java.utils.ItemExamines;
import com.rs.java.utils.Logger;
import com.rs.java.utils.MapArchiveKeys;
import com.rs.java.utils.MapAreas;
import com.rs.java.utils.MusicHints;
import com.rs.java.utils.NPCBonuses;
import com.rs.java.utils.NPCCombatDefinitionsL;
import com.rs.java.utils.NPCExamines;
import com.rs.java.utils.NPCSpawns;
import com.rs.java.utils.ObjectSpawns;
import com.rs.java.utils.ShopsHandler;
import com.rs.java.utils.WeaponTypesLoader;
import com.rs.java.utils.Weights;
import com.rs.java.utils.huffman.Huffman;
import com.rs.kotlin.game.player.command.CommandRegistry;

public final class Launcher {

	public static void main(String[] args) throws Exception {
		System.out.println("Current working directory: " + System.getProperty("user.dir"));
		if (args.length < 4) {
			Settings.VPS_HOSTED = false;
			Settings.PORT_ID = 43594;
			Settings.HOSTED = true;
			Settings.DEBUG = true;
		} else {
			Settings.VPS_HOSTED = Boolean.parseBoolean(args[3]);
			Settings.PORT_ID = Integer.parseInt(args[2]);
			Settings.HOSTED = Boolean.parseBoolean(args[1]);
			Settings.DEBUG = Boolean.parseBoolean(args[0]);
		}
		Cache.init();
		Rscm.loadAll();
		//RscmGenerator.INSTANCE.generateGroupedNpcRscm();
		DropTablesSetup.setup();
		AreaManager.init();
		ItemsEquipIds.init();
		Huffman.init();
		DisplayNames.init();
		MapArchiveKeys.init();
		MapAreas.init();
		IPBanL.init();
		ObjectSpawns.init();
		NPCSpawns.init();
		NPCCombatDefinitionsL.init();
		NPCBonuses.init();
		ItemExamines.init();
		ItemBonuses.init();
		MusicHints.init();
		ShopsHandler.init();
		FishingSpotsHandler.init();
		DialogueHandler.init();
		ControlerHandler.init();
		CutscenesHandler.init();
		FriendChatsManager.init();
		ClansManager.init();
		CoresManager.init();
		World.init();
		MapBuilder.init();
		Weights.init();
		GrandExchange.init();
		LimitedGEReader.init();
		UnlimitedGEReader.init();
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
		CustomStoreData.init();
		DTRank.init();
		CombatScriptsHandler.init();
		ObjectPluginLoader.init();
		NpcPluginLoader.init();
		ItemPluginLoader.init();
		WeaponScriptsManager.init();
		CommandRegistry.registerCommands();
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

        DiscordAnnouncer.announceGlobalEvent(
                "Server Online",
                "World is up and running!",
                null
        );
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

	public static void saveFiles() {
		for (Player player : World.getPlayers()) {
			if (player == null || !player.hasStarted() || player.hasFinished())
				continue;
			AccountCreation.savePlayer(player);
		}
		if (!World.getPlayers().isEmpty())
			Logger.log("Launcher", "There is currently " + World.getPlayers().size() + " players online.");

		List<String> playerNames = World.getPlayers().stream()
				.filter(p -> p != null && p.hasStarted() && !p.hasFinished())
				.map(Player::getUsername)
				.toList();

		if (!playerNames.isEmpty()) {
			Logger.log("Launcher", "Players: " + playerNames);
		}
		IPBanL.save();
		GrandExchange.save();
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
		ServerChannelHandler.shutdown();
		CoresManager.shutdown();
	}

	public static void restart() {
		closeServices();
		System.gc();

		try {
			String javaBin = System.getProperty("java.home") + "/bin/java";
			String classpath = "bin;data/libs/netty-3.5.2.Final.jar;data/libs/RuneTopListV2.jar;data/libs/FileStore.jar;data/lib/GTLVote.jar;data/lib/mysql2.jar";
			String mainClass = "com.rs.Launcher";

			ProcessBuilder builder = new ProcessBuilder(
					javaBin,
					"-XX:-OmitStackTraceInFastThrow",
					"-Xms1024m",
					"-cp",
					classpath,
					mainClass,
					"false",
					"false",
					"false"
			);

			builder.start();
			System.exit(0);
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

}
