package com.rs.kotlin.game.player.shop.shops

import com.rs.kotlin.game.player.shop.*
import com.rs.kotlin.game.player.shop.ShopDefinitions.shop

object AccessoriesShop : GameShop {
    override val definition: ShopDefinition = shop {
        title = "Accessories Store"
        currency = CurrencyType.COINS
        isGlobal = true

        item("item.ring_of_recoil")
        item("item.ring_of_duelling_8")
        item("item.ring_of_life")
        item("item.amulet_of_power")
        item("item.amulet_of_strength")
        item("item.amulet_of_glory")
        item("item.amulet_of_fury")
        item("item.amulet_of_torture")
        item("item.necklace_of_anguish")
        item("item.tormented_bracelet")
        item("item.berserker_necklace")
        item("item.combat_bracelet")
        item("item.regen_bracelet")
        item("item.berserker_ring")
        item("item.warrior_ring")
        item("item.archers_ring")
        item("item.seers_ring")
        item("item.mithril_gloves")
        item("item.adamant_gloves")
        item("item.rune_gloves")
        item("item.dragon_gloves")
        item("item.barrows_gloves")
        item("item.fighter_torso")
        item("item.fighter_hat")
        item("item.ava_s_accumulator")
        item("item.ava_s_alerter")
        item("item.ava_s_assembler")
        item("item.mithril_defender")
        item("item.adamant_defender")
        item("item.rune_defender")
        item("item.tokhaar_defender")
        item("item.tokhaar_defender_g")
        item("item.dragon_defender")
        item("item.avernic_defender")
        item("item.rune_pouch")
        item("item.imbued_saradomin_cape")
        item("item.imbued_zamorak_cape")
        item("item.imbued_guthix_cape")
        item("item.fire_cape")
        item("item.tokhaar_kal")
        item("item.tokhaar_kal_particles")
        item("item.infernal_cape")
        item("item.infernal_cape_particle")
    }
}
