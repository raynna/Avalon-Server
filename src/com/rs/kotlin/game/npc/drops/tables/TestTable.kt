package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.item.ItemId
import com.rs.java.game.player.Skills
import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.drops.*
import com.rs.kotlin.game.npc.drops.DropTablesSetup.gemDropTable

object TestTable {

    val table = dropTable(rolls = 1) {//1 means 1 roll per kill, special for things like zulrah who has multiple rolls on maintable

            alwaysDrops {
                drop(item = "item.mithril_2h_sword")
            }

            mainDrops {
                drop(item = "item.mithril_2h_sword", amount = 1)
                drop(item = "item.mithril_2h_sword", amount = 1..5, 2, 128)
            }

            /*specialDrops {
                mainDrop(
                    item = "banana",
                    min = 1, max = 2, probability = 1, chance = 16,
                    customLogic = {
                                  player, drop ->
                        val pineappleAmount = ThreadLocalRandom.current().nextInt(2, 8)
                        val noted = drop.amount > 2
                        val pineapple = ItemDefinitions.getItemDefinitions(ItemId.PINEAPPLE);
                        val pineappleId = if (noted)
                            pineapple.getCertId()
                        else
                            pineapple.id
                        drop.extraDrop = Drop(pineappleId, pineappleAmount)
                    })
            }*/

            rareTable { player, drops ->
                val gem = gemDropTable.roll(player)
                if (gem != null) {
                    drops.add(gem)
                    true
                } else {
                    false
                }
            }
        }
}
