package com.rs.java.game.player.dialogues.npcs;

import com.rs.java.game.npc.others.Ugi;
import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager;
import com.rs.java.game.player.dialogues.Dialogue;
import com.rs.java.utils.Utils;

public class UgiDialouge extends Dialogue {

	private int clueLevel = -1;

	@Override
	public void start() {
		Ugi npc = (Ugi) parameters[0];

		// Find the clue level where this interaction is valid
		for (int level = 0; level < TreasureTrailsManager.CLUE_SCROLLS.length; level++) {
			if (player.getInventory().getNumberOf(TreasureTrailsManager.CLUE_SCROLLS[level]) > 0
					&& player.getTreasureTrailsManager().getPhase() == 4) {
				clueLevel = level;
				break;
			}
		}

		stageInt = (clueLevel != -1 && npc.getTarget() == player) ? 0 : -1;
		run(-1, -1);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		Ugi npc = (Ugi) parameters[0];

		if (stageInt == 0) {
			sendNPCDialogue(npc.getId(), NORMAL,
					TreasureTrailsManager.UGIS_QUOTES[Utils.random(TreasureTrailsManager.UGIS_QUOTES.length)]);
			stageInt = 1;

		} else if (stageInt == 1) {
			sendPlayerDialogue(NORMAL, "What?");
			stageInt = 2;

		} else if (stageInt == 2) {
			end();
			npc.finish();

			if (clueLevel != -1) {
				player.getTreasureTrailsManager().setPhase(5);
				player.getTreasureTrailsManager().setNextClue(clueLevel, TreasureTrailsManager.SOURCE_EMOTE);
			}

		} else if (stageInt == -1) {
			sendNPCDialogue(npc.getId(), NORMAL, TreasureTrailsManager.UGI_BADREQS);
			stageInt = -2;

		} else {
			end();
		}
	}

	@Override
	public void finish() {
	}
}
