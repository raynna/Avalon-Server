package raynna.game.item.plugins.misc;

import raynna.game.item.Item;
import raynna.game.item.ItemPlugin;
import raynna.game.player.Player;
import raynna.game.player.content.DwarfMultiCannon;

public class DwarfCannon extends ItemPlugin {

    @Override
    public Object[] getKeys() {
        return new Object[]{"item.cannon_base", "item.gold_cannon_base", "item.royale_cannon_base"};
    }

    @Override
    public boolean processItem(Player player, Item item, int slotId, String option) {
        switch (option) {
            case "set-up":
                DwarfMultiCannon.setUp(player, item.isItem("item.cannon_base") ? 0 : item.isItem("item.gold_cannon_base") ? 1 : 2);
                return true;
        }
        return false;
    }
}