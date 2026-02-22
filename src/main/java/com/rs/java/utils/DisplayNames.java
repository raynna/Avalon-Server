package com.rs.java.utils;

import java.util.ArrayList;

import com.rs.java.game.World;
import com.rs.java.game.player.content.friendschat.FriendChatsManager;
import com.rs.kotlin.game.player.AccountCreation;
import com.rs.java.game.player.Player;

public final class DisplayNames {

	/**
	 * IMPORTANT:
	 * We store protocol-form usernames here (Utils.formatPlayerNameForProtocol).
	 *
	 * This list acts as a "reservation list" for PENDING username changes,
	 * so other players cannot create/login with that name until it's applied or released.
	 */
	private static ArrayList<String> cachedNames;

	private DisplayNames() { }

	public static void init() {
		cachedNames = SerializableFilesManager.loadDisplayNames();
		if (cachedNames == null)
			cachedNames = new ArrayList<>();
	}

	public static void save() {
		SerializableFilesManager.saveDisplayNames(cachedNames);
	}

	/**
	 * Returns true if this name is currently reserved by someone (pending rename).
	 */
	public static boolean isReserved(String nameRaw) {
		String requested = Utils.formatPlayerNameForProtocol(nameRaw);
		synchronized (cachedNames) {
			return cachedNames.contains(requested);
		}
	}

	/**
	 * Name availability check used for queueing.
	 * (This is NOT the login creation check - login will also check reservation.)
	 */
	public static boolean Unavailable(Player player, String nameRaw) {

		String requested = Utils.formatPlayerNameForProtocol(nameRaw);
		String current   = Utils.formatPlayerNameForProtocol(player.getUsername());

		if (requested.equals(current))
			return false;

		// block list (protocol compare)
		for (String bad : Credentials.blocked) {
			String badP = Utils.formatPlayerNameForProtocol(bad);
			if (!badP.isEmpty() && requested.contains(badP))
				return true;
		}

		return AccountCreation.exists(requested)
				|| cachedNames.contains(requested) // reserved counts as unavailable
				|| Utils.invalidAccountName(nameRaw);
	}

	/**
	 * Queue a username change and RESERVE the target name until logout.
	 * The rename is applied in applyPendingUsername(player) (call on logout).
	 */
	public static boolean queueUsernameChange(Player player, String username) {

		String newProtocol = Utils.formatPlayerNameForProtocol(username == null ? "" : username.trim());
		String oldProtocol = Utils.formatPlayerNameForProtocol(player.getUsername());

		if (newProtocol.isEmpty() || Utils.invalidAccountName(newProtocol)) {
			player.message("That username is not valid.");
			return false;
		}

		if (newProtocol.equals(oldProtocol)) {
			player.message("That is already your username.");
			return false;
		}

		// If they already have a pending name, release it first.
		releasePendingReservation(player);

		synchronized (cachedNames) {
			if (Unavailable(player, newProtocol)) {
				player.message("This username is not available.");
				return false;
			}

			cachedNames.add(newProtocol);
			save();
		}

		player.setPendingUsername(newProtocol);
		player.getPackets().sendGameMessage(
				"Your username will change to " + Utils.formatPlayerNameForDisplay(newProtocol) + " when you log out."
		);
		return true;
	}

	/**
	 * Release reservation if player changes their mind or if pending becomes invalid.
	 */
	public static void releasePendingReservation(Player player) {
		if (player == null || !player.hasPendingUsername())
			return;

		String pending = Utils.formatPlayerNameForProtocol(player.getPendingUsername());
		if (pending == null || pending.isEmpty()) {
			player.setPendingUsername(null);
			return;
		}

		synchronized (cachedNames) {
			cachedNames.remove(pending);
			save();
		}

		player.setPendingUsername(null);
	}

	/**
	 * Apply pending rename:
	 * - Renames files/account storage
	 * - Updates player's username
	 * - Releases reservation
	 * - Saves player
	 *
	 * Call this during logout BEFORE the player is removed from world lists.
	 */
	public static void applyPendingUsername(Player player) {
		if (player == null || !player.hasPendingUsername())
			return;

		String oldProtocol = Utils.formatPlayerNameForDisplay(player.getUsername());
		String newProtocol = Utils.formatPlayerNameForDisplay(player.getPendingUsername());

		if (newProtocol == null || newProtocol.isEmpty() || oldProtocol.equals(newProtocol)) {
			releasePendingReservation(player);
			return;
		}

		// Safety: if someone somehow managed to create it anyway, cancel.
		// (After LoginPacketsDecoder changes, this should never happen.)
		if (AccountCreation.exists(newProtocol)) {
			player.getPackets().sendGameMessage(
					"Your pending username '" + Utils.formatPlayerNameForDisplay(newProtocol)
							+ "' is no longer available. Please pick another."
			);
			releasePendingReservation(player);
			return;
		}

		// Rename account storage
		AccountCreation.rename(oldProtocol, newProtocol);

		// Apply to player
		player.setUsername(newProtocol);
		player.setPendingUsername(null);

		// Release reservation
		synchronized (cachedNames) {
			cachedNames.remove(newProtocol);
			save();
		}

		// Save player under new name
		AccountCreation.savePlayer(player);

		// Refresh friends/chat displays for everyone online
		player.getPackets().sendFriends();
		for (Player p : World.getPlayers()) {
			if (p == null)
				continue;

			// If they have the renamed player on friends list, refresh
			if (p.getFriendsIgnores() != null
					&& p.getFriendsIgnores().getFriends() != null
					&& p.getFriendsIgnores().getFriends().contains(player.getUsername())) {

				if (p.getCurrentFriendChat() != null)
					p.getCurrentFriendChat().refreshChannel();

				p.getFriendsIgnores().refreshChatName();
				p.getPackets().sendFriends();
			}
		}

		player.getAppearance().generateAppearenceData();
	}

	// ---- existing display name methods kept as-is, just fixed minor protocol usage ----

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
		player.setDisplayName(displayName);
		player.getPackets().sendFriends();
		player.getFriendsIgnores().refreshChatName();
		if (player.getCurrentFriendChat() != null)
			player.getCurrentFriendChat().refreshChannel();
		for (Player p : World.getPlayers()) {
			if (p == null)
				continue;
			if (!p.getFriendsIgnores().getFriends().contains(player.getUsername()))
				continue;
			if (p.getCurrentFriendChat() != null)
				p.getCurrentFriendChat().refreshChannel();
			p.getFriendsIgnores().refreshChatName();
			p.getPackets().sendFriends();
		}
		player.getAppearance().generateAppearenceData();
		player.getPackets().sendGameMessage("Changed display name!");
		return true;
	}

	public static boolean removeDisplayName(Player player) {
		if (!player.hasDisplayName())
			return false;
		synchronized (cachedNames) {
			cachedNames.remove(Utils.formatPlayerNameForProtocol(player.getDisplayName()));
		}
		player.setDisplayName(null);
		for (Player p : World.getPlayers()) {
			if (p == null)
				continue;
			if (p.getClanManager() != null) {
				p.getClanManager().refreshClanChannel();
				p.getClanManager().generateClanChannelDataBlock();
			}
			if (p.getCurrentFriendChat() != null)
				p.getCurrentFriendChat().refreshChannel();
			p.getFriendsIgnores().refreshChatName();
		}
		player.getAppearance().generateAppearenceData();
		return true;
	}
}