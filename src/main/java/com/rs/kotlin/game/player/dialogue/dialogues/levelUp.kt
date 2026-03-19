package com.rs.kotlin.game.player.dialogue.dialogues

import com.rs.java.game.Graphics
import com.rs.java.game.player.Player
import com.rs.java.game.player.Skills
import com.rs.kotlin.game.player.equipment.DecorativeUpgrader
import com.rs.kotlin.game.player.queue.QueueTask

private val FLASH_IDS =
    mapOf(
        Skills.ATTACK to 4732,
        Skills.STRENGTH to 4733,
        Skills.DEFENCE to 4734,
        Skills.RANGE to 4735,
        Skills.PRAYER to 4736,
        Skills.MAGIC to 4737,
        Skills.HITPOINTS to 4738,
        Skills.AGILITY to 4739,
        Skills.HERBLORE to 4740,
        Skills.THIEVING to 4741,
        Skills.CRAFTING to 4742,
        Skills.FLETCHING to 4743,
        Skills.MINING to 4744,
        Skills.SMITHING to 4745,
        Skills.FISHING to 4746,
        Skills.COOKING to 4747,
        Skills.FIREMAKING to 4748,
        Skills.WOODCUTTING to 4749,
        Skills.RUNECRAFTING to 4750,
        Skills.SLAYER to 4751,
        Skills.FARMING to 4752,
        Skills.CONSTRUCTION to 4753,
        Skills.HUNTER to 4754,
        Skills.SUMMONING to 4755,
        Skills.DUNGEONEERING to 7756,
    )

// Skill index → skill icon value (used for varbit.levelup_skill_icon)
private val ICON_IDS =
    mapOf(
        Skills.ATTACK to 1,
        Skills.STRENGTH to 2,
        Skills.RANGE to 3,
        Skills.MAGIC to 4,
        Skills.DEFENCE to 5,
        Skills.HITPOINTS to 6,
        Skills.PRAYER to 7,
        Skills.AGILITY to 8,
        Skills.HERBLORE to 9,
        Skills.THIEVING to 10,
        Skills.CRAFTING to 11,
        Skills.RUNECRAFTING to 12,
        Skills.MINING to 13,
        Skills.SMITHING to 14,
        Skills.FISHING to 15,
        Skills.COOKING to 16,
        Skills.FIREMAKING to 17,
        Skills.WOODCUTTING to 18,
        Skills.FLETCHING to 19,
        Skills.SLAYER to 20,
        Skills.FARMING to 21,
        Skills.CONSTRUCTION to 22,
        Skills.HUNTER to 23,
        Skills.SUMMONING to 24,
        Skills.DUNGEONEERING to 25,
    )

private val CONFIG_IDS =
    mapOf(
        Skills.ATTACK to 1,
        Skills.STRENGTH to 2,
        Skills.RANGE to 3,
        Skills.MAGIC to 4,
        Skills.DEFENCE to 5,
        Skills.HITPOINTS to 6,
        Skills.PRAYER to 7,
        Skills.AGILITY to 8,
        Skills.HERBLORE to 9,
        Skills.THIEVING to 10,
        Skills.CRAFTING to 11,
        Skills.RUNECRAFTING to 12,
        Skills.MINING to 13,
        Skills.SMITHING to 14,
        Skills.FISHING to 15,
        Skills.COOKING to 16,
        Skills.FIREMAKING to 17,
        Skills.WOODCUTTING to 18,
        Skills.FLETCHING to 19,
        Skills.SLAYER to 20,
        Skills.FARMING to 21,
        Skills.CONSTRUCTION to 22,
        Skills.HUNTER to 23,
        Skills.SUMMONING to 24,
        Skills.DUNGEONEERING to 25,
    )

val LEVEL_GAINED_CONFIGS =
    mapOf(
        Skills.ATTACK to 1469,
        Skills.STRENGTH to 1470,
        Skills.DEFENCE to 1471,
        Skills.RANGE to 1472,
        Skills.PRAYER to 1473,
        Skills.MAGIC to 1474,
        Skills.HITPOINTS to 1475,
        Skills.AGILITY to 1476,
        Skills.HERBLORE to 1477,
        Skills.THIEVING to 1478,
        Skills.CRAFTING to 1479,
        Skills.FLETCHING to 1480,
        Skills.MINING to 1481,
        Skills.SMITHING to 1482,
        Skills.FISHING to 1483,
        Skills.COOKING to 1484,
        Skills.FIREMAKING to 1485,
        Skills.WOODCUTTING to 1486,
        Skills.RUNECRAFTING to 1487,
        Skills.SLAYER to 1488,
        Skills.FARMING to 1489,
        Skills.CONSTRUCTION to 1490,
        Skills.HUNTER to 1491,
        Skills.SUMMONING to 1492,
        Skills.DUNGEONEERING to 1493,
    )

// Skill index → Pair(soundBelow50, soundAbove50)
private val LEVEL_UP_SOUNDS =
    mapOf(
        Skills.ATTACK to (29 to 30),
        Skills.STRENGTH to (65 to 66),
        Skills.DEFENCE to (37 to 38),
        Skills.HITPOINTS to (47 to 48),
        Skills.RANGE to (57 to 58),
        Skills.MAGIC to (51 to 52),
        Skills.PRAYER to (55 to 56),
        Skills.AGILITY to (28 to 322),
        Skills.HERBLORE to (45 to 46),
        Skills.THIEVING to (67 to 68),
        Skills.CRAFTING to (35 to 36),
        Skills.RUNECRAFTING to (59 to 60),
        Skills.MINING to (53 to 54),
        Skills.SMITHING to (63 to 64),
        Skills.FISHING to (41 to 42),
        Skills.COOKING to (33 to 34),
        Skills.FIREMAKING to (39 to 40),
        Skills.WOODCUTTING to (69 to 70),
        Skills.FLETCHING to (43 to 44),
        Skills.SLAYER to (61 to 62),
        Skills.FARMING to (11 to 10),
        Skills.CONSTRUCTION to (31 to 32),
        Skills.HUNTER to (49 to 50),
        Skills.SUMMONING to (300 to 301),
        Skills.DUNGEONEERING to (416 to 417),
    )

fun switchFlash(
    player: Player,
    skill: Int,
    on: Boolean,
) {
    val id = FLASH_IDS[skill] ?: return
    player.varsManager.sendVarBit(id, if (on) 1 else 0)
}

suspend fun QueueTask.levelUp(
    skill: Int,
    oldLevel: Int,
    newLevel: Int,
) {
    val name = Skills.SKILL_NAME[skill]
    val levelsGained = newLevel - oldLevel

    player.temporaryAttribute()["leveledUp[$skill]"] = true
    player.temporaryAttribute()["LEVELUP[$skill]:GAINEDLEVELS"] = levelsGained
    player.temporaryAttribute()["LEVELUP[$skill]:OLDLEVEL"] = oldLevel
    player.temporaryAttribute()["LEVELUP[$skill]:NEWLEVEL"] = newLevel

    if (newLevel >= 90) {
        player.adventureLog.addActivity("I levelled up my $name. I am now level $newLevel.")
    }

    val (soundLow, soundHigh) = LEVEL_UP_SOUNDS[skill] ?: return
    player.packets.sendMusicEffect(if (newLevel > 50) soundHigh else soundLow)

    player.gfx(Graphics(199))
    if (newLevel == 99 || newLevel == 120) {
        player.gfx(Graphics(1765))
    }

    player.lastlevelUp.apply {
        clear()
        add(newLevel.toString())
    }
    player.lastSkill.apply {
        clear()
        add(name)
    }

    DecorativeUpgrader.onLevelUp(player, skill, newLevel)
    // player.interfaceManager.sendFadingInterface("interface.levelup_orb")
    player.varsManager.sendVarBit("varbit.levelup_skill_icon", ICON_IDS[skill] ?: 0)
    player.packets.sendGlobalVar("globalvar.levelup_orb_skill", CONFIG_IDS[skill] ?: 0)
    val gainedLevelConfig = LEVEL_GAINED_CONFIGS[skill] ?: return
    player.packets.sendGlobalVar(gainedLevelConfig, newLevel - levelsGained)
    switchFlash(player, skill, true)

    renderer.levelup(skill, newLevel, levelsGained)
    waitContinue()
}
