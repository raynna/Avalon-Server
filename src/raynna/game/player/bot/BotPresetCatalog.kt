package raynna.game.player.bot

import raynna.game.item.Item
import raynna.util.Utils

object BotPresetCatalog {
    data class BotPresetSelection(
        val equipped: Array<IntArray> = emptyArray(),
        val inventory: Array<IntArray> = emptyArray(),
    )

    private data class GearBundle(
        val equipped: List<String> = emptyList(),
        val inventory: List<String> = emptyList(),
    )

    @JvmStatic
    fun pickSelection(archetypeShortName: String): BotPresetSelection =
        when (archetypeShortName.lowercase()) {
            "rngpure" -> BotPresetSelection(equipped = pureRangeOverrides())
            "dbpure" -> BotPresetSelection(equipped = darkBowPureOverrides())
            "hybpure" -> hybridPureSelection()
            "nhpure" -> pureNhSelection()
            "ghostnh" -> ghostNhSelection()
            "zerkdds", "zerkgm", "zerkkor" -> BotPresetSelection(equipped = zerkerMeleeOverrides())
            "runemain" -> BotPresetSelection(equipped = runeMeleeOverrides())
            "agsmain", "bgsmain" -> BotPresetSelection(equipped = mainMeleeOverrides())
            "mainnh" -> mainHybridSelection()
            "surgem" -> BotPresetSelection(equipped = surgeMageOverrides())
            "dhmain", "dhmed" -> dharokSelection()
            else -> BotPresetSelection()
        }

    private fun pureRangeOverrides(): Array<IntArray> =
        arrayOf(
            item(oneOf("amulet_of_fury", "amulet_of_glory", "amulet_of_strength")),
            item(oneOf("ava_s_accumulator", "fire_cape", "ghostly_cloak", "saradomin_cape", "guthix_cape", "zamorak_cape")),
            item(oneOf("coif", "helm_of_neitiznot", "berserker_helm")),
            item(oneOf("blue_d_hide_body", "red_d_hide_body", "black_d_hide_body")),
            item(oneOf("blue_d_hide_chaps", "red_d_hide_chaps", "black_d_hide_chaps")),
            item(oneOf("blue_d_hide_vambraces", "red_d_hide_vambraces", "black_d_hide_vambraces")),
            item(oneOf("snakeskin_boots", "climbing_boots", "rock_climbing_boots", "ranger_boots")),
        )

    private fun darkBowPureOverrides(): Array<IntArray> =
        arrayOf(
            item(oneOf("amulet_of_fury", "amulet_of_glory")),
            item(oneOf("ava_s_accumulator", "fire_cape", "saradomin_cape", "guthix_cape", "zamorak_cape")),
            item(oneOf("coif", "helm_of_neitiznot")),
            item(oneOf("red_d_hide_body", "black_d_hide_body")),
            item(oneOf("red_d_hide_chaps", "black_d_hide_chaps")),
            item(oneOf("red_d_hide_vambraces", "black_d_hide_vambraces")),
        )

    private fun hybridPureSelection(): BotPresetSelection {
        val mage = pickPureMageBundle()
        val range = pickPureRangeSwitchBundle()
        return selection(
            equipped = listOf(oneOf("amulet_of_fury", "amulet_of_glory", "amulet_of_strength")),
            inventory = mage.inventory + range.inventory,
        )
    }

    private fun pureNhSelection(): BotPresetSelection {
        val mage = pickPureMageBundle()
        val range = pickPureRangeSwitchBundle()
        return selection(
            equipped = listOf(oneOf("amulet_of_fury", "amulet_of_glory", "amulet_of_strength")),
            inventory = mage.inventory + range.inventory,
        )
    }

    private fun ghostNhSelection(): BotPresetSelection {
        val ghostMage = pickGhostMageBundle()
        val range = pickPureRangeSwitchBundle()
        return selection(
            equipped = listOf(oneOf("amulet_of_fury", "amulet_of_glory")),
            inventory = ghostMage.inventory + range.inventory,
        )
    }

    private fun zerkerMeleeOverrides(): Array<IntArray> =
        arrayOf(
            item(oneOf("berserker_helm", "warrior_helm")),
            item(oneOf("amulet_of_fury", "amulet_of_glory", "amulet_of_strength")),
            item(oneOf("fire_cape", "ardougne_cloak_4", "team_1_cape", "team_2_cape", "team_3_cape", "team_4_cape", "team_5_cape")),
            item(oneOf("rune_boots", "rock_climbing_boots")),
            item(oneOf("rune_platebody", "rune_chainbody")),
            item(oneOf("rune_platelegs", "rune_plateskirt")),
            item(oneOf("rune_kiteshield", "rune_sq_shield", "rune_defender")),
        )

    private fun runeMeleeOverrides(): Array<IntArray> =
        arrayOf(
            item(oneOf("helm_of_neitiznot", "berserker_helm")),
            item(oneOf("amulet_of_fury", "amulet_of_glory", "amulet_of_strength")),
            item(oneOf("fire_cape", "ardougne_cloak_4", "team_1_cape", "team_2_cape", "team_3_cape", "team_4_cape", "team_5_cape")),
            item(oneOf("dragon_boots", "rune_boots")),
            item(oneOf("rune_platebody")),
            item(oneOf("rune_platelegs")),
            item(oneOf("rune_defender", "toktz_ket_xil", "rune_kiteshield")),
        )

    private fun mainMeleeOverrides(): Array<IntArray> =
        arrayOf(
            item(oneOf("helm_of_neitiznot", "berserker_helm")),
            item(oneOf("amulet_of_fury", "amulet_of_glory")),
            item(oneOf("fire_cape", "ardougne_cloak_4", "team_1_cape", "team_2_cape", "team_3_cape", "team_4_cape", "team_5_cape")),
            item(oneOf("dragon_boots", "rune_boots")),
            item(oneOf("rune_defender", "toktz_ket_xil")),
        )

    private fun mainHybridSelection(): BotPresetSelection {
        val mage = if (Utils.random(3) == 0) pickVoidMageBundle() else pickMainMageBundle()
        val range = if (Utils.random(2) == 0) pickVoidRangeBundle() else pickMainRangeBundle()
        val melee = if (Utils.random(2) == 0) pickVoidMeleeBundle() else pickMainMeleeBundle()
        return selection(
            equipped = listOf(oneOf("amulet_of_fury", "amulet_of_glory")),
            inventory = mage.inventory + range.inventory + melee.inventory,
        )
    }

    private fun surgeMageOverrides(): Array<IntArray> =
        arrayOf(
            item(oneOf("helm_of_neitiznot", "ahrim_s_hood", "mystic_hat_dark", "mystic_hat_light")),
            item(oneOf("amulet_of_fury", "amulet_of_glory")),
            item(oneOf("ghostly_cloak", "fire_cape", "ardougne_cloak_4", "saradomin_cape", "guthix_cape", "zamorak_cape")),
            item(oneOf("mystic_boots", "infinity_boots")),
            item(oneOf("ahrim_s_robe_top", "mystic_robe_top_dark", "mystic_robe_top_light", "splitbark_body")),
            item(oneOf("ahrim_s_robe_skirt", "mystic_robe_bottoms_dark", "mystic_robe_bottom_light", "splitbark_legs")),
        )

    private fun dharokSelection(): BotPresetSelection =
        selection(
            equipped =
                listOf(
                    "dharok_s_helm",
                    oneOf("amulet_of_fury", "amulet_of_glory", "amulet_of_strength"),
                    oneOf("fire_cape", "ardougne_cloak_4", "team_1_cape", "team_2_cape", "team_3_cape", "team_4_cape", "team_5_cape"),
                    oneOf("dragon_boots", "rune_boots"),
                    "dharok_s_platebody",
                    "dharok_s_platelegs",
                ),
            inventory = listOf("dharok_s_greataxe"),
        )

    private fun pickPureMageBundle(): GearBundle =
        GearBundle(
            inventory =
                listOf(
                    oneOf("ghostly_hood", "mystic_hat_dark", "mystic_hat_light", "wizard_hat", "red_wizard_hat"),
                    oneOf("ghostly_cloak", "fire_cape", "saradomin_cape", "guthix_cape", "zamorak_cape"),
                    oneOf("ghostly_robe", "mystic_robe_top_dark", "mystic_robe_top_light"),
                    oneOf("ghostly_robe_2", "mystic_robe_bottoms_dark", "mystic_robe_bottom_light"),
                    oneOf("ghostly_gloves", "mystic_gloves_dark", "mystic_gloves_light"),
                    oneOf("ghostly_boots", "mystic_boots_dark", "mystic_boots_light", "wizard_boots"),
                    oneOf("ancient_staff", "staff_of_light", "master_wand"),
                    oneOf("mages_book", "unholy_book", "anti_dragon_shield"),
                ),
        )

    private fun pickGhostMageBundle(): GearBundle =
        GearBundle(
            inventory =
                listOf(
                    "ghostly_hood",
                    oneOf("ghostly_cloak", "fire_cape", "saradomin_cape", "guthix_cape", "zamorak_cape"),
                    "ghostly_robe",
                    "ghostly_robe_2",
                    oneOf("ghostly_gloves", "mystic_gloves_dark"),
                    oneOf("ghostly_boots", "mystic_boots_dark", "wizard_boots"),
                    oneOf("ancient_staff", "staff_of_light", "master_wand"),
                    oneOf("mages_book", "unholy_book", "anti_dragon_shield"),
                ),
        )

    private fun pickPureRangeSwitchBundle(): GearBundle =
        GearBundle(
            inventory =
                listOf(
                    oneOf("helm_of_neitiznot", "coif"),
                    oneOf("ava_s_accumulator", "fire_cape"),
                    oneOf("black_d_hide_body", "red_d_hide_body"),
                    oneOf("black_d_hide_chaps", "red_d_hide_chaps"),
                    oneOf("adamant_gloves", "black_d_hide_vambraces"),
                    oneOf("climbing_boots", "ranger_boots", "snakeskin_boots"),
                ),
        )

    private fun pickMainMageBundle(): GearBundle =
        GearBundle(
            inventory =
                listOf(
                    oneOf("ahrim_s_hood", "mystic_hat_dark", "mystic_hat_light"),
                    oneOf("ghostly_cloak", "fire_cape", "ardougne_cloak_4"),
                    oneOf("ahrim_s_robe_top", "splitbark_body"),
                    oneOf("ahrim_s_robe_skirt", "splitbark_legs"),
                    oneOf("mystic_boots", "infinity_boots"),
                ),
        )

    private fun pickMainRangeBundle(): GearBundle =
        GearBundle(
            inventory =
                listOf(
                    oneOf("karil_s_coif", "helm_of_neitiznot"),
                    oneOf("ava_s_accumulator", "fire_cape"),
                    oneOf("karil_s_top", "black_d_hide_body"),
                    oneOf("karil_s_skirt", "black_d_hide_chaps"),
                    oneOf("dragon_boots", "ranger_boots"),
                ),
        )

    private fun pickMainMeleeBundle(): GearBundle =
        GearBundle(
            inventory =
                listOf(
                    oneOf("helm_of_neitiznot", "berserker_helm"),
                    oneOf("fire_cape", "ardougne_cloak_4"),
                    oneOf("bandos_chestplate", "fighter_torso"),
                    oneOf("bandos_tassets", "verac_s_plateskirt"),
                    oneOf("dragon_boots", "rune_boots"),
                    oneOf("dragon_defender", "rune_defender"),
                ),
        )

    private fun pickVoidMageBundle(): GearBundle =
        GearBundle(
            inventory =
                listOf(
                    "void_mage_helm",
                    oneOf("mages_book", "unholy_book"),
                    oneOf("saradomin_cape", "guthix_cape", "zamorak_cape"),
                    "elite_void_knight_top",
                    "elite_void_knight_robe",
                    "void_knight_gloves",
                    oneOf("infinity_boots", "mystic_boots"),
                ),
        )

    private fun pickVoidRangeBundle(): GearBundle =
        GearBundle(
            inventory =
                listOf(
                    "void_ranger_helm",
                    oneOf("ava_s_accumulator", "fire_cape"),
                    "elite_void_knight_top",
                    "elite_void_knight_robe",
                    "void_knight_gloves",
                    oneOf("ranger_boots", "dragon_boots"),
                ),
        )

    private fun pickVoidMeleeBundle(): GearBundle =
        GearBundle(
            inventory =
                listOf(
                    "void_melee_helm",
                    oneOf("fire_cape", "ardougne_cloak_4"),
                    "elite_void_knight_top",
                    "elite_void_knight_robe",
                    "void_knight_gloves",
                    oneOf("dragon_boots", "rune_boots"),
                ),
        )

    private fun selection(
        equipped: List<String>,
        inventory: List<String>,
    ): BotPresetSelection =
        BotPresetSelection(
            equipped = equipped.map(::item).toTypedArray(),
            inventory = inventory.map(::item).toTypedArray(),
        )

    private fun item(name: String): IntArray = intArrayOf(Item.getId(name), 1)

    private fun oneOf(vararg names: String): String = names[Utils.random(names.size)]
}
