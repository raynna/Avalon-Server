package com.rs.java.game.player.content.customtab;

import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.quest.Quest;
import com.rs.java.game.player.content.quest.QuestList.Quests;
import com.rs.java.game.player.content.quest.State.QuestState;
import com.rs.java.utils.HexColours;
import com.rs.java.utils.HexColours.Colour;

public class QuestTab extends CustomTab {
	
	/**
	 * @author Andreas
	 *
	 */

	private static final QuestStore[] QUEST_STORES = QuestStore.values();

	public enum QuestStore {
		
		TITLE(25, "Quests"),
		POINTS(24, "Points"),
		COOKSASSISTANT(3, Quests.COOKS_ASSISTANT),
		DEMONSLAYER(4, Quests.DEMON_SLAYER),
		DORICSQUEST(5, Quests.DORICS_QUEST),
		DRUIDIC_RITUAL(6, Quests.DRUIDIC_RITUAL),
		GOBLIN_DIPLOMACY(7, Quests.GOBLIN_DIPLOMACY),
		GUNNARS_GROUND(8, Quests.GUNNARS_GROUND),
		IMP_CATCHER(9, Quests.IMP_CATCHER),
		PIRATES_TREASURE(10, Quests.PIRATES_TREASURE),
		PRINCE_ALI_RESCUE(11, Quests.PRINCE_ALI_RESCUE),
		RESTLESS_GHOST(12, Quests.THE_RESTLESS_GHOST),
		RUNE_MYSTERIES(13, Quests.RUNE_MYSTERIES),
		VAMPIRE_SLAYER(14, Quests.VAMPIRE_SLAYER),
		;
		

		private int compId;
		private Quests quest;
		private Colour state;
		private String text;

		private QuestStore(int compId, Quests quest) {
			this.compId = compId;
			this.setQuest(quest);
		}
		
		private QuestStore(int compId, String text) {
			this.compId = compId;
			this.text = text;
		}
		
		public void usage(Player p) {
		    p.getQuestManager().get(getQuest(p)).openJournal();
		}
		
		public String text(Player p) {
			if (text != null) {
				if (text.toLowerCase().contains("points"))
					return "Quest Points: " + p.getQuestManager().getQuestPoints();
				return text;
			}
		    return HexColours.getMessage(getState(p, quest), getQuestInfo(p).getQuestName());
		}

		public Quests getQuest(Player player) {
			this.setState(state);
			return quest;
		}
		
		public Quest getQuestInfo(Player player) {
			return player.getQuestManager().get(quest);
		}

		public void setQuest(Quests quest) {
			this.quest = quest;
		}

		public Colour getState(Player p, Quests quest) {
			return p.getQuestManager().get(quest).getState() == QuestState.COMPLETED ? Colour.GREEN : p.getQuestManager().get(quest).getState() == QuestState.STARTED ? Colour.YELLOW : Colour.RED;
		}

		public void setState(Colour state) {
			this.state = state;
		}

	}

	public static void open(Player player) {
		sendComponents(player);
		for (int i = firstSlot; i <= lastSlot; i++)
			player.getPackets().sendHideIComponent("interface.quest_tab", i, true);
		for (int i = 28; i <= 56; i++)
			player.getPackets().sendHideIComponent("interface.quest_tab", i, true);
		player.getTemporaryAttributtes().put("CUSTOMTAB", 4);
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:back", false);
		player.getPackets().sendHideIComponent("interface.quest_tab", "component.quest_tab:forward", true);
		player.getPackets().sendSpriteOnIComponent("interface.quest_tab", "component.quest_tab:purple_star", "sprite.quest_tab_purple_star_highlight");
		for (QuestStore store : QUEST_STORES) {
			if (store != null) {
				player.getPackets().sendHideIComponent("interface.quest_tab", store.compId, false);
				if (store.text(player) != null)
					player.getPackets().sendTextOnComponent("interface.quest_tab", store.compId, store.text(player));
			}
		}
		refreshScrollbar(player, QUEST_STORES.length);
	}

	public static void handleButtons(Player player, int compId) {
		for (QuestStore store : QUEST_STORES) {
			if (store != null) {
				if (compId != store.compId)
					continue;
				store.usage(player);
				//open(player);
			}
		}
		switch (compId) {
		case BACK_BUTTON:
			SettingsTab.open(player);
			break;
		default:
			break;
		}
	}
}
