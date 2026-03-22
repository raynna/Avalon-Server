package raynna.game.npc.dungeoneering;

import java.util.List;

import raynna.game.Entity;
import raynna.game.WorldTile;
import raynna.game.item.Item;
import raynna.game.player.Player;
import raynna.game.player.content.dungeoneering.DungeonManager;
import raynna.game.player.content.dungeoneering.RoomReference;
import raynna.util.Utils;
import raynna.game.npc.drops.Drop;
import raynna.game.npc.drops.DropTable;
import raynna.game.npc.drops.DropTableRegistry;

@SuppressWarnings("serial")
public class DungeonBoss extends DungeonNPC {

	private RoomReference reference;

	public DungeonBoss(int id, WorldTile tile, DungeonManager manager, RoomReference reference) {
		this(id, tile, manager, reference, 1);
	}

	public DungeonBoss(int id, WorldTile tile, DungeonManager manager, RoomReference reference, double multiplier) {
		super(id, tile, manager, multiplier);
		this.setReference(reference);
		setForceAgressive(true);
		setIntelligentRouteFinder(true);
		setLureDelay(0);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		getManager().openStairs(getReference());
	}

	@Override
	public void drop() {
		List<Player> players = getManager().getParty().getTeam();
		if (players.size() == 0)
			return;
		Player luckyPlayer = players.get(Utils.random(players.size()));
		DropTable droppedItems = DropTableRegistry.getDropTableForNpc(this.getId());
		if (droppedItems == null) {
			luckyPlayer.getPackets().sendGameMessage("Null drops");

		} else {
			List<Drop> rolled = droppedItems.rollDrops(luckyPlayer, getCombatLevel());
			for (Drop drop : rolled) {
				sendDrop(luckyPlayer, drop);
			}
			return;
		}
	}
	
	public void sendDrop(Player player, Drop drop) {
		Item item = new Item(drop.itemId);
		player.getInventory().addItemDrop(item.getId(), item.getAmount());
		player.getPackets().sendGameMessage("You received: " + item.getAmount() + " " + item.getName() + ".");
		List<Player> players = getManager().getParty().getTeam();
		if (players.size() == 0)
			return;
		for (Player p2 : players) {
			if (p2 == player)
				continue;
			p2.getPackets().sendGameMessage("" + player.getDisplayName() + " received: " + item.getAmount() + " " + item.getName() + ".");
		}
	}

	public RoomReference getReference() {
		return reference;
	}

	public void setReference(RoomReference reference) {
		this.reference = reference;
	}
}
