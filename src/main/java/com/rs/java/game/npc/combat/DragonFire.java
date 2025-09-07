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
import com.rs.java.utils.Utils;

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
    public static int applyDragonfireMitigation(Player player, int baseMaxHit, boolean allowPrayer) {
        // 1. Super antifire always wins
        if (player.getSuperAntifire() > 0) {
            player.message("Your potion fully protects you from the dragon's breath.");
            return 0;
        }

        boolean shield = hasDragonShield(player);
        boolean antifire = player.getAntifire() > 0;
        boolean prayer = allowPrayer && player.getPrayer().isMageProtecting();

        // 2. Shield + antifire OR prayer + antifire = full immunity
        if (antifire && (shield || prayer)) {
            player.message("Your protection fully shields you from the dragon's breath.");
            return 0;
        }

        // 3. Shield alone caps at ~5–10 damage
        if (shield) {
            int damage = Utils.random(0, 10);
            player.message("Your shield protects you from most of the dragon's breath.");
            return damage;
        }

        // 4. Antifire potion alone caps at ~35
        if (antifire) {
            int damage = Utils.random(0, 35);
            player.message("Your potion protects you from some of the dragon's breath.");
            return damage;
        }

        // 5. Prayer alone (if valid for this dragon) cuts damage a lot
        if (prayer) {
            int damage = Utils.random(0, baseMaxHit / 2);
            player.message("Your prayer protects you from some of the dragon's breath!");
            return damage;
        }

        // 6. No protection = full damage roll
        int damage = Utils.random(0, baseMaxHit);
        player.message("You're horribly burnt by the dragon fire!");
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