package com.rs.java.game.npc.quest;

import com.rs.java.game.Entity;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.quest.QuestList.Quests;
import com.rs.java.game.player.content.quest.impl.demonslayer.DelrithControler;

@SuppressWarnings("serial")
public class Delrith extends NPC {

	private DelrithControler delrith;

	public Delrith(int id, WorldTile tile, DelrithControler delrith) {
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
