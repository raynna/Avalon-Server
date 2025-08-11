package com.rs.kotlin.game.player.combat.magic

import com.rs.java.game.Graphics
import com.rs.java.game.WorldTile
import com.rs.kotlin.game.world.projectile.Projectile

sealed class SpellType {
    object Combat : SpellType()
    object Teleport : SpellType()
    object Instant : SpellType()
    object Item : SpellType()
    object Object : SpellType()
    object FloorItem : SpellType()
    object Target : SpellType()

    companion object {
        val ALL_TYPES = listOf(
            Combat,
            Teleport,
            Instant,
            Item,
            Object,
            FloorItem,
            Target
        )
    }
}

data class RuneRequirement(
    val id: Int,
    val amount: Int,
    val canBeInfinite: Boolean = false,
    val compositeRunes: List<Int> = emptyList()
)

data class StaffRequirement(val ids: List<Int> = emptyList())

data class Spell(
    val id: Int,
    val name: String,
    val level: Int,
    val damage: Int = -1,
    val xp: Double,
    val type: SpellType,
    val bind: Int = -1,
    val drain: Boolean = false,
    val element: ElementType = ElementType.None,
    val multi: Boolean = false,
    val runes: List<RuneRequirement>,
    val staff: StaffRequirement? = null,
    val teleportLocation: WorldTile? = null,
    val animationId: Int = -1,
    val graphicId: Graphics = Graphics(-1),
    val endGraphic: Graphics = Graphics(-1),
    val projectileType: Projectile = Projectile.ELEMENTAL_SPELL,
    val projectileId: Int = -1,
    val projectileIds: List<Int> = emptyList(),
    val requiredItem: Int? = null
) {
    fun isElemental(): Boolean {
        return element in listOf(
            ElementType.Air,
            ElementType.Water,
            ElementType.Earth,
            ElementType.Fire
        )
    }
}