package raynna.game.npc.drops.tables

import raynna.game.player.content.treasuretrails.TreasureTrailsManager
import raynna.game.npc.TableCategory
import raynna.game.npc.drops.config.HerbTableConfig
import raynna.game.npc.drops.config.RareTableConfig
import raynna.game.npc.drops.dropTable

object SkeletalWyvernDropTable {
    val table =
        dropTable(
            category = TableCategory.DRAGON,
            herbTables = HerbTableConfig(amount = 1..3, numerator = 7, denominator = 128),
            rareTable = RareTableConfig(3, 128),
        ) {
            always {
                drop("item.wyvern_bones")
            }
            prerollDenom(meta = { collectionLog = true }) {
                drop("item.granite_legs", numerator = 1, denominator = 512)
                drop("item.dragon_platelegs", numerator = 1, denominator = 512)
                drop("item.dragon_plateskirt", numerator = 1, denominator = 512)
            }
            main(128) {
                drop("item.earth_battlestaff", weight = 4)
                drop("item.battlestaff_noted", amount = 10, weight = 3)
                drop("item.rune_hatchet", weight = 3)
                drop("item.rune_battleaxe", weight = 2)
                drop("item.rune_warhammer", weight = 2)
                drop("item.rune_full_helm", weight = 2)
                drop("item.rune_kiteshield", weight = 2)
                drop("item.air_rune", amount = 225, weight = 6)
                drop("item.rune_arrow", amount = 36, weight = 5)
                drop("item.water_rune", amount = 150, weight = 4)
                drop("item.chaos_rune", amount = 80, weight = 4)
                drop("item.law_rune", amount = 45, weight = 4)
                drop("item.death_rune", amount = 40, weight = 4)
                drop("item.blood_rune", amount = 25, weight = 4)
                drop("item.adamant_bolts", amount = 75..99, weight = 3)
                drop("item.runite_bolts", amount = 35..44, weight = 3)
                drop("item.soul_rune", amount = 20, weight = 1)
                drop("item.pure_essence_noted", amount = 250, weight = 8)
                drop("item.magic_logs_noted", amount = 35, weight = 6)
                drop("item.adamant_bar_noted", amount = 10, weight = 6)
                drop("item.iron_ore_noted", amount = 200, weight = 3)
                drop("item.uncut_ruby_noted", amount = 10, weight = 2)
                drop("item.uncut_diamond_noted", amount = 5, weight = 2)
                drop("item.coins", amount = 300, weight = 12)
                drop("item.lobster", amount = 6, weight = 8)
                drop("item.prayer_potion_4", amount = 2, weight = 7)
                drop("item.unpowered_orb_noted", amount = 75, weight = 2)
                drop("item.runite_c_bow_u", weight = 2)
                drop("item.ranarr_seed", amount = 3, weight = 2)
                drop("item.snapdragon_seed", weight = 2)
            }
            tertiary {
                drop(
                    "item.scroll_box_elite",
                    numerator = 1,
                    denominator = 350,
                    condition = { context -> !context.player.treasureTrailsManager.hasClueScrollByLevel(TreasureTrailsManager.ELITE) },
                )
                drop("item.draconic_visage", 1, numerator = 1, denominator = 10000) {
                    collectionLog = true
                    announce = true
                }
                drop("item.ancient_wyvern_shield_uncharged", 1, numerator = 1, denominator = 10000) {
                    collectionLog = true
                    announce = true
                }
                drop("item.starved_ancient_effigy", 1, numerator = 1, denominator = 18000)
            }

            charm {
                gold(amount = 2, percent = 6.83)
                green(amount = 2, percent = 3.41)
                crimson(amount = 2, percent = 54.6)
                blue(amount = 2, percent = 1.37)
            }
        }.apply {
            name = "Skeletal wyvern"
            collectionGroup = "Dragons"
        }
}
