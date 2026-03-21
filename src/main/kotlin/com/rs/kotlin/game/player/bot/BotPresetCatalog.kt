package com.rs.kotlin.game.player.bot

import com.rs.java.game.item.Item
import com.rs.java.utils.Utils

object BotPresetCatalog {
    @JvmStatic
    fun pickOverrides(archetypeShortName: String): Array<IntArray> =
        when (archetypeShortName.lowercase()) {
            "zerkdds", "zerkgm", "zerkkor" -> zerkerMeleeOverrides()
            "runemain" -> runeMeleeOverrides()
            "agsmain", "bgsmain" -> mainMeleeOverrides()
            "mainnh" -> mainHybridOverrides()
            "surgem" -> surgeMageOverrides()
            else -> emptyArray()
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

    private fun mainHybridOverrides(): Array<IntArray> =
        arrayOf(
            item(oneOf("helm_of_neitiznot")),
            item(oneOf("amulet_of_fury")),
            item(oneOf("fire_cape", "ardougne_cloak_4", "team_1_cape", "team_2_cape", "team_3_cape", "team_4_cape", "team_5_cape")),
            item(oneOf("dragon_boots")),
        )

    private fun surgeMageOverrides(): Array<IntArray> =
        arrayOf(
            item(oneOf("helm_of_neitiznot")),
            item(oneOf("amulet_of_fury", "amulet_of_glory")),
            item(oneOf("mystic_boots", "infinity_boots")),
        )

    private fun item(name: String): IntArray = intArrayOf(Item.getId(name), 1)

    private fun oneOf(vararg names: String): String = names[Utils.random(names.size)]
}
