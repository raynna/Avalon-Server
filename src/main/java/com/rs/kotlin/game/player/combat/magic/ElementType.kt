package com.rs.kotlin.game.player.combat.magic

sealed class ElementType {
    object None : ElementType()

    object Air : ElementType()

    object Water : ElementType()

    object Earth : ElementType()

    object Fire : ElementType()

    object Ice : ElementType()

    object Blood : ElementType()

    object Shadow : ElementType()

    object Smoke : ElementType()

    fun key(): String =
        when (this) {
            is None -> "none"
            is Air -> "air"
            is Water -> "water"
            is Earth -> "earth"
            is Fire -> "fire"
            is Ice -> "ice"
            is Blood -> "blood"
            is Shadow -> "shadow"
            is Smoke -> "smoke"
        }

    companion object {
        val ALL_ELEMENTS =
            listOf(
                None,
                Air,
                Water,
                Earth,
                Fire,
                Ice,
                Blood,
                Shadow,
                Smoke,
            )

        fun fromString(name: String): ElementType? = ALL_ELEMENTS.find { it.toString().equals(name, ignoreCase = true) }
    }

    override fun toString(): String =
        when (this) {
            is None -> "None"
            is Air -> "Air"
            is Water -> "Water"
            is Earth -> "Earth"
            is Fire -> "Fire"
            is Ice -> "Ice"
            is Blood -> "Blood"
            is Shadow -> "Shadow"
            is Smoke -> "Smoke"
        }
}
