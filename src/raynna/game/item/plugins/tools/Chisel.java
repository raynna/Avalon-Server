package raynna.game.item.plugins.tools;

import raynna.game.item.Item;
import raynna.game.item.ItemId;
import raynna.game.item.ItemPlugin;
import raynna.game.player.Player;
import raynna.game.player.actions.skills.crafting.gem.GemData;
import raynna.game.player.actions.skills.fletching.*;

public class Chisel extends ItemPlugin {

    @Override
    public Object[] getKeys() {
        return new Object[]{ "item_group.chisel"};
    }

    @Override
    public boolean processItemOnItem(Player player, Item item, Item item2, int fromSlot, int toSlot) {
        GemData gem = GemData.Companion.forUncut(item.getId());
        if (gem == null)
            gem = GemData.Companion.forUncut(item2.getId());

        if (gem != null) {
            player.getDialogueManager().startDialogue("GemCuttingD", gem);
            return true;
        }
        FletchingData fletchingData = Fletching.findFletchingData(item, item2);//cutting gems to bolt tips
        if (fletchingData != null) {
            player.getDialogueManager().startDialogue("FletchingD", fletchingData);
            return true;
        }
        return false;
    }
}
