package com.rs.java.game.area;

import java.util.ArrayList;
import java.util.List;

import com.rs.java.game.WorldTile;
import com.rs.java.game.area.banks.*;
import com.rs.java.game.area.multi.*;
import com.rs.java.game.area.zones.WildernessArea;
import com.rs.java.game.area.zones.WildernessSafeArea;
import com.rs.java.game.player.Player;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;

public class AreaManager {

	private static List<Area> areas;

	public static void init() {
		try {
			areas(new ArrayList<>());

			//multi
			areas().add(new WildernessMulti());
			areas().add(new AlkharidMulti());
			areas().add(new WhiteWolfMountainMulti());
			areas().add(new ApeAtollMulti());
			areas().add(new PestControlMulti());
			areas().add(new FaladorMulti());
			areas().add(new BarbarianVillageMulti());
			areas().add(new BurthorpeMulti());
			areas().add(new CastleWarsMulti());
			areas().add(new PiscatorisColonyMulti());
			areas().add(new Islands());
			areas().add(new MorytaniaMulti());
			areas().add(new GodwarsMulti());
			areas().add(new KingBlackDragonMulti());
			areas().add(new WaterbirthDungeonMulti());
			areas().add(new KalphiteLairMulti());

			//zones
			areas().add(new WildernessArea());
			areas().add(new WildernessSafeArea());

			//pvp safezones
			areas().add(new EdgevilleBank());
			areas().add(new VarrockEastBank());
			areas().add(new VarrockWestBank());
			areas().add(new LumbridgeCastle());
			areas().add(new AlkharidBank());
			areas().add(new DuelArenaBank());
			areas().add(new CanifisBank());
			areas().add(new DraynorBank());
			areas().add(new FaladorEastBank());
			areas().add(new FaladorWestBank());
			areas().add(new CatherbyBank());
			areas().add(new CamelotBank());
			areas().add(new FishingGuildBank());
			areas().add(new ArdougneNorthBank());
			areas().add(new ArdougneSouthBank());
			areas().add(new CastleWarsBank());
			areas().add(new YanilleBank());
			areas().add(new OoglogBank());
			areas().add(new MobilisingArmiesBank());
			areas().add(new NardahBank());
			areas().add(new PortPhasmatysBank());
			areas().add(new MageBank());
			areas().add(new WarriorGuildBank());

		} catch (Exception e) {
			System.out.print(e);
		}
	}

	public static List<Area> getAll(final WorldTile location) {
		List<Area> result = new ArrayList<>();
		try {
			for (Area area : areas()) {
				if (area.contains(location)) {
					result.add(area);
				}
			}
		} catch (Exception e) {
			System.out.println("Error while checking areas: " + e);
		}
		return result;
	}

	public static Area get(final WorldTile location) {
		List<Area> matches = getAll(location);
		return matches.isEmpty() ? null : matches.get(0);
	}

	public static void update(final Player player, final Area area) {
		player.getPackets().sendTextOnComponent(1073, 10, "<col=ffffff>You have reached");
		player.getPackets().sendTextOnComponent(1073, 11, "<col=ffcff00>" + area.name());
		player.getInterfaceManager().sendTab(player.getInterfaceManager().hasRezizableScreen() ? 1 : 11, 1073);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getPackets().closeInterface(player.getInterfaceManager().hasRezizableScreen() ? 1 : 11);
				stop();
			}
		}, 3);
	}

	public static List<Area> areas() {
		return areas;
	}

	public static void areas(List<Area> list) {
		areas = list;
	}
}