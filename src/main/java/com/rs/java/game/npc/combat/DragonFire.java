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
import kotlin.Pair;

public class DragonFire {

    private static final int[] DRAGON_SHIELDS = {11283, 11284, 1540};
    private static final int DRAGONFIRE_ABSORB_ANIMATION = 6695;
    private static final int DRAGONFIRE_ABSORB_GFX = 1164;

    public enum DragonType {
        CHROMATIC,
        METALLIC,
        VORKATH,
        KING_BLACK_DRAGON,
        ELVARG
    }

    private static class Protection {
        boolean shield, prayer, antifire, superAntifire;
    }



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

    public static int applyDragonfireMitigation(Player player, boolean accuracyRoll, DragonType dragon) {
        return applyDragonfireMitigation(player, accuracyRoll, dragon, false);
    }

    public static int applyDragonfireMitigation(
            Player player,
            boolean accuracyRoll,
            DragonType dragon,
            boolean special
            ) {

        Protection protection = new Protection();
        protection.shield = hasDragonShield(player);
        protection.antifire = player.getAntifire() > 0;
        protection.superAntifire = player.getSuperAntifire() > 0;
        protection.prayer = dragon != DragonType.METALLIC && player.getPrayer().isMageProtecting();

        Pair<Integer, Integer> cap = getDragonfireCap(dragon, special, accuracyRoll, protection);

        int min = cap.getFirst();
        int max = cap.getSecond();

        int damage = Utils.random(min, max);
        if (damage == 0) {
            if (protection.superAntifire)
                player.message("Your potion fully protects you from the dragon's breath.");
            else if ((protection.antifire && protection.shield) || (protection.antifire && protection.prayer))
                player.message("Your protection fully shields you from the dragon's breath.");
            else
                player.message("You manage to resist some of the dragonfire!");
            return 0;
        }

        if (protection.shield)
            player.message("Your shield protects you from most of the dragon's breath.");
        else if (protection.antifire)
            player.message("Your potion protects you from some of the dragon's breath.");
        else if (protection.prayer && dragon != DragonType.METALLIC)
            player.message("Your prayer protects you from some of the dragon's breath!");
        else
            player.message("You're horribly burnt by the dragon fire!");

        return damage;
    }


    private static Pair<Integer, Integer> getDragonfireCap(
            DragonType dragon,
            boolean special,
            boolean accurayRoll,
            Protection p) {

        boolean shield = p.shield;
        boolean prayer = p.prayer;
        boolean antifire = p.antifire;
        boolean superAntifire = p.superAntifire;

        //best, all protections
        if (superAntifire && prayer && shield) {
            switch (dragon) {
                case CHROMATIC, METALLIC, ELVARG -> { return new Pair<>(0, 0);}
                case KING_BLACK_DRAGON -> { return special ? new Pair<>(0, 100) : new Pair<>(0, 0);}
            }
        }

        //super antifire & prayer
        if (superAntifire && prayer) {
            switch (dragon) {
                case CHROMATIC, METALLIC, ELVARG -> { return new Pair<>(0, 0);}
                case KING_BLACK_DRAGON -> { return special ? new Pair<>(0, 150) : new Pair<>(0, 0);}
            }
        }
        //super antifire & shield
        if (superAntifire && shield) {
            switch (dragon) {
                case CHROMATIC, ELVARG, METALLIC -> { return new Pair<>(0, 0);}
                case KING_BLACK_DRAGON -> { return special ? new Pair<>(0, 100) : new Pair<>(0, 0);}
            }
        }
        //just super antifire
        if (superAntifire) {
            switch (dragon) {
                case CHROMATIC, ELVARG, METALLIC -> { return new Pair<>(0, 0); }
                case KING_BLACK_DRAGON -> { return special ? new Pair<>(0, 500) : new Pair<>(0, 0); }
            }
        }

        //best protection with regular antifire
        if (shield && antifire && prayer) {
            switch (dragon) {
                case CHROMATIC, METALLIC -> { return new Pair<>(0, 0); }
                case KING_BLACK_DRAGON -> { return special ? new Pair<>(0, 100) : new Pair<>(0, 0); }
                case ELVARG -> { return new Pair<>(0, 40); }
            }
        }

        //shield & antifire
        if (shield && antifire) {
            switch (dragon) {
                case CHROMATIC, METALLIC -> { return new Pair<>(0, 0); }
                case KING_BLACK_DRAGON -> { return special ? new Pair<>(0, 100) : new Pair<>(0, 0); }
                case ELVARG -> { return new Pair<>(0, 70); }
            }
        }

        //prayer & antifire
        if (prayer && antifire) {
            switch (dragon) {
                case CHROMATIC -> { return new Pair<>(0, 0); }
                case KING_BLACK_DRAGON -> { return special ? new Pair<>(0, 150) : new Pair<>(0, 50);  }
                case ELVARG -> { return new Pair<>(0, 400); }
            }
        }

        //shield only
        if (shield) {
            switch (dragon) {
                case CHROMATIC, METALLIC -> { return new Pair<>(0, 50); }
                case KING_BLACK_DRAGON -> { return special ? new Pair<>(0, 100) : new Pair<>(0, 150); }
                case ELVARG -> { return new Pair<>(0, 100); }
            }
        }

        //prayer only
        if (prayer) {
            switch (dragon) {
                case CHROMATIC -> { return new Pair<>(0, 100); }
                case ELVARG -> { return new Pair<>(0, 550); }
                case KING_BLACK_DRAGON -> { return special ? new Pair<>(0, 150) : new Pair<>(0, 200); }
            }
        }

        //antifire only
        if (antifire) {
            switch (dragon) {
                case CHROMATIC, METALLIC -> { return accurayRoll ? new Pair<>(0, 150) : new Pair<>(0, 350);}
                case KING_BLACK_DRAGON -> { return new Pair<>(0, 500); }
                case ELVARG -> { return new Pair<>(0, 550); }
            }
        }

        switch (dragon) {//no protection at all
            case CHROMATIC, METALLIC -> { return accurayRoll ? new Pair<>(0, 300) : new Pair<>(200, 500);}
            case KING_BLACK_DRAGON -> { return special ? new Pair<>(0, 500) : new Pair<>(0, 650); }
            case ELVARG -> { return new Pair<>(0, 700); }
        }

        return new Pair<>(0, 0);
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