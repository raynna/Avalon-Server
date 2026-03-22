package raynna.game.player.dialogues.skilling;

import raynna.game.player.actions.combat.modernspells.ChargeOrb;
import raynna.game.player.content.SkillsDialogue;
import raynna.game.player.content.SkillsDialogue.ItemNameFilter;
import raynna.game.player.dialogues.Dialogue;
import raynna.game.player.combat.magic.Spell;

public class ChargeOrbD extends Dialogue {

	private int itemId;
	private Spell spell;

	@Override
	public void start() {

		this.spell = (Spell) parameters[0];
		this.itemId = (int) parameters[1];

		final int[] PRODUCTS = { itemId };

		SkillsDialogue.sendSkillsDialogue(
				player,
				SkillsDialogue.MAKE,
				"Choose how many you wish to make,<br>then click on the item to begin.",
				28,
				PRODUCTS,
				new ItemNameFilter() {
					@Override
					public String rename(String name) {
						return name;
					}
				});
	}

	@Override
	public void run(int interfaceId, int componentId) {

		int quantity = SkillsDialogue.getQuantity(player);

		player.getActionManager().setAction(
				new ChargeOrb(spell, itemId, quantity)
		);

		end();
	}

	@Override
	public void finish() {

	}
}