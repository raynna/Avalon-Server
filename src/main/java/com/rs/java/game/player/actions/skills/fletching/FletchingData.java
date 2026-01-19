package com.rs.java.game.player.actions.skills.fletching;

import com.rs.java.game.Animation;
import com.rs.java.game.item.Item;
import com.rs.kotlin.Rscm;

import java.util.HashMap;
import java.util.Map;

public enum FletchingData {

    REGULAR_BOW("item.logs", "item.knife", anim(6702),
            prod("item.arrow_shaft", 15, 1, 0.33),
            prod("item.shortbow_u", 1, 5, 5),
            prod("item.longbow_u", 1, 10, 10)),

    STRUNG_SHORT_BOW("item.shortbow_u", "item.bow_string", anim(6678),
            prod("item.shortbow", 1, 5, 5)),

    STRUNG_LONG_BOW("item.longbow_u", "item.bow_string", anim(6684),
            prod("item.longbow", 1, 10, 10)),

    OAK_BOW("item.oak_logs", "item.knife", anim(6702),
            prod("item.oak_shortbow_u", 1, 20, 16.5),
            prod("item.oak_longbow_u", 1, 25, 25),
            prod("item.arrow_shaft", 15, 24, 16)),

    STRUNG_OAK_SHORT("item.oak_shortbow_u", "item.bow_string", anim(6679),
            prod("item.oak_shortbow", 1, 20, 16.5)),

    STRUNG_OAK_LONG("item.oak_longbow_u", "item.bow_string", anim(6685),
            prod("item.oak_longbow", 1, 25, 25)),

    WILLOW_BOW("item.willow_logs", "item.knife", anim(6702),
            prod("item.willow_shortbow_u", 1, 35, 33.3),
            prod("item.willow_longbow_u", 1, 40, 41.5),
            prod("item.arrow_shaft", 15, 39, 22)),

    STRUNG_WILLOW_SHORT("item.willow_shortbow_u", "item.bow_string", anim(6680),
            prod("item.willow_shortbow", 1, 35, 33.3)),

    STRUNG_WILLOW_LONG("item.willow_longbow_u", "item.bow_string", anim(6686),
            prod("item.willow_longbow", 1, 40, 41.5)),

    MAPLE_BOW("item.maple_logs", "item.knife", anim(6702),
            prod("item.maple_shortbow_u", 1, 50, 50),
            prod("item.maple_longbow_u", 1, 55, 58.3),
            prod("item.arrow_shaft", 15, 54, 32)),

    STRUNG_MAPLE_SHORT("item.maple_shortbow_u", "item.bow_string", anim(6681),
            prod("item.maple_shortbow", 1, 50, 50)),

    STRUNG_MAPLE_LONG("item.maple_longbow_u", "item.bow_string", anim(6687),
            prod("item.maple_longbow", 1, 55, 58.3)),

    YEW_BOW("item.yew_logs", "item.knife", anim(6702),
            prod("item.yew_shortbow_u", 1, 65, 67.5),
            prod("item.yew_longbow_u", 1, 70, 75),
            prod("item.arrow_shaft", 15, 69, 50)),

    STRUNG_YEW_SHORT("item.yew_shortbow_u", "item.bow_string", anim(6682),
            prod("item.yew_shortbow", 1, 65, 67.5)),

    STRUNG_YEW_LONG("item.yew_longbow_u", "item.bow_string", anim(6688),
            prod("item.yew_longbow", 1, 70, 75)),

    MAGIC_BOW("item.magic_logs", "item.knife", anim(6702),
            prod("item.magic_shortbow_u", 1, 80, 83.25),
            prod("item.magic_longbow_u", 1, 85, 91.5)),

    STRUNG_MAGIC_SHORT("item.magic_shortbow_u", "item.bow_string", anim(6683),
            prod("item.magic_shortbow", 1, 80, 83.25)),

    STRUNG_MAGIC_LONG("item.magic_longbow_u", "item.bow_string", anim(6689),
            prod("item.magic_longbow", 1, 85, 91.5)),

    BLOODWOOD("item.bloodwood_logs", "item.knife", anim(6702),
            prod("item.bakriminel_bolt_shafts", 10, 93, 13),
            prod("item.bakriminel_bolt_tips", 10, 93, 13)),

    BLISTERWOOD("item.blisterwood_logs", "item.knife", anim(6702),
            prod("item.blisterwood_polearm", 1, 70, 10),
            prod("item.blisterwood_staff", 1, 70, 100),
            prod("item.blisterwood_stake", 10, 70, 100)),

    ACHEY("item.achey_tree_logs", "item.knife", anim(6702),
            prod("item.unstrung_comp_bow", 1, 5, 1.6),
            prod("item.ogre_arrow_shaft", 15, 30, 45)),

    STRUNG_OGRE("item.unstrung_comp_bow", "item.bow_string", anim(6688),
            prod("item.comp_ogre_bow", 1, 30, 45)),

    // ---- Crossbows ----
    U_BRONZE_CBOW("item.wooden_stock", "item.bronze_limbs", anim(-1),
            prod("item.bronze_c_bow_u", 1, 9, 6)),

    U_IRON_CBOW("item.wooden_stock", "item.iron_limbs", anim(-1),
            prod("item.iron_c_bow_u", 1, 39, 22)),

    U_BLURITE_CBOW("item.oak_stock", "item.blurite_limbs", anim(-1),
            prod("item.blurite_c_bow_u", 1, 24, 16)),

    U_STEEL_CBOW("item.teak_stock", "item.steel_limbs", anim(-1),
            prod("item.steel_c_bow_u", 1, 46, 27)),

    U_MITHRIL_CBOW("item.maple_stock", "item.mithril_limbs", anim(-1),
            prod("item.mithril_c_bow_u", 1, 54, 32)),

    U_ADAMANT_CBOW("item.mahogany_stock", "item.adamantite_limbs", anim(-1),
            prod("item.adamant_c_bow_u", 1, 61, 41)),

    U_RUNITE_CBOW("item.yew_stock", "item.runite_limbs", anim(-1),
            prod("item.runite_c_bow_u", 1, 69, 50)),

    BRONZE_CBOW("item.bronze_c_bow_u", "item.crossbow_string", anim(6671),
            prod("item.bronze_crossbow", 1, 9, 6.0)),

    IRON_CBOW("item.iron_c_bow_u", "item.crossbow_string", anim(6673),
            prod("item.iron_crossbow", 1, 39, 22)),

    STEEL_CBOW("item.steel_c_bow_u", "item.crossbow_string", anim(6674),
            prod("item.steel_crossbow", 1, 46, 27)),

    BLURITE_CBOW("item.blurite_c_bow_u", "item.crossbow_string", anim(6672),
            prod("item.blurite_crossbow", 1, 24, 16)),

    MITHRIL_CBOW("item.mithril_c_bow_u", "item.crossbow_string", anim(6675),
            prod("item.mith_crossbow", 1, 52, 32)),

    ADAMANT_CBOW("item.adamant_c_bow_u", "item.crossbow_string", anim(6676),
            prod("item.adamant_crossbow", 1, 61, 41)),

    RUNITE_CBOW("item.runite_c_bow_u", "item.crossbow_string", anim(6677),
            prod("item.rune_crossbow", 1, 69, 50)),

    // ---- Arrows ----
    HEADLESS_ARROWS("item.arrow_shaft", "item.feather", anim(-1),
            prod("item.headless_arrow", 15, 1, 1, 15)),

    BRONZE_ARROWS("item.bronze_arrowheads", "item.headless_arrow", anim(-1),
            prod("item.bronze_arrow", 15, 1, 0.4, 15)),

    IRON_ARROWS("item.iron_arrowheads", "item.headless_arrow", anim(-1),
            prod("item.iron_arrow", 15, 15, 3.8, 15)),

    STEEL_ARROWS("item.steel_arrowheads", "item.headless_arrow", anim(-1),
            prod("item.steel_arrow", 15, 30, 6.3, 15)),

    MITHRIL_ARROWS("item.mithril_arrowheads", "item.headless_arrow", anim(-1),
            prod("item.mithril_arrow", 15, 45, 8.8, 15)),

    BROAD_ARROWS("item.broad_arrowheads", "item.headless_arrow", anim(-1),
            prod("item.broad_arrow", 15, 52, 15, 15)),

    ADAMANT_ARROWS("item.adamant_arrowheads", "item.headless_arrow", anim(-1),
            prod("item.adamant_arrow", 15, 60, 11.3, 15)),

    RUNITE_ARROWS("item.rune_arrowheads", "item.headless_arrow", anim(-1),
            prod("item.rune_arrow", 15, 75, 13.8, 15)),

    DRAGON_ARROWS("item.dragon_arrowheads", "item.headless_arrow", anim(-1),
            prod("item.dragon_arrow", 15, 90, 16.3, 15)),

    // Bane arrows (from original)
    DRAGONBANE_ARROWS("item.dragonbane_arrowheads", "item.headless_arrow", anim(-1),
            prod("item.dragonbane_arrow", 15, 76, 10, 15)),

    ABYSSALBANE_ARROWS("item.abyssalbane_arrowheads", "item.headless_arrow", anim(-1),
            prod("item.abyssalbane_arrow", 15, 76, 10, 15)),

    BASILISKBANE_ARROWS("item.basiliskbane_arrowheads", "item.headless_arrow", anim(-1),
            prod("item.basiliskbane_arrow", 15, 76, 10, 15)),

    WALLASALKIBANE_ARROWS("item.wallasalkibane_arrowheads", "item.headless_arrow", anim(-1),
            prod("item.wallasalkibane_arrow", 15, 76, 10, 15)),

    // ---- Darts ----
    BRONZE_DART("item.bronze_dart_tip", "item.feather", anim(-1),
            prod("item.bronze_dart", 10, 1, 0.8, 10)),

    IRON_DART("item.iron_dart_tip", "item.feather", anim(-1),
            prod("item.iron_dart", 10, 22, 1, 10)),

    STEEL_DART("item.steel_dart_tip", "item.feather", anim(-1),
            prod("item.steel_dart", 10, 37, 1.7, 10)),

    MITHRIL_DART("item.mithril_dart_tip", "item.feather", anim(-1),
            prod("item.mithril_dart", 10, 52, 4, 10)),

    ADAMANT_DART("item.adamant_dart_tip", "item.feather", anim(-1),
            prod("item.adamant_dart", 10, 67, 7.6, 10)),

    RUNITE_DART("item.rune_dart_tip", "item.feather", anim(-1),
            prod("item.rune_dart", 10, 81, 12.2, 10)),

    DRAGON_DART("item.dragon_dart_tip", "item.feather", anim(-1),
            prod("item.dragon_dart", 10, 95, 18.4, 10)),

    // ---- Bolts ----
    BRONZE_BOLT("item.bronze_bolts_unf", "item.feather", anim(-1),
            prod("item.bronze_bolts", 10, 9, 0.5, 10)),

    IRON_BOLT("item.iron_bolts_unf", "item.feather", anim(-1),
            prod("item.iron_bolts", 10, 39, 1.5, 10)),

    STEEL_BOLT("item.steel_bolts_unf", "item.feather", anim(-1),
            prod("item.steel_bolts", 10, 46, 3.5, 10)),

    MITHRIL_BOLT("item.mithril_bolts_unf", "item.feather", anim(-1),
            prod("item.mithril_bolts", 10, 54, 5, 10)),

    ADAMANT_BOLT("item.adamant_bolts_unf", "item.feather", anim(-1),
            prod("item.adamant_bolts", 10, 61, 7, 10)),

    RUNITE_BOLT("item.runite_bolts_unf", "item.feather", anim(-1),
            prod("item.runite_bolts", 10, 69, 10, 10)),

    BROAD_BOLT("item.unfinished_broad_bolts", "item.feather", anim(-1),
            prod("item.broad_tipped_bolts", 10, 55, 3, 10)),

    BLURITE_BOLT("item.blurite_bolts_unf", "item.feather", anim(-1),
            prod("item.blurite_bolts", 10, 24, 1, 10)),

    SILVER_BOLT("item.silver_bolts_unf", "item.feather", anim(-1),
            prod("item.silver_bolts", 10, 43, 2.5, 10)),

    // Bane bolts (from original)
    DRAGONBANE_BOLTS("item.dragonbane_bolts_p_2", "item.feather", anim(-1),
            prod("item.dragonbane_bolt", 10, 80, 7, 10)),

    ABYSSALBANE_BOLTS("item.abyssalbane_bolts_unf", "item.feather", anim(-1),
            prod("item.abyssalbane_bolt", 10, 80, 7, 10)),

    BASILISKBANE_BOLTS("item.basiliskbane_bolts_unf", "item.feather", anim(-1),
            prod("item.basiliskbane_bolt", 10, 80, 7, 10)),

    WALLASALKIBANE_BOLTS("item.wallasalkibane_bolts_unf", "item.feather", anim(-1),
            prod("item.wallasalkibane_bolt", 10, 80, 7, 10)),

    // ---- Gem/Bolt Tips ----
    OPAL_TIP("item.opal", "item.chisel", anim(-1),
            prod("item.opal_bolt_tips", 12, 11, 1.5)),

    JADE_TIP("item.jade", "item.chisel", anim(-1),
            prod("item.jade_bolt_tips", 12, 26, 2)),

    PEARL_TIP("item.oyster_pearl", "item.chisel", anim(-1),
            prod("item.pearl_bolt_tips", 12, 41, 3.2)),

    RED_TOPAZ_TIP("item.red_topaz", "item.chisel", anim(-1),
            prod("item.topaz_bolt_tips", 12, 48, 4)),

    SAPPHIRE_TIP("item.sapphire", "item.chisel", anim(-1),
            prod("item.sapphire_bolt_tips", 12, 56, 4)),

    EMERALD_TIP("item.emerald", "item.chisel", anim(-1),
            prod("item.emerald_bolt_tips", 12, 58, 5.5)),

    RUBY_TIP("item.ruby", "item.chisel", anim(-1),
            prod("item.ruby_bolt_tips", 12, 63, 6.3)),

    DIAMOND_TIP("item.diamond", "item.chisel", anim(-1),
            prod("item.diamond_bolt_tips", 12, 65, 7)),

    DRAGONSTONE_TIP("item.dragonstone", "item.chisel", anim(-1),
            prod("item.dragonstone_bolt_tips", 12, 71, 8.2)),

    ONYX_TIP("item.onyx", "item.chisel", anim(-1),
            prod("item.onyx_bolt_tips", 24, 73, 9.4)),

    // ---- Special/Bolas ----
    BOLAS("item.excrescence", "item.mutated_vine", anim(-1),
            prod("item.bolas", 1, 87, 50.0)),

    SAGAIE("item.excrescence", "item.mutated_vine", anim(-1),
            prod("item.sagaie", 5, 83, 40.0)),

    // ---- Brutal Arrows ----
    FLIGHTED_OGRE_ARROW("item.ogre_arrow_shaft", "item.feather", anim(-1),
            prod("item.flighted_ogre_arrow", 6, 5, 0.9, 6)),

    BRONZE_BRUTAL("item.bronze_nails", "item.flighted_ogre_arrow", anim(-1),
            prod("item.bronze_brutal", 3, 7, 1.4, 3)),

    IRON_BRUTAL("item.iron_nails", "item.flighted_ogre_arrow", anim(-1),
            prod("item.iron_brutal", 3, 18, 2.6, 3)),

    STEEL_BRUTAL("item.steel_nails", "item.flighted_ogre_arrow", anim(-1),
            prod("item.steel_brutal", 3, 33, 5.1, 3)),

    BLACK_BRUTAL("item.black_nails", "item.flighted_ogre_arrow", anim(-1),
            prod("item.black_brutal", 3, 38, 6.4, 3)),

    MITHRIL_BRUTAL("item.mithril_nails", "item.flighted_ogre_arrow", anim(-1),
            prod("item.mithril_brutal", 3, 49, 7.5, 3)),

    ADAMANT_BRUTAL("item.adamantite_nails", "item.flighted_ogre_arrow", anim(-1),
            prod("item.adamant_brutal", 3, 62, 10.1, 3)),

    RUNE_BRUTAL("item.rune_nails", "item.flighted_ogre_arrow", anim(-1),
            prod("item.rune_brutal", 3, 77, 12.5, 3)),

    // ---- Other ----
    WOLFBONE_ARROWTIPS("item.wolf_bone", "item.chisel", anim(-1),
            prod("item.wolfbone_arrowheads", 6, 5, 2.5)),

    OGRE_ARROW("item.wolfbone_arrowheads", "item.flighted_ogre_arrow", anim(-1),
            prod("item.ogre_arrow", 6, 5, 1.0, 6)),

    MITHRIL_GRAPPLE_TIP("item.mith_grapple_tip", "item.mithril_bolts", anim(-1),
            prod("item.mithril_grapple_unf", 1, 59, 50.0)),

    UNF_MITHRIL_GRAPPE("item.mithril_grapple_unf", "item.rope", anim(-1),
            prod("item.mithril_grapple", 1, 59, 50.0)),

    KEBBIT_BOLTS("item.kebbit_spike", "item.chisel", anim(-1),
            prod("item.kebbit_bolts", 6, 32, 6.0, 6)),

    LONG_KEBBIT_BOLTS("item.long_kebbit_spike", "item.chisel", anim(-1),
            prod("item.long_kebbit_bolts", 6, 42, 7.89, 6)),

    BAKRIMINEL_BOLTS("item.bakriminel_bolt_shafts", "item.bakriminel_bolt_tips", anim(-1),
            prod("item.bakriminel_bolts", 12, 93, 13.0, 12)),

    BARBED_BOLT("item.barb_bolttips", "item.bronze_bolts", anim(-1),
            prod("item.barbed_bolts", 10, 51, 0.95, 10));

    private final Object base;
    private final Object tool;
    private final Animation animation;
    private final FletchingProduct[] products;

    FletchingData(Object base, Object tool, Animation animation, FletchingProduct... products) {
        this.base = base;
        this.tool = tool;
        this.animation = animation;
        this.products = products;
    }

    public int getBaseId() {
        return base instanceof Integer ? (Integer) base : Rscm.lookup((String) base);
    }

    public int getToolId() {
        return tool instanceof Integer ? (Integer) tool : Rscm.lookup((String) tool);
    }

    public Object getBase() {
        return base;
    }

    public Object getTool() {
        return tool;
    }

    public Animation getAnimation() {
        return animation;
    }

    public FletchingProduct[] getProducts() {
        return products;
    }

    private static FletchingProduct prod(Object id, int amount, int lvl, double xp, int inputPer) {
        return new FletchingProduct(id, amount, lvl, xp, inputPer);
    }

    private static FletchingProduct prod(Object id, int amount, int lvl, double xp) {
        return new FletchingProduct(id, amount, lvl, xp); // Uses default constructor
    }


    private static final Map<Integer, FletchingData> BY_BASE = new HashMap<>();

    static {
        for (FletchingData d : values()) {
            BY_BASE.put(d.getBaseId(), d);
        }
    }

    public static FletchingData findByBase(Item item) {
        for (FletchingData data : FletchingData.values()) {
            if (data.getBaseId() == item.getId())
                return data;
        }
        return null;
    }

    public static FletchingData forBase(int id) {
        return BY_BASE.get(id);
    }


    private static Animation anim(int id) {
        return id == -1 ? new Animation(-1) : new Animation(id);
    }
}