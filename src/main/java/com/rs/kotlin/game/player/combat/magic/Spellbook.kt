package com.rs.kotlin.game.player.combat.magic

import com.rs.java.game.player.Player
import com.rs.kotlin.game.player.combat.magic.ancient.AncientMagicks
import com.rs.kotlin.game.player.combat.magic.dungeoneering.DungeoneeringMagicks
import com.rs.kotlin.game.player.combat.magic.lunar.LunarMagicks
import com.rs.kotlin.game.player.combat.magic.modern.ModernMagicks

abstract class Spellbook(
    val id: Int,
) {
    companion object {
        const val MODERN_ID = 192
        const val ANCIENT_ID = 193
        const val LUNAR_ID = 430
        const val DUNGEONEERING_ID = 950

        val MODERN: ModernMagicks by lazy { ModernMagicks }
        val ANCIENT: AncientMagicks by lazy { AncientMagicks }
        val LUNAR: LunarMagicks by lazy { LunarMagicks }
        val DUNGEONEERING: DungeoneeringMagicks by lazy { DungeoneeringMagicks }

        @JvmStatic
        fun get(id: Int): Spellbook? =
            when (id) {
                MODERN_ID -> MODERN
                ANCIENT_ID -> ANCIENT
                LUNAR_ID -> LUNAR
                DUNGEONEERING_ID -> DUNGEONEERING
                else -> null
            }

        @JvmStatic
        fun getSpellById(
            player: Player,
            spellId: Int,
        ): Spell? =
            when (player.combatDefinitions.getSpellBook()) {
                MODERN_ID -> MODERN.getSpell(spellId)
                ANCIENT_ID -> ANCIENT.getSpell(spellId)
                LUNAR_ID -> LUNAR.getSpell(spellId)
                DUNGEONEERING_ID -> DUNGEONEERING.getSpell(spellId)
                else -> null
            }
    }

    abstract val spells: List<Spell>

    private val spellMap by lazy { spells.associateBy { it.id } }

    fun getRunesFor(spellId: Int): List<RuneRequirement> = spellMap[spellId]?.runes ?: emptyList()

    fun getSpell(spellId: Int): Spell? = spellMap[spellId]
}
