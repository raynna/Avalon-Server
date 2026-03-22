package raynna.game.player.actions.skills.construction.impl;

import raynna.game.Animation;
import raynna.game.item.Item;
import raynna.game.item.ItemId;
import raynna.game.player.Player;
import raynna.game.player.Skills;
import raynna.game.player.actions.Action;
import raynna.game.player.controllers.construction.SawmillController;


public class CutPlank extends Action {

	private SawmillController sawmill;
	private int amount;
	private int type;

	private static double[] XP = { 15, 18, 25, 22 };

	public CutPlank(int type, int amount, SawmillController sawmill) {
		this.type = type;
		this.amount = amount;
		this.sawmill = sawmill;
	}

	@Override
	public boolean start(Player player) {
		return process(player);
	}

	@Override
	public boolean process(Player player) {
		if (!player.getInventory().containsItemToolBelt(ItemId.SAW_8794, 1)
				&& !player.getInventory().containsItem(
						ItemId.CRYSTAL_SAW_9625, 1)) {
			player.getPackets().sendGameMessage(
					"You need a saw to cut the planks.");
			return false;
		}
		if (!player.getInventory().containsItem(ItemId.PLANK_960, 1)) {
			player.getPackets().sendGameMessage("You have run out of planks.");
			return false;
		}
		return amount > 0;
	}

	@Override
	public int processWithDelay(Player player) {
		boolean crystalSaw = player.getInventory().containsItem(9625, 1);
		player.animate(new Animation(crystalSaw ? 12382 : 12379));
		player.getSkills().addXp(Skills.WOODCUTTING,
				crystalSaw ? XP[type] * 2 : XP[type]);
		player.getInventory().deleteItem(new Item(960, 1));
		switch (type) {
		case 0:
			sawmill.addPlank(0, 1);
			sawmill.addPlank(1, 1);
			break;
		case 1:
			sawmill.addPlank(2, 2);
			break;
		case 2:
			sawmill.addPlank(3, 1);
			sawmill.addPlank(4, 1);
			break;
		default:
			sawmill.addPlank(5, 1);
			break;
		}
		return amount-- == 1 ? -1 : 1;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}
}