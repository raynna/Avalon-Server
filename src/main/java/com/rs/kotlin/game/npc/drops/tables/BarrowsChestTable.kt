package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.Player
import com.rs.kotlin.Rscm
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.DropContext
import com.rs.kotlin.game.npc.drops.dropTable

object BarrowsChestTable {
    @JvmStatic
    fun barrowsUniqueChance(brothersKilled: Int): Int = 450 - (58 * brothersKilled)

    private val BROTHER_ITEMS =
        mapOf(
            0 to listOf("item.ahrim_s_hood", "item.ahrim_s_robe_top", "item.ahrim_s_robe_skirt", "item.ahrim_s_staff"),
            1 to listOf("item.dharok_s_helm", "item.dharok_s_platebody", "item.dharok_s_platelegs", "item.dharok_s_greataxe"),
            2 to listOf("item.guthan_s_helm", "item.guthan_s_platebody", "item.guthan_s_chainskirt", "item.guthan_s_warspear"),
            3 to listOf("item.karil_s_coif", "item.karil_s_top", "item.karil_s_skirt", "item.karil_s_crossbow"),
            4 to listOf("item.torag_s_helm", "item.torag_s_platebody", "item.torag_s_platelegs", "item.torag_s_hammers"),
            5 to listOf("item.verac_s_helm", "item.verac_s_brassard", "item.verac_s_plateskirt", "item.verac_s_flail"),
        )

    private val ALL_BARROWS_ITEMS: List<String> =
        BROTHER_ITEMS.values.flatten()

    private fun rollBarrowsItem(context: DropContext): Int? {
        val player = context.player
        val used =
            player.temporaryAttribute()["barrows_used"] as? MutableSet<Int>
                ?: mutableSetOf<Int>().also {
                    player.temporaryAttribute()["barrows_used"] = it
                }

        val pool = mutableListOf<String>()

        val killed = player.killedBarrowBrothers
        for ((index, items) in BROTHER_ITEMS) {
            if (killed[index]) {
                pool += items
            }
        }

        if (pool.isEmpty()) return null

        val filtered =
            pool
                .map { Rscm.lookup(it) }
                .filter { !used.contains(it) }

        if (filtered.isEmpty()) return null

        val item = filtered.random()
        used.add(item)
        return item
    }

    @JvmField
    val BARROWS_CHEST_TABLE =
        dropTable(
            sourceAction = "opening",
            category = TableCategory.MINIGAME,
        ) {

            preRollDrops {
                drop(
                    numerator = 1,
                    denominator = 102,
                    dynamicItem = { ctx ->
                        rollBarrowsItem(ctx)
                    },
                    displayItems = ALL_BARROWS_ITEMS,
                ) {
                    collectionLog = true
                }
            }

            mainDrops(size = 1012) {

                drop("item.coins", amount = 2..774, weight = 380)
                drop("item.mind_rune", amount = 253..336, weight = 125)
                drop("item.chaos_rune", amount = 112..139, weight = 125)
                drop("item.death_rune", amount = 70..83, weight = 125)
                drop("item.blood_rune", amount = 37..43, weight = 125)
                drop("item.bolt_rack", amount = 35..40, weight = 125)
                drop("item.loop_half_of_a_key", weight = 6)
                drop("item.tooth_half_of_a_key", weight = 6)
                drop("item.dragon_helm", weight = 1)
            }

            tertiaryDrops {
                drop("item.scroll_box_elite", numerator = 1, denominator = 200)
            }
        }.apply {
            name = "Barrows Chest"
            collectionGroup = "Barrows"
        }
}
