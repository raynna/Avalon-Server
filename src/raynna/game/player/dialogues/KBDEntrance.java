package raynna.game.player.dialogues;

import java.util.concurrent.TimeUnit;

import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.core.thread.CoresManager;
import raynna.game.WorldTile;
import raynna.game.player.Player;
import raynna.game.player.actions.combat.Magic;
import raynna.util.Utils;

public class KBDEntrance extends Dialogue {

	/*
	 * 
	 * Written by Tristam
	 */

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	public static void Teleport(final Player player) {
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				Magic.pushLeverTeleport(player, new WorldTile(2273, 4681, 0));
			}
		});
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 1:
			sendOptionsDialogue("Select an option", "Yes", "Yes and remember my decision", "No, i'll just stay here.");
			stage = 2;
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				Teleport(player);
				end();
				break;
			case OPTION_2:
				player.rememberChoice(true);
				Teleport(player);
				player.message("The warning shall not pop up for you anymore.");
				end();
				break;
			case OPTION_3:
				end();
			}
			break;
		}

	}

	@Override
	public void start() {
		sendDialogue("Warning! You're about to enter the lair of King Black Dragon, are you sure you want to proceed?");
		stage = 1;

	}

}
