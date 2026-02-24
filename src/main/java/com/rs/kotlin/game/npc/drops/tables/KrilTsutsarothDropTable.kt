package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.GemTableConfig
import com.rs.kotlin.game.npc.drops.config.RareTableConfig
import com.rs.kotlin.game.npc.drops.dropTable

object KrilTsutsarothDropTable {
    val table =
        dropTable(
            godwarsGemTable = GemTableConfig(2, 127),
            godwarsRareTable = RareTableConfig(numerator = 8, denominator = 127),
            category = TableCategory.BOSS,
        ) {

            alwaysDrops {
                drop("item.infernal_ashes")
            }

            charmDrops {
                gold(amount = 1, percent = 10.1)
                green(amount = 1, percent = 5.04)
                crimson(amount = 1, percent = 10.1)
                blue(amount = 1, percent = 16.1)
            }

            preRollDrops {
                drop("item.steam_battlestaff", numerator = 1, denominator = 127) { collectionLog = true }
                drop("item.zamorakian_spear", numerator = 1, denominator = 127) {
                    collectionLog = true
                    announce = true
                }
                drop("item.zamorak_hilt", numerator = 1, denominator = 508) {
                    collectionLog = true
                    announce = true
                }
                drop("item.godsword_shard_1", numerator = 1, denominator = 762) { collectionLog = true }
                drop("item.godsword_shard_2", numerator = 1, denominator = 762) { collectionLog = true }
                drop("item.godsword_shard_3", numerator = 1, denominator = 762) { collectionLog = true }
            }

            mainDrops(127) {
                drop("item.adamant_arrow_p_3", amount = 295..300, weight = 8)
                drop("item.rune_scimitar", weight = 8)
                drop("item.adamant_platebody", weight = 8)
                drop("item.rune_platelegs", weight = 8)
                drop("item.dragon_dagger_p++", weight = 2)

                drop("item.super_attack_3", amount = 3, weight = 8)
                drop("item.super_strength_3", amount = 3, weight = 8)
                drop("item.super_restore_3", amount = 3, weight = 8)
                drop("item.zamorak_brew_3", amount = 3, weight = 8)

                drop("item.coins", amount = 19500..20000, weight = 37)
                drop("item.grimy_lantadyme_noted", amount = 10, weight = 8)
                drop("item.lantadyme_seed", amount = 3, weight = 8)
                drop("item.death_rune", amount = 120..125, weight = 8)
                drop("item.blood_rune", amount = 80..85, weight = 8)
                drop("item.nature_rune", amount = 65..70, weight = 8)
                drop("item.coins", amount = 20100..20600, weight = 1)
                drop("item.wine_of_zamorak_noted", amount = 2..10, weight = 4)
            }

            tertiaryDrops {
                drop("item.long_bone", numerator = 1, denominator = 400) { collectionLog = true }
                drop("item.curved_bone", numerator = 1, denominator = 5012) { collectionLog = true }
                drop(
                    "item.scroll_box_elite",
                    numerator = 1,
                    denominator = 250,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.ELITE) },
                )
            }
        }.apply { name = "General graardor" }
}
