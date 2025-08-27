package com.rs.kotlin.game.player.combat.effects

import com.rs.java.game.Hit
import com.rs.java.game.item.Item
import com.rs.java.game.npc.NPC
import com.rs.java.game.player.Equipment
import com.rs.java.game.player.Player
import com.rs.java.utils.Utils
import com.rs.kotlin.game.player.combat.CombatStyle
import com.rs.kotlin.game.player.combat.CombatType
import com.rs.kotlin.game.player.combat.damage.CombatMultipliers.Style
import com.rs.kotlin.game.player.combat.magic.MagicStyle
import com.rs.kotlin.game.player.combat.melee.MeleeStyle
import com.rs.kotlin.game.player.combat.range.RangedStyle
import kotlin.math.ceil

object EquipmentEffects {

    enum class Phase { OUTGOING, INCOMING }

    data class Spec(
        val name: String,
        val itemIds: MutableList<Int>,
        val slot: Byte,                        // Equipment slot to check (e.g., Equipment.SLOT_SHIELD)
        val phase: Phase,                     // OUTGOING or INCOMING
        val style: Style,                     // MELEE/RANGE/MAGIC/HYBRID
        val predicate: (Player, Any) -> Boolean, // extra check (target type, etc.). For INCOMING second arg can be Unit
        val apply: (Player, Any, Hit) -> Unit // effect body
    )

    fun ctToStyle(ct: CombatType): Style = when (ct) {
        CombatType.MELEE -> Style.MELEE
        CombatType.RANGED -> Style.RANGE
        CombatType.MAGIC -> Style.MAGIC
    }

    private fun combatTypeOf(style: CombatStyle): CombatType = when (style) {
        is MeleeStyle -> CombatType.MELEE
        is RangedStyle -> CombatType.RANGED
        is MagicStyle -> CombatType.MAGIC
        else           -> CombatType.MELEE
    }

    private fun hasItem(player: Player, slot: Byte, ids: MutableList<Int>): Boolean {
        val id = player.equipment.items[slot.toInt()]?.id ?: return false
        return ids.any { it == id }
    }

    private fun isKalphite(target: Any): Boolean {
        val npc = target as? NPC ?: return false
        val n = npc.definitions?.name ?: return false
        return n.contains("kalphite", true) || n.contains("scarab", true)
    }

    private val DIVINE = Spec(
        name = "Divine spirit shield",
        itemIds = Item.getIds("item.divine_spirit_shield", "item.lucky_divine_spirit_shield"),
        slot = Equipment.SLOT_SHIELD,
        phase = Phase.INCOMING,
        style = Style.HYBRID,
        predicate = { _, _ -> true },
        apply = { defender, _, hit ->
            if (hit.damage <= 0) return@Spec
            defender as Player

            val base = hit.damage
            val drainNeeded = ceil(base * 0.06).toInt()
            if (defender.prayer.prayerPoints < drainNeeded) return@Spec

            val prevented = (base * 0.30).toInt()
            defender.prayer.drainPrayer(drainNeeded)
            hit.damage = (hit.damage - prevented).coerceAtLeast(0)
            defender.gfx(93, 0);
        }
    )

    private val ELYSIAN = Spec(
        name = "Elysian spirit shield",
        itemIds = Item.getIds("item.elysian_spirit_shield", "item.lucky_elysian_spirit_shield"),
        slot = Equipment.SLOT_SHIELD,
        phase = Phase.INCOMING,
        style = Style.HYBRID,
        predicate = { _, _ -> true },
        apply = { defender, _, hit ->
            if (hit.damage <= 0) return@Spec
            defender as Player
            if (Utils.roll(7, 10)) {
                val base = hit.damage
                val prevented = (base * 0.25).toInt()
                hit.damage = (hit.damage - prevented).coerceAtLeast(0)
                defender.gfx(93, 0);//533, -100 might be a good one too
            }
        }
    )

    private val KERIS = Spec(
        name = "Keris vs Kalphite",
        itemIds = Item.getIds("item.keris"),
        slot = Equipment.SLOT_WEAPON,
        phase = Phase.OUTGOING,
        style = Style.MELEE,
        predicate = { _, target -> isKalphite(target) },
        apply = { attacker, _, hit ->
            if (hit.damage <= 0) return@Spec
            attacker as Player
            val proc = 1.0 / 51.0 // tune to your spec
            if (Utils.randomDouble() < proc) {
                hit.damage *= 3
                // attacker.message("Your Keris strikes deep into the Kalphite!")
            }
        }
    )

    val ALL: List<Spec> = listOf(
        DIVINE,
        ELYSIAN,
        KERIS
    )

    fun applyOutgoing(attacker: Player, defender: Any, hit: Hit, style: CombatStyle) {
        applyOutgoing(attacker, defender, hit, combatTypeOf(style))
    }

    fun applyIncoming(defender: Player, hit: Hit, style: CombatStyle) {
        applyIncoming(defender, hit, combatTypeOf(style))
    }

    fun applyOutgoing(attacker: Player, defender: Any, hit: Hit, combatType: CombatType) {
        val style = ctToStyle(combatType)
        ALL.forEach { spec ->
            if (spec.phase != Phase.OUTGOING) return@forEach
            if (spec.style != Style.HYBRID && spec.style != style) return@forEach
            if (!hasItem(attacker, spec.slot, spec.itemIds)) return@forEach
            if (!spec.predicate(attacker, defender)) return@forEach
            spec.apply(attacker, defender, hit)
        }
    }

    fun applyIncoming(defender: Player, hit: Hit, combatType: CombatType) {
        val style = ctToStyle(combatType)
        ALL.forEach { spec ->
            if (spec.phase != Phase.INCOMING) return@forEach
            if (spec.style != Style.HYBRID && spec.style != style) return@forEach
            if (!hasItem(defender, spec.slot, spec.itemIds)) return@forEach
            if (!spec.predicate(defender, Unit)) return@forEach
            spec.apply(defender, Unit, hit)
        }
    }
}
