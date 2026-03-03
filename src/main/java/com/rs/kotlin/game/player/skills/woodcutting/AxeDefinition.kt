package com.rs.kotlin.game.player.skills.woodcutting

import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills

enum class AxeDefinition(
    val itemId: Int,
    val levelRequired: Int,
    val animation: Int,
    val priority: Int,
) {
    BRONZE(1351, 1, 879, 1),
    IRON(1349, 1, 877, 2),
    STEEL(1353, 6, 875, 3),
    BLACK(1361, 11, 873, 4),
    MITHRIL(1355, 21, 871, 5),
    ADAMANT(1357, 31, 869, 6),
    RUNE(1359, 41, 867, 7),
    DRAGON(6739, 61, 2846, 8),
    INFERNO_ADZE(13661, 61, 10251, 9),
    ;

    fun isInfernalAdze(): Boolean = this == INFERNO_ADZE

    fun successCurveFor(tree: TreeDefinition): SucessCurve =
        when (tree) {
            TreeDefinition.NORMAL -> {
                when (this) {
                    BRONZE -> SucessCurve(low = 64, high = 200)
                    IRON -> SucessCurve(low = 96, high = 300)
                    STEEL -> SucessCurve(low = 128, high = 400)
                    BLACK -> SucessCurve(low = 144, high = 450)
                    MITHRIL -> SucessCurve(low = 160, high = 500)
                    ADAMANT -> SucessCurve(low = 192, high = 600)
                    RUNE -> SucessCurve(low = 224, high = 700)
                    DRAGON -> SucessCurve(low = 240, high = 750)
                    INFERNO_ADZE -> SucessCurve(low = 240, high = 750)
                }
            }

            TreeDefinition.OAK -> {
                when (this) {
                    BRONZE -> SucessCurve(low = 32, high = 100)
                    IRON -> SucessCurve(low = 48, high = 150)
                    STEEL -> SucessCurve(low = 64, high = 200)
                    BLACK -> SucessCurve(low = 72, high = 225)
                    MITHRIL -> SucessCurve(low = 80, high = 250)
                    ADAMANT -> SucessCurve(low = 96, high = 300)
                    RUNE -> SucessCurve(low = 112, high = 350)
                    DRAGON -> SucessCurve(low = 120, high = 375)
                    INFERNO_ADZE -> SucessCurve(low = 120, high = 375)
                }
            }

            TreeDefinition.WILLOW -> {
                when (this) {
                    BRONZE -> SucessCurve(low = 16, high = 50)
                    IRON -> SucessCurve(low = 24, high = 75)
                    STEEL -> SucessCurve(low = 32, high = 100)
                    BLACK -> SucessCurve(low = 36, high = 112)
                    MITHRIL -> SucessCurve(low = 40, high = 125)
                    ADAMANT -> SucessCurve(low = 48, high = 150)
                    RUNE -> SucessCurve(low = 56, high = 175)
                    DRAGON -> SucessCurve(low = 60, high = 187)
                    INFERNO_ADZE -> SucessCurve(low = 60, high = 187)
                }
            }

            TreeDefinition.MAPLE -> {
                when (this) {
                    BRONZE -> SucessCurve(low = 8, high = 25)
                    IRON -> SucessCurve(low = 12, high = 37)
                    STEEL -> SucessCurve(low = 16, high = 50)
                    BLACK -> SucessCurve(low = 18, high = 56)
                    MITHRIL -> SucessCurve(low = 20, high = 62)
                    ADAMANT -> SucessCurve(low = 24, high = 75)
                    RUNE -> SucessCurve(low = 28, high = 87)
                    DRAGON -> SucessCurve(low = 30, high = 93)
                    INFERNO_ADZE -> SucessCurve(low = 30, high = 93)
                }
            }

            TreeDefinition.FRUIT_TREES -> {
                when (this) {
                    BRONZE -> SucessCurve(low = 8, high = 25)
                    IRON -> SucessCurve(low = 12, high = 37)
                    STEEL -> SucessCurve(low = 16, high = 50)
                    BLACK -> SucessCurve(low = 18, high = 56)
                    MITHRIL -> SucessCurve(low = 20, high = 62)
                    ADAMANT -> SucessCurve(low = 24, high = 75)
                    RUNE -> SucessCurve(low = 28, high = 87)
                    DRAGON -> SucessCurve(low = 30, high = 93)
                    INFERNO_ADZE -> SucessCurve(low = 30, high = 93)
                }
            }

            TreeDefinition.YEW -> {
                when (this) {
                    BRONZE -> SucessCurve(low = 4, high = 12)
                    IRON -> SucessCurve(low = 6, high = 19)
                    STEEL -> SucessCurve(low = 8, high = 25)
                    BLACK -> SucessCurve(low = 9, high = 28)
                    MITHRIL -> SucessCurve(low = 10, high = 31)
                    ADAMANT -> SucessCurve(low = 12, high = 37)
                    RUNE -> SucessCurve(low = 14, high = 44)
                    DRAGON -> SucessCurve(low = 15, high = 47)
                    INFERNO_ADZE -> SucessCurve(low = 15, high = 47)
                }
            }

            TreeDefinition.IVY -> {
                when (this) {
                    BRONZE -> SucessCurve(low = 4, high = 12)
                    IRON -> SucessCurve(low = 6, high = 19)
                    STEEL -> SucessCurve(low = 8, high = 25)
                    BLACK -> SucessCurve(low = 9, high = 28)
                    MITHRIL -> SucessCurve(low = 10, high = 31)
                    ADAMANT -> SucessCurve(low = 12, high = 37)
                    RUNE -> SucessCurve(low = 14, high = 44)
                    DRAGON -> SucessCurve(low = 15, high = 47)
                    INFERNO_ADZE -> SucessCurve(low = 15, high = 47)
                }
            }

            TreeDefinition.MAGIC -> {
                when (this) {
                    BRONZE -> SucessCurve(low = 2, high = 6)
                    IRON -> SucessCurve(low = 3, high = 9)
                    STEEL -> SucessCurve(low = 4, high = 12)
                    BLACK -> SucessCurve(low = 5, high = 13)
                    MITHRIL -> SucessCurve(low = 5, high = 15)
                    ADAMANT -> SucessCurve(low = 6, high = 18)
                    RUNE -> SucessCurve(low = 7, high = 21)
                    DRAGON -> SucessCurve(low = 7, high = 22)
                    INFERNO_ADZE -> SucessCurve(low = 7, high = 22)
                }
            }

            TreeDefinition.BLOODWOOD -> {
                when (this) {
                    BRONZE -> SucessCurve(low = 2, high = 6)
                    IRON -> SucessCurve(low = 3, high = 9)
                    STEEL -> SucessCurve(low = 4, high = 12)
                    BLACK -> SucessCurve(low = 5, high = 13)
                    MITHRIL -> SucessCurve(low = 5, high = 15)
                    ADAMANT -> SucessCurve(low = 6, high = 18)
                    RUNE -> SucessCurve(low = 7, high = 21)
                    DRAGON -> SucessCurve(low = 7, high = 22)
                    INFERNO_ADZE -> SucessCurve(low = 7, high = 22)
                }
            }
        }

    companion object {
        fun getBestAxe(player: Player): AxeDefinition? =
            entries
                .filter {
                    player.hasTool(it.itemId) &&
                        player.skills.getLevel(Skills.WOODCUTTING) >= it.levelRequired
                }.maxByOrNull { it.priority }
    }
}
