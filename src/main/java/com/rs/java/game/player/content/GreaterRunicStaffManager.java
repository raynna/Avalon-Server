package com.rs.java.game.player.content;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map.Entry;

import com.rs.java.game.item.Item;
import com.rs.java.game.item.meta.GreaterRunicStaffMetaData;
import com.rs.java.game.player.Equipment;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;

public class GreaterRunicStaffManager implements Serializable {

    /**
     * @Author -Andreas 2019-21-17
     * @Updated 2019-12-24
     */

    private static final long serialVersionUID = -1603383212410235305L;

    private transient Player player;

    public void setPlayer(Player player) {
        this.player = player;
    }

    private int component;

    public int getComponentId() {
        return component;
    }

    public static final int AIR = 556, WATER = 555, EARTH = 557, FIRE = 554, MIND = 558, NATURE = 561, CHAOS = 562, DEATH = 560, BLOOD = 565, SOUL = 566, ASTRAL = 9075, LAW = 563, ARMADYL = 21773, BODY = 559, BANANA = 1964, COSMIC = 564;

    private static RunicStaffSpellStore[] store = RunicStaffSpellStore.values();

    public enum RunicStaffSpellStore {

        // NORMAL SPELLBOOK
        WIND_RUSH(69, 98, 1, new Item[]{new Item(AIR, 1)}),

        WIND_STRIKE(7, 25, 1, new Item[]{new Item(AIR, 1), new Item(MIND, 1)}),

        CONFUSE(8, 26, 3, new Item[]{new Item(WATER, 3), new Item(EARTH, 2), new Item(BODY, 1)}),

        WATER_STRIKE(9, 28, 5, new Item[]{new Item(WATER, 1), new Item(AIR, 1), new Item(BODY, 1)}),

        ENCHANT1(10, 29, 7, new Item[]{new Item(WATER, 1), new Item(COSMIC, 1)}),

        EARTH_STRIKE(11, 28, 9, new Item[]{new Item(EARTH, 2), new Item(AIR, 1), new Item(MIND, 1)}),

        MOBILISING_TELEPORT(18, 37, 10, new Item[]{new Item(LAW, 1), new Item(WATER, 1), new Item(AIR, 1)}),

        WEAKEN(12, 30, 11, new Item[]{new Item(WATER, 3), new Item(EARTH, 2), new Item(BODY, 1)}),

        FIRE_STRIKE(13, 32, 13, new Item[]{new Item(FIRE, 3), new Item(AIR, 2), new Item(MIND, 1)}),

        BONES_TO_BANANA(14, 33, 15, new Item[]{new Item(EARTH, 2), new Item(WATER, 2), new Item(NATURE, 1)}),

        WIND_BOLT(15, 34, 17, new Item[]{new Item(AIR, 2), new Item(CHAOS, 1)}),

        CURSE(16, 35, 19, new Item[]{new Item(WATER, 2), new Item(EARTH, 3), new Item(BODY, 1)}),

        BIND(17, 36, 20, new Item[]{new Item(EARTH, 3), new Item(WATER, 3), new Item(NATURE, 2)}),

        LOW_ALCHEMY(19, 38, 20, new Item[]{new Item(FIRE, 3), new Item(NATURE, 1)}),

        WATER_BOLT(20, 39, 23, new Item[]{new Item(WATER, 2), new Item(AIR, 2), new Item(CHAOS, 1)}),

        VARROCK_TELEPORT(21, 40, 25, new Item[]{new Item(FIRE, 1), new Item(AIR, 3), new Item(LAW, 1)}),

        ENCHANT2(22, 41, 27, new Item[]{new Item(AIR, 3), new Item(COSMIC, 1)}),

        EARTH_BOLT(23, 42, 29, new Item[]{new Item(EARTH, 3), new Item(AIR, 2), new Item(CHAOS, 1)}),

        LUMBRIDGE_TELEPORT(24, 43, 31, new Item[]{new Item(EARTH, 1), new Item(AIR, 3), new Item(LAW, 1)}),

        TELEGRAB(25, 44, 33, new Item[]{new Item(AIR, 1), new Item(LAW, 1)}),

        FIRE_BOLT(26, 45, 35, new Item[]{new Item(FIRE, 4), new Item(AIR, 3), new Item(CHAOS, 1)}),

        FALADOR_TELEPORT(27, 46, 37, new Item[]{new Item(WATER, 1), new Item(AIR, 3), new Item(LAW, 1)}),

        CRUMBLE_UNDEAD(28, 47, 39, new Item[]{new Item(EARTH, 2), new Item(AIR, 2), new Item(CHAOS, 1)}),

        HOUSE_TELEPORT(29, 48, 40, new Item[]{new Item(LAW, 1), new Item(AIR, 1), new Item(EARTH, 1)}),

        WIND_BLAST(30, 49, 41, new Item[]{new Item(AIR, 3), new Item(DEATH, 1)}),

        SUPERHEAT(31, 50, 43, new Item[]{new Item(FIRE, 4), new Item(NATURE, 1)}),

        CAMELOT_TELEPORT(32, 51, 45, new Item[]{new Item(AIR, 5), new Item(LAW, 1)}),

        WATER_BLAST(33, 52, 47, new Item[]{new Item(WATER, 3), new Item(AIR, 3), new Item(DEATH, 1)}),

        ENCHANT3(34, 53, 49, new Item[]{new Item(FIRE, 5), new Item(COSMIC, 1)}),

        SNARE(35, 55, 50, new Item[]{new Item(EARTH, 4), new Item(WATER, 4), new Item(NATURE, 3)}),

        SLAYER_DART(36, 56, 50, new Item[]{new Item(DEATH, 1), new Item(MIND, 4)}),

        ARDOUGNE_TELEPORT(37, 57, 51, new Item[]{new Item(WATER, 2), new Item(LAW, 2)}),

        EARTH_BLAST(38, 58, 53, new Item[]{new Item(EARTH, 4), new Item(AIR, 3), new Item(DEATH, 1)}),

        HIGH_ALCHEMY(39, 59, 55, new Item[]{new Item(FIRE, 5), new Item(NATURE, 1)}),

        WATER_ORB(40, 60, 56, new Item[]{new Item(WATER, 30), new Item(COSMIC, 3)}),

        ENCHNAT4(41, 61, 57, new Item[]{new Item(EARTH, 10), new Item(COSMIC, 1)}),

        WATCHTOWER_TELEPORT(42, 62, 58, new Item[]{new Item(EARTH, 2), new Item(LAW, 2)}),

        FIRE_BLAST(43, 63, 59, new Item[]{new Item(FIRE, 5), new Item(AIR, 4), new Item(DEATH, 1)}),

        EARTH_ORB(44, 64, 60, new Item[]{new Item(EARTH, 30), new Item(COSMIC, 3)}),

        BONES_TO_PEACHES(45, 65, 60, new Item[]{new Item(NATURE, 2), new Item(WATER, 4), new Item(EARTH, 4)}),

        TROLLHEIM_TELEPORT(46, 69, 61, new Item[]{new Item(FIRE, 2), new Item(LAW, 2)}),

        WIND_WAVE(47, 70, 62, new Item[]{new Item(AIR, 5), new Item(BLOOD, 1)}),

        FIRE_ORB(48, 71, 63, new Item[]{new Item(FIRE, 30), new Item(COSMIC, 3)}),

        APE_ATOLL_TELEPORT(49, 72, 64, new Item[]{new Item(FIRE, 2), new Item(WATER, 2), new Item(LAW, 2), new Item(BANANA, 1)}),

        WATER_WAVE(50, 73, 65, new Item[]{new Item(WATER, 7), new Item(AIR, 5), new Item(BLOOD, 1)}),

        AIR_ORB(51, 74, 66, new Item[]{new Item(AIR, 30), new Item(COSMIC, 3)}),

        VULNERABILITY(52, 75, 66, new Item[]{new Item(EARTH, 5), new Item(WATER, 5), new Item(SOUL, 1)}),

        ENCHANT5(53, 76, 68, new Item[]{new Item(EARTH, 15), new Item(WATER, 15), new Item(COSMIC, 1)}),

        EARTH_WAVE(54, 77, 70, new Item[]{new Item(EARTH, 7), new Item(AIR, 5), new Item(BLOOD, 1)}),

        ENFEEBLE(55, 78, 73, new Item[]{new Item(EARTH, 8), new Item(WATER, 8), new Item(SOUL, 1)}),

        TELEOTHER_LUMBRIDGE(56, 79, 74, new Item[]{new Item(SOUL, 1), new Item(LAW, 1), new Item(EARTH, 1)}),

        FIRE_WAVE(57, 80, 75, new Item[]{new Item(FIRE, 7), new Item(AIR, 5), new Item(BLOOD, 1)}),

        STORM_OF_ARMADYL(70, 99, 77, new Item[]{new Item(ARMADYL, 1)}),

        ENTANGLE(58, 81, 79, new Item[]{new Item(EARTH, 5), new Item(WATER, 5), new Item(NATURE, 4)}),

        STUN(59, 82, 80, new Item[]{new Item(EARTH, 12), new Item(WATER, 12), new Item(SOUL, 1)}),

        CHARGE(60, 83, 80, new Item[]{new Item(FIRE, 3), new Item(BLOOD, 3), new Item(AIR, 3)}),

        WIND_SURGE(61, 84, 81, new Item[]{new Item(AIR, 7), new Item(BLOOD, 1), new Item(DEATH, 1)}),

        TELEOTHER_FALADOR(62, 85, 82, new Item[]{new Item(SOUL, 1), new Item(WATER, 1), new Item(LAW, 1)}),

        TELEPORT_BLOCK(63, 86, 85, new Item[]{new Item(CHAOS, 1), new Item(LAW, 1), new Item(DEATH, 1)}),

        WATER_SURGE(64, 87, 85, new Item[]{new Item(WATER, 10), new Item(AIR, 7), new Item(BLOOD, 1), new Item(DEATH, 1)}),

        ENCHANT6(65, 88, 87, new Item[]{new Item(EARTH, 20), new Item(FIRE, 20), new Item(COSMIC, 1)}),

        EARTH_SURGE(66, 89, 90, new Item[]{new Item(EARTH, 10), new Item(AIR, 7), new Item(BLOOD, 1), new Item(DEATH, 1)}),

        TELEOTHER_CAMELOT(67, 90, 90, new Item[]{new Item(SOUL, 2), new Item(LAW, 1)}),

        FIRE_SURGE(68, 91, 95, new Item[]{new Item(FIRE, 10), new Item(AIR, 10), new Item(BLOOD, 1), new Item(DEATH, 1)}),

        // ANCIENT SPELLBOOK
        SMOKE_RUSH(129, 28, 50, new Item[]{new Item(CHAOS, 2), new Item(DEATH, 2), new Item(FIRE, 1), new Item(AIR, 1)}),

        SHADOW_RUSH(133, 32, 52, new Item[]{new Item(CHAOS, 2), new Item(DEATH, 2), new Item(AIR, 1), new Item(SOUL, 1)}),

        PADDEWWA_TELEPORT(137, 40, 54, new Item[]{new Item(LAW, 2), new Item(FIRE, 1), new Item(AIR, 1)}),

        BLOOD_RUSH(125, 24, 56, new Item[]{new Item(CHAOS, 2), new Item(DEATH, 2), new Item(BLOOD, 1)}),

        ICE_RUSH(121, 20, 58, new Item[]{new Item(CHAOS, 2), new Item(DEATH, 2), new Item(WATER, 2)}),

        SENNTISTEN(138, 41, 60, new Item[]{new Item(LAW, 2), new Item(SOUL, 1)}),

        SMOKE_BURST(131, 30, 62, new Item[]{new Item(CHAOS, 4), new Item(DEATH, 2), new Item(FIRE, 2), new Item(AIR, 2)}),

        SHADOW_BURST(135, 34, 64, new Item[]{new Item(CHAOS, 4), new Item(DEATH, 2), new Item(AIR, 1), new Item(SOUL, 2)}),

        KHARYRLL_TELEPORT(139, 42, 66, new Item[]{new Item(LAW, 2), new Item(BLOOD, 1)}),

        BLOOD_BURST(127, 26, 68, new Item[]{new Item(CHAOS, 4), new Item(DEATH, 2), new Item(BLOOD, 2)}),

        ICE_BURST(123, 22, 70, new Item[]{new Item(CHAOS, 4), new Item(DEATH, 2), new Item(WATER, 4)}),

        LASSAR_TELEPORT(140, 43, 72, new Item[]{new Item(LAW, 2), new Item(WATER, 4)}),

        SMOKE_BLITZ(130, 29, 74, new Item[]{new Item(DEATH, 2), new Item(BLOOD, 2), new Item(FIRE, 2), new Item(AIR, 2)}),

        SHADOW_BLITZ(134, 33, 76, new Item[]{new Item(DEATH, 2), new Item(BLOOD, 2), new Item(AIR, 2), new Item(SOUL, 2)}),

        DAREEYAK_TELEPORT(141, 44, 78, new Item[]{new Item(LAW, 2), new Item(FIRE, 3), new Item(AIR, 2)}),

        BLOOD_BLITZ(126, 25, 80, new Item[]{new Item(DEATH, 2), new Item(BLOOD, 4)}),

        ICE_BLITZ(122, 21, 82, new Item[]{new Item(DEATH, 2), new Item(BLOOD, 2), new Item(WATER, 3)}),

        CARRALLANGAR_TELEPORT(142, 45, 84, new Item[]{new Item(LAW, 2), new Item(SOUL, 2)}),

        SMOKE_BARRAGE(132, 31, 86, new Item[]{new Item(DEATH, 4), new Item(BLOOD, 2), new Item(FIRE, 4), new Item(AIR, 4)}),

        SHADOW_BARRAGE(136, 35, 88, new Item[]{new Item(DEATH, 4), new Item(BLOOD, 2), new Item(AIR, 4), new Item(SOUL, 3)}),

        ANNAKARL_TELEPORT(143, 46, 90, new Item[]{new Item(LAW, 2), new Item(BLOOD, 2)}),

        BLOOD_BARRAGE(128, 27, 92, new Item[]{new Item(DEATH, 4), new Item(BLOOD, 4), new Item(SOUL, 1)}),

        ICE_BARRAGE(124, 23, 94, new Item[]{new Item(DEATH, 4), new Item(BLOOD, 2), new Item(WATER, 6)}),

        GHORROCK_TELEPORT(144, 47, 96, new Item[]{new Item(LAW, 2), new Item(BLOOD, 2)}),

        ;

        public int component;
        public int spellId;
        private int level;
        private Item[] rune;

        RunicStaffSpellStore(int component, int spellId, int level, Item[] rune) {
            this.component = component;
            this.spellId = spellId;
            this.level = level;
            this.setRune(rune);
        }

        public Item[] getRune() {
            return rune;
        }

        public void setRune(Item[] rune) {
            this.rune = rune;
        }

        public static RunicStaffSpellStore getSpell(int spellId) {
            for (RunicStaffSpellStore s : store) {
                if (s.spellId == spellId) {
                    return s;
                }
            }
            return null;
        }

        public RunicStaffSpellStore getSpellComp(int component) {
            for (RunicStaffSpellStore s : store) {
                if (s.component == component) {
                    return s;
                }
            }
            return null;
        }

        public static RunicStaffSpellStore getSpellStore(int spellId, int component) {
            for (RunicStaffSpellStore s : store) {
                if (s.spellId == spellId && s.component == component)
                    return s;
            }
            return null;
        }
    }

    private GreaterRunicStaffMetaData getMeta() {
        Item weapon = player.getEquipment().getItem(3);
        if (weapon == null) return null;
        return weapon.getMetadata() instanceof GreaterRunicStaffMetaData m ? m : null;
    }

    private GreaterRunicStaffMetaData getOrCreateMeta() {
        Item weapon = player.getEquipment().getItem(3);
        if (weapon == null) return null;
        GreaterRunicStaffMetaData meta = weapon.getMetadata() instanceof GreaterRunicStaffMetaData m ? m : null;
        if (meta == null) {
            meta = new GreaterRunicStaffMetaData();
            weapon.setMetadata(meta);
        }
        return meta;
    }

    public int getCharges() {
        GreaterRunicStaffMetaData meta = getOrCreateMeta();
        return meta != null ? meta.getCharges() : 0;
    }

    public void removeCharge(int spellId, int amount) {
        GreaterRunicStaffMetaData meta = getOrCreateMeta();
        if (meta == null) return;

        if (meta.getSpellId() != spellId) return;

        meta.removeCharges(amount);

        if (meta.getCharges() <= 0) {
            Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
            if (weapon != null) {
                weapon.setId(Item.getId("item.greater_runic_staff_uncharged"));
                player.getEquipment().refresh(Equipment.SLOT_WEAPON);
                player.getAppearence().generateAppearenceData();
                player.getPackets().sendGameMessage("You are out of charges in your runic staff.");
            }
        }
    }

    private boolean hasRequirements(Player player, int component, int spellId) {
        RunicStaffSpellStore s = RunicStaffSpellStore.getSpellStore(spellId, component);
        if (s == null)
            return false;
        if (player.getSkills().getLevel(Skills.MAGIC) < s.level) {
            player.getPackets().sendGameMessage("You need at least a level of " + s.level + " magic to set this spell.");
            return false;
        }
        hasRunes(player, component, spellId);
        return true;
    }

    private boolean hasRunes(Player player, int component, int spellId) {
        RunicStaffSpellStore s = RunicStaffSpellStore.getSpellStore(spellId, component);
        if (s == null)
            return false;
        for (Item item : s.getRune()) {
            if (item == null)
                continue;
            if (!player.getInventory().containsItem(item.getId(), item.getAmount())) {
                player.getPackets().sendGameMessage("You don't have enough " + item.getName() + " to cast this spell.");
                return false;
            }
        }
        Item item = (Item) player.getTemporaryAttributtes().get("CHOOSE_STAFF_SPELL");
        player.getRunicStaff().setSpellId(item, component, spellId);
        return true;
    }

    public void setSpellId(Item staff, int component, int spellId) {
        this.component = component;
        RunicStaffSpellStore s = RunicStaffSpellStore.getSpellStore(spellId, component);
        if (s == null)
            return;
        if (staff.getMetadata() == null) {
            staff.setMetadata(new GreaterRunicStaffMetaData(s.spellId, 0));
        }
        GreaterRunicStaffMetaData data = (GreaterRunicStaffMetaData) staff.getMetadata();
        if (data != null) {
            data.setSpellId(s.spellId);
            player.message("Runic staff can now charge " + s.name().toLowerCase().replace('_', ' ') + ".");
        }
        if (wearing) {
            player.getEquipment().getItem(Equipment.SLOT_WEAPON).setId(Item.getId("item.greater_runic_staff_uncharged"));
            player.getEquipment().refresh(Equipment.SLOT_WEAPON);
            player.getAppearence().generateAppearenceData();
        } else {
            Item newStaff = new Item(staff.clone());
            newStaff.setId(Item.getId("item.greater_runic_staff_uncharged"));
            player.getInventory().deleteItem(staff);
            player.getInventory().addItem(newStaff);
        }
    }

    public int getSpellId() {
        for (Entry<Integer, Item[]> charges : player.getStaffCharges().entrySet()) {
            if (charges == null)
                continue;
            return charges.getKey();
        }
        return -1;
    }

    public boolean hasRunes() {
        boolean hasRunes = true;
        for (Entry<Integer, Item[]> charges : player.getStaffCharges().entrySet()) {
            if (charges == null)
                continue;
            hasRunes = charges.getValue() != null;
        }
        return hasRunes;
    }

    public void clearCharges(boolean wearing, boolean bank) {
        Item weapon = wearing ? player.getEquipment().getItem(3)
                : (Item) player.getTemporaryAttributtes().get("GREATER_RUNIC_STAFF");
        if (weapon == null)
            return;

        GreaterRunicStaffMetaData meta = weapon.getMetadata() instanceof GreaterRunicStaffMetaData m ? m : null;
        if (meta == null || meta.getSpellId() == -1 || meta.getCharges() <= 0) {
            player.getPackets().sendGameMessage("Your runic staff has no stored spells.");
            return;
        }

        int spellId = meta.getSpellId();
        int charges = meta.getCharges();

        RunicStaffSpellStore s = RunicStaffSpellStore.getSpell(spellId);
        if (s == null)
            return;

        if (!bank) {
            for (Item rune : s.getRune()) {
                int totalAmount = rune.getAmount() * charges;
                if (!player.getInventory().containsItem(rune.getId(), 1) && !player.getInventory().hasFreeSlots()) {
                    player.getPackets().sendGameMessage("You don't have enough inventory space to clear the staff.");
                    return;
                }
            }
        }

        for (Item rune : s.getRune()) {
            int totalAmount = rune.getAmount() * charges;
            if (bank) {
                player.getBank().addItem(rune.getId(), totalAmount, true);
            } else {
                player.getInventory().addItem(rune.getId(), totalAmount);
            }
        }
        meta.removeCharges(charges);
        if (wearing) {
            weapon.setId(Item.getId("item.greater_runic_staff_uncharged"));
            player.getEquipment().refresh(Equipment.SLOT_WEAPON);
            player.getAppearence().generateAppearenceData();
        } else {
            Item unchargedStaff = weapon.clone();
            unchargedStaff.setId(Item.getId("item.greater_runic_staff_uncharged"));
            player.getInventory().deleteItem(weapon);
            player.getInventory().addItem(unchargedStaff);
        }

        if (bank) {
            player.getPackets().sendGameMessage("You clear your runic staff.");
            player.getPackets().sendGameMessage("All your runes in your runic staff were banked.");
        } else {
            player.getPackets().sendGameMessage("You clear your greater runic staff spell.");
        }
    }


    public boolean wearing;

    public void clearSpell(boolean wearing, boolean bank) {
        Item item = (Item) player.getTemporaryAttributtes().get("GREATER_RUNIC_STAFF");
        GreaterRunicStaffMetaData meta = (GreaterRunicStaffMetaData) item.getMetadata();
        if (meta == null || meta.getSpellId() == -1 || meta.getCharges() <= 0) {
            player.getPackets().sendGameMessage("Your runic staff has no stored spells.");
            return;
        }

        int spellId = meta.getSpellId();
        int charges = meta.getCharges();

        RunicStaffSpellStore s = RunicStaffSpellStore.getSpell(spellId);
        if (s == null) {
            player.getPackets().sendGameMessage("Unknown spell stored in staff.");
            return;
        }

        if (!bank) {
            for (Item rune : s.getRune()) {
                int totalAmount = rune.getAmount() * charges;
                if (!player.getInventory().containsItem(rune.getId(), 1) && !player.getInventory().hasFreeSlots()) {
                    player.getPackets().sendGameMessage("You don't have enough inventory space to clear the staff.");
                    return;
                }
            }
        }

        for (Item rune : s.getRune()) {
            int totalAmount = rune.getAmount() * charges;
            if (bank) {
                player.getBank().addItem(rune.getId(), totalAmount, true);
            } else {
                player.getInventory().addItem(rune.getId(), totalAmount);
            }
        }

        meta.removeCharges(charges);

        Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
        if (weapon != null) {
            if (wearing) {
                weapon.setId(Item.getId("item.greater_runic_staff_inactive"));
                player.getEquipment().refresh(Equipment.SLOT_WEAPON);
                player.getAppearence().generateAppearenceData();
            } else {
                player.getInventory().deleteItem("item.greater_runic_staff_uncharged", 1);
                player.getInventory().addItem("item.greater_runic_staff_inactive", 1);
            }
        }

        if (bank) {
            player.getPackets().sendGameMessage("You clear your runic staff.");
            player.getPackets().sendGameMessage("All your runes in your runic staff were banked.");
        } else {
            player.getPackets().sendGameMessage("You clear your greater runic staff spell.");
        }
    }


    public void openChooseSpell(Player player, Item item) {
        if (player.getCombatDefinitions().getSpellBook() == 430) {
            player.getPackets().sendGameMessage("You can't currently set lunar spells to your runic staff.");
            return;
        }
        player.temporaryAttribute().put("CHOOSE_STAFF_SPELL", item);
        player.getInterfaceManager().sendInventoryInterface(1276);
    }

    public void chargeCombat(int amount) {
        Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
        if (weapon == null)
            return;

        GreaterRunicStaffMetaData meta = weapon.getMetadata() instanceof GreaterRunicStaffMetaData m ? m : null;
        if (meta == null || meta.getSpellId() == -1) {
            player.message("Your greater runic staff does not have a spell stored.");
            return;
        }

        int current = meta.getCharges();
        int toAdd = Math.min(amount, meta.getMaxValue() - current);
        if (toAdd <= 0) {
            player.message("Your greater runic staff cannot hold any more charges.");
            return;
        }

        meta.addCharges(toAdd);

        if (weapon.isItem("item.greater_runic_staff_uncharged")) {
            weapon.setId(Item.getId("item.greater_runic_staff_charged"));
            player.getEquipment().refresh(Equipment.SLOT_WEAPON);
            player.getAppearence().generateAppearenceData();
        }

        player.message("Your greater runic staff has been charged with " + toAdd + " spell" + (toAdd > 1 ? "s." : "."));
    }

    public void chargeStaff(int amount, int spellId, boolean inventory) {
        Item staff = (Item) player.getTemporaryAttributtes().remove("GREATER_RUNIC_STAFF");
        GreaterRunicStaffMetaData meta = (GreaterRunicStaffMetaData) staff.getMetadata();
        if (meta == null) return;

        RunicStaffSpellStore s = RunicStaffSpellStore.getSpell(spellId);
        if (s == null) return;

        if (meta.getSpellId() != -1 && meta.getSpellId() != spellId) {
            player.getPackets().sendGameMessage("Your staff already contains a different spell. Clear it first.");
            return;
        }

        int current = meta.getCharges();
        int toAdd = Math.min(amount, meta.getMaxValue() - current);
        if (toAdd <= 0) {
            player.getPackets().sendGameMessage("You can't have more than 1,000 charges.");
            return;
        }

        for (Item rune : s.getRune()) {
            if (!player.getInventory().containsItem(rune.getId(), rune.getAmount() * toAdd)) {
                player.getPackets().sendGameMessage("You need at least " + rune.getAmount() * toAdd + " " + rune.getName() + "s.");
                return;
            }
        }

        for (Item rune : s.getRune()) {
            player.getInventory().deleteItem(rune.getId(), rune.getAmount() * toAdd);
        }

        meta.addCharges(toAdd);
        if (meta.getSpellId() == -1) {
            meta = new GreaterRunicStaffMetaData(spellId, meta.getCharges());
            player.getEquipment().getItem(Equipment.SLOT_WEAPON).setMetadata(meta);
        }
        if (inventory) {
            staff.setId(Item.getId("item.greater_runic_staff_charged"));
            player.getInventory().refresh();
        } else {
        Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
        if (weapon.isItem("item.greater_runic_staff_uncharged")) {
            weapon.setId(Item.getId("item.greater_runic_staff_charged"));
            player.getEquipment().refresh(Equipment.SLOT_WEAPON);
            player.getAppearence().generateAppearenceData();
            }
        }

        player.getPackets().sendGameMessage("You charge your staff with " + toAdd + " " + s.name().toLowerCase().replace('_', ' ') + " spells.");
    }

    private Item[] scaleRunes(Item[] runes, int amount) {
        return Arrays.stream(runes)
                .map(r -> new Item(r.getId(), r.getAmount() * amount))
                .toArray(Item[]::new);
    }

    public final void processSpell(Player player, int componentId, int packetId) {
        player.stopAll(false);
        for (RunicStaffSpellStore s : store) {
            if (s.component == componentId)
                checkSpell(player, s.component, s.spellId);
        }
    }

    public final boolean checkSpell(Player player, int component, int spellId) {
        RunicStaffSpellStore s = RunicStaffSpellStore.getSpellStore(spellId, component);
        if (s == null)
            return false;
        if (!hasRequirements(player, component, spellId))
            return false;
        return true;
    }

}
