package com.rs.java.game.player.actions.skills.crafting.glass;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.Animation;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.Action;
import com.rs.java.game.player.actions.skills.crafting.leather.ReqItem;

public class GlassBlowing extends Action {

    public static final int GLASSBLOWING_PIPE = 1785;
    private static final int BLOW_ANIMATION = 884;

    private final GlassBlowingData data;
    private final GlassProduct product;
    private int quantity;

    public GlassBlowing(GlassBlowingData data, int option, int quantity) {
        this.data = data;
        this.product = data.getProducts()[option];
        this.quantity = quantity;
    }

    @Override
    public boolean start(Player player) {
        return check(player);
    }

    private boolean check(Player player) {

        if (!player.getInventory().containsItem(GLASSBLOWING_PIPE, 1)) {
            player.message("You need a glassblowing pipe to do that.");
            return false;
        }

        if (player.getSkills().getLevel(Skills.CRAFTING) < product.getLevel()) {
            player.message("You need a Crafting level of " + product.getLevel() + ".");
            return false;
        }

        if (!player.getInventory().containsItem(data.getBase(), 1)) {
            String name = ItemDefinitions.getItemDefinitions(data.getBase()).getName();
            player.message("You need some " + name + " to do that.");
            return false;
        }

        return true;
    }

    @Override
    public boolean process(Player player) {

        if (quantity <= 0)
            return false;

        if (!check(player))
            return false;

        player.animate(BLOW_ANIMATION);
        return true;
    }

    @Override
    public int processWithDelay(Player player) {
        quantity--;

        player.getInventory().deleteItem(data.getBase(), 1);
        player.getInventory().addItem(product.getId(), 1);
        player.getSkills().addXp(Skills.CRAFTING, product.getXp());

        return 3;
    }

    @Override
    public void stop(Player player) {
        setActionDelay(player, 3);
    }
}
