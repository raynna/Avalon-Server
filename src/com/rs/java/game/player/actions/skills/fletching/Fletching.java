package com.rs.java.game.player.actions.skills.fletching;

import java.util.HashMap;
import java.util.Map;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.Animation;
import com.rs.java.game.item.Item;
import com.rs.java.game.player.Inventory;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.Action;
import com.rs.java.game.player.content.tasksystem.TaskManager.Tasks;
import com.rs.java.utils.Utils;

/**
 * @Improved Andreas - AvalonPK
 */

public class Fletching extends Action {

    public static final int KNIFE = 946;
    public static final int CHISEL = 1755;
    public static final int BOW_STRING = 1777;
    public static final int CROSSBOW_STRING = 9438;

    public enum FletchingData {

        /**
         * (u)'s and completed bows
         */

        REGULAR_BOW(1511, KNIFE, new int[]{52, 50, 48, 9440}, new int[]{1, 5, 10, 9}, new double[]{0.33, 5, 10, 6}, new Animation(6702)),

        STRUNG_SHORT_BOW(50, BOW_STRING, new int[]{841}, new int[]{5}, new double[]{5}, new Animation(6678)),

        STRUNG_LONG_BOW(48, BOW_STRING, new int[]{839}, new int[]{10}, new double[]{10}, new Animation(6684)),

        OAK_BOW(1521, KNIFE, new int[]{54, 56, 9442}, new int[]{20, 25, 24}, new double[]{16.5, 25, 16}, new Animation(6702)),

        STRUNG_OAK_SHORT_BOW(54, BOW_STRING, new int[]{843}, new int[]{20}, new double[]{16.5}, new Animation(6679)),

        STRUNG_OAK_LONG_BOW(56, BOW_STRING, new int[]{845}, new int[]{25}, new double[]{25}, new Animation(6685)),

        WILLOW_BOW(1519, KNIFE, new int[]{60, 58, 9444}, new int[]{35, 40, 39}, new double[]{33.3, 41.5, 22}, new Animation(6702)),

        STRUNG_WILLOW_SHORT_BOW(60, BOW_STRING, new int[]{849}, new int[]{35}, new double[]{33.3}, new Animation(6680)),

        STRUNG_WILLOW_LONG_BOW(58, BOW_STRING, new int[]{847}, new int[]{40}, new double[]{41.5}, new Animation(6686)),

        MAPLE_BOW(1517, KNIFE, new int[]{64, 62, 9448}, new int[]{50, 55, 54}, new double[]{50, 58.3, 32}, new Animation(6702)),

        STRUNG_MAPLE_SHORT_BOW(64, BOW_STRING, new int[]{853}, new int[]{50}, new double[]{50}, new Animation(6681)),

        STRUNG_MAPLE_LONG_BOW(62, BOW_STRING, new int[]{851}, new int[]{55}, new double[]{58.3}, new Animation(6687)),

        YEW_BOW(1515, KNIFE, new int[]{68, 66, 9452}, new int[]{65, 70, 69}, new double[]{67.5, 75, 50}, new Animation(6702)),

        STRUNG_YEW_SHORT_BOW(68, BOW_STRING, new int[]{857}, new int[]{65}, new double[]{67.5}, new Animation(6682)),

        STRUNG_YEW_LONG_BOW(66, BOW_STRING, new int[]{855}, new int[]{70}, new double[]{75}, new Animation(6688)),

        MAGIC_BOW(1513, KNIFE, new int[]{72, 70}, new int[]{80, 85}, new double[]{83.25, 91.5}, new Animation(6702)),

        STRUNG_MAGIC_SHORT_BOW(72, BOW_STRING, new int[]{861}, new int[]{80}, new double[]{83.25}, new Animation(6683)),

        STRUNG_MAGIC_LONG_BOW(70, BOW_STRING, new int[]{859}, new int[]{85}, new double[]{91.5}, new Animation(6689)),

        BLOODWOOD(24121, KNIFE, new int[]{24122, 24127}, new int[]{93, 93}, new double[]{13.0, 13.0}, new Animation(6702)),

        BLISTERWOOD(21600, 946, new int[]{21581, 21580, 21582}, new int[]{70, 70, 70}, new double[]{10, 100, 100}, new Animation(6702)),

        ACHEY_TREE(2862, 946, new int[]{2864, 4825}, new int[]{5, 30}, new double[]{1.6, 45}, new Animation(6702)),

        STRUNG_OGRE_COMP_BOW(4825, 1777, new int[]{4827}, new int[]{30}, new double[]{45}, new Animation(6688)),

        /**
         * Crossbows
         */

        U_BRONZE_CBOW(9440, 9420, new int[]{9454}, new int[]{9}, new double[]{6}, new Animation(-1)),

        U_IRON_CBOW(9444, 9423, new int[]{9457}, new int[]{39}, new double[]{22}, new Animation(-1)),

        U_BLURITE_CBOW(9442, 9422, new int[]{9456}, new int[]{24}, new double[]{16}, new Animation(-1)),

        U_STEEL_CBOW(9446, 9425, new int[]{9459}, new int[]{46}, new double[]{27}, new Animation(-1)),

        U_MITHRIL_CBOW(9448, 9427, new int[]{9461}, new int[]{54}, new double[]{32}, new Animation(-1)),

        U_ADAMANT_CBOW(9450, 9429, new int[]{9463}, new int[]{61}, new double[]{41}, new Animation(-1)),

        U_RUNITE_CBOW(9452, 9431, new int[]{9465}, new int[]{69}, new double[]{50}, new Animation(-1)),

        BRONZE_CBOW(9454, 9438, new int[]{9174}, new int[]{9}, new double[]{6.0}, new Animation(6671)),

        IRON_CBOW(9457, 9438, new int[]{9177}, new int[]{39}, new double[]{22}, new Animation(6673)),

        STEEL_CBOW(9459, 9438, new int[]{9465}, new int[]{46}, new double[]{27}, new Animation(6674)),

        BLURITE_CBOW(9456, 9438, new int[]{9176}, new int[]{24}, new double[]{16}, new Animation(6672)),

        MITHRIL_CBOW(9461, 9438, new int[]{9181}, new int[]{52}, new double[]{32}, new Animation(6675)),

        ADAMANT_CBOW(9463, 9438, new int[]{9183}, new int[]{61}, new double[]{41}, new Animation(6676)),

        RUNITE_CBOW(9465, 9438, new int[]{9185}, new int[]{69}, new double[]{50}, new Animation(6677)),

        /**
         * Arrows
         */

        HEADLESS_ARROWS(52, 314, new int[]{53}, new int[]{1}, new double[]{1}, new Animation(-1)),

        BRONZE_ARROWS(39, 53, new int[]{882}, new int[]{1}, new double[]{0.4}, new Animation(-1)),

        IRON_ARROWS(40, 53, new int[]{884}, new int[]{15}, new double[]{3.8}, new Animation(-1)),

        STEEL_ARROWS(41, 53, new int[]{886}, new int[]{30}, new double[]{6.3}, new Animation(-1)),

        MITHRIL_ARROWS(42, 53, new int[]{888}, new int[]{45}, new double[]{8.8}, new Animation(-1)),

        BROAD_ARROWS(13278, 53, new int[]{4160}, new int[]{52}, new double[]{15}, new Animation(-1)),

        ADAMANT_ARROWS(43, 53, new int[]{890}, new int[]{60}, new double[]{11.3}, new Animation(-1)),

        RUNITE_ARROWS(44, 53, new int[]{892}, new int[]{75}, new double[]{13.8}, new Animation(-1)),

        DRAGON_ARROWS(11237, 53, new int[]{11212}, new int[]{90}, new double[]{16.3}, new Animation(-1)),

        DRAGONBANE_ARROWS(21823, 53, new int[]{21640}, new int[]{76}, new double[]{10}, new Animation(-1)),

        ABYSSALBANE_ARROWS(21838, 53, new int[]{21655}, new int[]{76}, new double[]{10}, new Animation(-1)),

        BASILISKBANE_ARROWS(21833, 53, new int[]{21650}, new int[]{76}, new double[]{10}, new Animation(-1)),

        WALLASALKIBANE_ARROWS(21828, 53, new int[]{21645}, new int[]{76}, new double[]{10}, new Animation(-1)),

        /**
         * Brutal Arrows
         */

        FLIGHTED_OGRE_ARROW(2864, 314, new int[]{2865}, new int[]{5}, new double[]{0.9}, new Animation(-1)),

        BRONZE_BRUTAL(4819, 2865, new int[]{4773}, new int[]{7}, new double[]{1.4}, new Animation(-1)),

        IRON_BRUTAL(4820, 2865, new int[]{4778}, new int[]{18}, new double[]{2.6}, new Animation(-1)),

        STEEL_BRUTAL(1539, 2865, new int[]{4783}, new int[]{33}, new double[]{5.1}, new Animation(-1)),

        BLACK_BRUTAL(4821, 2865, new int[]{4788}, new int[]{38}, new double[]{6.4}, new Animation(-1)),

        MITHRIL_BRUTAL(4822, 2865, new int[]{4793}, new int[]{49}, new double[]{7.5}, new Animation(-1)),

        ADAMANT_BRUTAL(4823, 2865, new int[]{4798}, new int[]{62}, new double[]{10.1}, new Animation(-1)),

        RUNE_BRUTAL(4824, 2865, new int[]{4803}, new int[]{77}, new double[]{12.5}, new Animation(-1)),

        /**
         * Bolts
         */

        BRONZE_BOLT(9375, 314, new int[]{877}, new int[]{9}, new double[]{0.5}, new Animation(-1)),

        IRON_BOLT(9337, 314, new int[]{9140}, new int[]{39}, new double[]{1.5}, new Animation(-1)),

        STEEL_BOLT(9339, 314, new int[]{9141}, new int[]{46}, new double[]{3.5}, new Animation(-1)),

        MITHRIL_BOLT(9379, 314, new int[]{9142}, new int[]{54}, new double[]{5}, new Animation(-1)),

        ADAMANT_BOLT(9380, 314, new int[]{9143}, new int[]{61}, new double[]{7}, new Animation(-1)),

        RUNITE_BOLT(9381, 314, new int[]{9144}, new int[]{69}, new double[]{10}, new Animation(-1)),

        OPAL_BOLTS(45, 877, new int[]{879}, new int[]{11}, new double[]{1.6}, new Animation(-1)),

        BLURITE_BOLTS(9376, 314, new int[]{9139}, new int[]{24}, new double[]{1}, new Animation(-1)),

        JADE_BOLTS(9187, 9376, new int[]{9335}, new int[]{26}, new double[]{2.4}, new Animation(-1)),

        PEARL_BOLTS(46, 9140, new int[]{880}, new int[]{41}, new double[]{3.2}, new Animation(-1)),

        SILVER_BOLTS(9382, 314, new int[]{9145}, new int[]{43}, new double[]{2.5}, new Animation(-1)),

        RED_TOPAZ_BOLTS(9188, 9141, new int[]{9336}, new int[]{48}, new double[]{3.9}, new Animation(-1)),

        BARBED_BOLTS(47, 877, new int[]{881}, new int[]{51}, new double[]{9.5}, new Animation(-1)),

        BROAD_BOLTS(13279, 314, new int[]{13280}, new int[]{55}, new double[]{3}, new Animation(-1)),

        SAPPHIRE_BOLTS(9189, 9142, new int[]{9337}, new int[]{56}, new double[]{2.4}, new Animation(-1)),

        EMERALD_BOLTS(9190, 9142, new int[]{9335}, new int[]{26}, new double[]{4.7}, new Animation(-1)),

        RUBY_BOLTS(9191, 9143, new int[]{9339}, new int[]{63}, new double[]{6.3}, new Animation(-1)),

        DIAMOND_BOLTS(9192, 9143, new int[]{9340}, new int[]{65}, new double[]{7}, new Animation(-1)),

        DRAGON_BOLTS(9193, 9144, new int[]{9341}, new int[]{71}, new double[]{8.2}, new Animation(-1)),

        ONYX_BOLTS(9194, 9144, new int[]{9342}, new int[]{73}, new double[]{9.4}, new Animation(-1)),

        KEBBIT_BOLTS(10105, CHISEL, new int[]{10158}, new int[]{32}, new double[]{6.0}, new Animation(-1)),

        LONG_KEBBIT_BOLTS(10107, CHISEL, new int[]{10159}, new int[]{42}, new double[]{7.89}, new Animation(-1)),

        BAKRIMINEL_BOLTS(24127, 24122, new int[]{24116}, new int[]{93}, new double[]{13.0}, new Animation(-1)),


        DRAGONBANE_BOLTS(21843, 314, new int[]{21660}, new int[]{80}, new double[]{7}, new Animation(-1)),

        ABYSSALBANE_BOLTS(21858, 314, new int[]{21675}, new int[]{80}, new double[]{7}, new Animation(-1)),

        BASILISKBANE_BOLTS(21848, 314, new int[]{21670}, new int[]{80}, new double[]{7}, new Animation(-1)),

        WALLASALKIBANE_BOLTS(21853, 314, new int[]{21665}, new int[]{80}, new double[]{7}, new Animation(-1)),

        /**
         * Darts
         */
        BRONZE_DART(819, 314, new int[]{806}, new int[]{1}, new double[]{0.8}, new Animation(-1)),

        IRON_DART(820, 314, new int[]{807}, new int[]{22}, new double[]{1}, new Animation(-1)),

        STEEL_DART(821, 314, new int[]{808}, new int[]{37}, new double[]{1.7}, new Animation(-1)),

        MITHRIL_DART(822, 314, new int[]{809}, new int[]{52}, new double[]{4}, new Animation(-1)),

        ADAMANT_DART(823, 314, new int[]{810}, new int[]{67}, new double[]{7.6}, new Animation(-1)),

        RUNITE_DART(824, 314, new int[]{811}, new int[]{81}, new double[]{12.2}, new Animation(-1)),

        DRAGON_DART(11232, 314, new int[]{11230}, new int[]{95}, new double[]{18.4}, new Animation(-1)),

        /**
         * Gem/Bolt Tips
         */

        OPAL(1609, CHISEL, new int[]{45}, new int[]{11}, new double[]{1.5}, new Animation(-1)),

        JADE(1611, CHISEL, new int[]{9187}, new int[]{26}, new double[]{2}, new Animation(-1)),

        PEARL(411, CHISEL, new int[]{46}, new int[]{41}, new double[]{3.2}, new Animation(-1)),

        RED_TOPAZ(1613, CHISEL, new int[]{9188}, new int[]{48}, new double[]{4}, new Animation(-1)),

        SAPPHIRE(1607, CHISEL, new int[]{9189}, new int[]{56}, new double[]{4}, new Animation(-1)),

        EMERALD(1605, CHISEL, new int[]{9190}, new int[]{58}, new double[]{5.5}, new Animation(-1)),

        RUBY(1603, CHISEL, new int[]{9191}, new int[]{63}, new double[]{6.3}, new Animation(-1)),

        DIAMOND(1601, CHISEL, new int[]{9192}, new int[]{65}, new double[]{7}, new Animation(-1)),

        DRAGONSTONE(1615, CHISEL, new int[]{9193}, new int[]{71}, new double[]{8.2}, new Animation(-1)),

        ONYX(6573, CHISEL, new int[]{9194}, new int[]{73}, new double[]{9.4}, new Animation(-1)),

        /**
         * Other
         */

        WOLFBONE_ARROWTIPS(2859, CHISEL, new int[]{2861}, new int[]{5}, new double[]{2.5}, new Animation(-1)),

        OGRE_ARROW(2861, 2865, new int[]{2866}, new int[]{5}, new double[]{1.0}, new Animation(-1)),

        BOLAS(21359, 21358, new int[]{21365}, new int[]{87}, new double[]{50.0}, new Animation(-1)),

        SAGAIE(21353, 21358, new int[]{21364}, new int[]{83}, new double[]{40.0}, new Animation(-1)),

        MITHRIL_GRAPPLE_TIP(9416, 9142, new int[]{9418}, new int[]{59}, new double[]{50.0}, new Animation(-1)),

        UNF_MITHRIL_GRAPPE(9418, 954, new int[]{9419}, new int[]{59}, new double[]{50.0}, new Animation(-1)),

        ;

        private static Map<Integer, FletchingData> fletching = new HashMap<Integer, FletchingData>();

        public static FletchingData forId(int id) {
            return fletching.get(id);
        }

        static {
            for (FletchingData fletchingData : FletchingData.values())
                fletching.put(fletchingData.id, fletchingData);
        }

        private int[] product, level;
        private int id, tool;
        private double[] xp;
        private Animation anim;

        private FletchingData(int id, int tool, int[] product, int level[], double[] xp, Animation anim) {
            this.id = id;
            this.product = product;
            this.tool = tool;
            this.xp = xp;
            this.anim = anim;
            this.level = level;
        }

        public int getId() {
            return id;
        }

        public int getTool() {
            return tool;
        }

        public int[] getProduct() {
            return product;
        }

        public int[] getLevel() {
            return level;
        }

        public double[] getXp() {
            return xp;
        }

        public Animation getAnim() {
            return anim;
        }
    }

    private FletchingData fletchingData;
    private int option;
    private int ticks;

    public Fletching(FletchingData fletchingData, int option, int ticks) {
        this.fletchingData = fletchingData;
        this.option = option;
        this.ticks = ticks;
    }


    public ItemDefinitions getDefs(int id) {
        return ItemDefinitions.getItemDefinitions(id);
    }

    @Override
    public boolean start(Player player) {
        if (option >= fletchingData.getProduct().length)
            return false;
        int tool = fletchingData.getTool();
        if (!hasTool(player)) {
            if (tool == CHISEL || tool == KNIFE) {
                player.message("You need a " + getDefs(tool).getName() + " to fletch this.");
            } else {
                player.message("You don't have any " + getDefs(tool).getName() + " to fletch this.");
            }
            return false;
        }
        if (!process(player))
            return false;
        setActionDelay(player, 1);
        return true;
    }

    @Override
    public boolean process(Player player) {
        if (ticks <= 0) {
            return false;
        }
        Inventory inventory = player.getInventory();
        Skills skills = player.getSkills();
        int itemId = fletchingData.getId();
        int finishedProduct = fletchingData.getProduct()[option];
        int levelRequirement = fletchingData.getLevel()[option];
        if (fletchingData == FletchingData.BOLAS && !inventory.containsItem(itemId, 2)) {
            player.message("You need at least 2 " + getDefs(itemId).getName() + " to fletch bolas.");
            return false;
        }
        if (finishedProduct == 2845 && !inventory.containsOneItem(2859)) {
            player.message("You need wolf bones to fletch this bow.");
            return false;
        }
        if (!hasTool(player)) {
            return false;
        }
        if (skills.getLevel(Skills.FLETCHING) < levelRequirement) {
            player.message("You need a level of " + levelRequirement + " to fletch this.");
            return false;
        }
        if (!inventory.hasFreeSlots() && getDefs(finishedProduct).isStackable() && !inventory.containsOneItem(finishedProduct)) {
            player.message("You don't have any inventory space left to fletch this.");
            return false;
        }
        return true;
    }


    private boolean hasTool(Player player) {
        return player.getInventory().containsOneItem(getTool()) || player.getToolbelt().contains(getTool());
    }

    public int getTool() {
        return fletchingData.getTool();
    }

    public static int maxMakeQuantity(FletchingData fletchingData, Item item) {
        String defs = ItemDefinitions.getItemDefinitions(fletchingData.getId()).getName().toLowerCase();
        String itemDefs = ItemDefinitions.getItemDefinitions(item.getId()).getName().toLowerCase();
        if (fletchingData.getTool() == CHISEL) {
            if (fletchingData.getId() == 6573)
                return 24;
            if (fletchingData.getId() == 10105 || fletchingData.getId() == 10107)
                return 6;
            if (fletchingData.getId() == 2859)
                return Utils.random(2, 6);
            return 12;
        }
        if (item.getId() == 24127)
            return 12;
        if (item.getId() == 21581)
            return 10;
        if (item.getId() == 2864 || item.getId() == 2866)
            return Utils.random(2, 6);
        if (item.getId() == 2865)
            return 4;
        if (itemDefs.contains("shaft"))
            return 15;
        if (itemDefs.contains("brutal"))
            return 3;
        if (defs.contains("arrow"))
            return 15;
        if (defs.contains("dart"))
            return 10;
        if (defs.contains("bolt"))
            return 10;
        if (defs.contains("sagaie"))
            return 5;
        return 1;

    }

    public FletchingData getFletch() {
        return fletchingData;
    }

    public static boolean maxMakeQuantityTen(Item item) {
        String defs = ItemDefinitions.getItemDefinitions(item.getId()).getName().toLowerCase();
        if (defs.contains(" arrow") || defs.toLowerCase().equalsIgnoreCase("mutated vine") || defs.contains(" dart") || defs.contains("feather") || defs.contains(" bolt"))
            return true;
        return false;
    }

    @Override
    public int processWithDelay(Player player) {
        ticks--;
        player.animate(fletchingData.getAnim());
        int amount = maxMakeQuantity(fletchingData, new Item(fletchingData.getProduct()[option]));
        if (!player.getInventory().containsItem(fletchingData.getTool(), amount) && fletchingData.getTool() != CHISEL && fletchingData.getTool() != KNIFE)
            amount = player.getInventory().getNumberOf(fletchingData.getTool());
        if (!player.getInventory().containsItem(fletchingData.getId(), amount) && fletchingData.getTool() != CHISEL && fletchingData.getTool() != KNIFE)
            amount = player.getInventory().getNumberOf(fletchingData.getId());
        if (fletchingData.getProduct()[option] == 4825)
            player.getInventory().deleteItem(2859, 1);
        if (fletchingData.getId() == 21359)
            player.getInventory().deleteItem(fletchingData.getId(), 2);
        else
            player.getInventory().deleteItem(fletchingData.getId(), (fletchingData.getProduct()[option] == 52 || fletchingData.getProduct()[option] == 24122 || fletchingData.getProduct()[option] == 2864 || fletchingData.getProduct()[option] == 21581 || fletchingData.getTool() == CHISEL) ? 1 : amount);
        player.getInventory().addItem(fletchingData.getProduct()[option], amount);
        if (fletchingData.getTool() != KNIFE && fletchingData.getTool() != CHISEL)
            player.getInventory().deleteItem(fletchingData.getTool(), amount);
        player.getPackets().sendGameMessage(fletchingMessage(new Item(fletchingData.getProduct()[option]), fletchingData.getTool(), fletchingData.getId(), amount));
        player.getSkills().addXp(Skills.FLETCHING, fletchingData.getXp()[option] * amount);
        if (new Item(fletchingData.getProduct()[option]).getId() == 50)
            player.getTaskManager().checkComplete(Tasks.FLETCH_SHORTBOW);
        if (new Item(fletchingData.getProduct()[option]).getId() == 62)
            player.getTaskManager().checkComplete(Tasks.FLETCH_MAPLE_LONGBOW);
        if (new Item(fletchingData.getProduct()[option]).getId() == 68)
            player.getTaskManager().checkComplete(Tasks.FLETCH_YEW_SHORTBOW);
        if (new Item(fletchingData.getProduct()[option]).getId() == 70)
            player.getTaskManager().checkComplete(Tasks.FLETCH_MAGIC_LONGBOW);
        return 2;
    }

    @Override
    public void stop(final Player player) {
        setActionDelay(player, 3);
    }

    public static FletchingData findFletchingData(Item first, Item second) {
        FletchingData fletchingData = FletchingData.forId(first.getId());
        int tool = second.getId();
        if (fletchingData == null) {
            fletchingData = FletchingData.forId(second.getId());
            tool = first.getId();
        }
        return fletchingData != null && tool == fletchingData.getTool() ? fletchingData : null;
    }

    private String fletchingMessage(Item item, int used, int usedWith, int amount) {
        Item usedItem = new Item(used);
        Item usedWithItem = new Item(usedWith);
        String defs = ItemDefinitions.getItemDefinitions(item.getId()).getName().toLowerCase();
        String usedItemDefs = ItemDefinitions.getItemDefinitions(usedItem.getId()).getName().toLowerCase();
        String usedItemWithDefs = ItemDefinitions.getItemDefinitions(usedWithItem.getId()).getName().toLowerCase();
        if (usedItemDefs.contains(" limbs") || usedItemWithDefs.contains(" limbs"))
            return "You attach the stock to the limbs and create an unstrung crossbow";
        if (usedItemDefs.contains("string") || usedItemWithDefs.contains("string")) {
            if (usedItemDefs.contains("crossbow") || usedItemWithDefs.contains("crossbow"))
                return "You add a string to the crossbow.";
            return "You add a string to the bow.";
        }
        if (item.getId() == 9419)
            return "You attach the unfinished mithril grapple with the rope, completing a mithril grapple.";
        if (defs.contains("grapple"))
            return "You attach the mithril grapple tip to mithril bolt, making a unfinished mithril grapple.";
        if (defs.contains("sagaie"))
            return "You fletch " + amount + " " + (amount > 1 ? defs.replace("sagaie", "sagaies'") : defs) + ".";
        if (defs.contains("bolas")) {
            return "You add 2 excrescence and a mutated vine together and make some bolas.";
        }
        if (defs.contains("headless"))
            return "You attach feathers to " + amount + " arrow shaft" + (amount > 1 ? "s" : "") + ".";
        if (defs.contains("dart"))
            return "You finish making " + amount + " " + (amount > 1 ? defs.replace("dart", "darts") : defs) + ".";
        if (item.getId() == 52)
            return "You carefully cut the wood into " + amount + " arrow shaft" + (amount > 1 ? "s" : "") + ".";
        if (defs.contains("arrow"))
            return "You attach " + defs.replace(" arrow", "") + " heads to some of your arrows.<br>You attach arrow head" + (amount > 1 ? "s" : "") + " to " + amount + " arrow shaft" + (amount > 1 ? "s" : "") + ".";
        if (defs.contains("bolt") || defs.contains("stake") || defs.contains("brutal"))
            return "You fletch " + amount + " " + (amount > 1 ? defs : defs.replace("bolts", "bolt")) + ".";
        return "You carefully cut the wood into a " + defs.replace(" (u)", "") + ".";
    }

}
