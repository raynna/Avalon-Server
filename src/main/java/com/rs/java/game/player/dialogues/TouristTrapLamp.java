package com.rs.java.game.player.dialogues;

import com.rs.java.game.player.Skills;

public class TouristTrapLamp extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Choose a skill", "Agility", "Fletching", "Thieving", "Smithing");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				end();
				player.getInventory().deleteItem(2528, 1);
				player.getSkills().addXp(Skills.AGILITY, 4350, false);
			} else if (componentId == OPTION_2) {
				end();
				player.getInventory().deleteItem(2528, 1);
				player.getSkills().addXp(Skills.FLETCHING, 4350, false);
			} else if (componentId == OPTION_3) {
				end();
				player.getInventory().deleteItem(2528, 1);
				player.getSkills().addXp(Skills.THIEVING, 4350, false);
			} else if (componentId == OPTION_4) {
				end();
				player.getInventory().deleteItem(2528, 1);
				player.getSkills().addXp(Skills.SMITHING, 4350, false);
			}
		}

	}

	@Override
	public void finish() {

	}
}