package com.rs.kotlin.game.npc.drops.tables

import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.dropTable

object TzTokJadTable {
    val table =
        dropTable(category = TableCategory.BOSS) {

            always {
                drop("item.fire_cape") { collectionLog = true }
            }
        }.apply { name = "TzTok-Jad" }
}
