package com.rs.java.utils;

import java.util.ArrayList;

import com.rs.java.game.World;
import com.rs.java.game.player.AccountCreation;
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
		return ((AccountCreation.exists(Utils.formatPlayerNameForProtocol(name))) || cachedNames.contains(name)
				|| AccountCreation.exists(name) || Utils.invalidAccountName(name));
	}

	public static boolean setDisplayName(Player player, String displayName) {
		synchronized (cachedNames) {
			if (Unavailable(player, displayName)) {
				player.message("This username is not available.");
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
