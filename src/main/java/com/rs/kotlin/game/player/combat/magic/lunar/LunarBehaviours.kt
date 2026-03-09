package com.rs.kotlin.game.player.combat.magic.lunar

import com.rs.kotlin.game.player.combat.magic.SpellBehaviour
import com.rs.kotlin.game.player.combat.magic.SpellHandler
import com.rs.kotlin.game.player.combat.magic.lunar.spells.BakePieService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.BoostPotionShareService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.CureGroupService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.CureMeService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.CureOtherService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.CurePlantService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.DisruptionShieldService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.DreamService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.EnergyTransferService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.FertileSoilService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.HealGroupService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.HealOtherService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.HumidifyService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.HunterKitService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.MagicImbueService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.MakeLeatherService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.MonsterExamineService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.NPCContactService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.PlankMakeService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.RepairRunePouchService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.StatRestoreShareService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.StatSpyService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.StringJewelleryService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.SuperGlassMakeService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.TuneOreService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.VengeanceGroupService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.VengeanceOtherService
import com.rs.kotlin.game.player.combat.magic.lunar.spells.VengeanceService

object LunarBehaviours {
    val bakePie =
        SpellBehaviour { player, spell ->
            BakePieService.cast(player)
        }

    val curePlant =
        SpellBehaviour { player, spell ->

            val obj = SpellHandler.getTargetObject(player) ?: return@SpellBehaviour false
            CurePlantService.cast(player, obj)
        }

    val monsterExamine =
        SpellBehaviour { player, spell ->

            val target = SpellHandler.getTargetNpc(player) ?: return@SpellBehaviour false
            MonsterExamineService.cast(player, target)
        }
    val cureOther =
        SpellBehaviour { player, spell ->

            val target = SpellHandler.getTarget(player) ?: return@SpellBehaviour false
            CureOtherService.cast(player, target)
        }

    val humidify =
        SpellBehaviour { player, spell ->
            HumidifyService.cast(player)
        }

    val hunterKit =
        SpellBehaviour { player, spell ->
            HunterKitService.cast(player)
        }

    val cureMe =
        SpellBehaviour { player, spell ->
            CureMeService.cast(player)
        }
    val cureGroup =
        SpellBehaviour { player, spell ->
            CureGroupService.cast(player)
        }

    val repairRunePouch =
        SpellBehaviour { player, spell ->
            RepairRunePouchService.cast(player)
        }

    val statSpy =
        SpellBehaviour { player, spell ->

            val target = SpellHandler.getTarget(player)
            StatSpyService.cast(player, target)
        }

    val superGlassMake =
        SpellBehaviour { player, spell ->
            SuperGlassMakeService.cast(player)
        }

    val npcContact =
        SpellBehaviour { player, spell ->
            NPCContactService.cast(player)
        }

    val stringJewellery =
        SpellBehaviour { player, spell ->
            StringJewelleryService.cast(player)
        }

    val fertileSoil =
        SpellBehaviour { player, spell ->
            val obj = SpellHandler.getTargetObject(player) ?: return@SpellBehaviour false
            FertileSoilService.cast(player, obj)
        }

    val makeLeather =
        SpellBehaviour { player, spell ->
            MakeLeatherService.cast(player)
        }

    val magicImbue =
        SpellBehaviour { player, spell ->
            MagicImbueService.cast(player)
        }

    val statRestoreShare =
        SpellBehaviour { player, spell ->

            val itemId = SpellHandler.getTargetItemId(player) ?: return@SpellBehaviour false
            val slotId = SpellHandler.getTargetSlotId(player) ?: return@SpellBehaviour false
            StatRestoreShareService.cast(player, itemId, slotId)
        }

    val boostPotionShare =
        SpellBehaviour { player, spell ->

            val itemId = SpellHandler.getTargetItemId(player) ?: return@SpellBehaviour false
            val slotId = SpellHandler.getTargetSlotId(player) ?: return@SpellBehaviour false
            BoostPotionShareService.cast(player, itemId, slotId)
        }
    val plankMake =
        SpellBehaviour { player, spell ->

            val item = SpellHandler.getTargetItemId(player) ?: return@SpellBehaviour false
            val slot = SpellHandler.getTargetSlotId(player) ?: return@SpellBehaviour false
            PlankMakeService.cast(player, item, slot, false)
        }

    val disruptionShield =
        SpellBehaviour { player, spell ->
            DisruptionShieldService.cast(player)
        }

    val energyTransfer =
        SpellBehaviour { player, spell ->
            val target = SpellHandler.getTargetPlayer(player) ?: return@SpellBehaviour false
            EnergyTransferService.cast(player, target)
        }

    val vengeance =
        SpellBehaviour { player, spell ->
            VengeanceService.cast(player)
        }

    val vengeanceOther =
        SpellBehaviour { player, spell ->
            val target = SpellHandler.getTargetPlayer(player) ?: return@SpellBehaviour false
            VengeanceOtherService.cast(player, target)
        }

    val vengeanceGroup =
        SpellBehaviour { player, spell ->
            VengeanceGroupService.cast(player)
        }

    val healOther =
        SpellBehaviour { player, spell ->
            val target = SpellHandler.getTargetPlayer(player) ?: return@SpellBehaviour false
            HealOtherService.cast(player, target)
        }

    val spellbookSwap =
        SpellBehaviour { player, _ ->
            player.dialogueManager.startDialogue("SpellbookSwap")
            true
        }

    val healGroup =
        SpellBehaviour { player, spell ->
            HealGroupService.cast(player)
        }

    val tuneBaneOre =
        SpellBehaviour { player, spell ->

            val item = SpellHandler.getTargetItemId(player) ?: return@SpellBehaviour false
            TuneOreService.cast(player, item)
        }

    val dream =
        SpellBehaviour { player, spell ->
            DreamService.cast(player)
        }
}
