package com.rs.java.game.player.content;

import com.rs.java.game.World;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Ranks.Rank;

public class ServerMessage {

	private static final String[] INVALID_CHARS = {
			"<euro", "<img", "<img=", "<col", "<col=", "<shad", "<shad=", "<str>", "<u>"
	};

	/**
	 * Filters a message for illegal characters and sends globally if valid.
	 *
	 * @param player The player sending the message.
	 * @param data The message content.
	 * @param staff Whether the message is staff-only.
	 */
	public static void filterMessage(Player player, String data, boolean staff) {
		if (!player.isDeveloper() && containsInvalidChars(data)) {
			player.getPackets().sendGameMessage("Your message failed to send due to illegal characters.");
			return;
		}

		sendGlobalMessage(player, data, staff);
	}

	/**
	 * Sends a global message formatted based on player rank.
	 */
	public static void sendGlobalMessage(Player player, String data, boolean staff) {
		String formatted;

		if (player.isDeveloper()) {
			formatted = String.format("[<shad=000000><col=F27C1D>Server Message</col></shad>] <img=1>%s: %s",
					player.getDisplayName(), data);
			sendNews(true, formatted, staff, false);
			return;
		}

		if (player.isModerator()) {
			formatted = String.format("[<shad=000000><col=948D8D>Player Moderator</col></shad>] <img=0>%s: <col=007FB5>%s",
					player.getDisplayName(), data);
			sendNews(true, formatted, staff, false);
			return;
		}

		if (player.getPlayerRank().getRank()[0] == Rank.YOUTUBER) {
			formatted = String.format("[<col=ff1100>Youtuber</col>] <img=8>%s: %s",
					player.getDisplayName(), data);
			sendNews(false, formatted, false, false);
			return;
		}

		formatted = String.format("[<col=003fff>Player</col>] %s: %s",
				player.getDisplayName(), data);
		sendNews(false, formatted, false, false);
	}

	/**
	 * Sends the message to all players in the world.
	 */
	public static void sendNews(boolean important, String message, boolean staffOnly, boolean autoColor) {
		for (Player user : World.getPlayers()) {
			if (user == null || !user.isActive()) continue;
			if (!important && user.isYellOff()) continue;
			if (staffOnly && !user.isStaff()) continue;

			if (autoColor) {
				user.getPackets().sendColoredMessage(message, important);
			} else {
				user.getPackets().sendGameMessage(message, true);
			}
		}
	}

	/**
	 * Checks if the message contains any invalid characters.
	 */
	private static boolean containsInvalidChars(String data) {
		for (String invalid : INVALID_CHARS) {
			if (data.contains(invalid)) return true;
		}
		return false;
	}
}
