package com.rs.kotlin.game.npc.drops.tables

import com.rs.core.cache.defintions.ItemDefinitions
import com.rs.java.game.item.ItemId
import com.rs.java.game.player.Skills
import com.rs.kotlin.game.npc.drops.*
import com.rs.kotlin.game.npc.drops.DropTable.Companion.BLUE
import com.rs.kotlin.game.npc.drops.DropTable.Companion.CRIMSON
import com.rs.kotlin.game.npc.drops.DropTable.Companion.GOLD
import com.rs.kotlin.game.npc.drops.DropTable.Companion.GREEN
import com.rs.kotlin.game.npc.drops.DropTablesSetup.rareDropTable
import java.util.concurrent.ThreadLocalRandom

object BlackDragonDropTable {

    val table = dropTable(rolls = 1) {//1 means 1 roll per kill, special for things like zulrah who has multiple rolls on maintable

            alwaysDrops {
                alwaysDrop("dragon bones")
                alwaysDrop("black dragonhide");
            }

            preRollDrops {
                preRollDrop(
                    item = "dragon token",
                    condition = { player -> false }
                )
            }

            mainDrops {//item: Any, amount: Int = 1, numerator: Int = 1, denominator: Int = 4
                mainDrop(item = "mithril 2h sword", amount = 1, probability = 1, chance = 32)
                mainDrop(item = "mithril hatchet", amount = 1, probability = 1, chance = 42)
                mainDrop(item = "mithril battleaxe", amount = 1, probability = 1, chance = 42)
                mainDrop(item = "rune knife", amount = 2, probability = 1, chance = 42)
                mainDrop(item = "mithril kiteshield", amount = 1, probability = 1, chance = 128)
                mainDrop(item = "adamant platebody", amount = 1, probability = 1, chance = 128)
                mainDrop(item = "rune longsword", amount = 1, probability = 1, chance = 128)
                mainDrop(item = "adamant javelin", amount = 30, probability = 1, chance = 6)
                mainDrop(item = "fire rune", amount = 50, probability = 1, chance = 16)
                mainDrop(item = "adamant dart(p)", amount = 16, probability = 1, chance = 18)
                mainDrop(item = "law rune", amount = 10, probability = 1, chance = 25)
                mainDrop(item = "blood rune", amount = 15, probability = 1, chance = 42)
                mainDrop(item = "air rune", amount = 75, probability = 1, chance = 128)
                mainDrop(item = ItemId.COINS, amount = 196, probability = 1, chance = 3)
                mainDrop(item = ItemId.COINS, amount = 330, probability = 1, chance = 12)
                mainDrop(item = ItemId.COINS, amount = 690, probability = 1, chance = 128)
                mainDrop(item = "adamant bar", amount = 1, probability = 1, chance = 42)
                mainDrop(item = "chocolate cake", amount = 1, probability = 1, chance = 42)
            }

            tertiaryDrops {
                tertiaryDrop(
                    item = "black dragon tail-bone",
                    probability = 1,
                    chance = 4,
                    condition = { player -> false }) //TODO fur'n seek wishlist
                tertiaryDrop(
                    item = "black dragon egg",
                    probability = 1,
                    chance = 100,
                    condition = { player -> player?.skills?.getLevelForXp(Skills.SUMMONING) != 99 })
                tertiaryDrop(item = "draconic visage", 1, probability = 1, chance = 500)
                tertiaryDrop(item = "starved ancient effigy", 1, probability = 1, chance = 128)
            }

            percentDrops {
                percentDrop("gold charm", amount = 1, percent = 8.96)
                percentDrop("green charm", amount = 1, percent = 26.9)
                percentDrop("crimson charm", amount = 1, percent = 6.72)
                percentDrop("blue charm", amount = 1, percent = 1.34)
            }

            specialDrops {
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
            }

            rareTable { player, drops ->
                val rare = rareDropTable.roll(player)
                if (rare != null) {
                    drops.add(rare)
                    true
                } else {
                    false
                }
            }
        }
}
