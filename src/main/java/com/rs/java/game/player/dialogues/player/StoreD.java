package com.rs.java.game.player.dialogues.player;

import com.rs.java.game.player.content.SkillsDialogue;
import com.rs.java.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.java.game.player.dialogues.Dialogue;
import com.rs.kotlin.game.player.shop.OpenShopAction;

public class StoreD extends Dialogue {

    @Override
    public void start() {
        var displays = OpenShopAction.ShopDisplay.valuesInOrder()  ;

        int[] ids = new int[displays.size()];
        for (int i = 0; i < displays.size(); i++) {
            ids[i] = displays.get(i).getIconItemId();
        }

        SkillsDialogue.sendStoreDialogue(player,
                "Click on the alternative stores down below.", ids, new ItemNameFilter() {
                    int count = 0;

                    @Override
                    public String rename(String name) {
                        return displays.get(count++).name().replace("_", " ");
                    }
                });
    }

    @Override
    public void run(int interfaceId, int componentId) {
        int index = SkillsDialogue.getItemSlot(componentId);
        var displays = OpenShopAction.ShopDisplay.valuesInOrder();
        if (index >= 0 && index < displays.size()) {
            var display = displays.get(index);
            player.getActionManager().setAction(
                    new OpenShopAction(display.getShopId(), 0)); // ticks unused
        }
        end();
    }

    @Override
    public void finish() {
    }
}
