package com.rs.java.game.player.controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.rs.java.game.minigames.clanwars.FfaZone;
import com.rs.java.game.minigames.clanwars.RequestController;
import com.rs.java.game.minigames.clanwars.WarController;
import com.rs.java.game.minigames.duel.DuelArena;
import com.rs.java.game.minigames.duel.DuelController;
import com.rs.java.game.minigames.lividfarm.LividFarmController;
import com.rs.java.game.minigames.warriorguild.WarriorsGuild;
import com.rs.java.game.player.actions.skills.construction.HouseController;
import com.rs.java.game.player.content.quest.impl.demonslayer.DelrithController;
import com.rs.java.game.player.controllers.castlewars.CastleWarsPlaying;
import com.rs.java.game.player.controllers.castlewars.CastleWarsWaiting;
import com.rs.java.game.player.controllers.construction.SawmillController;
import com.rs.java.game.player.controllers.fightpits.FightPitsArena;
import com.rs.java.game.player.controllers.fightpits.FightPitsLobby;
import com.rs.java.game.player.controllers.pestcontrol.PestControlGame;
import com.rs.java.game.player.controllers.pestcontrol.PestControlLobby;
import com.rs.java.utils.Logger;

public class ControlerHandler {

	private static final HashMap<Object, Class<Controller>> handledControlers = new HashMap<Object, Class<Controller>>();

	@SuppressWarnings("unchecked")
	public static void init() {
		try {
			Class<Controller> value1 = (Class<Controller>) Class.forName(WildernessController.class.getCanonicalName());
			handledControlers.put("WildernessControler", value1);
			Class<Controller> value4 = (Class<Controller>) Class.forName(GodWars.class.getCanonicalName());
			handledControlers.put("GodWars", value4);
			Class<Controller> value5 = (Class<Controller>) Class.forName(ZGDController.class.getCanonicalName());
			handledControlers.put("ZGDControler", value5);
			Class<Controller> value9 = (Class<Controller>) Class.forName(DuelArena.class.getCanonicalName());
			handledControlers.put("DuelArena", value9);
			Class<Controller> value10 = (Class<Controller>) Class.forName(DuelController.class.getCanonicalName());
			handledControlers.put("DuelControler", value10);
			Class<Controller> value11 = (Class<Controller>) Class.forName(CorpBeastController.class.getCanonicalName());
			handledControlers.put("CorpBeastControler", value11);
			Class<Controller> value14 = (Class<Controller>) Class.forName(DTController.class.getCanonicalName());
			handledControlers.put("DTControler", value14);
			Class<Controller> value17 = (Class<Controller>) Class.forName(CastleWarsPlaying.class.getCanonicalName());
			handledControlers.put("CastleWarsPlaying", value17);
			Class<Controller> value18 = (Class<Controller>) Class.forName(CastleWarsWaiting.class.getCanonicalName());
			handledControlers.put("CastleWarsWaiting", value18);
			handledControlers.put("Instance", (Class<Controller>) Class.forName(Instance.class.getCanonicalName()));
			handledControlers.put("clan_wars_request",
					(Class<Controller>) Class.forName(RequestController.class.getCanonicalName()));
			handledControlers.put("clan_war", (Class<Controller>) Class.forName(WarController.class.getCanonicalName()));
			handledControlers.put("clan_wars_ffa", (Class<Controller>) Class.forName(FfaZone.class.getCanonicalName()));
			handledControlers.put("NomadsRequiem",
					(Class<Controller>) Class.forName(NomadsRequiem.class.getCanonicalName()));
			handledControlers.put("BorkControler",
					(Class<Controller>) Class.forName(BorkController.class.getCanonicalName()));
			handledControlers.put("FightCavesControler",
					(Class<Controller>) Class.forName(FightCaves.class.getCanonicalName()));
			handledControlers.put("FightKilnControler",
					(Class<Controller>) Class.forName(FightKiln.class.getCanonicalName()));
			handledControlers.put("FightPitsLobby",
					(Class<Controller>) Class.forName(FightPitsLobby.class.getCanonicalName()));
			handledControlers.put("FightPitsArena",
					(Class<Controller>) Class.forName(FightPitsArena.class.getCanonicalName()));
			handledControlers.put("PestControlGame",
					(Class<Controller>) Class.forName(PestControlGame.class.getCanonicalName()));
			handledControlers.put("PestControlLobby",
					(Class<Controller>) Class.forName(PestControlLobby.class.getCanonicalName()));
			handledControlers.put("Barrows", (Class<Controller>) Class.forName(Barrows.class.getCanonicalName()));
			handledControlers.put("Falconry", (Class<Controller>) Class.forName(Falconry.class.getCanonicalName()));
			handledControlers.put("QueenBlackDragonControler",
					(Class<Controller>) Class.forName(QueenBlackDragonController.class.getCanonicalName()));
			handledControlers.put("RuneSpanControler",
					(Class<Controller>) Class.forName(RunespanController.class.getCanonicalName()));
			handledControlers.put("CrucibleControler",
					(Class<Controller>) Class.forName(CrucibleController.class.getCanonicalName()));
			handledControlers.put("WarriorsGuild",
					(Class<Controller>) Class.forName(WarriorsGuild.class.getCanonicalName()));
			handledControlers.put("RecipeDisasterControler",
					(Class<Controller>) Class.forName(RecipeForDisaster.class.getCanonicalName()));
			handledControlers.put("WelcomeTutorial",
					(Class<Controller>) Class.forName(WelcomeTutorial.class.getCanonicalName()));
			handledControlers.put("DungeonControler",
					(Class<Controller>) Class.forName(DungeonController.class.getCanonicalName()));
			handledControlers.put("HouseControler",
					(Class<Controller>) Class.forName(HouseController.class.getCanonicalName()));
			handledControlers.put("EdgevillePvPControler",
					(Class<Controller>) Class.forName(EdgevillePvPController.class.getCanonicalName()));
			handledControlers.put("DelrithControler",
					(Class<Controller>) Class.forName(DelrithController.class.getCanonicalName()));
			handledControlers.put("LividFarmHandler",
					(Class<Controller>) Class.forName(LividFarmController.class.getCanonicalName()));
			handledControlers.put("SawmillController",
					(Class<Controller>) Class.forName(SawmillController.class.getCanonicalName()));
			handledControlers.put("SorceressGarden",
					(Class<Controller>) Class.forName(SorceressGarden.class.getCanonicalName()));
			handledControlers.put("ArtisanControler",
					(Class<Controller>) Class.forName(ArtisanController.class.getCanonicalName()));
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static void reload() {
		handledControlers.clear();
		init();
	}

	public static Controller getControler(Object key) {
		if (key instanceof Controller)
			return (Controller) key;
		Class<Controller> classC = handledControlers.get(key);
		if (classC == null)
			return null;
		try {
			return classC.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			Logger.handle(e);
		}
		return null;
	}

}
