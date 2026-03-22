package raynna.game.item.plugins.misc;

import raynna.core.thread.CoresManager;
import raynna.game.Entity;
import raynna.game.World;
import raynna.game.item.Item;
import raynna.game.item.ItemPlugin;
import raynna.game.npc.NPC;
import raynna.game.player.Player;
import raynna.util.Utils;

import java.util.concurrent.TimeUnit;

public class OrbOfCounting extends ItemPlugin {

    @Override
    public Object[] getKeys() {
        return new Object[]{15073};
    }

    @Override
    public boolean processItem(Player player, Item item, int slotId, String option) {
        if (player.getTemporaryAttributtes().get("ORB_OF_COUNTING") == Boolean.TRUE) {
            player.message("You already have one counting.");
            return true;
        }
        switch (option) {
            case "nearby-count":
			case "world-count":
                sendCount(player, option.equalsIgnoreCase("world-count"));
                return true;
        }
        return false;
    }

    public void sendCount(Player player, boolean world) {
        player.getTemporaryAttributtes().put("ORB_OF_COUNTING", Boolean.TRUE);
        player.message("Counting...");
        CoresManager.getSlowExecutor().schedule(() -> {
            if (!player.isActive())
                return;
            int blueHats = 0;
            int redHats = 0;
            if (world) {
                for (Player players : World.getPlayers()) {
                    if (players.getEquipment().getHatId() == 15069)
                        redHats++;
                    if (players.getEquipment().getHatId() == 15071)
                        blueHats++;
                }
            } else {
                for (Entity players : Utils.getAroundEntities(player, player, 14)) {
                    if (players instanceof NPC)
                        continue;
                    Player p = (Player) players;
                    if (p.getEquipment().getHatId() == 15069)
                        redHats++;
                    if (p.getEquipment().getHatId() == 15071)
                        blueHats++;
                }
            }
            player.getTemporaryAttributtes().remove("ORB_OF_COUNTING");
            player.message("Blue hats: " + blueHats + ", Red hats: " + redHats);
            if (blueHats == redHats) {
                player.message("No winner! It's a tie!");
            } else
                player.message("Winner is: " + (blueHats < redHats ? "Red!" : "Blue!"));
        }, 2000, TimeUnit.MILLISECONDS);
    }
}