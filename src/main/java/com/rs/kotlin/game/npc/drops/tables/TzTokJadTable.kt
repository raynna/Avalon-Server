package com.rs.kotlin.game.npc.drops.tables

import com.rs.kotlin.game.npc.MonsterCategory
import com.rs.kotlin.game.npc.drops.dropTable

object TzTokJadTable {

    val table = dropTable(
        category = MonsterCategory.BOSS) {

        alwaysDrops {
            drop("item.fire_cape")
        }
    }.apply { name = "TzTok-Jad" }
}
