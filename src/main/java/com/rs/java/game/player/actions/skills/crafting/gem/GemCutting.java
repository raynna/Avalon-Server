package com.rs.java.game.player.actions.skills.crafting.gem;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.Animation;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.Action;
import com.rs.java.game.player.content.tasksystem.TaskManager.Tasks;

public class GemCutting extends Action {

    private GemProduct product;
    private int quantity;

    public GemCutting(GemProduct product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    @Override
    public boolean start(Player player) {
        return check(player);
    }

    private boolean check(Player player) {
        if (!player.hasTool("item.chisel")) {
            player.message("You need a chisel to cut this item.");
            return false;
        }
        if (player.getSkills().getLevel(Skills.CRAFTING) < product.getLevel()) {
            player.message("You need a Crafting level of " + product.getLevel() + " to cut this gem.");
            return false;
        }

        if (!player.getInventory().containsItem(product.getUncut(), 1)) {
            String name = ItemDefinitions.getItemDefinitions(product.getUncut()).getName().toLowerCase();
            player.message("You have run out of " + name + ".");
            return false;
        }

        return true;
    }

    @Override
    public boolean process(Player player) {
        return quantity > 0 && check(player);
    }

    @Override
    public int processWithDelay(Player player) {
        quantity--;

        player.animate(new Animation(product.getAnimation()));

        player.getInventory().deleteItem(product.getUncut(), 1);

        player.getInventory().addItem(product.getCut(), 1);
        player.getSkills().addXp(Skills.CRAFTING, product.getXp());

        player.message("You cut the " +
                ItemDefinitions.getItemDefinitions(product.getUncut()).getName().toLowerCase() + ".", true);

        if (product.getUncut() == 1623)
            player.getTaskManager().checkComplete(Tasks.CUT_UNCUT_SAPPHIRE);
        if (product.getUncut() == 1617)
            player.getTaskManager().checkComplete(Tasks.CUT_UNCUT_DIAMOND);
        if (product.getUncut() == 1631)
            player.getTaskManager().checkComplete(Tasks.CUT_UNCUT_DRAGONSTONE);
        if (product.getUncut() == 6571)
            player.getTaskManager().checkComplete(Tasks.CUT_UNCUT_ONYX);

        return quantity > 0 ? 1 : -1;
    }

    @Override
    public void stop(Player player) {
        setActionDelay(player, 3);
    }
}