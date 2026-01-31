package com.rs.java.game.player.content;

import com.rs.java.game.World;
import com.rs.java.game.player.Player;

public class Skulls {

	public static void checkSkulls(Player player) {
		if (player.getPlayerKillcount() < 50) {
			player.skullId = 0;
			player.getAppearence().generateAppearenceData();
		}
		if (player.getPlayerKillcount() >= 50 && player.getPlayerKillcount() < 100 && player.skullId != 1) {
			player.skullId = 1;
			player.getAppearence().generateAppearenceData();
			World.sendWorldMessage("<img=7><col=36648b>News: " + player.getDisplayName() + " has achieved "
					+ player.getPlayerKillcount() + " kills.", false);
		}
		if (player.getPlayerKillcount() >= 100 && player.getPlayerKillcount() < 250 && player.skullId != 6) {
			player.skullId = 6;
			player.getAppearence().generateAppearenceData();
			World.sendWorldMessage("<img=7><col=36648b>News: " + player.getDisplayName() + " has achieved "
					+ player.getPlayerKillcount() + " kills.", false);
		}
		if (player.getPlayerKillcount() >= 250 && player.getPlayerKillcount() < 500 && player.skullId != 5) {
			player.skullId = 5;
			player.getAppearence().generateAppearenceData();
			World.sendWorldMessage("<img=7><col=36648b>News: " + player.getDisplayName() + " has achieved "
					+ player.getPlayerKillcount() + " kills.", false);
		}
		if (player.getPlayerKillcount() >= 500 && player.getPlayerKillcount() < 750 && player.skullId != 4) {
			player.skullId = 4;
			player.getAppearence().generateAppearenceData();
			World.sendWorldMessage("<img=7><col=36648b>News: " + player.getDisplayName() + " has achieved "
					+ player.getPlayerKillcount() + " kills.", false);
		}
		if (player.getPlayerKillcount() >= 750 && player.getPlayerKillcount() < 1000 && player.skullId != 3) {
			player.skullId = 3;
			player.getAppearence().generateAppearenceData();
			World.sendWorldMessage("<img=7><col=36648b>News: " + player.getDisplayName() + " has achieved "
					+ player.getPlayerKillcount() + " kills.", false);
		}
		if (player.getPlayerKillcount() >= 1000 && player.skullId != 2) {
			player.skullId = 2;
			player.getAppearence().generateAppearenceData();
			World.sendWorldMessage("<img=7><col=36648b>News: " + player.getDisplayName() + " has achieved "
					+ player.getPlayerKillcount() + " kills.", false);
		}
	}

	public static void checkSkullId(Player player) {
		if (player.getPlayerKillcount() < 50) {
			player.skullId = 0;
			player.getAppearence().generateAppearenceData();
		}
		if (player.getPlayerKillcount() >= 50 && player.getPlayerKillcount() < 100 && player.skullId != 1) {
			player.skullId = 1;
			player.getAppearence().generateAppearenceData();
		}
		if (player.getPlayerKillcount() >= 100 && player.getPlayerKillcount() < 250 && player.skullId != 6) {
			player.skullId = 6;
			player.getAppearence().generateAppearenceData();
		}
		if (player.getPlayerKillcount() >= 250 && player.getPlayerKillcount() < 500 && player.skullId != 5) {
			player.skullId = 5;
			player.getAppearence().generateAppearenceData();
		}
		if (player.getPlayerKillcount() >= 500 && player.getPlayerKillcount() < 750 && player.skullId != 4) {
			player.skullId = 4;
			player.getAppearence().generateAppearenceData();
		}
		if (player.getPlayerKillcount() >= 750 && player.getPlayerKillcount() < 1000 && player.skullId != 3) {
			player.skullId = 3;
			player.getAppearence().generateAppearenceData();
		}
		if (player.getPlayerKillcount() >= 1000 && player.skullId != 2) {
			player.skullId = 2;
			player.getAppearence().generateAppearenceData();
		}
	}
}
