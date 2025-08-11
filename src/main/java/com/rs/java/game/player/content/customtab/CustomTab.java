package com.rs.java.game.player.content.customtab;

import com.rs.java.game.player.Player;

public class CustomTab {
	
	protected static final int BLUE_STAR_COMP = 62;
	protected static final int GREEN_STAR_COMP = 61;
	protected static final int RED_STAR_COMP = 60;
	protected static final int PURPLE_STAR_COMP = 59;
	protected static final int YELLOW_STAR_COMP = 26;
	
	protected static final int BACK_BUTTON = 58;
	protected static final int FORWARD_BUTTON = 27;
	
	
	protected static final int BLUE_HIGHLIGHTED = 12184;
	protected static final int GREEN_HIGHLIGHTED = 12182;
	protected static final int RED_HIGHLIGHTED = 12186;
	protected static final int PURPLE_HIGHLIGHTED = 12185;
	protected static final int YELLOW_HIGHLIGHTED = 12187;
	
	protected static final int BLUE_UNSELECECTED = 12188;
	protected static final int GREEN_UNSELECECTED = 12189;
	protected static final int RED_UNSELECECTED = 12190;
	protected static final int PURPLE_UNSELECECTED = 12191;
	protected static final int YELLOW_UNSELECECTED = 675;//12192

	protected static int firstSlot = 3, lastSlot = 22;

	
	public static void sendComponents(Player player) {
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:blue_star", false);
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:green_star", false);
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:red_star", false);
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:purple_star", false);
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:yellow_star", false);
		player.getPackets().sendSpriteOnIComponent("interface.quest_tab", "component.quest_tab:blue_star", "sprite.quest_tab_blue_star");
		player.getPackets().sendSpriteOnIComponent("interface.quest_tab", "component.quest_tab:green_star", "sprite.quest_tab_green_star");
		player.getPackets().sendSpriteOnIComponent("interface.quest_tab", "component.quest_tab:red_star", "sprite.quest_tab_red_star");
		player.getPackets().sendSpriteOnIComponent("interface.quest_tab", "component.quest_tab:purple_star", "sprite.quest_tab_purple_star");
		player.getPackets().sendSpriteOnIComponent("interface.quest_tab", "component.quest_tab:yellow_star", "sprite.quest_tab_yellow_star");
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:title", true);
	}
	
	public static void sendHideComponents(Player player) {
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:blue_star", true);
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:green_star", true);
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:red_star", true);
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:purple_star", true);
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:yellow_star", true);
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:title", true);
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:back", true);
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:forward", true);
		for (int i = firstSlot; i <= lastSlot; i++)
			player.getPackets().sendHideIComponent("interface.quest_tab", i, true);
		//for (int i = 28; i <= 56; i++)
		//	player.getPackets().sendHideIComponent("interface.quest_tab", i, true);
	}

}
