package com.rs.java.game.player;

import java.io.Serializable;

import com.rs.java.game.World;
import com.rs.java.game.player.Ranks.Rank;

/**
 * @author -Andreas 5 feb. 2020 16:09:56
 * @project 1. Avalon
 * 
 */

public class PlayerRank implements Serializable {

	private static final long serialVersionUID = -773408484495708325L;

	private transient Player player;
	private final Rank[] rank;

	private final transient int PLAYER_INDEX = 0, DONATOR_INDEX = 1, IRONMAN_INDEX = 2;

	public PlayerRank() {
        rank = new Rank[3];
		rank[PLAYER_INDEX] = Rank.PLAYER;

	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Rank[] getRank() {
		return this.rank;
	}

	public void setRank(int index, Rank rank) {
		this.rank[index] = rank;
		if (this.rank[index] != Rank.DEVELOPER && this.rank[index] != Rank.MODERATOR)
			World.sendNewsMessage(player.getDisplayName() + " is now a " + rank.getRankName() + "!", false);
	}

	public boolean isAtLeast(Rank other) {
		Rank current = rank[PLAYER_INDEX]; // primary rank
		if (current == null) {
			return false;
		}
		return current.ordinal() >= other.ordinal();
	}

	public void addRank(Rank rank) {
		if (rank == Rank.IRONMAN || rank == Rank.HARDCORE_IRONMAN) {
			setRank(IRONMAN_INDEX, rank);
			return;
		}
		if (rank.getRankName().toLowerCase().contains("donator")) {
			setRank(DONATOR_INDEX, rank);
			return;
		}
		setRank(PLAYER_INDEX, rank);
	}

	public String getRankNames() {
		StringBuffer names = new StringBuffer();
		for (Rank ranks : rank) {
			if (ranks == null)
				continue;
			names.append(ranks.getRankName()).append(", ");
		}
		return names.replace(names.length() - 2, names.length(), "").toString();
	}

	public String getRankName(int index) {
		Rank currentRank = rank[index];
		if (currentRank == null)
			return null;
		return currentRank.getRankName();
	}

	public boolean isDonator() {
		for (Rank ranks : rank) {
			if (ranks == null)
				continue;
			if (ranks.getRankName().toLowerCase().contains("donator"))
				return true;
		}
		return false;
	}

	public int getIconId() {
		Rank rank = getRank()[PLAYER_INDEX];
		return rank.getIconId();
	}

	public boolean isDeveloper() {
		return rank[PLAYER_INDEX] == Rank.DEVELOPER;
	}

	public boolean isModerator() {
		return rank[PLAYER_INDEX] == Rank.MODERATOR;
	}

	public boolean isIronman() {
		return rank[IRONMAN_INDEX] == Rank.IRONMAN || rank[IRONMAN_INDEX] == Rank.HARDCORE_IRONMAN;
	}

	public boolean isHardcore() {
		return rank[IRONMAN_INDEX] == Rank.HARDCORE_IRONMAN;
	}

	public boolean isStaff() {
		Rank rank = getRank()[PLAYER_INDEX];
		return rank != Rank.PLAYER && rank != Rank.YOUTUBER;
	}
}
