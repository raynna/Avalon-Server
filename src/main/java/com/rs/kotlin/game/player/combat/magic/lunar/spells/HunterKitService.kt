package com.rs.kotlin.game.player.combat.magic.lunar.spells

import com.rs.java.game.Animation
import com.rs.java.game.Graphics
import com.rs.java.game.player.Player

object HunterKitService {
    private const val HUNTER_KIT_ID = 11159

    private val kitItems =
        intArrayOf(
            10150, // Noose wand
            10010, // Butterfly net
            10006, // Bird snare
            10031, // Rabbit snare
            10029, // Teasing stick
            596, // Unlit torch
            10008, // Box trap
            11260, // Impling jar
        )

    fun cast(player: Player): Boolean {
        if (player.inventory.containsItem(HUNTER_KIT_ID, 1)) {
            player.message("Why do you want to create a kit? You already have one.")
            return false
        }

        val hasAllItems = kitItems.all { player.inventory.containsItem(it, 1) }

        if (hasAllItems) {
            player.message("Why do you want to create a kit? You already have all the Hunter equipment it contains.")
            return false
        }

        if (!player.inventory.hasFreeSlots()) {
            player.message("You need at least one free inventory slot.")
            return false
        }

        player.animate(Animation(6303))
        player.gfx(Graphics(1063))

        player.inventory.addItem(HUNTER_KIT_ID, 1)

        return true
    }
}
