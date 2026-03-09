package com.rs.java.game.player.dialogues;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.player.Player;
import com.rs.kotlin.game.player.combat.magic.lunar.spells.SpellbookSwapService;

public class SpellbookSwap extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue(
				"Choose a Spellbook",
				"Ancient Spellbook",
				"Modern Spellbook"
		);
	}

	@Override
	public void run(int interfaceId, int componentId) {

		switch (componentId) {

			case OPTION_1 -> {
				if (SpellbookSwapService.INSTANCE.swap(player, SpellbookSwapService.Spellbook.ANCIENT)) {
					playSwapEffect(player);
				}
				end();
			}

			case OPTION_2 -> {
				if (SpellbookSwapService.INSTANCE.swap(player, SpellbookSwapService.Spellbook.MODERN)) {
					playSwapEffect(player);
				}
				end();
			}
		}
	}

	private void playSwapEffect(Player player) {
		player.gfx(new Graphics(1062));
		player.animate(new Animation(6299));
	}

	@Override
	public void finish() {
	}
}