package com.rs.kotlin.game.npc.drops.tables

import com.rs.java.game.player.content.treasuretrails.TreasureTrailsManager
import com.rs.kotlin.game.npc.TableCategory
import com.rs.kotlin.game.npc.drops.config.SeedTableConfig
import com.rs.kotlin.game.npc.drops.dropTable
import com.rs.kotlin.game.npc.drops.seed.SeedTableType

object TormentedDemonDropTable {
    val table =
        dropTable(
            category = TableCategory.BOSS,
            seedTable = SeedTableConfig(SeedTableType.TREE_HERB, numerator = 1, denominator = 51),
        ) {
            always {
                drop("item.infernal_ashes")
            }
            prerollDenom {
                drop("item.dragon_claws", numerator = 1, denominator = 512) {
                    collectionLog = true
                    announce = true
                }
                drop("item.emberlight", numerator = 1, denominator = 512) {
                    collectionLog = true
                    announce = true
                }
                drop("item.ruined_dragon_armour_slice", numerator = 1, denominator = 256) { collectionLog = true }
                drop("item.ruined_dragon_armour_lump", numerator = 1, denominator = 256) { collectionLog = true }
                drop("item.ruined_dragon_armour_shard", numerator = 1, denominator = 256) { collectionLog = true }
                drop("item.dragon_crossbow", numerator = 1, denominator = 256) { collectionLog = true }
            }
            main(51) {
                drop("item.rune_platebody", weight = 4)
                drop("item.dragon_dagger", weight = 3)
                drop("item.battlestaff_noted", weight = 3)
                drop("item.rune_kiteshield", amount = 2, weight = 2)

                drop("item.chaos_rune", amount = 25..100, weight = 4)
                drop("item.rune_arrow", amount = 65..125, weight = 4)
                drop("item.soul_rune", amount = 50..75, weight = 2)

                drop("item.manta_ray", amount = 1..2, 4)
                drop("item.prayer_potion_4", amount = 1, 1)
                drop("item.prayer_potion_2", amount = 1, 1)

                drop("item.infernal_ashes", amount = 2..3, 2)
                drop("item.fire_orb_noted", amount = 5..7, 2)
                drop("item.dragon_arrowheads", amount = 30..40, 1)

                nestedTable(weight = 6, size = 48) {
                    drop("item.grimy_kwuarm", weight = 10)
                    drop("item.grimy_dwarf_weed", weight = 8)
                    drop("item.grimy_cadantine", weight = 8)
                    drop("item.grimy_lantadyme", weight = 6)
                    drop("item.grimy_avantoe", weight = 5)
                    drop("item.grimy_ranarr", weight = 4)
                    drop("item.grimy_snapdragon", weight = 4)
                    drop("item.grimy_torstol", weight = 3)
                }

                nestedTable(weight = 6, size = 30) {
                    drop("item.magic_shortbow_u_noted", weight = 29)
                    drop("item.magic_longbow_u", weight = 1)
                }
            }
            tertiary {
                drop(
                    "item.scroll_box_elite",
                    numerator = 1,
                    denominator = 128,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.ELITE) },
                )
            }
        }.apply { name = "Tormented demon" }
}
