package com.rs.java.game.npc.combat;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.meta.DragonFireShieldMetaData;
import com.rs.java.game.item.meta.ItemMetadata;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Equipment;
import com.rs.java.game.player.Player;

public class DragonFire {

    private static final int[] DRAGON_SHIELDS = {11283, 11284, 1540};
    private static final int DRAGONFIRE_ABSORB_ANIMATION = 6695;
    private static final int DRAGONFIRE_ABSORB_GFX = 1164;


    public static boolean hasFireProtection(Entity entity) {
        if (entity instanceof NPC) return false;
        if (!(entity instanceof Player player)) return false;

        if (player.getSuperAntifire() > 0)
            return true;
        if (player.getAntifire() > 0)
            return true;
        if (player.getPrayer().isMageProtecting())
            return true;
        return hasDragonShield(player);
    }

    /**
     * Applies dragonfire mitigation to a damage value and sends the appropriate messages.
     */
    public static int applyDragonfireMitigation(Player player, int damage) {
        if (player.getSuperAntifire() > 0) {
            player.getPackets().sendGameMessage(
                    "Your potion fully protects you from the heat of the dragon's breath."
            );
            return 0;
        }

        boolean shield = hasDragonShield(player);
        boolean antifire = player.getAntifire() > 0;
        boolean prayer = player.getPrayer().isMageProtecting();

        if (shield || antifire) {
            damage *= 0.1;
            String source = shield ? "shield" : "potion";

            if (prayer) {
                player.getPackets().sendGameMessage(
                        "Your " + source + " and prayer fully protect you from the heat of the dragon's breath."
                );
                return 0;
            } else {
                player.getPackets().sendGameMessage(
                        "Your " + source + " protects you from most of the dragon's breath."
                );
                return damage;
            }
        }

        if (prayer) {
            player.getPackets().sendGameMessage(
                    "Your prayer protects you from some of the heat of the dragon's breath!"
            );
            damage *= 0.1;
            return damage;
        }

        player.getPackets().sendGameMessage("You are hit by the dragon's fiery breath!", true);
        return damage;
    }

    /**
     * Handles absorbing a dragonfire attack with a Dragonfire shield, incrementing charges.
     */
    public static void handleDragonfireShield(Player player) {
        if (!player.getEquipment().containsOneItem(11283, 11284)) return;

        Item shield = player.getEquipment().getItem(Equipment.SLOT_SHIELD);
        if (shield == null) return;

        ItemMetadata meta = shield.getMetadata();

        if (meta == null) {
            if (shield.isItem("item.dragonfire_shield_charged")) {
                shield.setMetadata(new DragonFireShieldMetaData(0));
            }
            if (shield.isItem("item.dragonfire_shield_uncharged")) {
                shield.setId(Item.getId("item.dragonfire_shield_charged"));
                shield.setMetadata(new DragonFireShieldMetaData(0));
                player.getEquipment().refresh(Equipment.SLOT_SHIELD);
                player.getAppearence().generateAppearenceData();
            }
        }

        if (shield.getMetadata() instanceof DragonFireShieldMetaData dfsMeta) {
            if (dfsMeta.getValue() < dfsMeta.getMaxValue()) {
                dfsMeta.increment(1);
                player.animate(new Animation(DRAGONFIRE_ABSORB_ANIMATION));
                player.gfx(new Graphics(DRAGONFIRE_ABSORB_GFX, 1, 0));
                player.getPackets().sendGameMessage("Your dragonfire shield absorbs the dragon breath.");
                player.getEquipment().refresh(Equipment.SLOT_SHIELD);
            }
        }
    }

    /**
     * Checks if the player is wearing any dragonfire shield.
     */
    public static boolean hasDragonShield(Player player) {
        int shieldId = player.getEquipment().getShieldId();
        for (int id : DRAGON_SHIELDS) {
            if (shieldId == id) return true;
        }
        return false;
    }
}