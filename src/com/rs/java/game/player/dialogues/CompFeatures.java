package com.rs.java.game.player.dialogues;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.utils.Utils;

public class CompFeatures extends Dialogue {

	public CompFeatures() {

	}

	@Override
	public void start() {
		stage = 1;
		sendOptionsDialogue("Completionist Cape Features", "Restore Summoning Points", "Teleport to Kandarin Monestary",
				"Teleport to Ardougne Farm", "Exit Options");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			if (componentId == OPTION_1) {
				renewSummoningPoints(player);
				end();
			} else if (componentId == OPTION_2) {
				player.message("Kandarin Monestary Teleport");
				end();
			} else if (componentId == OPTION_3) {
				player.message("Ardougne Farm Teleport");
				end();
			} else if (componentId == OPTION_4) {
				end();

			}
		}
	}

	@Override
	public void finish() {
	}

	public static void renewSummoningPoints(Player player) {
		int summonLevel = player.getSkills().getLevelForXp(Skills.SUMMONING);

		if (player.restoreDelay < Utils.currentTimeMillis()
				&& player.getSkills().getLevel(Skills.SUMMONING) < summonLevel) {
			player.restoreDelay = (Utils.currentTimeMillis() + 30000);
			player.getSkills().set(Skills.SUMMONING, summonLevel);
			player.animate(new Animation(8502));
			player.gfx(new Graphics(1308));
			player.getPackets().sendGameMessage("You restored your Summoning points with the Completionist cape!",
					true);
		} else if (player.restoreDelay > Utils.currentTimeMillis()) {
			player.message("Your cape is still recharging from it's last use.");
		} else {
			player.message("Your cape does not respond due to you already having full summoning points.");
		}
	}

}
