package com.rs.java.game.player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rs.Settings;
import com.rs.discord.DiscordAnnouncer;
import com.rs.java.game.World;
import com.rs.java.game.player.content.friendschat.FriendChatsManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.player.AccountCreation;
import com.rs.kotlin.game.player.command.CommandRegistry;

/**
 * 
 * @author Savions Sw
 * @author Andreas Fixed by Tristam <Hassan>. Issue: Not saving IP when server
 *         restarted
 */

public class StarterProtection {

	private static List<String> StarterIPS = new ArrayList<String>();

	private static final String Path = "data/starter/starterIPS.txt";

	public static void addStarter(Player player) {
		player.recievedStarter = true;
	}

	public static void addStarterIP(String IP) {
		if (IP == null) {
			return;
		}
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(new FileReader(Path));
			String line;
			while ((line = reader.readLine()) != null)
				StarterIPS.add(line);
			reader.close();
			StarterIPS.add(0, IP);
			writer = new BufferedWriter(new FileWriter(Path));
			for (String list : StarterIPS)
				writer.write(list + "\r\n");
			System.err.print(IP + " has just been added to the log. \n");
		} catch (Exception e) {
			System.err.print(IP + " was not added to starter list.");
		} finally {
			assert reader != null;
			assert writer != null;
			try {
				reader.close();
				writer.close();
			} catch (IOException e) {

			}
		}
	}

	public static void loadIPS() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(Path));
		String line;
		StarterIPS.clear();
		while ((line = br.readLine()) != null) {
			StarterIPS.add(line);
		}
		br.close();
	}

	public static final void sendStarterPack(Player player) {
		player.recievedStarter = true;
		player.reset();
		player.message("Experience rate: " + Settings.SKILLING_XP_RATE + "x for skilling, 1x in wilderness for combat.");
		World.sendWorldMessage(
				"<img=5><col=b25200>News: " + player.getDisplayName() + " has joined " + Settings.SERVER_NAME + "!",
				false);
		DiscordAnnouncer.announce("First time player!", player.getDisplayName() + " has joined the server for the very first time!.");
		if (Settings.ECONOMY_MODE  == Settings.FULL_SPAWN) {
			String otherName = Utils.formatPlayerNameForDisplay("fullspawn");
			Player p2 = World.getPlayerByDisplayName(otherName);
			if (p2 == null)
				p2 = AccountCreation.loadPlayer(otherName);
			if (p2 != null) {
				player.getBank().generateContainer();
				player.getBank().setBankTabs(p2.getBank().bankTabs);
				for (int i = 0; i <= Skills.MAGIC; i++) {
					player.getSkills().setXp(i, p2.getSkills().getXp(i));
				}
				player.setPrivateChatSetup(p2.getPrivateChatSetup());
				player.setFriendChatSetup(p2.getFriendChatSetup());
				player.setClanChatSetup(p2.getClanChatSetup());
				player.setGuestChatSetup(p2.getGuestChatSetup());
				player.getSkills().setXp(Skills.SUMMONING, p2.getSkills().getXp(Skills.SUMMONING));
				player.getSkills().setXp(Skills.SLAYER, Skills.getXPForLevel(99));
				player.getSkills().setXp(Skills.AGILITY, Skills.getXPForLevel(99));
				player.getSkills().setXp(Skills.RUNECRAFTING, Skills.getXPForLevel(99));
				player.getSkills().restoreSkills();
				player.setFamiliar(p2.getFamiliar());
				player.setSummoningLeftClickOption(p2.getSummoningLeftClickOption());
				player.getVarsManager().sendVar(1493, player.getSummoningLeftClickOption());
				player.getVarsManager().sendVar(1494, player.getSummoningLeftClickOption());
				player.getPresetManager().PRESET_SETUPS = p2.getPresetManager().PRESET_SETUPS;
			}
			player.getAppearence().generateAppearenceData();
			player.getPresetManager().loadPreset("max hybrid", null, true);
		}
		if (Settings.ECONOMY_MODE == Settings.HALF_ECONOMY) {
			String otherName = Utils.formatPlayerNameForDisplay("halfeco");
			Player p2 = World.getPlayerByDisplayName(otherName);
			if (p2 == null)
				p2 = AccountCreation.loadPlayer(otherName);
			if (p2 != null) {
				player.getBank().generateContainer();
				player.getBank().setBankTabs(p2.getBank().bankTabs);
				for (int i = 0; i <= Skills.MAGIC; i++) {
					player.getSkills().setXp(i, p2.getSkills().getXp(i));
				}
				player.setPrivateChatSetup(p2.getPrivateChatSetup());
				player.setFriendChatSetup(p2.getFriendChatSetup());
				player.setClanChatSetup(p2.getClanChatSetup());
				player.setGuestChatSetup(p2.getGuestChatSetup());
				player.getSkills().setXp(Skills.SUMMONING, p2.getSkills().getXp(Skills.SUMMONING));
				player.getSkills().setXp(Skills.SLAYER, Skills.getXPForLevel(99));
				player.getSkills().setXp(Skills.AGILITY, Skills.getXPForLevel(99));
				player.getSkills().setXp(Skills.RUNECRAFTING, Skills.getXPForLevel(99));
				player.getSkills().restoreSkills();
				player.setFamiliar(p2.getFamiliar());
				player.setSummoningLeftClickOption(p2.getSummoningLeftClickOption());
				player.getVarsManager().sendVar(1493, player.getSummoningLeftClickOption());
				player.getVarsManager().sendVar(1494, player.getSummoningLeftClickOption());
				player.getPresetManager().PRESET_SETUPS = p2.getPresetManager().PRESET_SETUPS;
			}
			player.getAppearence().generateAppearenceData();
			player.getPresetManager().loadPreset("hybrid", null, true);
		}
		if (Settings.ECONOMY_MODE == 0) {
			String otherName = Utils.formatPlayerNameForDisplay("economy");
			Player p2 = World.getPlayerByDisplayName(otherName);
			if (p2 == null)
				p2 = AccountCreation.loadPlayer(otherName);
			if (p2 != null) {
				player.setInventory(p2);
				player.setEquipment(p2);
				player.setBank(p2);
				player.setCombatDefinitions(p2);
				player.getInterfaceManager().sendInterfaces();
			}
			player.getMoneyPouch().addMoney(2000000, false);
			player.getDialogueManager().startDialogue("StarterQuickStatsD");
		}
		if (player.getCurrentFriendChat() == null) {
			FriendChatsManager.joinChat(Settings.HELP_CC_NAME, player, true);
			FriendChatsManager.refreshChat(player);
		}
		player.toggles.put("ONEXHITS", false);
		player.toggles.put("ONEXPPERHIT", false);
		player.toggles.put("HEALTH_OVERLAY", true);
		player.toggles.put("HITCHANCE_OVERLAY", true);
		player.toggles.put("LEVELSTATUS_OVERLAY", true);
		player.toggles.put("KDRINTER", true);
		player.toggles.put("BREAK_VIALS", true);
		player.toggles.put("DROPVALUE", 10000);
		player.toggles.put("LOOTBEAMS", true);
		player.switchShiftDrop();
		player.switchZoom();
		player.heal(player.getMaxHitpoints());
		player.getPrayer().restorePrayer(990);
		player.combatDefinitions.switchAutoRelatie();
		player.getAppearence().generateAppearenceData();
		player.getSkills().switchXPPopup(true);
		player.getSkills().switchXPPopup(true);
		CommandRegistry.execute(player, "command");
	}

	public static final boolean containsIP(String ip) {
		return StarterIPS.contains(ip);
	}

}