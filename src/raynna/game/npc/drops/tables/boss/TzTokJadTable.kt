package raynna.game.npc.drops.tables.boss

import raynna.game.npc.TableCategory
import raynna.game.npc.drops.dropTable

object TzTokJadTable {
    val table =
        dropTable(category = TableCategory.BOSS) {

            always {
                drop("item.fire_cape") { collectionLog = true }
            }
        }.apply { name = "TzTok-Jad" }
}
