package com.rs.kotlin.game.player.equipment

import com.rs.java.game.item.Item
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills

object DecorativeUpgrader {

    fun onLevelUp(player: Player, skill: Int, level: Int) {
        val rules = RULES_BY_SKILL[skill] ?: return
        applyRules(player, level, rules)
    }

    private fun applyRules(player: Player, level: Int, rules: List<UpgradeRule>) {
        val equip = player.equipment
        var upgradesApplied = 0

        for (rule in rules) {
            val maxTierIndex = allowedTierIndex(level, rule.levelBreakpoints)
            if (maxTierIndex == 0) continue

            var item = equip.getItem(rule.slot.toInt()) ?: continue

            for (i in 0 until maxTierIndex) {
                if (matches(item, rule.tiers[i])) {
                    update(equip, rule.slot.toInt(), rule.tiers[i + 1])
                    upgradesApplied++
                    player.message(rule.message)
                    item = equip.getItem(rule.slot.toInt()) ?: break
                }
            }
        }
    }

    private fun allowedTierIndex(level: Int, breakpoints: IntArray): Int {
        var idx = 0
        for (bp in breakpoints) {
            if (level >= bp) idx++
        }
        return idx
    }

    private fun matches(item: Item, tier: Tier): Boolean = when (tier) {
        is Tier.Key -> item.isItem(tier.key)
        is Tier.Id  -> item.id == tier.id
    }

    private fun update(equip: Equipment, slot: Int, tier: Tier) {
        when (tier) {
            is Tier.Key -> equip.updateItem(slot, tier.key)
            is Tier.Id  -> equip.updateItem(slot, tier.id)
        }
    }

    private val RULES_BY_SKILL: Map<Int, List<UpgradeRule>> = mapOf(
        Skills.ATTACK to listOf(
            UpgradeRule(
                skill = Skills.ATTACK,
                slot = Equipment.SLOT_WEAPON,
                tiers = listOf(
                    Tier.Key("item.basic_decorative_sword"),
                    Tier.Key("item.detailed_decorative_sword"),
                    Tier.Key("item.intricate_decorative_sword"),
                    Tier.Key("item.profound_decorative_sword"),
                ),
                message = "Your decorative weapon has been upgraded."
            )
        ),

        Skills.DEFENCE to listOf(
            UpgradeRule(
                skill = Skills.DEFENCE,
                slot = Equipment.SLOT_HEAD,
                tiers = listOf(
                    Tier.Key("item.basic_decorative_helm"),
                    Tier.Key("item.detailed_decorative_helm"),
                    Tier.Key("item.intricate_decorative_helm"),
                    Tier.Key("item.profound_decorative_helm"),
                ),
                message = "Your decorative helm has been upgraded."
            ),
            UpgradeRule(
                skill = Skills.DEFENCE,
                slot = Equipment.SLOT_CHEST,
                tiers = listOf(
                    Tier.Key("item.basic_decorative_platebody"),
                    Tier.Key("item.detailed_decorative_platebody"),
                    Tier.Key("item.intricate_decorative_platebody"),
                    Tier.Key("item.profound_decorative_platebody"),
                ),
                message = "Your decorative platebody has been upgraded."
            ),
            // If you only have IDs for legs/shield, just use Tier.Id:
            UpgradeRule(
                skill = Skills.DEFENCE,
                slot = Equipment.SLOT_LEGS,
                tiers = listOf(
                    Tier.Key("item.basic_decorative_platelegs"),
                    Tier.Key("item.detailed_decorative_platelegs"),
                    Tier.Key("item.intricate_decorative_platelegs"),
                    Tier.Key("item.profound_decorative_platelegs"),
                ),
                message = "Your decorative platelegs has been upgraded."
            ),
            UpgradeRule(
                skill = Skills.DEFENCE,
                slot = Equipment.SLOT_SHIELD,
                tiers = listOf(
                    Tier.Key("item.basic_decorative_shield"),
                    Tier.Key("item.detailed_decorative_shield"),
                    Tier.Key("item.intricate_decorative_shield"),
                    Tier.Key("item.profound_decorative_shield"),
                ),
                message = "Your decorative shield has been upgraded."
            )
        )
    )
}
