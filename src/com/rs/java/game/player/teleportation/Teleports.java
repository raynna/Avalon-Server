package com.rs.java.game.player.teleportation;

import com.rs.java.game.WorldTile;
import com.rs.kotlin.Rscm;

//TODO: Add Guild teleports page & options
public class Teleports {

	public enum TeleportLocations {

		ALKHARID("location.alkharid"),
		ARDOUGNE("location.ardougne"),
		BURTHORPE("location.burthorpe"),
		CAMELOT("location.camelot"),
		CATHERBY("location.catherby"),
		CANIFIS("location.canifis"),
		DRAYNOR("location.draynor"),
		EDGEVILLE("location.edgeville"),
		FALADOR("location.falador"),
		KARAMJA("location.karamja"),
		LUMBRIDGE("location.lumbridge"),
		LUNAR("location.lunar"),
		NEITIZNOT("location.neitiznot"),
		PISCATORIS("location.piscatoris"),
		RIMMINGTON("location.rimmington"),
		SHILOVILLAGE("location.shilovillage"),
		TREEGNOME("location.treegnome"),
		VARROCK("location.varrock"),
		YANILLE("location.yanille"),

		// MINING LOCATIONS
		ALKHARID_MINING("location.alkharid_mining"),
		FALADOR_MINING("location.falador_mining"),
		TZHAAR_MINING("location.tzhaar_mining"),
		YANILLE_MINING("location.yanille_mining"),
		RUNE_WILDY_MINING("location.rune_wildy_mining"),
		HERO_GUILD_MINING("location.hero_guild_mining"),
		CRANDOR_MINING("location.crandor_mining"),
		GEM_ROCK_MINING("location.gem_rock_mining"),

		// SMITHING LOCATIONS
		ALKHARID_FURNACE("location.alkharid_furnace"),
		WILDY_FURNACE("location.wildy_furnace"),
		NEIT_FURNACE("location.neit_furnace"),

		// FISHING LOCATIONS
		CATHERBY_FISHING("location.catherby_fishing"),
		DRAYNOR_FISHING("location.draynor_fishing"),
		FISHING_GUILD_FISHING("location.fishing_guild_fishing"),

		// COOKING LOCATIONS
		CATHERBY_RANGE("location.catherby_range"),
		CCOOKS_GUILD_RANGE("location.ccooks_guild_range"),

		// WOODCUTTING LOCATIONS
		CAMELOT_WOODCUTTING("location.camelot_woodcutting"),
		GE_WOODCUTTING("location.ge_woodcutting"),
		ETCETERIA_WOODCUTTING("location.etceteria_woodcutting"),
		PORT_SARIM_WOODCUTTING("location.port_sarim_woodcutting"),

		// FARMING LOCATIONS
		CATHERBY_PATCHES("location.catherby_patches"),
		PORT_FATASS_PATCHES("location.port_fatass_patches"),
		ARDY_PATCHES("location.ardy_patches"),
		YANILLE_PATCHES("location.yanille_patches"),
		LLETYA_PATCHES("location.lleyta_patches"),
		FALADOR_SOUTH_PATCHES("location.falador_south_patches"),

		// AGILITY LOCATIONS
		GNOME_COURSE("location.gnome_course"),
		BARB_COURSE("location.barb_course"),
		WILDY_COURSE("location.wildy_course"),
		PYARMID_COURSE("location.pyramid_course"),
		APE_COURSE("location.ape_course"),

		// THIEVING LOCATIONS
		ARDOUGNE_STALLS("location.ardougne_stalls"),
		DRAYNOR_STALLS("location.draynor_stalls"),

		// RUNECRAFTING LOCATIONS
		AIR_ALTAR("location.air_altar"),
		MIND_ALTAR("location.mind_altar"),
		WATER_ALTAR("location.water_altar"),
		EARTH_ALTAR("location.earth_altar"),
		FIRE_ALTAR("location.fire_altar"),
		BODY_ALTAR("location.body_altar"),
		COSMIC_ALTAR("location.cosmic_altar"),
		CHAOS_ALTAR("location.chaos_altar"),
		ASTRAL_ALTAR("location.astral_altar"),
		NATURE_ALTAR("location.nature_altar"),
		LAW_ALTAR("location.law_altar"),
		DEATH_ALTAR("location.death_altar"),
		BLOOD_ALTAR("location.blood_altar"),
		OURANIA_ALTAR("location.ourania_altar"),
		ABYSS("location.abyss"),

		HUNTER_ISLAND("location.hunter_island"),
		FALCONRY("location.falconry"),
		GRENWALL("location.grenwall"),

		// MONSTER TELEPORTS
		ROCK_CRABS("location.rock_crabs"),
		GOBLINS("location.goblins"),
		MINOTAURS("location.minotaurs"),
		YAKS("location.yaks"),
		OGRES("location.ogres"),
		OGRECAGE("location.ogrecage"),
		COCKROACHES("location.cockroaches"),
		JUNG_WYRM("location.jung_wyrm"),
		DESERT_WYRM("location.desert_wyrm"),
		ICE_WYRM("location.ice_wyrm"),
		MONKEY_GUARD("location.monkey_guard"),

		// DUNGEON/SLAYER TELEPORTS
		SLAYER_TOWER("location.slayer_tower"),
		TAVERLY("location.taverly"),
		BRIMHAVEN("location.brimhaven"),
		WATERBIRTH("location.waterbirth"),
		SECURITYDUNGEON("location.securitydungeon"),
		STRONGHOLD("location.stronghold"),
		ANCIENTCAVERN("location.ancientcavern"),
		FREMENNIK_SLAYER("location.fremennik_slayer"),
		ASGARNIA_ICE("location.asgarnia_ice"),
		KALPHITE("location.kalphite"),
		TZHAAR("location.tzhaar"),
		JADINKO("location.jadinko"),
		LIVINGROCK("location.livingrock"),
		FORINTHRY("location.forinthry"),
		DUNGEONEERING("location.dungeoneering"),
		IKOV_TEMPLE("location.ikov_temple"),

		// BOSS TELEPORTS
		GODWARS("location.godwars"),
		CORP("location.corp"),
		KQ("location.kq"),
		TD("location.td"),
		DAG_KINGS("location.dag_kings"),
		LEEUNI("location.leeuni"),
		PEST_QUEEN("location.pest_queen"),
		NOMAD("location.nomad"),
		BORK("location.bork"),
		KBD("location.kbd"),
		CHAOS_ELEMENTAL("location.chaos_elemental"),
		KALPHITE_QUEEN("location.kalphite_queen"),
		CORPOREAL_BEAST("location.corporeal_beast"),
		QUEEN_BLACK_DRAGON("location.queen_black_dragon"),

		// MINIGAME TELEPORTS
		FIGHTCAVES("location.fightcaves"),
		FIGHTKILN("location.fightkiln"),
		PESTCONTROL("location.pestcontrol"),
		WARRIORGUILD("location.warriorguild"),
		DUELARENA("location.duelarena"),
		CLANWARS("location.clanwars"),
		DOMINION("location.dominion"),
		CASTLE_WARS("location.castle_wars"),
		BARROWS("location.barrows"),

		// WILDERNESS TELEPORTS
		WEST_DRAGONS("location.west_dragons"),
		EAST_DRAGONS("location.east_dragons"),
		CHAOS_WILDY_ALTAR("location.chaos_wildy_altar"),
		PORTS1("location.ports1"),
		PORTS2("location.ports2"),
		PORTS3("location.ports3"),
		PORTS4("location.ports4"),
		PORTS5("location.ports5"),
		PORTS6("location.ports6"),
		MAGEBANK("location.magebank"),
		WILDYAGILITY("location.wildyagility"),
		EDGEVILLE_PVP_INSTANCE("location.edgeville_pvp_instance");

		private final WorldTile tile;

		TeleportLocations(String location) {
			this.tile = new WorldTile(Rscm.lookupLocation(location));
		}

		public WorldTile getLocation() {
			return tile;
		}

		public static TeleportLocations getLocation(String name) {
			for (TeleportLocations teleport : TeleportLocations.values()) {
				if (teleport.name().toLowerCase().replace("_", " ").contains(name.toLowerCase().replace("_", " ").replace("$", "")))
					return teleport;
			}
			return null;
		}
	}

}
