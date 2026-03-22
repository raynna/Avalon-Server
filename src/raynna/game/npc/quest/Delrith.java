package raynna.game.npc.quest;

import raynna.game.Entity;
import raynna.game.WorldTile;
import raynna.game.npc.NPC;
import raynna.game.player.Player;
import raynna.game.player.content.quest.QuestList.Quests;
import raynna.game.player.content.quest.impl.demonslayer.DelrithController;

@SuppressWarnings("serial")
public class Delrith extends NPC {

	private DelrithController delrith;

	public Delrith(int id, WorldTile tile, DelrithController delrith) {
		super(id, tile, -1, true, true);
		this.delrith = delrith;
	}

	@Override
	public void sendDeath(Entity source) {
		if (delrith != null) {
			delrith = null;
		}
		if (source instanceof Player) {
			Player player = (Player) source;
			if (player.getQuestManager().get(Quests.DEMON_SLAYER).getStage() == 1) {
				player.getQuestManager().get(Quests.DEMON_SLAYER).setStage(2);
				player.setNextWorldTile(new WorldTile(3221, 3362, 0));
				player.message("I should go tell Gypsy Aris i've killed the demon.");
			}
		}
		super.sendDeath(source);
	}

	public void disapear() {
		delrith = null;
		finish();
	}


}
