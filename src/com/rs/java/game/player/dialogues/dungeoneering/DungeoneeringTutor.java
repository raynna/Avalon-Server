package com.rs.java.game.player.dialogues.dungeoneering;

import com.rs.java.game.item.Item;
import com.rs.java.game.player.dialogues.Dialogue;

public class DungeoneeringTutor extends Dialogue {

	private static final int DUNGEON_TUTOR = 9712;

	@Override
	public void start() {
		sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "Greetings, adventurer!");
		if (!player.getInventory().containsOneItem(15707))
			stage = -1;
		else
			stage = 2;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "Before we carry on, let me give you this.");
			if (player.getInventory().hasFreeSlots())
				stage = 0;
			else
				stage = 1;
		} else if (stage == 0) {
			sendDialogue("He hands you a ring.");
			player.getInventory().addItem(new Item(15707, 1));
			stage = 2;
		} else if (stage == 1) {
			sendDialogue("Your inventory is currently full!");
			stage = 2;
		} else if (stage == 2) {
			sendOptionsDialogue("Select an Option.", "What is this place?", "What can I do here?", "What does this ring do?"/*, "I want to play daily challenges!"*/);
			stage = 3;
		} else if (stage == 3) {
			if (componentId == OPTION_1) {
				sendPlayerDialogue(NORMAL, "What is this place?");
				stage = 4;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(NORMAL, "What can I do here?");
				stage = 8;
			} else if (componentId == OPTION_3) {
				sendPlayerDialogue(NORMAL, "What does this ring do?");
				stage = 10;
			} else if (componentId == OPTION_4) {
				sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "Sure.", "Before you start though, remember that you can try each challenge only once.");
				stage = 40;
			}
		} else if (stage == 4) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "This is a place of treasures, fierce battles and bitter defeats.");
			stage = 5;
		} else if (stage == 5) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "We fought our way into the dungeons beneath this place.");
			stage = 6;
		} else if (stage == 6) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "Those of us who made it out alive...");
			stage = 7;
		} else if (stage == 7) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "...called this place Daemonhiem.");
			stage = 100;
		} else if (stage == 8) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "Beneath these ruins you will find a multitude of dungeons, filled with strange creatures and resources.");
			stage = 9;
		} else if (stage == 9) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "Unfortunately, due to the taint that permiates this place, we cannot risk you taking items in or out of Daemonhiem.");
			stage = 100;
		} else if (stage == 10) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "Raiding these foresaken dungeons can be alot more rewarding if you're fighting alongside friends and allies. It should be more fun and you gain experience faster.");
			stage = 11;
		} else if (stage == 11) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "The ring shows others you are interested in raiding a dungeon. It allowes you to form, join, and manage a raiding party.");
			stage = 12;
		} else if (stage == 12) {
			sendNPCDialogue(DUNGEON_TUTOR, NORMAL, "We've also setup rooms with the specific purpose of finding a party for you.");
			stage = 100;
		} else if (stage == 40) {
			//DailyDungeon.checkForUpdates();
			//String[] challenges = new String[5];
			//for (int i = 0; i < 5; i++)
			//	challenges[i] = "Floor " + DailyDungeon.FLOORS[i] + " (Team size " + (i+1) + ")";
			//sendOptionsDialogue("What challenge would you like?", challenges);
			//stage = 41;
		} else if (stage == 41) {
			end();
			switch (componentId) {
				default:
				case OPTION_1:
					//player.getDungManager().enterDailyDungeon(0);
					break;
				case OPTION_2:
					//player.getDungManager().enterDailyDungeon(1);
					break;
				case OPTION_3:
					//player.getDungManager().enterDailyDungeon(2);
					break;
				case OPTION_4:
					//player.getDungManager().enterDailyDungeon(3);
					break;
				case OPTION_5:
					//player.getDungManager().enterDailyDungeon(4);
					break;
			}

		} else if (stage == 100) {
			end();
		}
	}

	@Override
	public void finish() {

	}

}
