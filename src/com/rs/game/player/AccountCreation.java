package com.rs.game.player;

import java.io.File;

import com.rs.json.GSONParser;


/**
* @author -Andreas 27 jan. 2020
* 
*/

public class AccountCreation {

	public static Player loadPlayer(String username) {
		return (Player) GSONParser.load(System.getProperty("user.dir") + "/data/characters/" + username + ".json", Player.class);
	}

	public static void savePlayer(Player player) {
		GSONParser.save(player, System.getProperty("user.dir") + "/data/characters/" + player.getDisplayName() + ".json", Player.class);
	}

	public static boolean exists(String username) {
		return new File(System.getProperty("user.dir") + "/data/characters/" + username + ".json").exists();
	}

}
