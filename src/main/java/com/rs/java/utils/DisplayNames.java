package com.rs.java.utils;

import java.util.ArrayList;

import com.dropbox.core.v2.users.Account;
import com.rs.java.game.World;
import com.rs.kotlin.game.player.AccountCreation;
import com.rs.java.game.player.Player;

public final class DisplayNames {

	private static ArrayList<String> cachedNames;

	private DisplayNames() {

	}

	public static void init() {
		cachedNames = SerializableFilesManager.loadDisplayNames();
	}

	public static void save() {
		SerializableFilesManager.saveDisplayNames(cachedNames);
	}

	public static boolean Unavailable(Player player, String name) {

		String requested = Utils.formatPlayerNameForProtocol(name);
		String current = Utils.formatPlayerNameForProtocol(player.getUsername());

		if (requested.equals(current))
			return false;

		return AccountCreation.exists(requested)
				|| cachedNames.contains(name)
				|| Utils.invalidAccountName(name);
	}


	public static boolean setUsername(Player player, String username) {

		String oldName = player.getUsername();
		String newName = username.trim();

		synchronized (cachedNames) {
			if (Unavailable(player, newName)) {
				player.message("This username is not available.");
				return false;
			}
		}

		for (String blockedNames : Credentials.blocked) {
			if (newName.contains(blockedNames)) {
				player.message("Name not available.");
				return false;
			}
		}


		if (!oldName.equalsIgnoreCase(newName)) {
			AccountCreation.rename(oldName, newName);
		}
		player.setUsername(newName);
		AccountCreation.savePlayer(player);
		player.getPackets().sendFriends();

		for (Player p : World.getPlayers()) {
			if (!p.getFriendsIgnores().getFriends().contains(player.getUsername()))
				continue;

			if (p.getCurrentFriendChat() != null)
				p.getCurrentFriendChat().refreshChannel();

			p.getFriendsIgnores().refreshChatName();
			p.getPackets().sendFriends();
		}

		player.getAppearence().generateAppearenceData();

		player.getPackets().sendGameMessage(
				"Changed your username from " + oldName + " to " + newName + "."
		);

		return true;
	}

	public static boolean setDisplayName(Player player, String displayName) {
		synchronized (cachedNames) {
			if (Unavailable(player, displayName)) {
				player.message("This display name is not available.");
				return false;
			}
		}
		for (String blockedNames : Credentials.blocked) {
			if (displayName.contains(blockedNames)) {
				player.message("Name not available.");
				return false;
			}
		}
		player.setDisplayName(Utils.formatPlayerNameForDisplay(displayName));
		player.getPackets().sendFriends();
		for (Player p : World.getPlayers()) {
			if (!p.getFriendsIgnores().getFriends().contains(player.getUsername()))
				continue;
			if (p.getCurrentFriendChat() != null)
				p.getCurrentFriendChat().refreshChannel();
			p.getFriendsIgnores().refreshChatName();
			p.getPackets().sendFriends();
		}
		player.getAppearence().generateAppearenceData();
		player.getPackets().sendGameMessage("Changed display name!");
		return true;
	}

	public static boolean removeDisplayName(Player player) {
		if (!player.hasDisplayName())
			return false;
		synchronized (cachedNames) {
			cachedNames.remove(player.getDisplayName());
		}
		player.setDisplayName(null);
		for (Player p : World.getPlayers()) {
			if (p.getClanManager() != null) {
				p.getClanManager().refreshClanChannel();
				p.getClanManager().generateClanChannelDataBlock();
			}
			if (p.getCurrentFriendChat() != null)
				p.getCurrentFriendChat().refreshChannel();
			p.getFriendsIgnores().refreshChatName();
		}
		player.getAppearence().generateAppearenceData();
		return true;
	}
}
