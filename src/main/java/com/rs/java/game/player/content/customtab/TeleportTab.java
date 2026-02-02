package com.rs.java.game.player.content.customtab;

import java.util.function.Consumer;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.controlers.WildernessControler;
import com.rs.java.game.player.teleportation.Teleports.TeleportLocations;
import com.rs.kotlin.game.player.combat.magic.SpellHandler;

public class TeleportTab extends CustomTab {
	private static final int CITY_TELEPORTS = 0;
	private static final int SKILLING = 1;
	private static final int MONSTERS = 2;
	private static final int DUNGEONS = 3;
	private static final int BOSSES = 4;
	private static final int MINIGAMES = 5;
	private static final int WILDERNESS = 6;

	private static final int SKILLING_MINING = 13;
	private static final int SKILLING_SMITHING = 14;
	private static final int SKILLING_FISHING = 15;
	private static final int SKILLING_COOKING = 16;
	private static final int SKILLING_WOODCUTTING = 17;
	private static final int SKILLING_FARMING = 18;
	private static final int SKILLING_AGILITY = 19;
	private static final int SKILLING_THIEVING = 20;
	private static final int SKILLING_RUNECRAFTING = 21;
	private static final int SKILLING_HUNTER = 22;

	private static final TeleportsTab[] TELEPORT_TABS = TeleportsTab.values();
	private static final TeleportTabData[] TELEPORT_DATA = TeleportTabData.values();

	public enum TeleportsTab {
		TITLE(25, "Teleports"),
		CITIES(3, "City Teleports"),
		SKILLING(4, "Skilling Teleports"),
		MONSTERS(5, "Monster Teleports"),
		DUNGEONS(6, "Dungeon/Slayer Teleports"),
		BOSSES(7, "Boss Teleports"),
		MINIGAMES(8, "Minigame Teleports"),
		WILDERNESS(9, "Wilderness Teleports"),
		PREVIOUS(11, "<u>Previous Teleport");

		private final int componentId;
		private final String text;

		TeleportsTab(int componentId, String text) {
			this.componentId = componentId;
			this.text = text;
		}

		public int getComponentId() {
			return componentId;
		}

		public String getText() {
			return text;
		}
	}

	public enum TeleportTabData {
		ALKHARID("A", 3, CITY_TELEPORTS, "Al Kharid"),
		ARDOUGNE("A", 4, CITY_TELEPORTS, "Ardougne"),
		BURTHORPE("B", 5, CITY_TELEPORTS, "Burthorpe"),
		CAMELOT("C", 6, CITY_TELEPORTS, "Camelot"),
		CATHERBY("C", 7, CITY_TELEPORTS, "Catherby"),
		CANIFIS("C", 8, CITY_TELEPORTS, "Canifis"),
		DRAYNOR("D", 9, CITY_TELEPORTS, "Draynor Village"),
		EDGEVILLE("E", 10, CITY_TELEPORTS, "Edgeville"),
		FALADOR("F", 11, CITY_TELEPORTS, "Falador"),
		KARAMJA("K", 12, CITY_TELEPORTS, "Karamja"),
		LUMBRIDGE("L", 13, CITY_TELEPORTS, "Lumbridge"),
		LUNAR("L", 14, CITY_TELEPORTS, "Lunar Isle"),
		NEITIZNOT("N", 15, CITY_TELEPORTS, "Neitiznot"),
		PISCATORIS("P", 16, CITY_TELEPORTS, "Piscatoris Colony"),
		RIMMINGTON("R", 17, CITY_TELEPORTS, "Rimmington"),
		SHILOVILLAGE("S", 18, CITY_TELEPORTS, "Shilo Village"),
		TREEGNOME("T", 19, CITY_TELEPORTS, "Tree Gnome Stronghold"),
		VARROCK("V", 20, CITY_TELEPORTS, "Varrock"),
		YANILLE("Y", 21, CITY_TELEPORTS, "Yanille"),

		MINING(3, SKILLING, "Mining", true),
		SMITHING(4, SKILLING, "Smithing", true),
		FISHING(5, SKILLING, "Fishing", true),
		COOKING(6, SKILLING, "Cooking", true),
		WOODCUTTING(7, SKILLING, "Woodcutting", true),
		FARMING(8, SKILLING, "Farming", true),
		AGILITY(9, SKILLING, "Agility", true),
		THIEVING(10, SKILLING, "Thieving", true),
		RUNECRAFTING(11, SKILLING, "Runecrafting", true),
		HUNTER(12, SKILLING, "Hunter", true),

		ALKHARID_MINING(3, SKILLING_MINING, "Al-kharid Mining"),
		FALADOR_MINING(4, SKILLING_MINING, "Falador Mining"),
		TZHAAR_MINING(5, SKILLING_MINING, "Tzhaar Mining"),
		YANILLE_MINING(6, SKILLING_MINING, "Yanille Mining"),
		RUNE_WILDY_MINING(7, SKILLING_MINING, true, "Runite Mining"),
		HERO_GUILD_MINING(8, SKILLING_MINING, "Hero's Guild Mining"),
		CRANDOR_MINING(9, SKILLING_MINING, "Crandor Mining"),
		GEM_ROCK_MINING(10, SKILLING_MINING, "Gem Rock Mining"),

		ALKHARID_FURNACE(3, SKILLING_SMITHING, "Al-kharid Furnace"),
		WILDY_FURNACE(4, SKILLING_SMITHING, true, "Wilderness Furnace"),
		NEIT_FURNACE(5, SKILLING_SMITHING, "Neitiznot Furnace"),

		CATHERBY_FISHING(3, SKILLING_FISHING, "Catherby Fishing"),
		FISHING_GUILD_FISHING(4, SKILLING_FISHING, "Fishing Guild Fishing"),

		CATHERBY_RANGE(3, SKILLING_COOKING, "Catherby Range"),
		COOKS_GUILD_RANGE(4, SKILLING_COOKING, "Cook's Guild Range"),

		CAMELOT_WOODCUTTING(3, SKILLING_WOODCUTTING, "Camelot Woodcutting"),
		GE_WOODCUTTING(4, SKILLING_WOODCUTTING, "Grand Exchange Woodcutting"),
		ETCETERIA_WOODCUTTING(5, SKILLING_WOODCUTTING, "Etceteria Woodcutting"),
		PORT_SARIM_WOODCUTTING(6, SKILLING_WOODCUTTING, "Port Sarim Woodcutting"),

		CATHERBY_PATCHES(3, SKILLING_FARMING, "Catherby Patches"),
		PORT_PHATASS_PATCHES(4, SKILLING_FARMING, "Port Phasmatys Patches"),
		ARDY_PATCHES(5, SKILLING_FARMING, "Ardougne Patches"),
		YANILLE_PATCHES(6, SKILLING_FARMING, "Yanille Patches"),
		LLETYA_PATCHES(7, SKILLING_FARMING, "Letya Patches"),
		FALADOR_SOUTH_PATCHES(8, SKILLING_FARMING, "Falador South Patches"),

		GNOME_COURSE(3, SKILLING_AGILITY, "Gnome Agility Course"),
		BARB_COURSE(4, SKILLING_AGILITY, "Barbarian Agility Course"),
		WILDY_COURSE(5, SKILLING_AGILITY, "Wilderness Agility Course"),
		PYRAMID_COURSE(6, SKILLING_AGILITY, "Pyramid Agility Course"),
		APE_COURSE(7, SKILLING_AGILITY, "Ape Atoll Agility Course"),

		ARDOUGNE_STALLS(3, SKILLING_THIEVING, "Ardougne Stalls"),
		DRAYNOR_STALLS(4, SKILLING_THIEVING, "Draynor Stalls"),

		AIR_ALTAR(3, SKILLING_RUNECRAFTING, "Air Altar", 1),
		MIND_ALTAR(4, SKILLING_RUNECRAFTING, "Mind Altar", 2),
		WATER_ALTAR(5, SKILLING_RUNECRAFTING, "Water Altar", 5),
		EARTH_ALTAR(6, SKILLING_RUNECRAFTING, "Earth Altar", 9),
		FIRE_ALTAR(7, SKILLING_RUNECRAFTING, "Fire Altar", 14),
		BODY_ALTAR(8, SKILLING_RUNECRAFTING, "Body Altar", 20),
		COSMIC_ALTAR(9, SKILLING_RUNECRAFTING, "Cosmic Altar", 27),
		CHAOS_ALTAR(10, SKILLING_RUNECRAFTING, "Chaos Altar", 35),
		ASTRAL_ALTAR(11, SKILLING_RUNECRAFTING, "Astral Altar", 40),
		NATURE_ALTAR(12, SKILLING_RUNECRAFTING, "Nature Altar", 44),
		LAW_ALTAR(13, SKILLING_RUNECRAFTING, "Law Altar", 54),
		DEATH_ALTAR(14, SKILLING_RUNECRAFTING, "Death Altar", 65),
		BLOOD_ALTAR(15, SKILLING_RUNECRAFTING, "Blood Altar", 77),
		OURANIA_ALTAR(16, SKILLING_RUNECRAFTING, "Ourania Altar", 1),
		ABYSS(17, SKILLING_RUNECRAFTING, "Abyss Alters", 1),

		HUNTER_ISLAND(3, SKILLING_HUNTER, "Hunter Island", 1),
		FALCONRY(4, SKILLING_HUNTER, "Falconry Training", 43),
		GRENWALL(5, SKILLING_HUNTER, "Grenwall Hunter", 77),

		ROCK_CRABS(3, MONSTERS, "Rock Crabs"),
		GOBLINS(4, MONSTERS, "Goblins"),
		MINOTAURS(5, MONSTERS, "Minotaurs"),
		YAKS(6, MONSTERS, "Yaks"),
		OGRES(7, MONSTERS, "Ogres"),
		OGRECAGE(8, MONSTERS, "Ogre Cage"),
		COCKROACHES(9, MONSTERS, "Cockroaches"),
		MONKEY_GUARD(10, MONSTERS, "Monkey Guards"),
		JUNG_WYRM(11, MONSTERS, "Jungle Strykewyrm"),
		DESERT_WYRM(12, MONSTERS, "Desert Strykewyrm"),
		ICE_WYRM(13, MONSTERS, "Ice Strykewyrm"),

		SLAYER_TOWER(3, DUNGEONS, "Slayer Tower"),
		TAVERLY(4, DUNGEONS, "Taverly Dungeon"),
		BRIMHAVEN(5, DUNGEONS, "Brimhaven Dungeon"),
		WATERBIRTH(6, DUNGEONS, "Waterbirth Dungeon"),
		SECURITYDUNGEON(7, DUNGEONS, "Stronghold of Security"),
		STRONGHOLD(8, DUNGEONS, "Stronghold of Safety"),
		ANCIENTCAVERN(9, DUNGEONS, "Ancient Cavern"),
		FREMENNIK_SLAYER(10, DUNGEONS, "Fremennik Slayer Dungeon"),
		ASGARNIA_ICE(11, DUNGEONS, "Asgarnia Ice Dungeon"),
		KALPHITE(12, DUNGEONS, "Kalphite Hive"),
		TZHAAR(13, DUNGEONS, "Tzhaar Area"),
		JADINKO(14, DUNGEONS, "Jadinko Lair"),
		LIVINGROCK(15, DUNGEONS, "Living Rock Cavern"),
		FORINTHRY(16, DUNGEONS, true, "Forinthry Dungeon"),
		DUNGEONEERING(17, DUNGEONS, "Dungeoneering"),
		IKOV_TEMPLE(18, DUNGEONS, "Temple of Ikov Dungeon"),

		GODWARS(3, BOSSES, "Godwars"),
		CORP(4, BOSSES, "Corpreal Beast"),
		KQ(5, BOSSES, "Kalphite Queen"),
		TD(6, BOSSES, "Tormented Demons"),
		DAG_KINGS(7, BOSSES, "Dagannoth kings"),
		LEEUNI(8, BOSSES, "Leeuni"),
		NOMAD(9, BOSSES, "Nomad"),
		BORK(10, BOSSES, "Bork"),
		KBD(11, BOSSES, true, "King Black Dragon"),
		CHAOS_ELEMENTAL(12, BOSSES, true, "Chaos Elemental"),

		FIGHTCAVES(3, MINIGAMES, "Fight Caves"),
		FIGHTKILN(4, MINIGAMES, "Fight Kiln"),
		PESTCONTROL(5, MINIGAMES, "Pest Control"),
		BARROWS(6, MINIGAMES, "Barrows"),
		WARRIORGUILD(7, MINIGAMES, "Warrior Guild"),
		DUELARENA(8, MINIGAMES, "Duel Arena"),
		CLANWARS(9, MINIGAMES, "Clan Wars"),
		DOMINION(10, MINIGAMES, "Dominion Tower"),
		CASTLE_WARS(11, MINIGAMES, "Castle Wars"),
		OURANIA_ALTAR$(12, MINIGAMES, "Ourania Altar"),

		WEST_DRAGONS(3, WILDERNESS, true, "West Dragons"),
		EAST_DRAGONS(4, WILDERNESS, true, "East Dragons"),
		CHAOS_WILDY_ALTAR(5, WILDERNESS, true, "Chaos Altar"),
		PORTS1(6, WILDERNESS, true, "13 Ports - Multi"),
		PORTS2(7, WILDERNESS, true, "17 Ports - Multi"),
		PORTS3(8, WILDERNESS, true, "26 Ports"),
		PORTS4(9, WILDERNESS, true, "35 Ports"),
		PORTS5(10, WILDERNESS, true, "44 Ports"),
		PORTS6(11, WILDERNESS, true, "50 Ports - Multi"),
		MAGEBANK(12, WILDERNESS, "Mage Bank"),
		WILDYAGILITY(13, WILDERNESS, true, "Wilderness Agility");

		private final int componentId;
		private final int category;
		private final String text;
		public final boolean dangerous;
		public final String order;
		public final boolean skilling;
		private final int levelReq;

		TeleportTabData(String order, int componentId, int category, String text) {
			this(order, componentId, category, text, false, null, false, 0);
		}

		TeleportTabData(int componentId, int category, String text) {
			this(null, componentId, category, text, false, null, false, 0);
		}

		TeleportTabData(int componentId, int category, String text, boolean skilling) {
			this(null, componentId, category, text, false, null, skilling, 0);
		}

		TeleportTabData(int componentId, int category, String text, int levelReq) {
			this(null, componentId, category, text, false, null, false, levelReq);
		}

		TeleportTabData(int componentId, int category, boolean dangerous, String text) {
			this(null, componentId, category, text, dangerous, null, false, 0);
		}

		TeleportTabData(String order, int componentId, int category, String text,
								boolean dangerous, Consumer<Player> consumer,
								boolean skilling, int levelReq) {
			this.order = order;
			this.componentId = componentId;
			this.category = category;
			this.text = text;
			this.dangerous = dangerous;
			this.skilling = skilling;
			this.levelReq = levelReq;
		}

		public int getComponentId() {
			return componentId;
		}

		public int getCategory() {
			return category;
		}

		public String getText() {
			return text;
		}

		public boolean isDangerous() {
			return dangerous;
		}

		public boolean isSkilling() {
			return skilling;
		}

		public int getLevelReq() {
			return levelReq;
		}
	}

	public static void open(Player player) {
		int rows = TELEPORT_TABS.length;
		initializeTeleportTab(player);
		displayMainTeleportOptions(player);
		refreshScrollbar(player, rows);
	}

	private static void initializeTeleportTab(Player player) {
		sendComponents(player);
		hideAllComponents(player);
		player.getTemporaryAttributtes().remove("ACHIEVEMENTTAB");
		player.getTemporaryAttributtes().remove("DANGEROUSTELEPORT");
		player.getTemporaryAttributtes().remove("TELEPORTTYPE");
		player.getTemporaryAttributtes().put("CUSTOMTAB", 1);
		player.getPackets().sendHideIComponent(3002, BACK_BUTTON, false);
		player.getPackets().sendHideIComponent(3002, FORWARD_BUTTON, false);
		player.getPackets().sendSpriteOnIComponent(3002, GREEN_STAR_COMP, GREEN_HIGHLIGHTED);
	}

	private static void hideAllComponents(Player player) {
		for (int i = 3; i <= 22; i++) {
			player.getPackets().sendHideIComponent(3002, i, true);
		}
		for (int i = 28; i <= 56; i++) {
			player.getPackets().sendHideIComponent(3002, i, true);
		}
	}

	private static void displayMainTeleportOptions(Player player) {
		for (TeleportsTab tab : TELEPORT_TABS) {
			if (tab != null) {
				player.getPackets().sendHideIComponent(3002, tab.getComponentId(), false);
				if (tab.getText() != null) {
					player.getPackets().sendTextOnComponent(3002, tab.getComponentId(),
							(tab.getComponentId() == 25 ? "" : "<col=f4ee42>") + tab.getText());
				}
			}
		}
	}

	private static void sendDangerousTeleport(Player player, WorldTile tile) {
		sendHideComponents(player);
		player.getTemporaryAttributtes().put("DANGEROUSTELEPORT", tile);
		player.getPackets().sendHideIComponent(3002, BACK_BUTTON, false);
		player.getPackets().sendHideIComponent(3002, 3, false);
		player.getPackets().sendHideIComponent(3002, 7, false);
		player.getPackets().sendHideIComponent(3002, 9, false);
		player.getPackets().sendHideIComponent(3002, BLUE_STAR_COMP, false);
		player.getPackets().sendTextOnComponent(3002, 3,
				"<br><br>This is a <col=BB0404>dangerous</col> teleport, <br>Are you sure you want <br>to teleport?");
		player.getPackets().sendTextOnComponent(3002, 7, "<col=04BB3B>Yes</col>, I want to teleport.");
		player.getPackets().sendTextOnComponent(3002, 9, "<col=BB0404>No</col>, I don't want to teleport.");
		player.getPackets().sendTextOnComponent(3002, 25, "Dangerous!");
		player.getPackets().sendSpriteOnIComponent(3002, BLUE_STAR_COMP, 439);
		refreshScrollbar(player, 3);
	}

	private static void sendTeleport(Player player, WorldTile tile, int type) {
		if (WildernessControler.getWildLevel(player) >= 20 && player.isAtWild()) {
			player.getPackets().sendGameMessage("You can't use this teleport deeper than 20 wilderness.");
			openTeleports(player, type);
			return;
		}
		if (player.isInCombat()) {
			player.getPackets().sendGameMessage("You can't use this teleport in combat.");
			return;
		}
		player.getTemporaryAttributtes().remove("PREVIOUSTELEPORT");
		SpellHandler.INSTANCE.sendTeleportSpell(player, tile);
		open(player);
		player.getTemporaryAttributtes().put("PREVIOUSTELEPORT", tile);
	}

	public static void handleButtons(Player player, int componentId) {
		if (player.isLocked()) {
			return;
		}

		Integer type = (Integer) player.temporaryAttribute().get("TELEPORTTYPE");
		WorldTile dangerTile = (WorldTile) player.getTemporaryAttributtes().get("DANGEROUSTELEPORT");
		WorldTile previousTile = (WorldTile) player.getTemporaryAttributtes().get("PREVIOUSTELEPORT");
		if (dangerTile != null) {
			handleDangerousTeleportButtons(player, componentId, type, dangerTile);
			return;
		}

		if (componentId == BACK_BUTTON && type != null) {
			handleBackButton(player, type);
			return;
		}

		if (type != null && type >= 0) {
			handleTeleportSelection(player, componentId, type);
		} else {
			handleMainMenuButtons(player, componentId, previousTile);
		}
	}

	private static void handleDangerousTeleportButtons(Player player, int componentId, Integer type, WorldTile dangerTile) {
		if (componentId == BACK_BUTTON || componentId == 9) {
			openTeleports(player, type);
		} else if (componentId == 7) {
			sendTeleport(player, dangerTile, type);
		}
	}

	private static void handleBackButton(Player player, Integer type) {
		if (type != null && type >= 0) {
			if (type >= SKILLING_MINING) {
				openTeleports(player, SKILLING);
			} else {
				open(player);
			}
		}
	}

	private static void handleTeleportSelection(Player player, int componentId, int type) {
		for (TeleportTabData teleport : TELEPORT_DATA) {
			if (teleport != null && teleport.getCategory() == type && teleport.getComponentId() == componentId) {
				processTeleportSelection(player, teleport, type);
				return;
			}
		}
	}

	private static void processTeleportSelection(Player player, TeleportTabData teleport, int type) {
		if (teleport.isSkilling()) {
			openSkillingTeleports(player, teleport.getComponentId() + 10);
			return;
		}

		TeleportLocations location = TeleportLocations.getLocation(teleport.name());
		if (location == null) {
			player.message("This teleport is not handled yet.");
			open(player);
			return;
		}

		if (teleport.isDangerous()) {
			sendDangerousTeleport(player, location.getLocation());
		} else {
			sendTeleport(player, location.getLocation(), type);
		}
	}

	private static void handleMainMenuButtons(Player player, int componentId, WorldTile previousTile) {
		player.temporaryAttribute().remove("TELEPORTTYPE");
		if (componentId >= 3 && componentId <= 15) {
			if (componentId == 11) {
				handlePreviousTeleport(player, previousTile);
				return;
			}
			openTeleports(player, componentId - 3);
			return;
		}

		switch (componentId) {
			case FORWARD_BUTTON:
				SettingsTab.open(player);
				break;
			case BACK_BUTTON:
				JournalTab.open(player);
				break;
		}
	}

	private static void handlePreviousTeleport(Player player, WorldTile previousTile) {
		if (previousTile != null) {
			sendTeleport(player, previousTile, -1);
		} else {
			player.getPackets().sendGameMessage("You don't have any previous teleport location.");
		}
	}

	private static void openSkillingTeleports(Player player, int type) {
		initializeTeleportCategory(player, type);

		String categoryName = getSkillingCategoryName(type);
		player.getPackets().sendTextOnComponent(3002, 25, categoryName);

		displayTeleportOptions(player, type);
	}

	private static String getSkillingCategoryName(int type) {
        return switch (type) {
            case SKILLING_MINING -> "Mining";
            case SKILLING_SMITHING -> "Smithing";
            case SKILLING_FISHING -> "Fishing";
            case SKILLING_COOKING -> "Cooking";
            case SKILLING_WOODCUTTING -> "Woodcutting";
            case SKILLING_FARMING -> "Farming";
            case SKILLING_AGILITY -> "Agility";
            case SKILLING_THIEVING -> "Thieving";
            case SKILLING_HUNTER -> "Hunter";
            default -> "Runecrafting";
        };
	}

	public static void openTeleports(Player player, int type) {
		int rows = countTeleportsForCategory(type);
		initializeTeleportCategory(player, type);
		String categoryName = getMainCategoryName(type);
		player.getPackets().sendTextOnComponent(3002, 25, categoryName);

		displayTeleportOptions(player, type);

		refreshScrollbar(player, rows);
	}


	private static String getMainCategoryName(int type) {
        return switch (type) {
            case CITY_TELEPORTS -> "City Teleports";
            case SKILLING -> "Skilling Teleports";
            case MONSTERS -> "Monster Teleports";
            case DUNGEONS -> "Dungeon Teleports";
            case BOSSES -> "Boss Teleports";
            case MINIGAMES -> "Minigame Teleports";
            default -> "Wilderness Teleports";
        };
	}

	private static void initializeTeleportCategory(Player player, int type) {
		sendComponents(player);
		hideCategoryComponents(player);
		player.getTemporaryAttributtes().remove("DANGEROUSTELEPORT");
		player.getTemporaryAttributtes().put("TELEPORTTYPE", type);
		player.getPackets().sendHideIComponent(3002, 27, true);
		player.getPackets().sendSpriteOnIComponent(3002, GREEN_STAR_COMP, GREEN_HIGHLIGHTED);
	}

	private static void hideCategoryComponents(Player player) {
		for (int i = 3; i <= 15; i++) {
			player.getPackets().sendHideIComponent(3002, i, true);
		}
	}

	private static void displayTeleportOptions(Player player, int type) {
		for (TeleportTabData teleport : TELEPORT_DATA) {
			if (teleport != null && (teleport.getCategory() == type || teleport.getCategory() == -1)) {
				player.getPackets().sendHideIComponent(3002, teleport.getComponentId(), false);
				if (teleport.getText() != null) {
					String text = buildTeleportText(teleport);
					player.getPackets().sendTextOnComponent(3002, teleport.getComponentId(), text);
				}
			}
		}
	}

	private static String buildTeleportText(TeleportTabData teleport) {
		StringBuilder text = new StringBuilder();

		if (teleport.order != null) {
			text.append(teleport.order).append("-");
		}

		text.append(teleport.isDangerous() ? "<col=BB0404>" : "<col=f4ee42>")
				.append(teleport.getText());

		if (teleport.isDangerous()) {
			text.append(" - Dangerous!");
		}

		if (teleport.getLevelReq() > 0) {
			text.append(" - Level: ").append(teleport.getLevelReq());
		}

		return text.toString();
	}

	private static int countTeleportsForCategory(int type) {
		int count = 0;

		for (TeleportTabData teleport : TELEPORT_DATA) {
			if (teleport != null &&
					(teleport.getCategory() == type || teleport.getCategory() == -1)) {
				count++;
			}
		}

		return count;
	}
}